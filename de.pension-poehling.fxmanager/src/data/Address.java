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
import java.sql.Types;
import java.text.ParseException;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import util.DateComparator;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author lumpiluk
 *
 */
public class Address extends HotelData {

	public static final String SQL_TABLE_NAME = "addresses";
	
	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ SQL_TABLE_NAME + " (address_id INTEGER PRIMARY KEY, "
			+ "addressee INTEGER, "
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
			+ SQL_TABLE_NAME + "_fts (address_fts_id, fts_title, fts_first_names, fts_surnames, "
			+ "fts_street, fts_town, fts_zip, fts_phone, fts_email, fts_cellphone, fts_flags) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_UPDATE = "UPDATE " + SQL_TABLE_NAME
			+ " SET addressee = ?, addition = ?, street = ?, "
			+ "short_country = ?, zip = ?, town = ?, postbox = ?, "
			+ "postbox_zip = ?, postbox_town = ?, state = ?, phone = ?, "
			+ "cellphone = ?, fax = ?, email = ?, website = ?, memo = ?, "
			+ "flags = ?, created = ? WHERE address_id = ?";
	
	private static final String SQL_UPDATE_FTS = "UPDATE "
			+ SQL_TABLE_NAME + "_fts SET fts_title = ?, fts_first_names = ?, "
			+ "fts_surnames = ?, fts_street = ?, fts_town = ?, fts_zip = ?, fts_phone = ?, "
			+ "fts_email = ?, fts_cellphone = ?, fts_flags = ? WHERE address_fts_id = ?";
	
	private static final String SQL_DELETE = "DELETE FROM " + SQL_TABLE_NAME
			+ " WHERE address_id = ?"; // TODO: delete people, invoices, etc.?
	
	private static final String SQL_DELETE_FTS = "DELETE FROM " + SQL_TABLE_NAME
			+ "_fts WHERE address_fts_id = ?";
	
	private static final String SQL_SELECT_ALL = String.format(
			"SELECT * FROM %1$s LEFT OUTER JOIN %2$s ON %1$s.addressee = "
			+ "%2$s.person_id", SQL_TABLE_NAME, Person.SQL_TABLE_NAME);
	
	private static final String SQL_SELECT = String.format(
			"%1$s WHERE %2$s.address_id = ?", SQL_SELECT_ALL, SQL_TABLE_NAME
			);	
	
	private static final String SQL_SELECT_PEOPLE = "SELECT * FROM "
			+ Person.SQL_TABLE_NAME + " WHERE address = ?";
	
	private LongProperty id = new SimpleLongProperty();
	
	/** 
	 * The person's id this address belongs to.
	 * More people can be assigned to this address by their ADDRESS field
	 * and obtained by this object's getPeople method (TODO).
	 */
	private Person addressee;
	
	private String addition;
	
	//private String street;
	private StringProperty street = new SimpleStringProperty();
	
	//private String shortCountry;
	private StringProperty shortCountry = new SimpleStringProperty();
	
	//private String zipCode;
	private StringProperty zipCode = new SimpleStringProperty();
	
	//private String town;
	private StringProperty town = new SimpleStringProperty();
	
	//private String memo;
	private StringProperty memo = new SimpleStringProperty();
	
	//private String state;
	private StringProperty state = new SimpleStringProperty();
	
	//private String postbox, postboxZip, postboxTown;
	private StringProperty postbox = new SimpleStringProperty(),
			postboxZip = new SimpleStringProperty(),
			postboxTown = new SimpleStringProperty();
	
	//private String phone, fax, email, website, cellphone;
	private StringProperty phone = new SimpleStringProperty(),
			fax = new SimpleStringProperty(),
			email = new SimpleStringProperty(),
			website = new SimpleStringProperty(),
			mobile = new SimpleStringProperty();
	
	private Set<String> flags;
	
	private static boolean fullTextSearchTableCreated = false;
	
	//private int people, children; // TODO: replace local variables with DB queries?
	
	private Date created;

	/**
	 * @param con Database connection for this object
	 */
	public Address(final Connection con) {
		super(con);
		this.created = new java.sql.Date(0);
		this.flags = new HashSet<String>();
	}
	
	/** @return the id of this address, 0 if not set */
	public long getId() { return id.getValue() != null ? id.get() : 0; }

	/** @param id the id to set */
	public void setId(long id) { this.id.set(id); }
	
	public LongProperty idProperty() { return id; }

	/** @return the addressee Person object associated with this address */
	public Person getAddressee() { return addressee; }
	
	/** @return the toString() value of the addressee Person object. */
	public String getAddresseeString() {
		return addressee == null ? "" : addressee.toString();
	}

	/** @param addressee the addressee to set */
	public void setAddressee(Person addressee) { this.addressee = addressee; }

	/** @return the addition */ // TODO: what is this anyway?!
	public String getAddition() { return addition; }

	/** @param addition the addition to set */
	public void setAddition(String addition) { this.addition = addition; }

	/** @return the street */
	public String getStreet() { return street.get(); }

	/** @param street the street to set */
	public void setStreet(String street) { this.street.set(street); }
	
	public StringProperty streetProperty() { return street; }

	/** @return the shortCountry */
	public String getShortCountry() { return shortCountry.get(); }

	/** @param shortCountry the shortCountry to set */
	public void setShortCountry(String shortCountry) { this.shortCountry.set(shortCountry); }
	
	public StringProperty shortCountryProperty() { return shortCountry; }

	/** @return the zipCode */
	public String getZipCode() { return zipCode.get(); }

	/** @param zipCode the zipCode to set */
	public void setZipCode(String zipCode) { this.zipCode.set(zipCode); }
	
	public StringProperty zipCodeProperty() { return zipCode; }

	/** @return the town */
	public String getTown() { return town.get(); }

	/** @param town the town to set */
	public void setTown(String town) { this.town.set(town); }
	
	public StringProperty townProperty() { return town; }

	/** @return the state */
	public String getState() { return state.get(); }

	/** @param state the state to set */
	public void setState(String state) { this.state.set(state); }
	
	public StringProperty stateProperty() { return state; }
	
	/*public String getFullAddressString() {
		StringBuilder sb = new StringBuilder();
		if (getStreet() != null) {
			sb.append(getStreet());
			if (getZipCode() != null || getTown() != null || getState() != null || getShortCountry() != null)
				sb.append(", ");
		}
		if (getZipCode() != null || getTown() != null) {
			sb.append(getZipCode());
			sb.append(getTown());
			if (getState() != null || getShortCountry() != null)
				sb.append(", ");
		}
		if (getState() != null) {
			sb.append(getState());
			if (getShortCountry() != null)
				sb.append(", ");
		}
		if (getShortCountry() != null)
			sb.append(getShortCountry());		
		return sb.toString();
	}*/

	/** @return the postbox */
	public String getPostbox() { return postbox.get(); }

	/** @param postbox the postbox to set */
	public void setPostbox(String postbox) { this.postbox.set(postbox); }
	
	public StringProperty postboxProperty() { return postbox; }

	/** @return the postboxZip */
	public String getPostboxZip() { return postboxZip.get(); }

	/** @param postboxZip the postboxZip to set */
	public void setPostboxZip(String postboxZip) { this.postboxZip.set(postboxZip); }
	
	public StringProperty postboxZipProperty() { return postboxZip; }

	/** @return the postboxTown */
	public String getPostboxTown() { return postboxTown.get(); }

	/** @param postboxTown the postboxTown to set */
	public void setPostboxTown(String postboxTown) { this.postboxTown.set(postboxTown); }
	
	public StringProperty postboxTownProperty() { return postboxTown; }

	/** @return the phone */
	public String getPhone() { return phone.get(); }

	/** @param phone the phone to set */
	public void setPhone(String phone) { this.phone.set(phone);	}
	
	public StringProperty phoneProperty() { return phone; }

	/** @return the fax */
	public String getFax() { return fax.get(); }

	/** @param fax the fax to set */
	public void setFax(String fax) { this.fax.set(fax); }
	
	public StringProperty faxProperty() { return fax; }

	/** @return the email */
	public String getEmail() { return email.get(); }

	/** @param email the email to set */
	public void setEmail(String email) { this.email.set(email); }
	
	public StringProperty emailProperty() { return email; }

	/** @return the website */
	public String getWebsite() { return website.get(); }

	/** @param website the website to set */
	public void setWebsite(String website) { this.website.set(website); }
	
	public StringProperty websiteProperty() { return website; }

	/** @return the mobile number */
	public String getMobile() { return mobile.get(); }

	/** @param cellphone mobile number */
	public void setMobile(String mobile) { this.mobile.set(mobile); }
	
	public StringProperty mobileProperty() { return mobile; }

	/** @return the memo */
	public String getMemo() { return memo.get(); }

	/** @param memo the memo to set */
	public void setMemo(String memo) { this.memo.set(memo); }
	
	public StringProperty memoProperty() { return memo; }

	/** @return the set of flags set for this address. */
	public Set<String> getFlags() { return flags; }

	/** @return the creation date of this address */
	public Date getCreated() { return created; }

	/** @param created the created to set */
	public void setCreated(Date created) { this.created = created; }
	
	public void setCreated(String isoDateCreated) throws ParseException {
		this.created = new Date(DateComparator.getDateFormat().parse(isoDateCreated).getTime());
	}
	
	public String getCreatedString() {
		return DateComparator.getDateFormat().format(created);
	}

	public void prepareDataFromResultSet(final ResultSet rs) throws SQLException {
		this.setId(rs.getLong("address_id"));
		
		try {
			Person a = new Person(con);
			a.setAddress(this);
			a.setId(rs.getLong("person_id"));
			a.setFirstNames(rs.getString("first_names"));
			a.setSurnames(rs.getString("surnames"));
			a.setTitle(rs.getString("title"));
			try {
				a.setBirthday(rs.getString("birthday"));
			} catch (ParseException e) {
				Date tmp = null; a.setBirthday(tmp);
			}
			a.setFoodMemo(rs.getString("food_memo"));
			this.setAddressee(a);
		} catch (IllegalArgumentException e) {
			// may be thrown by Person#setId
			// just don't set an addressee then...
		}
		
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
		this.setMobile(rs.getString("cellphone"));
		this.setMemo(rs.getString("memo"));
		this.flagsFromDbString(rs.getString("flags"));
		try {
			this.setCreated(rs.getString("created")); // TODO: create strings only once statically!
		} catch (ParseException e) {
			this.setCreated(new Date((new GregorianCalendar()).getTimeInMillis()));
		}
	}
	
	/**
	 * Loads the people associated with this address from the database.
	 * @return an iterable object containing the people found in the database
	 */
	public Iterable<Person> getPeople() throws SQLException {
		List<Person> people = new LinkedList<Person>();
		try (PreparedStatement stmt = con.prepareStatement(SQL_SELECT_PEOPLE)) {
			stmt.setLong(1, this.getId());
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Person p = new Person(con);
					p.prepareDataFromResultSet(rs);					
					people.add(p);
				}
			}
		}
		return people;
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException
	 */
	@Override
	public boolean fromDbAtId(long id)
			throws NoSuchElementException, SQLException {
		boolean success = false;
		try (PreparedStatement stmt = con.prepareStatement(SQL_SELECT)) {
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
			//stmt.setDate(23, getCreated());
			stmt.setString(18, getCreatedString());
			stmt.setLong(19, getId());
			
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
				stmt.setString(10, flagsToDbString());
				stmt.setLong(11, this.getId());
				
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
			if (getAddressee() == null || getAddressee().getId() == 0) {
				stmt.setNull(1, java.sql.Types.INTEGER);
			} else {
				stmt.setLong(1, getAddressee().getId());
			}
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
			//stmt.setDate(18, getCreated());
			stmt.setString(18, getCreatedString());
			
			stmt.executeUpdate();
			this.setId(stmt.getGeneratedKeys().getLong(1));
			
			if (fullTextSearchTableCreated) {
				System.out.println("updating fts table");
				try (PreparedStatement ftsStmt = con.prepareStatement(SQL_INSERT_FTS_SINGLE)) {
					ftsStmt.setLong(1, this.getId());
					if (getAddressee() != null) {
						ftsStmt.setString(2, getAddressee().getTitle());
						ftsStmt.setString(3, getAddressee().getFirstNames());
						ftsStmt.setString(4, getAddressee().getSurnames());
					} else {
						ftsStmt.setNull(2, Types.NULL);
						ftsStmt.setNull(3, Types.NULL);
						ftsStmt.setNull(4, Types.NULL);
					}
					ftsStmt.setString(5, getStreet());
					ftsStmt.setString(6, getTown());
					ftsStmt.setString(7, getZipCode());
					ftsStmt.setString(8, getPhone());
					ftsStmt.setString(9, getEmail());
					ftsStmt.setString(10, getMobile());
					ftsStmt.setString(11, flagsToDbString());
					
					ftsStmt.executeUpdate();
				}
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
			stmt.executeUpdate();
		}
		if (fullTextSearchTableCreated) {
			try (PreparedStatement stmt = con.prepareStatement(SQL_DELETE_FTS)) {
				stmt.setLong(1, getId());
				stmt.executeUpdate();
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
					+ "_fts USING fts4 (address_fts_id, fts_title, fts_first_names, fts_surnames, "
					+ "fts_street, fts_town, fts_zip, fts_phone, fts_email, fts_cellphone, fts_flags)";
			String populateQuery = "INSERT INTO " + SQL_TABLE_NAME + "_fts "
					+ "SELECT address_id, people.title, people.first_names, "
					+ "people.surnames, addresses.street, addresses.town, "
					+ "addresses.zip, addresses.phone, addresses.email, "
					+ "addresses.cellphone, addresses.flags FROM addresses "
					+ "LEFT OUTER JOIN people ON addresses.addressee "
					+ "= people.person_id";
			stmt.executeUpdate(dropQuery);
			stmt.executeUpdate(createQuery);
			stmt.executeUpdate(populateQuery);
			fullTextSearchTableCreated = true;
		}
	}
	
}
