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
import java.sql.ResultSet;
import java.sql.SQLException;
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
	private boolean initialized = false;
	
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
	
	/** Task for handling adding of new flags concurrently. */
	private class InsertFlagTask extends Task<Void> {
		private ListChangeListener.Change<? extends String> c;
		
		public InsertFlagTask(ListChangeListener.Change<? extends String> c) {
			this.c = c;
		}
		
		@Override protected Void call() throws Exception {
			String insertQuery = "INSERT INTO flags (name) VALUES (?)";
			// TODO: finish
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
			// TODO: finish
			return null;
		}
	}
	
	private void initFlags() throws SQLException {
		ArrayList<String> items = new ArrayList<String>();
		final String query = "SELECT name FROM flags";
		
		try (ResultSet rs = con.createStatement().executeQuery(query)) {
			while(rs.next()) {
				items.add(rs.getString(0));
			}
		}
		
		flags.setAll(items);
		
		// handle adding to and removing from flags concurrently
		flags.addListener(new ListChangeListener<String>() {
			@Override public void onChanged(ListChangeListener.Change<? extends String> c) {
				if (c.wasAdded()) {
					databaseExecutor.submit(new InsertFlagTask(c));					
				} else if (c.wasRemoved()) {
					databaseExecutor.submit(new RemoveFlagTask(c));
				}
			}
		});
	}
	
	/** @return True iff connection to the DB has been established and all lists are loaded. */
	public boolean isInitialized() {
		return initialized;
	}
	
	/** @return List of possible flags an address can have. Used throughout the application. */
	public ObservableList<String> getFlagsObservable() {
		return flags;
	}
	
	/**
	 * Creates all required tables in a new database.
	 * @throws SQLException
	 */
	public void createTables() throws SQLException {
		(new Room(con)).createTables();
		(new Person(con)).createTables();
		(new Address(con)).createTables();
		// TODO: boookings/orders, invoices, flags, states, cities(?)...
	}
	
}
