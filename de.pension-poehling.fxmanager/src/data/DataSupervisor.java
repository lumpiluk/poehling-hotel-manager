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

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import util.Messages;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * Provides access to everything related to a HotelManager database.
 * @author lumpiluk
 */
public class DataSupervisor {

	private static final long CONNECTION_CLOSE_TIMEOUT = 3;
	
	public static enum ConnectionStatus {
		CONNECTION_ESTABLISHED, DRIVER_NOT_FOUND, SQL_EXCEPTION, SQL_TIMEOUT;
	}
	
	private static final String SQL_ADDRESS_SEARCH = "SELECT * FROM addresses "
			+ "LEFT OUTER JOIN people ON "
			+ "addresses.addressee = people.address";
	
	public class ObservableConnectionState extends Observable {
    	private DataSupervisor.ConnectionStatus status;
    	
    	public void setConnectionState(DataSupervisor.ConnectionStatus value) {
    		this.status = value;
    		this.setChanged();
    		Platform.runLater(new Runnable() {

				@Override
				public void run() {
					notifyObservers();					
				}
    			
    		});
    	}
    	
    	public DataSupervisor.ConnectionStatus getStatus() {
    		return status;
    	}
    }
	
	/** Connection to the database */
	private Connection con;
	
	/** Notifies observers when a connection to the db has been established. */
	private ObservableConnectionState conState;
	
	/** executes database operations concurrent to JavaFX operations. */
	private ExecutorService databaseExecutor;
	
	/** List of possible flags an address can have. Used throughout the application */
	private final ObservableList<String> flags = FXCollections.observableArrayList();
	
	/** Search result from searching in addresses */
	private final ObservableList<Address> addressesSearchResult = FXCollections.observableArrayList();
	
	private String addressSearchString = "";
	
	private String[] addressSearchFlags = new String[0];
	
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
		conState = new ObservableConnectionState();
		databaseExecutor = Executors.newFixedThreadPool(1, new DatabaseThreadFactory()); // allows only one thread at a time to work on the db
	}
	
	private void initVirtualTables() throws SQLException {
		(new Address(con)).createVirtualFTSTable();
	}
	
	private void init() throws SQLException {
		initFlags();
		initVirtualTables();
	}
	
	/** 
	 * Alternative to calling one of the connectToDb methods.
	 * @param con The database connection to a Hotel Manager database.
	 */
	public void setConnection(Connection con) {
		this.con = con;
	}
	
	/** @return the Connection */
	public Connection getConnection() {
		return con;
	}
	
	/** 
	 * Attempts to connect to an offline database.
	 * May be called by one of the connectToDbConcurrently methods.
	 * @param dbFile the database file.
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * @param newDb if true, will create all necessary tables
	 */
	public void connectToDb(File dbFile, boolean newDb)
			throws ClassNotFoundException, SQLException {
		
		final String sDriverName = "org.sqlite.JDBC";
		final String conString = "jdbc:sqlite:%s";
		Class.forName(sDriverName);
		
		con = DriverManager.getConnection(String.format(conString,
				dbFile.getAbsolutePath()));
		if (newDb) {
			createTables();
		}
		init();
	}
	
	/**
	 * Concurrently connects to an offline SQLite database.
	 * Creates a new file in ~/hotelManager.sqlite if necessary.
	 * @param o will be notified about changes in the connection status.
	 * May be null.
	 */
	public void connectToDbConcurrently(Observer o) {
		File file = new File(System.getProperty("user.home") + "/hotelManager.sqlite");
		connectToDbConcurrently(file, true, o);
	}
	
	/** Convenience method for connectToDbConcurrently(File dbFile, null). */
	public void connectToDbConcurrently(File dbFile) {
		connectToDbConcurrently(dbFile, true, null);
	}
	
	/**
	 * Concurrently connects to an offline SQLite database.
	 * @param dbFile the database file
	 * @param will be notified about changes in the connection status.
	 * May be null.
	 */
	public void connectToDbConcurrently(File dbFile, boolean newDb, Observer o) {
		if (o != null) {
			this.conState.addObserver(o);
		}
		
		databaseExecutor.submit(new Task<Void>() {

			@Override protected Void call() throws Exception {
				try {
					connectToDb(dbFile, newDb);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					conState.setConnectionState(ConnectionStatus.DRIVER_NOT_FOUND);
					Messages.showError(e, Messages.ErrorType.DB);
				} catch (SQLTimeoutException e) {
					conState.setConnectionState(ConnectionStatus.SQL_TIMEOUT);
				} catch (SQLException e) {
					e.printStackTrace();
					conState.setConnectionState(ConnectionStatus.SQL_EXCEPTION);
				}
				conState.setConnectionState(ConnectionStatus.CONNECTION_ESTABLISHED);
				return null;
			}
			
		});
	}
	
	/**
	 * Attempts to close the database connection.
	 * @throws SQLException
	 */
	public void closeDbConnection() {
		databaseExecutor.shutdown();
		try {
			databaseExecutor.awaitTermination(CONNECTION_CLOSE_TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Messages.showError(Messages.getString("DB.ClosingTimeout"),
					Messages.ErrorType.DB);
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				Messages.showError(e, Messages.ErrorType.DB);
			}
		}
		
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
				for (String flagToDelete : c.getRemoved()) {
					ps.setString(1, flagToDelete);
				}
			}
			return null;
		}
	}
	
	/** 
	 * Listens to changes in the list of flags, 
	 * adds to or removes from db accordingly. 
	 */
	private ListChangeListener<String> flagsListener = new ListChangeListener<String>() {
		@Override public void onChanged(ListChangeListener.Change<? extends String> c) {
			if (c.wasAdded()) {
				databaseExecutor.submit(new InsertFlagTask(c));					
			} else if (c.wasRemoved()) {
				databaseExecutor.submit(new RemoveFlagTask(c));
			}
		}
	};
	
	/** Initializes the list of flags, including listeners etc. */
	private void initFlags() throws SQLException {
		ArrayList<String> items = new ArrayList<String>();
		final String query = "SELECT name FROM flags";
		
		try (ResultSet rs = con.createStatement().executeQuery(query)) {
			while(rs.next()) {
				items.add(rs.getString(1)); // TODO: necessary to run this in Platform.runLater()?
			}
		}
		
		// remove listener first so that adding items to the list will have no effect on the database
		flags.removeListener(flagsListener);
		flags.setAll(items);
		flags.addListener(flagsListener);
	}
	
	/** Creates a table for flags in the database */
	private void createFlagsTable() throws SQLException {
		final String createQuery = "CREATE TABLE flags ("
				+ "name TEXT NOT NULL)";
		try (Statement stmt = con.createStatement()) {
			stmt.executeUpdate(createQuery);
		}
	}
	
	/** @return True iff connection to the DB has been established and all lists are loaded. */
	public boolean isInitialized() {
		return flagsInitialized; // TODO finish, needed?
	}
	
	/** @return List of possible flags an address can have. Used throughout the application. */
	public ObservableList<String> getFlagsObservable() {
		return flags;
	}
	
	/**
	 * Queries addresses table using the flags to filter in addressSearchFlags
	 * and the user query in addressSearchString.
	 * Result is output in addressesSearchResult.
	 * @see searchAddresses()
	 */
	private class AddressSearchTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			addressesSearchResult.clear();
			
			StringBuilder match = new StringBuilder(); // will be inserted at the end of SQL_ADDRESS_SEARCH
			
			// only use "WHERE" clause if there actually are any search parameters
			if ((addressSearchString != null && !addressSearchString.equals(""))
					|| addressSearchFlags.length > 0) {
				match.append(" WHERE addresses.address_id IN (");
				match.append("SELECT address_fts_id FROM addresses_fts WHERE ");
				match.append("addresses_fts MATCH '");

				if (addressSearchString != null && !addressSearchString.equals("")) {
					match.append("(fts_title:%1$s OR fts_first_names:%1$s ");
					match.append("OR fts_surnames:%1$s OR fts_street:%1$s ");
					match.append("OR fts_town:%1$s OR fts_zip:%1$s ");
					match.append("OR fts_phone:%1$s OR fts_email:%1$s ");
					match.append("OR fts_cellphone:%1$s) ");
				}
				
				for (String flag : addressSearchFlags) {
					match.append("AND NOT fts_flags:");
					match.append(flag.toLowerCase()); // TODO: ensure lower case on insertion?
					match.append(" ");
				}
				match.delete(match.length() - 1, match.length());
				match.append("')");
			}
			
			String query = SQL_ADDRESS_SEARCH + " %s";
			try {
				query = String.format(query,
						String.format(match.toString(), addressSearchString));
			} catch (IllegalFormatException e) {
				// intentionally left empty
				// may occur if content of previous if-statement wasn't executed
			}
			
			System.out.println("Searching: " + query); // TODO: remove debugging output once this works
			
			try (Statement stmt = con.createStatement()) {
				try (ResultSet rs = stmt.executeQuery(query)) {
					while (rs.next() && !this.isCancelled()) {
						Address a = new Address(con);
						a.prepareDataFromResultSet(rs);
						addressesSearchResult.add(a);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
	};
	
	private AddressSearchTask runningAddressSearch;
	
	/**
	 * Initiates a new concurrent search in the addresses table of the db.
	 * Results will be made available via getAddressSearchResultObservable().
	 * Will stop any currently running search.
	 * The characters "'", "\"" and ":" in the query will be ignored.
	 * @param userQuery Search string entered by the user.
	 * @param flags Any address having any of these flags set in its flags
	 * column will be left out of the search results.
	 */
	public void searchAddresses(String userQuery, String... flags) {
		// TODO
		if (runningAddressSearch != null && runningAddressSearch.isRunning()) {
			runningAddressSearch.cancel(true); // try to stop search if running
		}
		addressSearchString = userQuery.replaceAll("['\":]", "").toLowerCase();
		addressSearchFlags = flags;
		runningAddressSearch = new AddressSearchTask();
		databaseExecutor.submit(runningAddressSearch);
	}
	
	/** @return Search result from searching in addresses */
	public ObservableList<Address> getAddressSearchResultObservable() {
		return addressesSearchResult;
	}
	
	/**
	 * Creates all required tables in a new database.
	 * @see createTablesConcurrently
	 * @return true if table creation completed successfully
	 */
	private boolean createTables() {
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
	private void createTablesConcurrently() { // TODO: needed?
		databaseExecutor.submit(new Task<Void>() {
			@Override protected Void call() {
				createTables();
				return null;
			}
		});
		// TODO: bookings/orders, invoices, flags, states, cities(?)...
	}
	
	/**
	 * Concurrently inserts the HotelData object o into the database.
	 * @see HotelData.insertIntoDb();
	 * @param o the object to call insertIntoDb() on.
	 */
	public void insertConcurrently(HotelData o) {
		databaseExecutor.submit(new Task<Void>() {
			@Override protected Void call() throws Exception {
				try {
					o.insertIntoDb();
				} catch (SQLException e) {
					Messages.showError(e, Messages.ErrorType.DB);
				} catch (Exception e) {
					Messages.showError(e, Messages.ErrorType.UNKNOWN);
				}
				return null;
			}
		});
	}
	
	/**
	 * Concurrently updates the HotelData object o within the database.
	 * @see HotelData.updateDb();
	 * @param o the object to call updateDb() on.
	 */
	public void updateConcurrently(HotelData o) { // XXX is there a lambda expression for this?
		databaseExecutor.submit(new Task<Void>() {
			@Override protected Void call() throws Exception {
				try {
					o.updateDb();
				} catch (SQLException e) {
					Messages.showError(e, Messages.ErrorType.DB);
				}
				return null;
			}
		});
	}
	
}
