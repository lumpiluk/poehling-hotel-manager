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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author lumpiluk
 *
 */
public class Address extends HotelData {

	public static final String SQL_TABLE_NAME = "addresses";
	
	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ SQL_TABLE_NAME + " (id INTEGER PRIMARY KEY, "
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
			+ "flags TEXT, "
			+ "created TEXT)"; // (date)
	
	private static final String SQL_INSERT = "INSERT INTO " + SQL_TABLE_NAME
			+ " (addressee, addition, street, short_country, zip, town, " // TODO: when importing: with id-col!!!
			+ "postbox, postbox_zip, postbox_town, state, phone, cellphone, "
			+ "fax, email, website, memo, flags, created) "
			+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String SQL_INSERT_FTS_SINGLE = "INSERT INTO "
			+ SQL_TABLE_NAME + "_fts (id, title, first_names, surnames, "
			+ "street, town, zip, phone, email, cellphone) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_UPDATE = "UPDATE " + SQL_TABLE_NAME
			+ " SET addressee = ?, addition = ?, street = ?, "
			+ "short_country = ?, zip = ?, town = ?, postbox = ?, "
			+ "postbox_zip = ?, postbox_town = ?, state = ?, phone = ?, "
			+ "cellphone = ?, fax = ?, email = ?, website = ?, memo = ?, "
			+ "flags = ?, created = ? WHERE id = ?";
	
	private static final String SQL_UPDATE_FTS = "UPDATE "
			+ SQL_TABLE_NAME + "_fts SET title = ?, first_names = ?, "
			+ "surnames = ?, street = ?, town = ?, zip = ?, phone = ?, "
			+ "email = ?, cellphone = ? WHERE id = ?";
	
	private static final String SQL_DELETE = "DELETE FROM " + SQL_TABLE_NAME
			+ " WHERE id = ?";
	
	private static final String SQL_DELETE_FTS = "DELETE FROM " + SQL_TABLE_NAME
			+ "_fts WHERE id = ?";
	
	private static final String SQL_SELECT_ALL = String.format(
			"SELECT * FROM %1$s LEFT OUTER JOIN %2$s ON %1$s.addressee = "
			+ "%2$s.address", SQL_TABLE_NAME, Person.SQL_TABLE_NAME);
	
	private static final String SQL_SELECT = String.format(
			"%1$s WHERE %2$s.id = ?", SQL_SELECT_ALL, SQL_TABLE_NAME
			);
	
	
	private long id;
	
	/** 
	 * The person's id this address belongs to.
	 * More people can be assigned to this address by their ADDRESS field
	 * and obtained by this object's getPeople method (TODO).
	 */
	private Person addressee;
	
	private String addition;
	
	private String street;
	
	private String shortCountry;
	
	private String zipCode;
	
	private String town;
	
	private String memo;
	
	private String state;
	
	private String postbox, postboxZip, postboxTown;
	
	private String phone, fax, email, website, cellphone;
	
	private Set<String> flags;
	
	private boolean fullTextSearchTableCreated = false;
	
	//private int people, children; // TODO: replace local variables with DB queries?
	
	private Date created, added;

	/**
	 * @param con Database connection for this object
	 */
	public Address(final Connection con) {
		super(con);
		this.id = 0;
		this.created = new java.sql.Date(0);
		this.added = new java.sql.Date(0);
		this.flags = new HashSet<String>();
	}
	
	/** @return the id */
	public long getId() { return id; }

	/** @param id the id to set */
	public void setId(long id) { this.id = id; }

	/** @return the addressee Person object associated with this address */
	public Person getAddressee() { return addressee; }
	
	/** @return the toString() value of the addressee Person object. */
	public String getAddresseeString() { return addressee.toString(); }

	/** @param addressee the addressee to set */
	public void setAddressee(Person addressee) { this.addressee = addressee; }

	/** @return the addition */ // TODO: what is this anyway?!
	public String getAddition() { return addition; }

	/** @param addition the addition to set */
	public void setAddition(String addition) { this.addition = addition; }

	/** @return the street */
	public String getStreet() { return street; }

	/** @param street the street to set */
	public void setStreet(String street) { this.street = street; }

	/** @return the shortCountry */
	public String getShortCountry() { return shortCountry; }

	/** @param shortCountry the shortCountry to set */
	public void setShortCountry(String shortCountry) { this.shortCountry = shortCountry; }

	/** @return the zipCode */
	public String getZipCode() { return zipCode; }

	/** @param zipCode the zipCode to set */
	public void setZipCode(String zipCode) { this.zipCode = zipCode; }

	/** @return the town */
	public String getTown() { return town; }

	/** @param town the town to set */
	public void setTown(String town) { this.town = town; }

	/** @return the state */
	public String getState() { return state; }

	/** @param state the state to set */
	public void setState(String state) { this.state = state; }

	/** @return the postbox */
	public String getPostbox() { return postbox; }

	/** @param postbox the postbox to set */
	public void setPostbox(String postbox) { this.postbox = postbox; }

	/** @return the postboxZip */
	public String getPostboxZip() { return postboxZip; }

	/** @param postboxZip the postboxZip to set */
	public void setPostboxZip(String postboxZip) { this.postboxZip = postboxZip; }

	/** @return the postboxTown */
	public String getPostboxTown() { return postboxTown; }

	/** @param postboxTown the postboxTown to set */
	public void setPostboxTown(String postboxTown) { this.postboxTown = postboxTown; }

	/** @return the phone */
	public String getPhone() { return phone; }

	/** @param phone the phone to set */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/** @return the fax */
	public String getFax() { return fax; }

	/** @param fax the fax to set */
	public void setFax(String fax) { this.fax = fax; }

	/** @return the email */
	public String getEmail() { return email; }

	/** @param email the email to set */
	public void setEmail(String email) { this.email = email; }

	/** @return the website */
	public String getWebsite() { return website; }

	/** @param website the website to set */
	public void setWebsite(String website) { this.website = website; }

	/** @return the mobile number */
	public String getMobile() { return cellphone; }

	/** @param cellphone mobile number */
	public void setMobile(String mobile) { this.cellphone = mobile; }

	/** @return the memo */
	public String getMemo() { return memo; }

	/** @param memo the memo to set */
	public void setMemo(String memo) { this.memo = memo; }

	/** @return the set of flags set for this address. */
	public Set<String> getFlags() { return flags; }

	/** @return the creation date of this address */
	public Date getCreated() { return created; }

	/** @param created the created to set */
	public void setCreated(Date created) { this.created = created; }

	/** @return the date this address was added */
	public Date getAdded() { return added; }
	
	public String getAddedString() {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd"); // TODO: settings specific?
		return format.format(added);
	}

	/**
	 * @param added the added to set
	 */
	public void setAdded(Date added) {
		this.added = added;
	}

	public void prepareDataFromResultSet(final ResultSet rs) throws SQLException {
		this.id = rs.getLong(SQL_TABLE_NAME + ".id");
		
		Person a = new Person(con);
		a.setAddress(this);
		a.setId(rs.getLong(Person.SQL_TABLE_NAME + ".id"));
		a.setFirstNames(rs.getString(Person.SQL_TABLE_NAME + ".first_names"));
		a.setSurnames(rs.getString(Person.SQL_TABLE_NAME + ".surnames"));
		a.setTitle(rs.getString(Person.SQL_TABLE_NAME + ".title"));
		a.setBirthday(rs.getDate(Person.SQL_TABLE_NAME + ".birthday"));
		a.setFoodMemo(rs.getString(Person.SQL_TABLE_NAME + ".food_memo"));
		this.setAddressee(a);
		
		this.setAddition(rs.getString(SQL_TABLE_NAME + ".addition"));
		this.setStreet(rs.getString(SQL_TABLE_NAME + ".street"));
		this.setShortCountry(rs.getString(SQL_TABLE_NAME + ".short_country"));
		this.setZipCode(rs.getString(SQL_TABLE_NAME + ".zip"));
		this.setTown(rs.getString(SQL_TABLE_NAME + ".town"));
		this.setState(rs.getString(SQL_TABLE_NAME + ".state"));
		this.setPostbox(rs.getString(SQL_TABLE_NAME + ".postbox"));
		this.setPostboxZip(rs.getString(SQL_TABLE_NAME + ".postbox_zip"));
		this.setPostboxTown(rs.getString(SQL_TABLE_NAME + ".postbox_town"));
		this.setPhone(rs.getString(SQL_TABLE_NAME + ".phone"));
		this.setFax(rs.getString(SQL_TABLE_NAME + ".fax"));
		this.setEmail(rs.getString(SQL_TABLE_NAME + ".email"));
		this.setWebsite(rs.getString(SQL_TABLE_NAME + ".website"));
		this.setMobile(rs.getString(SQL_TABLE_NAME + ".cellphone"));
		this.setMemo(rs.getString(SQL_TABLE_NAME + ".memo"));
		this.flagsFromDbString(rs.getString(SQL_TABLE_NAME + ".flags"));
		this.setCreated(rs.getDate(SQL_TABLE_NAME + ".created"));
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException
	 */
	@Override
	public boolean fromDbAtId(long id)
			throws NoSuchElementException, SQLException {
		boolean success = false;
		try (PreparedStatement stmt = con.prepareStatement(SQL_SELECT)){
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.first()) {
					throw new NoSuchElementException();
				}
				prepareDataFromResultSet(rs);
				
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
		if (getAddressee() == null)
			throw new NullPointerException("Address needs an addressee!");
		
		try (PreparedStatement stmt = con.prepareStatement(SQL_UPDATE)) {
			stmt.setLong(1, getAddressee().getId());
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
			stmt.setString(12, getMobile());
			stmt.setString(13, getFax());
			stmt.setString(14, getEmail());
			stmt.setString(15, getWebsite());
			stmt.setString(16, getMemo());
			stmt.setString(17, flagsToDbString());
			stmt.setDate(23, getCreated());
			stmt.setLong(24, getId());
			
			stmt.executeUpdate();
		}
		
		if (fullTextSearchTableCreated) {
			try (PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_FTS)) {
				stmt.setString(1, getAddressee().getTitle());
				stmt.setString(2, getAddressee().getFirstNames());
				stmt.setString(3, getAddressee().getSurnames());
				stmt.setString(4, getStreet());
				stmt.setString(5, getTown());
				stmt.setString(6, getZipCode());
				stmt.setString(7, getPhone());
				stmt.setString(8, getEmail());
				stmt.setString(9, getMobile());
				stmt.setLong(10, this.getId());
				
				stmt.executeUpdate();
			}
		}
	}
	
	private String flagsToDbString() {
		StringBuilder result = new StringBuilder("|"); // insert | at start so that the following will work in sqlite: ...MATCHES '|flagName|'...
		for (String flag : flags) {
			result.append(flag);
			result.append("|");
		}
		return result.toString();
	}
	
	private void flagsFromDbString(String dbString) {
		flags.clear();
		String[] flagElems = dbString.split("|");
		for (String flagElem : flagElems) {
			flags.add(flagElem);
		}
	}
	
	/**
	 * Also updates full text search virtual table.<br />
	 * {@inheritDoc}
	 */
	@Override public void insertIntoDb() throws SQLException {
		try (PreparedStatement stmt = con.prepareStatement(SQL_INSERT,
				Statement.RETURN_GENERATED_KEYS)) {
			stmt.setLong(1, getAddressee().getId());
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
			stmt.setString(12, getMobile());
			stmt.setString(13, getFax());
			stmt.setString(14, getEmail());
			stmt.setString(15, getWebsite());
			stmt.setString(16, getMemo());
			stmt.setString(17, flagsToDbString());
			stmt.setDate(18, getCreated());
			
			stmt.executeUpdate();
			this.setId(stmt.getGeneratedKeys().getLong(1));
		}
		if (fullTextSearchTableCreated) {
			try (PreparedStatement stmt = con.prepareStatement(SQL_INSERT_FTS_SINGLE)) {
				stmt.setLong(1, this.getId());
				stmt.setString(2, getAddressee().getTitle());
				stmt.setString(3, getAddressee().getFirstNames());
				stmt.setString(4, getAddressee().getSurnames());
				stmt.setString(5, getStreet());
				stmt.setString(6, getTown());
				stmt.setString(7, getZipCode());
				stmt.setString(8, getPhone());
				stmt.setString(9, getEmail());
				stmt.setString(10, getMobile());
				
				stmt.executeUpdate();
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteFromDb() throws SQLException {
		try (PreparedStatement stmt = con.prepareStatement(SQL_DELETE)) {
			stmt.setLong(1, getId());
		}
		if (fullTextSearchTableCreated) {
			try (PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FTS)) {
				stmt.setLong(1, getId());
			}
		}
	}

	/** 
	 * Creates the necessary tables for Addresses using the given connection.
	 * Does not include Person tables and full text search virtual table.
	 */
	@Override
	public void createTables() throws SQLException {
		try (Statement stmt = con.createStatement()) {
			stmt.executeUpdate(SQL_CREATE);
		}
	}
	
	/** 
	 * Creates a virtual SQLite table for full text search using fts4. 
	 * Has to be called every time a new connection has been opened.
	 * @throws SQLException
	 */
	public void createVirtualFTSTable() throws SQLException { // TODO: update on addresses update!!!
		try (Statement stmt = con.createStatement()) {
			String dropQuery = "DROP TABLE IF EXISTS " + SQL_TABLE_NAME + "_fts";
			String createQuery = "CREATE VIRTUAL TABLE " + SQL_TABLE_NAME
					+ "_fts USING fts4 (id, title, first_names, surnames, "
					+ "street, town, zip, phone, email, cellphone)";
			String populateQuery = "INSERT INTO " + SQL_TABLE_NAME + "_fts "
					+ "SELECT addresses.id, people.title, people.first_names, "
					+ "people.surnames, addresses.street, addresses.town, "
					+ "addresses.zip, addresses.phone, addresses.email, "
					+ "addresses.cellphone FROM addresses LEFT OUTER JOIN "
					+ "people ON addresses.addressee "
					+ "= people.address";
			stmt.executeUpdate(dropQuery);
			stmt.executeUpdate(createQuery);
			stmt.executeUpdate(populateQuery);
		}
	}
	
}
