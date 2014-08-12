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
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author lumpiluk
 *
 */
public class Person extends HotelData {

	private static final String SQL_TABLE_NAME = "people";

	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ SQL_TABLE_NAME + " (index INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "address INTEGER, "
			+ "title TEXT, "
			+ "first_names TEXT, "
			+ "surnames TEXT NOT NULL, "
			+ "birthday TEXT, "
			+ "food_memo TEXT)";
	
	private static final String SQL_INSERT = "INSERT INTO " + SQL_TABLE_NAME
			+  " (address, title, first_names, surnames, birthday, food_memo) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_UPDATE = "UPDATE " + SQL_TABLE_NAME
			+ " SET address = ?, title = ?, first_names = ?, surnames = ?, "
			+ "birthday = ?, food_memo = ? "
			+ "WHERE index = ?";
	
	private static final String SQL_DELETE = "DELETE FROM " + SQL_TABLE_NAME
			+ " WHERE index = ?";
	
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
		p.setId(rs.getLong("index")); // works although private :)
		p.setAddressId(rs.getLong("address")); // 0 if SQL NULL
		p.setBirthday(rs.getDate("birthday"));
		p.setFirstNames(rs.getString("first_names"));
		p.setFoodMemo(rs.getString("food_memo"));
		p.setSurnames(rs.getString("surnames"));
		p.setTitle(rs.getString("title"));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean fromDbAtIndex(final long d) throws NoSuchElementException,
			SQLException {
		boolean success = false;
		String query = "SELECT * FROM " + SQL_TABLE_NAME + " WHERE index = ?";
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
			sql.append("SELECT * FROM " + SQL_TABLE_NAME + " WHERE index IN (");
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
	@Override
	public void updateDb() throws SQLException {
		try (PreparedStatement stmt = con.prepareStatement(SQL_UPDATE)) {
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
		try (PreparedStatement stmt = con.prepareStatement(SQL_INSERT,
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
	public void deleteFromDb() throws SQLException {
		try(PreparedStatement stmt = con.prepareStatement(SQL_DELETE)) {
			stmt.setLong(1, getId());
		}
	}

	@Override
	public void createTables() throws SQLException {
		try(Statement stmt = con.createStatement()) {
			stmt.executeUpdate(SQL_CREATE);
		}
	}

}
