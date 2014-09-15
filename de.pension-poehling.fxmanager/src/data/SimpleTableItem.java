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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * @author lumpiluk
 *
 */
public class SimpleTableItem extends HotelData {

	private final static String SQL_CREATE = "CREATE TABLE IF NOT EXISTS %1$s (%2$s TEXT PRIMARY KEY NOT NULL UNIQUE)";
	
	private final static String SQL_INSERT = "INSERT INTO %1$s (%2$s) VALUES (?)";
	
	private final static String SQL_SELECT_ALL = "SELECT %2$s FROM %1$s";
	
	private final static String SQL_DELETE = "DELETE FROM %1$s WHERE %2$s = ?";
	
	private final String SQL_TABLE_NAME;
	
	private final String SQL_COL_NAME;
	
	private StringProperty value;
	
	public SimpleTableItem(Connection con, String tableName, String colName) {
		super(con);
		this.SQL_TABLE_NAME = tableName;
		this.SQL_COL_NAME = colName;
		this.value = new SimpleStringProperty();
	}
	
	private String getSqlCreate() {
		return String.format(SQL_CREATE, SQL_TABLE_NAME, SQL_COL_NAME);
	}
	
	private String getSqlInsert() {
		return String.format(SQL_INSERT, SQL_TABLE_NAME, SQL_COL_NAME);
	}
	
	public String getSqlSelectAll() {
		return String.format(SQL_SELECT_ALL, SQL_TABLE_NAME, SQL_COL_NAME);
	}
	
	private String getSqlDelete() {
		return String.format(SQL_DELETE, SQL_TABLE_NAME, SQL_COL_NAME);
	}
	
	public final String getSqlTableName() { return SQL_TABLE_NAME; }
	
	public final String getSqlColumnName() { return SQL_COL_NAME; }
	
	public String getValue() { return value.get(); }
	
	public void setValue(final String value) { this.value.set(value); }
	
	public StringProperty valueProperty() { return value; }

	/** Simple table items are not supposed to be loaded by their row id. */
	@Override
	public boolean fromDbAtId(long id) throws NoSuchElementException,
			SQLException {
		throw new UnsupportedOperationException();
	}

	/** Simple table items are not supposed to be updated. */
	@Override public void updateDb() throws SQLException {
		throw new UnsupportedOperationException();
	}

	/** {@inheritDoc} */
	@Override public void insertIntoDb() throws SQLException {
		try (PreparedStatement stmt = con.prepareStatement(getSqlInsert())) {
			stmt.setString(1, getValue());
			stmt.executeUpdate();
		}
	}

	/** {@inheritDoc} */
	@Override public void deleteFromDb() throws SQLException {
		try (PreparedStatement stmt = con.prepareStatement(getSqlDelete())) {
			stmt.setString(1, getValue());
			stmt.executeUpdate();
		}

	}

	/** {@inheritDoc} */
	@Override public void createTables() throws SQLException {
		try (Statement stmt = con.createStatement()) {
			stmt.executeUpdate(getSqlCreate());
		}
	}

}
