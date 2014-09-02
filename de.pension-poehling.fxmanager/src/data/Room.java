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

/**
 * Data object for a hotel room.
 * @author lumpiluk
 *
 */
public class Room extends HotelData {

	private static final String SQL_TABLE_NAME = "rooms";

	private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ SQL_TABLE_NAME + " (room_id INTEGER PRIMARY KEY, "
			+ "name TEXT NOT NULL, "
			+ "type TEXT NOT NULL, "
			+ "floor INTEGER NOT NULL, "
			+ "lift INTEGER NOT NULL, " // boolean
			+ "balcony INTEGER NOT NULL, " // boolean
			+ "area REAL, "
			+ "phone TEXT, "
			+ "view TEXT)";
	
	private static final String SQL_INSERT = "INSERT INTO " + SQL_TABLE_NAME
			+ " (name, type, floor, lift, balcony, area, phone, view) "
			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_UPDATE = "UPDATE " + SQL_TABLE_NAME
			+ " SET name = ?, type = ?, floor = ?, lift = ?, balcony = ?, "
			+ "area = ?, phone = ?, view = ?"
			+ "WHERE room_id = ?";
	
	private static final String SQL_DELETE = "DELETE FROM " + SQL_TABLE_NAME
			+ " WHERE room_id = ?";
	
	public static enum Type {
		SINGLE("E"),
		DOUBLE("D"),
		APARTMENT("F");
		
		private String letter;
		
		private Type(String letter) {
			this.letter = letter;
		}
		
		/**
		 * @return the letter associated with each room type. E for SINGLE,
		 * D for DOUBLE, F for APARTMENT
		 */
		public String getLetter() {
			return letter;
		}
		
		/**
		 * @param letter
		 * @return
		 * @throws NoSuchElementException
		 */
		public static Type byLetter(String letter)
				throws NoSuchElementException {
			switch (letter) {
			case "E": return SINGLE;
			case "D": return DOUBLE;
			case "F": return APARTMENT;
			default: throw new NoSuchElementException();
			}
		}
	}
	
	private long id;
	
	/** Usually the same as id. */
	private String name;
	
	/** Type of room: single, double or apartment. */
	private Type type;
	
	/** Floor on which room is located with 0 being the ground floor. */
	private int floor;
	
	/** 
	 * Whether or not room is accessible by lift.
	 * For rooms accessible without the use of stairs this value is true.
	 */
	private boolean accessibleByLift;
	
	/** Wheter or not room has a balcony. */
	private boolean balcony;
	
	/** Area available in the room. */
	private double area;
	
	/** 
	 * Digits usually added to the house's phone number to reach this room.
	 *  Can be null.
	 */
	private String phone;
	
	/**
	 * View from this room.
	 * E.g. "park" or "beach"
	 */
	private String view;
	
	/**
	 * 
	 */
	public Room(final Connection con) {
		super(con);
		this.setName("");
		this.setType(Type.SINGLE);
		this.setFloor(0);
		this.setAccessibleByLift(true);
		this.setBalcony(false);
		this.setArea(0.0);
		this.setPhone("");
		this.setView("");
	}
	
	/**
	 * 
	 * @param number
	 * @param type
	 * @param floor
	 * @param accessibleByLift
	 * @param balcony
	 * @param area
	 * @param phone
	 * @param view
	 */
	public Room(final long id, final String name, final Type type,
			final int floor, final boolean accessibleByLift,
			final boolean balcony, final double area, final String phone,
			final String view, final Connection con) {
		super(con);
		this.setName(name);
		this.setType(type);
		this.setFloor(floor);
		this.setAccessibleByLift(accessibleByLift);
		this.setBalcony(balcony);
		this.setArea(area);
		this.setPhone(phone);
		this.setView(view);
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
		return name;
	}

	/**
	 * @param number the room's number
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type of the room (e.g. single or double)
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type of the room (e.g. single or double)
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the floor the room is on
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * @param floor the floor the room is on
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	}

	/**
	 * @return whether or not the room is accessible by lift
	 */
	public boolean isAccessibleByLift() {
		return accessibleByLift;
	}

	/**
	 * @param accessibleByLift whether or not the room is accessible by lift
	 */
	public void setAccessibleByLift(boolean accessibleByLift) {
		this.accessibleByLift = accessibleByLift;
	}

	/**
	 * @return the whether or not the room has a balcony
	 */
	public boolean hasBalcony() {
		return balcony;
	}

	/**
	 * @param balcony whether or not the room has a balcony
	 */
	public void setBalcony(boolean balcony) {
		this.balcony = balcony;
	}

	/**
	 * @return the room's area
	 */
	public double getArea() {
		return area;
	}

	/**
	 * @param area the room's area
	 */
	public void setArea(double area) {
		this.area = area;
	}

	/**
	 * @return the room's phone number extension
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the room's phone number extension
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the room's view
	 */
	public String getView() {
		return view;
	}

	/**
	 * @param view the view out of the room's windows
	 */
	public void setView(String view) {
		this.view = view;
	}

	/**
	 * {@inheritDoc}
	 * @throws SQLException 
	 */
	@Override
	public boolean fromDbAtId(long id)
			throws NoSuchElementException, SQLException {
		boolean success = false;
		String query = "SELECT * FROM " + SQL_TABLE_NAME + " WHERE id = ?";
		try (PreparedStatement stmt = con.prepareStatement(query)){
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.first()) {
					throw new NoSuchElementException();
				}
				this.id = rs.getInt("room_id");
				this.setName(rs.getString("name"));
				this.setAccessibleByLift(rs.getBoolean("lift"));
				this.setArea(rs.getDouble("area"));
				this.setBalcony(rs.getBoolean("balcony"));
				this.setFloor(rs.getInt("floor"));
				this.setPhone(rs.getString("phone"));
				this.setType(Type.byLetter(rs.getString("type")));
				this.setView(rs.getString("view"));
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
			ps.setString(2, getType().getLetter());
			ps.setInt(3, getFloor());
			ps.setBoolean(4, isAccessibleByLift());
			ps.setBoolean(5, hasBalcony());
			ps.setDouble(6, getArea());
			ps.setString(7, getPhone());
			ps.setString(8, getView());
			ps.setLong(9, getId());
			ps.executeUpdate();
		}
		
	}
	
	@Override
	public void insertIntoDb() throws SQLException {
		try (PreparedStatement ps = con.prepareStatement(SQL_INSERT,
				Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, getName());
			ps.setString(2, getType().getLetter());
			ps.setInt(3, getFloor());
			ps.setBoolean(4, isAccessibleByLift());
			ps.setBoolean(5, hasBalcony());
			ps.setDouble(6, getArea());
			ps.setString(7, getPhone());
			ps.setString(8, getView());
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
	
}
