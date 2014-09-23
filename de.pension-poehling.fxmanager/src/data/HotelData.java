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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javafx.beans.property.Property;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import util.Messages;

/**
 * Provides methods to load a certain kind of data from a given DB into a subclass of HotelData.
 * 
 * @author lumpiluk
 *
 */
public abstract class HotelData {
	
	protected Connection con;
	
	/**
	 * @param con
	 */
	public HotelData(final Connection con) {
		setConnection(con);
	}
	
	/**
	 * @param con
	 */
	public void setConnection(Connection con) {
		this.con = con;
	}
	
	/**
	 * Used in createTableColumns for creating PropertyValueFactory objects.
	 * @return a collection containing all the properties of this class that
	 * should be displayable in a table.
	 */
	public abstract String[] getPropertyIdentifiers();
	
	public final Iterable<TableColumn<?, String>> createTableColumns() {
		List<TableColumn<?, String>> result =
				new ArrayList<TableColumn<?, String>>();
		
		for (String propertyIdent : getPropertyIdentifiers()) {
			TableColumn<Object, String> col = new TableColumn<Object, String>();
			col.setCellValueFactory(new PropertyValueFactory<Object, String>(propertyIdent));
			result.add(col);
		}
		
		return result;
	}
	
	/**
	 * Loads a database record into this object.
	 * See also http://docs.oracle.com/javase/tutorial/jdbc/overview/index.html
	 * for usage.
	 * 
	 * @return true if loading was successful
	 * @throws NoSuchElementException Record not found in DB.
	 * @throws SQLException table does not exist or does not provide the
	 * necessary columns or something else is wrong with the connection.
	 */
	public abstract boolean fromDbAtId(final long id)
			throws NoSuchElementException, SQLException;
	
	/**
	 * Gets a batch of HotelData objects.
	 * Method non-static to allow dynamic method binding for db-import.
	 * @param indices list of indices of HotelData objects to get.
	 * @return a list of HotelData objects returned from the database at the
	 * specified indices.
	 * @throws SQLException
	 */
	public Iterable<? extends HotelData> getBatch(final List<Long> indices) // TODO: override where possible or needed
			throws SQLException {
		List<HotelData> resultList = new LinkedList<HotelData>();
		for (Long id : indices) {
			HotelData d;
			try {
				// use this.getClass() to get the child class of the abstract
				// class HotelData currently running this method
				d = (HotelData)(this.getClass().newInstance());
				
				d.setConnection(con);
				d.fromDbAtId(id);
				resultList.add(d);
			} catch (InstantiationException | IllegalAccessException e) {
				System.out.println(Messages.getString(
						"DB.HotelData.BatchSelectConstructorException")); //$NON-NLS-1$
				//Exception: Could not create new HotelData instance while trying to do a batch SELECT in the DB.
			}
		}
		return resultList;
	}
	
	/**
	 * Updates the DB record at the given id.
	 * Advice: call setAutoCommit(false) on the DB connection,
	 * especially for batch updates. If you do so, don't forget con.commit();!
	 * @throws SQLException 
	 */
	public abstract void updateDb() throws SQLException;
	
	/**
	 * Creates a new record in the DB with a new primary key id.
	 * @throws SQLException
	 */
	public abstract void insertIntoDb() throws SQLException;
	
	/**
	 * Deletes the current record (usually determined by its id) from the
	 * database, if it exists.
	 * @throws SQLException
	 */
	public abstract void deleteFromDb() throws SQLException;
	
	/**
	 * Creates the necessary tables for the class in the given DB connection.
	 * Would've liked to make this method static, but unfortunately Java 1.7
	 * doesn't allow me to make a method both abstract and static.
	 * We also need dynamic method binding for db-import which only works with
	 * non-static methods.
	 * @throws SQLException Table probably already exists, perhaps in a
	 * different form.
	 */
	public abstract void createTables() throws SQLException;
	
}
