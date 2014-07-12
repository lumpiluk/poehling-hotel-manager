/**
 * 
 */
package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author lumpiluk
 *
 */
public class Person extends HotelData {

	/**
	 * Columns in this object's table.
	 * Use getCol(Cols col) to get the respective column name.
	 * @author lumpiluk
	 *
	 */
	protected static enum Cols {
		INDEX, ADDRESS, TITLE, FIRST_NAMES, SURNAMES, BIRTHDAY, FOOD_MEMO;
	}
	
	private long id;
	
	private long addressId;
	
	private String title;
	
	private String firstNames;
	
	private String surnames;
	
	private Date birthday;
	
	private String foodMemo;
	
	
	public Person(final Connection con) {
		super(con);
		this.setId(0);
		this.setAddressId(0);
	}

	/**
	 * Used for dynamically binding database constants to each class so that
	 * subclasses implementing a database import can override this method.
	 * @param col
	 * @return
	 */
	protected String getCol(final Cols col) {
		if (col == Cols.INDEX) { return "id"; }
		else if (col == Cols.ADDRESS) { return "address_id"; }
		else if (col == Cols.TITLE) { return "title"; }
		else if (col == Cols.FIRST_NAMES) { return "first_names"; }
		else if (col == Cols.SURNAMES) { return "surnames"; }
		else if (col == Cols.BIRTHDAY) { return "birthday"; }
		else if (col == Cols.FOOD_MEMO) { return "food_memo"; }
		else { throw new NoSuchElementException(); }
	}
	
	protected String getTableName() {
		return "people";
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	private void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @return the address associated with this Person. May be null.
	 * @throws SQLException
	 */
	public Address getAddress() throws SQLException {
		Address a = new Address(con);
		try {
			a.fromDbAtIndex(addressId);
		} catch (NoSuchElementException e) {
			return null;
		}
		return a;
	}
	
	private long getAddressId() {
		return addressId;
	}
	
	private void setAddressId(long addressId) {
		this.addressId = addressId;
	}
	
	public void setAddress(Address address) {
		setAddressId(address.getId());
	}

	/**
	 * @param id the id to set
	 */
	private void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the firstNames
	 */
	public String getFirstNames() {
		return firstNames;
	}

	/**
	 * @param firstNames the firstNames to set
	 */
	public void setFirstNames(String firstNames) {
		this.firstNames = firstNames;
	}

	/**
	 * @return the surnames
	 */
	public String getSurnames() {
		return surnames;
	}

	/**
	 * @param surnames the surnames to set
	 */
	public void setSurnames(String surnames) {
		this.surnames = surnames;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the foodMemo
	 */
	public String getFoodMemo() {
		return foodMemo;
	}

	/**
	 * @param foodMemo the foodMemo to set
	 */
	public void setFoodMemo(String foodMemo) {
		this.foodMemo = foodMemo;
	}
	
	/**
	 * Used in fromDbAtIndex and getBatch.
	 * @param p The Person the data of which will be prepared.
	 * @param rs The result set.
	 * @throws SQLException
	 */
	private void prepareDataFromResultSet(final Person p, final ResultSet rs) 
			throws SQLException {
		p.setId(rs.getLong(getCol(Cols.INDEX))); // works although private :)
		p.setAddressId(rs.getLong(getCol(Cols.ADDRESS))); // 0 if SQL NULL
		p.setBirthday(rs.getDate(getCol(Cols.BIRTHDAY)));
		p.setFirstNames(rs.getString(getCol(Cols.FIRST_NAMES)));
		p.setFoodMemo(rs.getString(getCol(Cols.FOOD_MEMO)));
		p.setSurnames(rs.getString(getCol(Cols.SURNAMES)));
		p.setTitle(rs.getString(getCol(Cols.TITLE)));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fromDbAtIndex(final long d) throws NoSuchElementException,
			SQLException {
		boolean success = false;
		String query = "SELECT * FROM " + getTableName() + " WHERE "
				+ getCol(Cols.INDEX) + " = ?";
		try (PreparedStatement stmt = con.prepareStatement(query)) {
			stmt.setLong(1, this.getId());
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.first()) {
					throw new NoSuchElementException();
				}
				prepareDataFromResultSet(this, rs);
				success = true;
			}
		}
		return success;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	public Iterable<Person> getBatch(List<Long> indices) throws SQLException {
		if (indices == null)
			throw new IllegalArgumentException();
		List<Person> resultList = new LinkedList<Person>();
		if (!indices.isEmpty()) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT * FROM " + getTableName() + " WHERE "
					+ getCol(Cols.INDEX) + " IN (");
			for (@SuppressWarnings("unused") Long index : indices) {
				sql.append("?,");
			}
			sql.deleteCharAt(sql.length() - 1); // remove last comma
			sql.append(")");
			
			try (PreparedStatement stmt = con.prepareStatement(
					sql.toString())) {
				int i = 0;
				for (Long index : indices) {
					i++;
					stmt.setLong(i, index);
				}
				
				ResultSet rs = stmt.executeQuery();
				// rs auto-closes with stmt, not try-with-resources necessary
				
				while (rs.next()) {
					Person p = new Person(con);
					prepareDataFromResultSet(p, rs);
					resultList.add(p);
				}
			}
		}
		return resultList;
	}

	/**
	 * {@inheritDoc}
	 * Updates via SQL command INSERT OR REPLACE, i.e. if this person
	 * has been deleted from the table before calling updateDB() it will be
	 * reinserted.
	 * @throws SQLException 
	 */
	@Override
	public void updateDb() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + getTableName() + " SET ");
		sb.append(getCol(Cols.ADDRESS) + " = ?,");
		sb.append(getCol(Cols.TITLE) + " = ?,");
		sb.append(getCol(Cols.FIRST_NAMES) + " = ?,");
		sb.append(getCol(Cols.SURNAMES) + " = ?,");
		sb.append(getCol(Cols.BIRTHDAY) + " = ?,");
		sb.append(getCol(Cols.FOOD_MEMO) + " = ?");
		sb.append(" WHERE " + getCol(Cols.INDEX) + " = ?");
		
		try (PreparedStatement stmt = con.prepareStatement(sb.toString())) {
			if (getAddressId() == 0) {
				stmt.setNull(1, java.sql.Types.INTEGER);
			} else {
				stmt.setLong(1, getAddressId());
			}
			stmt.setString(2, getTitle());
			stmt.setString(3, getFirstNames());
			stmt.setString(4, getSurnames());
			stmt.setDate(5, getBirthday());
			stmt.setString(6, getFoodMemo());
			stmt.setLong(7, getId());
			stmt.executeUpdate();
		}
	}

	@Override
	public void insertIntoDb() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT OR REPLACE INTO " + getTableName() + " (");
		sb.append(getCol(Cols.INDEX) + ",");
		sb.append(getCol(Cols.ADDRESS) + ",");
		sb.append(getCol(Cols.TITLE) + ",");
		sb.append(getCol(Cols.FIRST_NAMES) + ",");
		sb.append(getCol(Cols.SURNAMES) + ",");
		sb.append(getCol(Cols.BIRTHDAY) + ",");
		sb.append(getCol(Cols.FOOD_MEMO));
		sb.append(") VALUES (?,?,?,?,?,?,?)");
		
		try (PreparedStatement stmt = con.prepareStatement(sb.toString(),
				Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, getId());
			if (getAddressId() == 0) {
				stmt.setNull(2, java.sql.Types.INTEGER);
			} else {
				stmt.setLong(2, getAddressId());
			}
			stmt.setString(3, getTitle());
			stmt.setString(4, getFirstNames());
			stmt.setString(5, getSurnames());
			stmt.setDate(6, getBirthday());
			stmt.setString(7, getFoodMemo());
			stmt.executeUpdate();
			this.setId(stmt.getGeneratedKeys().getLong(1)); // get newly assigned id
		}
		
	}

	@Override
	public void createTables() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS " + getTableName() + " (");
		sb.append(getCol(Cols.INDEX) + " INTEGER PRIMARY KEY AUTOINCREMENT, "); // (import how?) looked it up; just insert! http://www.sqlite.org/autoinc.html
		sb.append(getCol(Cols.ADDRESS) + " INTEGER, ");
		sb.append(getCol(Cols.TITLE) + " TEXT, ");
		sb.append(getCol(Cols.FIRST_NAMES) + " TEXT, ");
		sb.append(getCol(Cols.SURNAMES) + " TEXT NOT NULL, ");
		sb.append(getCol(Cols.BIRTHDAY) + " TEXT"); //in PoehlingPerson use INTEGER!
		sb.append(getCol(Cols.FOOD_MEMO) + " TEXT");
		sb.append(")");
		
		try(Statement stmt = con.createStatement()) {
			stmt.executeUpdate(sb.toString());
		}
	}

}
