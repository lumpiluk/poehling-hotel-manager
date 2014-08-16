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
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import util.Messages;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * Provides access to everything related to a HotelManager database.
 * @author lumpiluk
 */
public class DataSupervisor {

	/** Connection to the database */
	private Connection con;
	
	/** executes database operations concurrent to JavaFX operations. */
	private ExecutorService databaseExecutor;
	
	/** List of possible flags an address can have. Used throughout the application */
	private final ObservableList<String> flags = FXCollections.observableArrayList();
	
	/** true iff connection to the DB has been established and all lists are loaded */
	private boolean flagsInitialized = false;
	
	/** 
	 * Thread factory for databaseExectuor.
	 * With help from https://gist.github.com/jewelsea/4957967
	 * @author lumpiluk
	 */
	static class DatabaseThreadFactory implements ThreadFactory {
		static final AtomicInteger poolNumber = new AtomicInteger(1);
	    
	    @Override public Thread newThread(Runnable runnable) {
	    	Thread thread = new Thread(runnable, "Database-Connection-" + poolNumber.getAndIncrement() + "-thread");
	    	thread.setDaemon(true);
	    	return thread;
	    }
	} 
	
	public DataSupervisor() {
		
		databaseExecutor = Executors.newFixedThreadPool(1, new DatabaseThreadFactory()); // allows only one thread at a time to work on the db
		
		databaseExecutor.submit(new Task<Void>(){
			@Override protected Void call() {
				
				try {
					initFlags();
				} catch (SQLException e) {
					Messages.showError(e, Messages.ErrorType.DB);
				}
				
				return null;
			}
		});
		
	}
	
	private void init() {
		
	}
	
	/** @param con The database connection to a Hotel Manager database. */
	public void setConnection(Connection con) {
		this.con = con;
	}
	
	/** Task for handling adding of new flags concurrently. */
	private class InsertFlagTask extends Task<Void> {
		private ListChangeListener.Change<? extends String> c;
		
		public InsertFlagTask(ListChangeListener.Change<? extends String> c) {
			this.c = c;
		}
		
		@Override protected Void call() throws Exception {
			String insertQuery = "INSERT INTO flags (name) VALUES (?)";
			try (PreparedStatement ps = con.prepareStatement(insertQuery)) {
				for (String newFlag : c.getAddedSubList()) {
					ps.setString(1, newFlag);
					ps.addBatch();
				}
				ps.executeBatch();
			}
			return null;
		}
	}
	
	/** Task for handling removal of flags concurrently. */
	private class RemoveFlagTask extends Task<Void> {
		private ListChangeListener.Change<? extends String> c;
		
		public RemoveFlagTask(ListChangeListener.Change<? extends String> c) {
			this.c = c;
		}
		
		@Override protected Void call() throws Exception {
			String deleteQuery = "DELETE FROM flags WHERE name = ?";
			try (PreparedStatement ps = con.prepareStatement(deleteQuery)) {
				for (String flatToDelete : c.getRemoved()) {
					ps.setString(1, flatToDelete);
				}
			}
			return null;
		}
	}
	
	private ListChangeListener<String> flagsListener = new ListChangeListener<String>() {
		@Override public void onChanged(ListChangeListener.Change<? extends String> c) {
			if (c.wasAdded()) {
				databaseExecutor.submit(new InsertFlagTask(c));					
			} else if (c.wasRemoved()) {
				databaseExecutor.submit(new RemoveFlagTask(c));
			}
		}
	};
	
	private void initFlags() throws SQLException {
		ArrayList<String> items = new ArrayList<String>();
		final String query = "SELECT name FROM flags";
		
		try (ResultSet rs = con.createStatement().executeQuery(query)) {
			while(rs.next()) {
				items.add(rs.getString(0)); // TODO: necessary to run this in Platform.runLater()?
			}
		}
		
		// remove listener first so that adding items to the list will have no effect on the database
		flags.removeListener(flagsListener);
		flags.setAll(items);
		flags.addListener(flagsListener);
	}
	
	private void createFlagsTable() throws SQLException {
		final String createQuery = "CREATE TABLE flags (name TEXT NOT NULL)";
		try (Statement stmt = con.createStatement()) {
			stmt.executeUpdate(createQuery);
		}
	}
	
	/** @return True iff connection to the DB has been established and all lists are loaded. */
	public boolean isInitialized() {
		return flagsInitialized;
	}
	
	/** @return List of possible flags an address can have. Used throughout the application. */
	public ObservableList<String> getFlagsObservable() {
		return flags;
	}
	
	/**
	 * Creates all required tables in a new database.
	 * @see createTablesConcurrently
	 * @return true if table creation completed successfully
	 */
	public boolean createTables() {
		boolean success = false;
		try {
			(new Room(con)).createTables();
			(new Person(con)).createTables();
			(new Address(con)).createTables();
			createFlagsTable();
			success = true;
		} catch (SQLException e) {
			Messages.showError(e, Messages.ErrorType.DB);
		}
		return success;
	}
	
	/**
	 * Concurrently creates all required tables in a new database.
	 * @see createTables
	 */
	public void createTablesConcurrently() {
		databaseExecutor.submit(new Task<Boolean>(){
			@Override protected Boolean call() {
				return createTables();				
			}
		});
		// TODO: bookings/orders, invoices, flags, states, cities(?)...
	}
	
}
