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
import java.sql.Statement;
import java.util.NoSuchElementException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Data object for a hotel room.
 * @author lumpiluk
 *
 */
public class Room extends HotelData {
	
	// TODO: number of beds?

	private static final String SQL_TABLE_NAME = "rooms";

	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ SQL_TABLE_NAME + " (room_id INTEGER PRIMARY KEY, "
			+ "name TEXT NOT NULL, "
			+ "type TEXT, "
			+ "beds INTEGER, "
			+ "floor INTEGER, "
			+ "lift INTEGER NOT NULL, " // boolean
			+ "balcony INTEGER NOT NULL, " // boolean
			+ "area REAL, "
			+ "phone TEXT, "
			+ "view TEXT)";
	
	private static final String SQL_INSERT = "INSERT INTO " + SQL_TABLE_NAME
			+ " (name, type, beds, floor, lift, balcony, area, phone, view) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_UPDATE = "UPDATE " + SQL_TABLE_NAME
			+ " SET name = ?, type = ?, beds = ?, floor = ?, lift = ?, "
			+ "balcony = ?, area = ?, phone = ?, view = ?"
			+ "WHERE room_id = ?";
	
	private static final String SQL_DELETE = "DELETE FROM " + SQL_TABLE_NAME
			+ " WHERE room_id = ?";
	
	public static final String SQL_SELECT_ALL = "SELECT * FROM "
			+ SQL_TABLE_NAME;
	
	private long id;
	
	/** Usually the same as id. */
	private StringProperty name = new SimpleStringProperty();
	
	/** Type of room: single, double or apartment. */
	private StringProperty type = new SimpleStringProperty();
	
	/** Number of beds in this room. */
	private IntegerProperty beds = new SimpleIntegerProperty();
	
	/** Floor on which room is located with 0 being the ground floor. */
	private IntegerProperty floor = new SimpleIntegerProperty();
	
	/** 
	 * Whether or not room is accessible by lift.
	 * For rooms accessible without the use of stairs this value is true.
	 */
	private BooleanProperty accessibleByLift = new SimpleBooleanProperty();
	
	/** Wheter or not room has a balcony. */
	private BooleanProperty balcony = new SimpleBooleanProperty();
	
	/** Area available in the room. */
	private DoubleProperty area = new SimpleDoubleProperty();
	
	/** 
	 * Digits usually added to the house's phone number to reach this room.
	 *  Can be null.
	 */
	private StringProperty phone = new SimpleStringProperty();
	
	/**
	 * View from this room.
	 * E.g. "park" or "beach"
	 */
	private StringProperty view = new SimpleStringProperty();
	
	/**
	 * 
	 */
	public Room(final Connection con) {
		super(con);
		this.setName("");
		this.setType("");
		this.setBeds(0);
		this.setFloor(0);
		this.setAccessibleByLift(true);
		this.setBalcony(false);
		this.setArea(0.0);
		this.setPhone("");
		this.setView("");
	}
	
	/**
	 * @return the room's ID in the DB
	 */
	public long getId() {
		return id;
	}
	
	private void setId(long id) {
		this.id = id;
	}
	
	/**
	 * @return the room's number
	 */
	public String getName() {
		return name.get();
	}

	/**
	 * @param number the room's number
	 */
	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() { return name; }

	/**
	 * @return the type of the room (e.g. single or double)
	 */
	public String getType() {
		return type.get();
	}

	/**
	 * @param type the type of the room (e.g. single or double)
	 */
	public void setType(String type) {
		this.type.set(type);
	}
	
	public StringProperty typeProperty() { return type; }

	// TODO javadoc
	public int getBeds() { return beds.get(); }
	
	public void setBeds(int count) { beds.set(count); }
	
	public IntegerProperty bedsProperty() { return beds; }
	
	/**
	 * @return the floor the room is on
	 */
	public int getFloor() {
		return floor.get();
	}

	/**
	 * @param floor the floor the room is on
	 */
	public void setFloor(int floor) {
		this.floor.set(floor);
	}
	
	public IntegerProperty floorProperty() { return floor; }

	/**
	 * @return whether or not the room is accessible by lift
	 */
	public boolean isAccessibleByLift() {
		return accessibleByLift.get();
	}

	/**
	 * @param accessibleByLift whether or not the room is accessible by lift
	 */
	public void setAccessibleByLift(boolean accessibleByLift) {
		this.accessibleByLift.set(accessibleByLift);
	}

	public BooleanProperty accessibleByLiftProperty() { return accessibleByLift; }
	
	/**
	 * @return the whether or not the room has a balcony
	 */
	public boolean hasBalcony() {
		return balcony.get();
	}

	/**
	 * @param balcony whether or not the room has a balcony
	 */
	public void setBalcony(boolean balcony) {
		this.balcony.set(balcony);
	}
	
	public BooleanProperty balconyProperty() { return balcony; }

	/**
	 * @return the room's area
	 */
	public double getArea() {
		return area.get();
	}

	/**
	 * @param area the room's area
	 */
	public void setArea(double area) {
		this.area.set(area);
	}
	
	public DoubleProperty areaProperty() { return area; }

	/**
	 * @return the room's phone number extension
	 */
	public String getPhone() {
		return phone.get();
	}

	/**
	 * @param phone the room's phone number extension
	 */
	public void setPhone(String phone) {
		this.phone.set(phone);
	}
	
	public StringProperty phoneProperty() { return phone; }

	/**
	 * @return the room's view
	 */
	public String getView() {
		return view.get();
	}

	/**
	 * @param view the view out of the room's windows
	 */
	public void setView(String view) {
		this.view.set(view);
	}
	
	public StringProperty viewProperty() { return view; }

	private static void prepareDataFromResultSet(final Room r, final ResultSet rs) 
			throws SQLException {
		r.id = rs.getInt("room_id");
		r.setName(rs.getString("name"));
		r.setBeds(rs.getInt("beds"));
		r.setAccessibleByLift(rs.getBoolean("lift"));
		r.setArea(rs.getDouble("area"));
		r.setBalcony(rs.getBoolean("balcony"));
		r.setFloor(rs.getInt("floor"));
		r.setPhone(rs.getString("phone"));
		r.setType(rs.getString("type"));
		r.setView(rs.getString("view"));
	}
	
	public void prepareDataFromResultSet(final ResultSet rs)
			throws SQLException {
		Room.prepareDataFromResultSet(this, rs);
	}
	
	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	public boolean fromDbAtId(long id)
			throws NoSuchElementException, SQLException {
		boolean success = false;
		String query = SQL_SELECT_ALL + " WHERE id = ?";
		try (PreparedStatement stmt = con.prepareStatement(query)){
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
	 */
	@Override
	public void updateDb() throws SQLException {	
		try (PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
			ps.setString(1, getName());
			ps.setString(2, getType());
			ps.setInt(3, getBeds());
			ps.setInt(4, getFloor());
			ps.setBoolean(5, isAccessibleByLift());
			ps.setBoolean(6, hasBalcony());
			ps.setDouble(7, getArea());
			ps.setString(8, getPhone());
			ps.setString(9, getView());
			ps.setLong(10, getId());
			ps.executeUpdate();
		}
		
	}
	
	@Override
	public void insertIntoDb() throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(SQL_INSERT,
				Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, getName());
			ps.setString(2, getType());
			ps.setInt(3, getBeds());
			ps.setInt(4, getFloor());
			ps.setBoolean(5, isAccessibleByLift());
			ps.setBoolean(6, hasBalcony());
			ps.setDouble(7, getArea());
			ps.setString(8, getPhone());
			ps.setString(9, getView());
			ps.executeUpdate();
			this.setId(ps.getGeneratedKeys().getLong(1));
		}
	}
	
	@Override
	public void deleteFromDb() throws SQLException {
		try(PreparedStatement stmt = con.prepareStatement(SQL_DELETE)) {
			stmt.setLong(1, getId());
			stmt.executeUpdate();
		}
	}

	/**
	 * {@inheritDoc}
	 * Assuming DB is an SQLite database.
	 */
	@Override
	public void createTables() throws SQLException {
		try (Statement stmt = con.createStatement()) {
			stmt.executeUpdate(SQL_CREATE);
		}
	}

	@Override
	public String[] getPropertyIdentifiers() {
		String[] idents = { "name", "type", "beds", "floor", "accessibleByLift",
				"balcony", "area", "phone", "view" };
		return idents;
	}
	
}
