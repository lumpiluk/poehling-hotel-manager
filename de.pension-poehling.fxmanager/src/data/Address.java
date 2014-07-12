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

	/**
	 * Columns in this object's table.
	 * Use getCol(Cols col) to get the respective column name.
	 * @author lumpiluk
	 *
	 */
	public static enum Cols {
		INDEX, 
		ADDRESSEE, // a Person index
		ADDITION, STREET, SHORT_COUNTRY, //TODO: addition here or in person?
		ZIP_CODE, TOWN, POSTBOX, POSTBOX_ZIP, POSTBOX_TOWN, STATE, PHONE,
		CELLPHONE, FAX, EMAIL, WEBSITE, MEMO, DEBITOR, CREDITOR,
		OTHER, DECEASED, CHILDREN, ADDED, CREATED, PRIVATE;
	}
	
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
	 * 
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
	
	/**
	 * @param id
	 * @param title
	 * @param firstNames
	 * @param surnames
	 * @param addition
	 * @param street
	 * @param shortCountry
	 * @param zipCode
	 * @param town
	 * @param state
	 * @param postbox
	 * @param postboxZip
	 * @param postboxTown
	 * @param phone
	 * @param fax
	 * @param email
	 * @param website
	 * @param cellphone
	 * @param debitor
	 * @param creditor
	 * @param other
	 * @param deceased
	 * @param privateAddress
	 * @param created
	 * @param added
	 */
	public Address(long id, Person addressee,
			String addition, String street, String shortCountry,
			String zipCode, String town, String state, String postbox,
			String postboxZip, String postboxTown, String phone, String fax,
			String email, String website, String cellphone, String memo,
			boolean debitor, boolean creditor, boolean other,
			boolean deceased, boolean privateAddress,
			int people, int children, Date created, Date added,
			final Connection con) {
		super(con);
		this.id = id;
		this.addresseeId = addressee.getId();
		this.addition = addition;
		this.street = street;
		this.shortCountry = shortCountry;
		this.zipCode = zipCode;
		this.town = town;
		this.state = state;
		this.postbox = postbox;
		this.postboxZip = postboxZip;
		this.postboxTown = postboxTown;
		this.phone = phone;
		this.fax = fax;
		this.email = email;
		this.website = website;
		this.memo = memo;
		this.cellphone = cellphone;
		this.debitor = debitor;
		this.creditor = creditor;
		this.other = other;
		this.deceased = deceased;
		this.privateAddress = privateAddress;
		this.created = created;
		this.added = added;
	}

	protected String getTableName() {
		return "addresses";
	}
	
	/**
	 * Used for dynamically binding database constants to each class so that
	 * subclasses implementing a database import can override this method.
	 * @param col
	 * @return
	 */
	protected String getCol(Cols col) {
		if (col == Cols.ADDED) { return "added"; }
		else if (col == Cols.ADDITION) { return "addition"; }
		else if (col == Cols.ADDRESSEE) { return "addressee"; }
		else if (col == Cols.CELLPHONE) { return "cellphone"; }
		else if (col == Cols.CHILDREN) { return "children"; }
		else if (col == Cols.CREATED) { return "created"; }
		else if (col == Cols.CREDITOR) { return "creditor"; }
		else if (col == Cols.DEBITOR) { return "debitor"; }
		else if (col == Cols.DECEASED) { return "deceased"; }
		else if (col == Cols.EMAIL) { return "email"; }
		else if (col == Cols.FAX) { return "fax"; }
		else if (col == Cols.INDEX) { return "id"; }
		else if (col == Cols.MEMO) { return "memo"; }
		else if (col == Cols.OTHER) { return "other"; }
		else if (col == Cols.PHONE) { return "phone"; }
		else if (col == Cols.POSTBOX) { return "postbox"; }
		else if (col == Cols.POSTBOX_TOWN) { return "postbox_town"; }
		else if (col == Cols.POSTBOX_ZIP) { return "postbox_zip"; }
		else if (col == Cols.PRIVATE) { return "private"; }
		else if (col == Cols.SHORT_COUNTRY) { return "short_country"; }
		else if (col == Cols.STATE) { return "state"; }
		else if (col == Cols.STREET) { return "street"; }
		else if (col == Cols.TOWN) { return "town"; }
		else if (col == Cols.WEBSITE) { return "website"; }
		else if (col == Cols.ZIP_CODE) { return "zip"; }
		else { throw new NoSuchElementException(); }
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
		String query = "SELECT * FROM " + getTableName() + " WHERE "
				+ getCol(Cols.INDEX) + " = ?";
		try (PreparedStatement stmt = con.prepareStatement(query)){
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.first()) {
					throw new NoSuchElementException();
				}
				this.id = rs.getLong(getCol(Cols.INDEX));
				this.setAddresseeId(rs.getLong(getCol(Cols.ADDRESSEE)));
				this.setAddition(rs.getString(getCol(Cols.ADDITION)));
				this.setStreet(rs.getString(getCol(Cols.STREET)));
				this.setShortCountry(rs.getString(getCol(Cols.SHORT_COUNTRY)));
				this.setZipCode(rs.getString(getCol(Cols.ZIP_CODE)));
				this.setTown(rs.getString(getCol(Cols.TOWN)));
				this.setState(rs.getString(getCol(Cols.STATE)));
				this.setPostbox(rs.getString(getCol(Cols.POSTBOX)));
				this.setPostboxZip(rs.getString(getCol(Cols.POSTBOX_ZIP)));
				this.setPostboxTown(rs.getString(getCol(Cols.POSTBOX_TOWN)));
				this.setPhone(rs.getString(getCol(Cols.PHONE)));
				this.setFax(rs.getString(getCol(Cols.FAX)));
				this.setEmail(rs.getString(getCol(Cols.EMAIL)));
				this.setWebsite(rs.getString(getCol(Cols.WEBSITE)));
				this.setCellphone(rs.getString(getCol(Cols.CELLPHONE)));
				this.setMemo(rs.getString(getCol(Cols.MEMO)));
				this.setDebitor(rs.getBoolean(getCol(Cols.DEBITOR)));
				this.setCreditor(rs.getBoolean(getCol(Cols.CREDITOR)));
				this.setOther(rs.getBoolean(getCol(Cols.OTHER)));
				this.setDeceased(rs.getBoolean(getCol(Cols.DECEASED)));
				this.setPrivateAddress(rs.getBoolean(getCol(Cols.PRIVATE)));
				this.setCreated(rs.getDate(getCol(Cols.CREATED)));
				this.setAdded(rs.getDate(getCol(Cols.ADDED)));
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
		
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + getTableName() + " SET ");
		sb.append(getCol(Cols.ADDRESSEE) + " = ?,");
		sb.append(getCol(Cols.ADDITION) + " = ?,"); // TODO: here or Person?
		sb.append(getCol(Cols.STREET) + " = ?,");
		sb.append(getCol(Cols.SHORT_COUNTRY) + " = ?,");
		sb.append(getCol(Cols.ZIP_CODE) + " = ?,");
		sb.append(getCol(Cols.TOWN) + " = ?,");
		sb.append(getCol(Cols.POSTBOX) + " = ?,");
		sb.append(getCol(Cols.POSTBOX_ZIP) + " = ?,");
		sb.append(getCol(Cols.POSTBOX_TOWN) + " = ?,");
		sb.append(getCol(Cols.STATE) + " = ?,");
		sb.append(getCol(Cols.PHONE) + " = ?,");
		sb.append(getCol(Cols.CELLPHONE) + " = ?,");
		sb.append(getCol(Cols.FAX) + " = ?,");
		sb.append(getCol(Cols.EMAIL) + " = ?,");
		sb.append(getCol(Cols.WEBSITE) + " = ?,");
		sb.append(getCol(Cols.MEMO) + " = ?,");
		sb.append(getCol(Cols.DEBITOR) + " = ?,"); // (boolean)
		sb.append(getCol(Cols.CREDITOR) + " = ?,");
		sb.append(getCol(Cols.OTHER) + " = ?,");
		sb.append(getCol(Cols.DECEASED) + " = ?,"); // TODO: here or Person?
		sb.append(getCol(Cols.PRIVATE) + " = ?,");
		sb.append(getCol(Cols.ADDED) + " = ?,"); // (date)
		sb.append(getCol(Cols.CREATED) + " = ?"); // TODO: difference?
		sb.append(" WHERE " + getCol(Cols.INDEX) + " = ?");
		
		try (PreparedStatement stmt = con.prepareStatement(sb.toString())) {
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
	
	@Override
	public void insertIntoDb() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + getTableName() + " (");
		sb.append(getCol(Cols.ADDRESSEE) + ",");
		sb.append(getCol(Cols.ADDITION) + ","); // TODO: here or Person?
		sb.append(getCol(Cols.STREET) + ",");
		sb.append(getCol(Cols.SHORT_COUNTRY) + ",");
		sb.append(getCol(Cols.ZIP_CODE) + ",");
		sb.append(getCol(Cols.TOWN) + ",");
		sb.append(getCol(Cols.POSTBOX) + ",");
		sb.append(getCol(Cols.POSTBOX_ZIP) + ",");
		sb.append(getCol(Cols.POSTBOX_TOWN) + ",");
		sb.append(getCol(Cols.STATE) + ",");
		sb.append(getCol(Cols.PHONE) + ",");
		sb.append(getCol(Cols.CELLPHONE) + ",");
		sb.append(getCol(Cols.FAX) + ",");
		sb.append(getCol(Cols.EMAIL) + ",");
		sb.append(getCol(Cols.WEBSITE) + ",");
		sb.append(getCol(Cols.MEMO) + ",");
		sb.append(getCol(Cols.DEBITOR) + ","); // (boolean)
		sb.append(getCol(Cols.CREDITOR) + ",");
		sb.append(getCol(Cols.OTHER) + ",");
		sb.append(getCol(Cols.DECEASED) + ","); // TODO: here or Person?
		sb.append(getCol(Cols.PRIVATE) + ",");
		sb.append(getCol(Cols.ADDED) + ","); // (date)
		sb.append(getCol(Cols.CREATED)); // TODO: difference?
		sb.append(") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"); // TODO: correct number of '?'?
		
		try (PreparedStatement stmt = con.prepareStatement(sb.toString(),
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
	 * Creates the necessary tables for Addresses using the given connection.
	 * Does not include Person tables.
	 */
	@Override
	public void createTables() throws SQLException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS " + getTableName() + " (");
		sb.append(getCol(Cols.INDEX) + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
		sb.append(getCol(Cols.ADDRESSEE) + " INTEGER NOT NULL, ");
		sb.append(getCol(Cols.ADDITION) + " TEXT, "); // TODO: here or Person?
		sb.append(getCol(Cols.STREET) + " TEXT, ");
		sb.append(getCol(Cols.SHORT_COUNTRY) + " TEXT, ");
		sb.append(getCol(Cols.ZIP_CODE) + " TEXT, ");
		sb.append(getCol(Cols.TOWN) + " TEXT, ");
		sb.append(getCol(Cols.POSTBOX) + " TEXT, ");
		sb.append(getCol(Cols.POSTBOX_ZIP) + " TEXT, ");
		sb.append(getCol(Cols.POSTBOX_TOWN) + " TEXT, ");
		sb.append(getCol(Cols.STATE) + " TEXT, ");
		sb.append(getCol(Cols.PHONE) + " TEXT, ");
		sb.append(getCol(Cols.CELLPHONE) + " TEXT, ");
		sb.append(getCol(Cols.FAX) + " TEXT, ");
		sb.append(getCol(Cols.EMAIL) + " TEXT, ");
		sb.append(getCol(Cols.WEBSITE) + " TEXT, ");
		sb.append(getCol(Cols.MEMO) + " TEXT, ");
		sb.append(getCol(Cols.DEBITOR) + " INTEGER, "); // (boolean)
		sb.append(getCol(Cols.CREDITOR) + " INTEGER, ");
		sb.append(getCol(Cols.OTHER) + " INTEGER, ");
		sb.append(getCol(Cols.DECEASED) + " INTEGER, "); // TODO: here or Person?
		sb.append(getCol(Cols.PRIVATE) + " INTEGER, ");
		sb.append(getCol(Cols.ADDED) + " TEXT, "); // (date)
		sb.append(getCol(Cols.CREATED) + " TEXT"); // TODO: difference?
		sb.append(")");
		
		try(Statement stmt = con.createStatement()) {
			stmt.executeUpdate(sb.toString());
		}
	}
	
}
