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
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
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
import java.util.function.Consumer;

import application.customControls.PersonPane;
import application.customControls.PersonPane.PersonItem;
import util.Messages;
import util.Messages.ErrorType;
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
			+ "addresses.addressee = people.person_id";
	
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
	
	
	// Simple table item lists
	/** List of possible flags an address can have. Used throughout the application */
	private final ObservableList<String> flags = FXCollections.observableArrayList();
	
	/** List of all states (geographically speaking) ever used in the db. */
	private final ObservableList<String> states = FXCollections.observableArrayList();
	
	/** List of all countries ever used in the db. */
	private final ObservableList<String> countries = FXCollections.observableArrayList();
	
	/** List of possible titles a person can have. */
	private final ObservableList<String> titles = FXCollections.observableArrayList();
	
	// Address search related
	/** Search result from searching in addresses */
	private final ObservableList<Address> addressesSearchResult = FXCollections.observableArrayList();
	
	private String addressSearchString = "";
	
	private String[] addressSearchFlags = new String[0];
	
	
	/** 
	 * Thread factory for databaseExectuor.
	 * With help from https://gist.github.com/jewelsea/4957967
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
	
	/**
	 * Initialize virtual tables required for SQLite full text search.
	 * @throws SQLException
	 */
	private void initVirtualTables() throws SQLException {
		(new Address(con)).createVirtualFTSTable();
	}
	
	private void init() throws SQLException {
		initSimpleTableItemList(countries, Country.class, new SimpleTableItemListListener(Country.class));
		initSimpleTableItemList(states, State.class, new SimpleTableItemListListener(State.class));
		initSimpleTableItemList(titles, Title.class, new SimpleTableItemListListener(Title.class));
		initSimpleTableItemList(flags, Flag.class, new SimpleTableItemListListener(Flag.class));
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
	
	/** 
	 * Listens to changes in a list of simple table items, 
	 * adds to or removes from db accordingly. 
	 */
	private class SimpleTableItemListListener implements ListChangeListener<String> {
		private Class<? extends SimpleTableItem> stiClass;
		public SimpleTableItemListListener(Class<? extends SimpleTableItem> c) {
			this.stiClass = c;
		}
		@Override public void onChanged(
				javafx.collections.ListChangeListener.Change<? extends String> change) {
			try {
				while (change.next()) {
					if (change.wasAdded()) {
						for (String newItem : change.getAddedSubList()) {
							SimpleTableItem s = stiClass.getDeclaredConstructor(Connection.class).newInstance(con);
							s.setValue(newItem);
							insertConcurrently(s);
						}
					}
					if (change.wasRemoved()) {
						for (String itemToDelete : change.getRemoved()) {
							SimpleTableItem s = stiClass.getDeclaredConstructor(Connection.class).newInstance(con);
							s.setValue(itemToDelete);
							deleteConcurrently(s);
						}
					}
				}
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException
					| SecurityException e) { // exceptions possibly thrown in initialization of s
				Messages.showError(e, ErrorType.UNKNOWN);
			}
		}
	}
	
	/** 
	 * Initializes a list of simple table items, including listeners etc.
	 * @param l the list to initialize
	 * @param c subclass of SimpleTableItem to be used to load records from the
	 * @param listener ListChangeListener to update the database
	 * db
	 * @throws SQLException
	 */
	private void initSimpleTableItemList(ObservableList<String> l,
			Class<? extends SimpleTableItem> c,
			ListChangeListener<String> listener) throws SQLException {
		try {
			ArrayList<String> items = new ArrayList<String>();
			final String query = (c.getDeclaredConstructor(Connection.class)
					.newInstance(con)).getSqlSelectAll();
			
			try (ResultSet rs = con.createStatement().executeQuery(query)) {
				while (rs.next()) {
					items.add(rs.getString(1));
				}
			}
			
			// remove listener first so that adding items to the list will have no effect on the database
			l.removeListener(listener);
			l.setAll(items);
			l.addListener(listener);			
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// exceptions possibly thrown by initialization of query string
			Messages.showError(e, ErrorType.UNKNOWN);
		}
	}
	
	/** @return List of possible flags an address can have. Used throughout the application. */
	public ObservableList<String> getFlagsObservable() {
		return flags;
	}
	
	/** @return List of possible titles a person can have. */
	public ObservableList<String> getTitlesObservable() {
		return titles;
	}
	
	/** @return Observable list of all states ever used in the db. */
	public ObservableList<String> getStatesObservable() {
		return states;
	}
	
	/** @return Observable list of all countries ever used in the db. */
	public ObservableList<String> getCountriesObservable() {
		return countries;
	}
	
	private boolean queryIsValid() {
		return addressSearchString != null && !addressSearchString.equals("")
				&& !addressSearchString.equals("*");
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
			StringBuilder match = new StringBuilder(); // will be inserted at the end of SQL_ADDRESS_SEARCH
			boolean searchingForText = false;
			
			// TODO: something like the following should work:
			// Searching: SELECT * FROM addresses LEFT OUTER JOIN people ON addresses.addressee = people.person_id  WHERE addresses.address_id IN (SELECT address_fts_id FROM addresses_fts WHERE addresses_fts MATCH '- fts_flags:"| unrein |"')
			
			// only use "WHERE" clause if there actually are any search parameters
			if (queryIsValid() || addressSearchFlags.length > 0) {
				match.append(" WHERE addresses.address_id IN (");
				match.append("SELECT address_fts_id FROM addresses_fts WHERE ");
				match.append("addresses_fts MATCH '");

				if (queryIsValid()) {
					match.append("(fts_title:%1$s OR fts_first_names:%1$s ");
					match.append("OR fts_surnames:%1$s OR fts_street:%1$s ");
					match.append("OR fts_town:%1$s OR fts_zip:%1$s ");
					match.append("OR fts_phone:%1$s OR fts_email:%1$s ");
					match.append("OR fts_cellphone:%1$s) ");
					searchingForText = true;
				}
				
				//for (String flag : addressSearchFlags) {
				for (int i = 0; i < addressSearchFlags.length; i++) {
					Flag flag = new Flag(con);
					flag.setValue(addressSearchFlags[i]);
					if (i > 0 || searchingForText) { // don't put "AND" in the front of the match expression if nothing comes before the "AND"
						match.append("AND");
					}
					match.append("- fts_flags:\"");
					match.append(flag.toDbString()); // TODO: ensure lower case on insertion?
					match.append("\" ");
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
						Platform.runLater(() -> addressesSearchResult.add(a));
					}
				}
			} catch (Exception e) {
				Platform.runLater(() ->
						Messages.showError(e, Messages.ErrorType.DB));
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
		if (runningAddressSearch != null && runningAddressSearch.isRunning()) {
			runningAddressSearch.cancel(true); // try to stop search if running
		}
		addressSearchString = userQuery.replaceAll("['\":]", "").toLowerCase();
		addressSearchFlags = flags;
		addressesSearchResult.clear();
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
			(new Flag(con)).createTables();
			(new Country(con)).createTables();
			(new State(con)).createTables(); // TODO: combine with country?
			(new Title(con)).createTables();
			success = true;
		} catch (SQLException e) {
			Platform.runLater(() ->
					Messages.showError(e, Messages.ErrorType.DB));
		}
		return success;
	}
	
	/**
	 * Do any operation on a HotelData object, as specified by the Consumer
	 * parameter, concurrently.
	 * @see insertConcurrently(), updateConcurrently() etc.
	 * @param h HotelData instance the operation should be performed on.
	 * @param c The action to be performed, usually in a lambda expression.
	 */
	private void consumeHotelDataConcurrently(HotelData h,
			Consumer<HotelData> c) {
		databaseExecutor.submit(() -> {
			try {
				c.accept(h);
			} catch (Exception e) { // c should handle SQLExceptions by itself
				Platform.runLater(() ->
						Messages.showError(e, ErrorType.UNKNOWN));
			}
		});
	}
	
	private void consumeConnectionConcurrently(Consumer<Connection> c) {
		databaseExecutor.submit(() -> {
			try {
				c.accept(con);
			} catch (Exception e) { // c should handle SQLExceptions by itself
				Platform.runLater(() ->
						Messages.showError(e, ErrorType.UNKNOWN));
			}
		});
	}
	
	/**
	 * Concurrently inserts the HotelData object o into the database.
	 * @see HotelData.insertIntoDb();
	 * @param o the object to call insertIntoDb() on.
	 */
	public void insertConcurrently(HotelData o) {
		consumeHotelDataConcurrently(o, hotelDatum -> {
			try {
				hotelDatum.insertIntoDb();
			} catch (SQLException e) {
				Platform.runLater(() ->	Messages.showError(e, ErrorType.DB));
			}
		});
	}
	
	/**
	 * Concurrently updates the HotelData object o within the database.
	 * @see HotelData.updateDb();
	 * @param o the object to call updateDb() on.
	 */
	public void updateConcurrently(HotelData o) {
		consumeHotelDataConcurrently(o, hotelDatum -> {
			try {
				hotelDatum.updateDb();
			} catch (SQLException e) {
				Platform.runLater(() ->	Messages.showError(e, ErrorType.DB));
			}
		});
	}
	
	/**
	 * Concurrently deletes a HotelData's database record, usually identified
	 * by its id, from the database.
	 * @see HotelData.deleteFromDb();
	 * @param o the object to call deleteFromDb() on.
	 */
	public void deleteConcurrently(HotelData o) {
		consumeHotelDataConcurrently(o, hotelDatum -> {
			try {
				hotelDatum.deleteFromDb();
			} catch (SQLException e) {
				Platform.runLater(() ->	Messages.showError(e, ErrorType.DB));
			}
		});
	}

	/**
	 * Concurrently calls commit() on the current connection and re-enables
	 * AutoCommit afterwards.
	 * @see Connection#commit()
	 */
	public void commitConcurrently() {
		consumeConnectionConcurrently(connection -> {
			try {
				connection.commit();
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				Platform.runLater(() ->	Messages.showError(e, ErrorType.DB));
			}
		});
	}
	
	/**
	 * Concurrently calls rollback() on the current connection and re-enables
	 * AutoConnect afterwards.
	 * @see Connection#rollback()
	 */
	public void rollbackConcurrently() {
		consumeConnectionConcurrently(connection -> {
			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				Platform.runLater(() ->	Messages.showError(e, ErrorType.DB));
			}
		});
	}
	
	public ObservableList<PersonItem> getPersonItemsFromAddress(final Address a,
			final PersonPane pp) {
		ObservableList<PersonItem> ol = FXCollections.observableArrayList();
		databaseExecutor.submit(() -> {
			try {
				for (Person p : a.getPeople()) {
					ol.add(pp.new PersonItem(p, p.equals(a.getAddressee())));
				}
			} catch (SQLException e) {
				Platform.runLater(() -> Messages.showError(e, ErrorType.DB));
			} catch (Exception e) {
				Platform.runLater(() ->
						Messages.showError(e, ErrorType.UNKNOWN));
			}
		});
		return ol;
	}
	
}
