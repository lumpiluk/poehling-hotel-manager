/* 
 * Copyright 2014 Lukas Stratmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import util.DateComparator;

/**
 * @author lumpiluk
 *
 */
public class Person extends HotelData implements Cloneable {

	public static final String SQL_TABLE_NAME = "people";

	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ SQL_TABLE_NAME + " (person_id INTEGER PRIMARY KEY, "
			+ "address INTEGER, "
			+ "title TEXT, "
			+ "first_names TEXT, "
			+ "surnames TEXT NOT NULL, "
			+ "birthday TEXT, "
			+ "food_memo TEXT)";

	private static final String SQL_INSERT = "INSERT INTO " + SQL_TABLE_NAME
			+  " (address, title, first_names, surnames, birthday, food_memo) "
			+ "VALUES (?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_UPDATE = "UPDATE " + SQL_TABLE_NAME
			+ " SET address = ?, title = ?, first_names = ?, surnames = ?, "
			+ "birthday = ?, food_memo = ? "
			+ "WHERE person_id = ?";
	
	private static final String SQL_DELETE = "DELETE FROM " + SQL_TABLE_NAME
			+ " WHERE person_id = ?";
	
	private static final String SQL_SELECT = "SELECT * FROM " + SQL_TABLE_NAME
			+ " WHERE person_id = ?";
	
	private long id;
	
	private StringProperty title = new SimpleStringProperty();
	
	private StringProperty firstNames = new SimpleStringProperty();
	
	private StringProperty surnames = new SimpleStringProperty();
	
	private Date birthday;
	
	private StringProperty foodMemo = new SimpleStringProperty();

	private Address address;
	
	
	public Person(final Connection con) {
		super(con);
		this.setId(0);
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * @param id the new ID
	 * @throws IllegalArgumentException if id is less than or equal to 0
	 */
	public void setId(long id) throws IllegalArgumentException {
		if (id <= 0)
			throw new IllegalArgumentException();
		this.id = id;
	}
	
	/** 
	 * Check whether id has been initialized.<br />
	 * Used in updateInsert().
	 * @return false iff the id has not been set yet.
	 */
	public boolean isIdSet() { // TODO: do actually use this!
		return id == 0;
	}
	
	/**
	 * Loads the associated address from the database.
	 * @return the address associated with this Person. May be null.
	 * @throws SQLException
	 */
	public Address getAddress() throws SQLException {
		/*Address a = new Address(con);
		try {
			a.fromDbAtId(addressId);
		} catch (NoSuchElementException e) {
			return null;
		}*/
		return this.address;
	}
	
	/** @return the address id, 0 if not set */
	private long getAddressId() {
		if (this.address == null) {
			return 0;
		}
		return this.address.getId();
	}
	
	/** 
	 * Will create an empty address for this person object with only the id set. // TODO: right choice?
	 * @param addressId the address id to set
	 */
	private void setAddressId(long addressId) {
		//this.addressId = addressId;
		Address a = new Address(con);
		a.setId(addressId);
	}
	
	/** @param address the address to set (will only save the id) */
	public void setAddress(Address address) { 
		//setAddressId(address.getId());
		this.address = address;
	}

	/** @param id the id to set */
	private void setId(int id) { this.id = id; }

	/** @return the title */
	public String getTitle() { return title.get(); }

	/** @param title the title to set */
	public void setTitle(String title) { this.title.set(title); }
	
	public StringProperty titleProperty() { return title; }

	/** @return the firstNames */
	public String getFirstNames() { return firstNames.get(); }

	/** @param firstNames the firstNames to set */
	public void setFirstNames(String firstNames) { this.firstNames.set(firstNames); }
	
	public StringProperty firstNamesProperty() { return firstNames; }

	/** @return the surnames */
	public String getSurnames() { return surnames.get(); }

	/** @param surnames the surnames to set */
	public void setSurnames(String surnames) { this.surnames.set(surnames); }
	
	public StringProperty surnamesProperty() { return surnames; }

	/** @return the birthday */
	public Date getBirthday() { return birthday; }

	/** @param birthday the birthday to set */
	public void setBirthday(Date birthday) { this.birthday = birthday; }
	
	public void setBirthday(String isoDateCreated) throws ParseException {
		if (isoDateCreated != null && !isoDateCreated.trim().equals("")) {
			this.birthday = new Date(DateComparator.getDateFormat()
					.parse(isoDateCreated).getTime());
		} else {
			this.birthday = null;
		}
	}

	/** @return the foodMemo */
	public String getFoodMemo() { return foodMemo.get(); }

	/** @param foodMemo the foodMemo to set */
	public void setFoodMemo(String foodMemo) { this.foodMemo.set(foodMemo); }
	
	public StringProperty foodMemoProperty() { return foodMemo; }
	
	@Override
	public String toString() {
		return String.format("%1$s %2$s %3$s", getTitle(), getFirstNames(), getSurnames());
	}
	
	@Override protected Object clone() throws CloneNotSupportedException {
		Person p = new Person(con);
		p.setAddress(this.address);
		p.setBirthday((Date) this.birthday.clone());
		p.setFirstNames(this.getFirstNames());
		p.setSurnames(this.getSurnames());
		p.setFoodMemo(this.getFoodMemo());
		p.setId(this.id);
		p.setTitle(this.getTitle());
		return p;
	}
	
	/**
	 * Used in fromDbAtId and getBatch.
	 * @param p The Person the data of which will be prepared.
	 * @param rs The result set.
	 * @throws SQLException
	 */
	private static void prepareDataFromResultSet(final Person p, final ResultSet rs) 
			throws SQLException {
		p.setId(rs.getLong("person_id")); // works although private :)
		p.setAddressId(rs.getLong("address")); // 0 if SQL NULL
		try {
			p.setBirthday(rs.getString("birthday"));
		} catch (ParseException e) {
			Date tmp = null; p.setBirthday(tmp);
		}
		p.setFirstNames(rs.getString("first_names"));
		p.setFoodMemo(rs.getString("food_memo"));
		p.setSurnames(rs.getString("surnames"));
		p.setTitle(rs.getString("title"));
	}
	
	public void prepareDataFromResultSet(final ResultSet rs) throws SQLException {
		Person.prepareDataFromResultSet(this, rs);
	}
	
	/** {@inheritDoc} */
	@Override public boolean equals(Object obj) {
		// ignore properties other than id, might've already changed as user edited the person
		return obj != null && obj instanceof Person &&
				((Person) obj).getId() == this.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fromDbAtId(final long d) throws NoSuchElementException,
			SQLException {
		boolean success = false;
		try (PreparedStatement stmt = con.prepareStatement(SQL_SELECT)) {
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
			sql.append("SELECT * FROM " + SQL_TABLE_NAME + " WHERE person_id IN (");
			for (@SuppressWarnings("unused") Long id : indices) {
				sql.append("?,");
			}
			sql.deleteCharAt(sql.length() - 1); // remove last comma
			sql.append(")");
			
			try (PreparedStatement stmt = con.prepareStatement(
					sql.toString())) {
				int i = 0;
				for (Long id : indices) {
					i++;
					stmt.setLong(i, id);
				}
				
				ResultSet rs = stmt.executeQuery();
				// rs auto-closes with stmt, no try-with-resources necessary
				
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
	@Override public void updateDb() throws SQLException {
		try (PreparedStatement stmt = con.prepareStatement(SQL_UPDATE)) {
			if (getAddressId() == 0) {
				stmt.setNull(1, java.sql.Types.INTEGER);
			} else {
				stmt.setLong(1, getAddressId());
			}
			stmt.setString(2, getTitle());
			stmt.setString(3, getFirstNames());
			stmt.setString(4, getSurnames());
			if (getBirthday() != null) {
				stmt.setString(5, DateComparator.getDateFormat().format(getBirthday()));
			} else {
				stmt.setNull(5, java.sql.Types.NULL);
			}
			stmt.setString(6, getFoodMemo());
			stmt.setLong(7, getId());
			stmt.executeUpdate();
		}
	}

	@Override public void insertIntoDb() throws SQLException {
		try (PreparedStatement stmt = con.prepareStatement(SQL_INSERT,
				Statement.RETURN_GENERATED_KEYS)) {
			if (getAddressId() == 0) {
				stmt.setNull(1, java.sql.Types.INTEGER);
			} else {
				stmt.setLong(1, getAddressId());
			}
			stmt.setString(2, getTitle());
			stmt.setString(3, getFirstNames());
			stmt.setString(4, getSurnames());
			if (getBirthday() != null) {
				stmt.setString(5, DateComparator.getDateFormat().format(getBirthday()));
			} else {
				stmt.setNull(5, java.sql.Types.NULL);
			}
			stmt.setString(6, getFoodMemo());
			stmt.executeUpdate();
			this.setId(stmt.getGeneratedKeys().getLong(1)); // get newly assigned id
		}
		
	}
	
	/**
	 * If isIdSet() returns true, this person will be updated, otherwise it
	 * will be inserted as a new person into the database.
	 * @throws SQLException
	 */
	public void updateInsert() throws SQLException {
		if (isIdSet()) {
			updateDb();
		} else {
			insertIntoDb();
		}
	}
	
	@Override public void deleteFromDb() throws SQLException {
		try(PreparedStatement stmt = con.prepareStatement(SQL_DELETE)) {
			stmt.setLong(1, getId());
		}
	}

	@Override public void createTables() throws SQLException {
		try(Statement stmt = con.createStatement()) {
			stmt.executeUpdate(SQL_CREATE);
		}
	}

}
