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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Statement;
import java.util.NoSuchElementException;

/**
 * @author lumpiluk
 *
 */
public class Address extends HotelData {

	private static final String SQL_TABLE_NAME = "addresses";
	
	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ SQL_TABLE_NAME + " (index INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "addressee INTEGER NOT NULL, "
			+ "addition TEXT, " // TODO: here or Person?
			+ "street TEXT, "
			+ "short_country TEXT, "
			+ "zip TEXT, "
			+ "town TEXT, "
			+ "postbox TEXT, "
			+ "postbox_zip TEXT, "
			+ "postbox_town TEXT, "
			+ "state TEXT, "
			+ "phone TEXT, "
			+ "cellphone TEXT, "
			+ "fax TEXT, "
			+ "email TEXT, "
			+ "website TEXT, "
			+ "memo TEXT, "
			+ "debitor INTEGER, " // (boolean?)
			+ "creditor INTEGER, "
			+ "other INTEGER, "
			+ "deceased INTEGER, " // TODO: here or Person?
			+ "private INTEGER, "
			+ "created TEXT)"; // (date)
	
	private static final String SQL_INSERT = "INSERT INTO " + SQL_TABLE_NAME
			+ " (addressee, addition, street, short_country, zip, town, " // TODO: when importing: with index-col!!!
			+ "postbox, postbox_zip, postbox_town, state, phone, cellphone, "
			+ "fax, email, website, memo, debitor, creditor, other, deceased, "
			+ "children, added, created, private) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String SQL_UPDATE = "UPDATE " + SQL_TABLE_NAME
			+ " SET addressee = ?, addition = ?, street = ?, "
			+ "short_country = ?, zip = ?, town = ?, postbox = ?, "
			+ "postbox_zip = ?, postbox_town = ?, state = ?, phone = ?, "
			+ "cellphone = ?, fax = ?, email = ?, website = ?, memo = ?, "
			+ "debitor = ?, creditor = ?, other = ?, deceased = ?, "
			+ "private = ?, added = ?, created = ? WHERE index = ?";
	
	private static final String SQL_DELETE = "DELETE FROM " + SQL_TABLE_NAME
			+ " WHERE index = ?";
	
	private long id;
	
	/** 
	 * The person's index this address belongs to.
	 * More people can be assigned to this address by their ADDRESS field
	 * and obtained by this object's getPeople method (TODO).
	 */
	private long addresseeId;
	
	private String addition;
	
	private String street;
	
	private String shortCountry;
	
	private String zipCode;
	
	private String town;
	
	private String memo;
	
	private String state;
	
	private String postbox, postboxZip, postboxTown;
	
	private String phone, fax, email, website, cellphone;
	
	private boolean debitor, creditor, other, deceased, privateAddress;
	
	//private int people, children; // TODO: replace local variables with DB queries?
	
	private Date created, added;

	/**
	 * @param con Database connection for this object
	 */
	public Address(final Connection con) {
		super(con);
		this.id = 0;
		this.addresseeId = 0;
		this.debitor = false;
		this.creditor = false;
		this.other = false;
		this.deceased = false;
		this.privateAddress = false;
		this.created = new java.sql.Date(0);
		this.added = new java.sql.Date(0);
	}

	protected String getTableName() {
		return "addresses";
	}
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	private long getAddresseeId() {
		return addresseeId;
	}
	
	/**
	 * @return the addressee
	 */
	public Person getAddressee() throws SQLException {
		Person addressee = new Person(con);
		addressee.fromDbAtIndex(getAddresseeId());
		return addressee;
	}

	/**
	 * @param addressee the addressee to set
	 */
	public void setAddressee(Person addressee) {
		this.addresseeId = addressee.getId();
	}
	
	private void setAddresseeId(long id) {
		this.addresseeId = id;
	}

	/**
	 * @return the addition
	 */
	public String getAddition() {
		return addition;
	}

	/**
	 * @param addition the addition to set
	 */
	public void setAddition(String addition) {
		this.addition = addition;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @return the shortCountry
	 */
	public String getShortCountry() {
		return shortCountry;
	}

	/**
	 * @param shortCountry the shortCountry to set
	 */
	public void setShortCountry(String shortCountry) {
		this.shortCountry = shortCountry;
	}

	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	/**
	 * @return the town
	 */
	public String getTown() {
		return town;
	}

	/**
	 * @param town the town to set
	 */
	public void setTown(String town) {
		this.town = town;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the postbox
	 */
	public String getPostbox() {
		return postbox;
	}

	/**
	 * @param postbox the postbox to set
	 */
	public void setPostbox(String postbox) {
		this.postbox = postbox;
	}

	/**
	 * @return the postboxZip
	 */
	public String getPostboxZip() {
		return postboxZip;
	}

	/**
	 * @param postboxZip the postboxZip to set
	 */
	public void setPostboxZip(String postboxZip) {
		this.postboxZip = postboxZip;
	}

	/**
	 * @return the postboxTown
	 */
	public String getPostboxTown() {
		return postboxTown;
	}

	/**
	 * @param postboxTown the postboxTown to set
	 */
	public void setPostboxTown(String postboxTown) {
		this.postboxTown = postboxTown;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the website
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * @param website the website to set
	 */
	public void setWebsite(String website) {
		this.website = website;
	}

	/**
	 * @return the cellphone number
	 */
	public String getCellphone() {
		return cellphone;
	}

	/**
	 * @param cellphone cellphone number
	 */
	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return the debitor
	 */
	public boolean isDebitor() {
		return debitor;
	}

	/**
	 * @param debitor the debitor to set
	 */
	public void setDebitor(boolean debitor) {
		this.debitor = debitor;
	}

	/**
	 * @return the creditor
	 */
	public boolean isCreditor() {
		return creditor;
	}

	/**
	 * @param creditor the creditor to set
	 */
	public void setCreditor(boolean creditor) {
		this.creditor = creditor;
	}

	/**
	 * @return the other
	 */
	public boolean isOther() {
		return other;
	}

	/**
	 * @param other the other to set
	 */
	public void setOther(boolean other) {
		this.other = other;
	}

	/**
	 * @return whether or not person is deceased
	 */
	public boolean isDeceased() {
		return deceased;
	}

	/**
	 * @param deceased the deceased to set
	 */
	public void setDeceased(boolean deceased) {
		this.deceased = deceased;
	}

	/**
	 * @return the privateAddress
	 */
	public boolean isPrivateAddress() {
		return privateAddress;
	}

	/**
	 * @param privateAddress the privateAddress to set
	 */
	public void setPrivateAddress(boolean privateAddress) {
		this.privateAddress = privateAddress;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the added
	 */
	public Date getAdded() {
		return added;
	}

	/**
	 * @param added the added to set
	 */
	public void setAdded(Date added) {
		this.added = added;
	}

	/**
	 * {@inheritDoc}
	 * @throws SQLException
	 */
	@Override
	public boolean fromDbAtIndex(long id)
			throws NoSuchElementException, SQLException {
		boolean success = false;
		String query = "SELECT * FROM " + getTableName() + " WHERE index = ?";
		try (PreparedStatement stmt = con.prepareStatement(query)){
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.first()) {
					throw new NoSuchElementException();
				}
				this.id = rs.getLong("index");
				this.setAddresseeId(rs.getLong("addressee"));
				this.setAddition(rs.getString("addition"));
				this.setStreet(rs.getString("street"));
				this.setShortCountry(rs.getString("short_country"));
				this.setZipCode(rs.getString("zip"));
				this.setTown(rs.getString("town"));
				this.setState(rs.getString("state"));
				this.setPostbox(rs.getString("postbox"));
				this.setPostboxZip(rs.getString("postbox_zip"));
				this.setPostboxTown(rs.getString("postbox_town"));
				this.setPhone(rs.getString("phone"));
				this.setFax(rs.getString("fax"));
				this.setEmail(rs.getString("email"));
				this.setWebsite(rs.getString("website"));
				this.setCellphone(rs.getString("cellphone"));
				this.setMemo(rs.getString("memo"));
				this.setDebitor(rs.getBoolean("debitor"));
				this.setCreditor(rs.getBoolean("creditor"));
				this.setOther(rs.getBoolean("other"));
				this.setDeceased(rs.getBoolean("deceased"));
				this.setPrivateAddress(rs.getBoolean("private"));
				this.setCreated(rs.getDate("created"));
				this.setAdded(rs.getDate("added"));
				//TODO: difference between created and added?
				
				success = true;
			}
		}
		return success;
	}

	/**
	 * {@inheritDoc}
	 * @throws NullPointerException No addressee is assigned to the address
	 */
	@Override
	public void updateDb() throws SQLException, NullPointerException {
		if (getAddresseeId() == 0)
			throw new NullPointerException("Address needs an addressee!");
		
		try (PreparedStatement stmt = con.prepareStatement(SQL_UPDATE)) {
			stmt.setLong(1, getAddresseeId());
			stmt.setString(2, getAddition());
			stmt.setString(3, getStreet());
			stmt.setString(4, getShortCountry());
			stmt.setString(5, getZipCode());
			stmt.setString(6, getTown());
			stmt.setString(7, getPostbox());
			stmt.setString(8, getPostboxZip());
			stmt.setString(9, getPostboxTown());
			stmt.setString(10, getState());
			stmt.setString(11, getPhone());
			stmt.setString(12, getCellphone());
			stmt.setString(13, getFax());
			stmt.setString(14, getEmail());
			stmt.setString(15, getWebsite());
			stmt.setString(16, getMemo());
			stmt.setBoolean(17, isDebitor());
			stmt.setBoolean(18, isCreditor());
			stmt.setBoolean(19, isOther());
			stmt.setBoolean(20, isDeceased());
			stmt.setBoolean(21, isPrivateAddress());
			stmt.setDate(22, getAdded());
			stmt.setDate(23, getCreated());
			stmt.setLong(24, getId());
			
			stmt.executeUpdate();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertIntoDb() throws SQLException {
		try (PreparedStatement stmt = con.prepareStatement(SQL_INSERT,
				Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, getAddresseeId());
			stmt.setString(2, getAddition());
			stmt.setString(3, getStreet());
			stmt.setString(4, getShortCountry());
			stmt.setString(5, getZipCode());
			stmt.setString(6, getTown());
			stmt.setString(7, getPostbox());
			stmt.setString(8, getPostboxZip());
			stmt.setString(9, getPostboxTown());
			stmt.setString(10, getState());
			stmt.setString(11, getPhone());
			stmt.setString(12, getCellphone());
			stmt.setString(13, getFax());
			stmt.setString(14, getEmail());
			stmt.setString(15, getWebsite());
			stmt.setString(16, getMemo());
			stmt.setBoolean(17, isDebitor());
			stmt.setBoolean(18, isCreditor());
			stmt.setBoolean(19, isOther());
			stmt.setBoolean(20, isDeceased());
			stmt.setBoolean(21, isPrivateAddress());
			stmt.setDate(22, getAdded());
			stmt.setDate(23, getCreated());
			
			stmt.executeUpdate();
			this.setId(stmt.getGeneratedKeys().getLong(1));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteFromDb() throws SQLException {
		try(PreparedStatement stmt = con.prepareStatement(SQL_DELETE)) {
			stmt.setLong(1, getId());
		}
	}

	/** 
	 * Creates the necessary tables for Addresses using the given connection.
	 * Does not include Person tables.
	 */
	@Override
	public void createTables() throws SQLException {
		try(Statement stmt = con.createStatement()) {
			stmt.executeUpdate(SQL_CREATE);
		}
	}
	
}
