/**
 * 
 */
package dbtests;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import data.Address;
import data.Person;
import data.Room;

/**
 * @author lumpiluk
 *
 */
public class DbTest {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws SQLException,
				ClassNotFoundException {
		// register the driver
		String sDriverName = "org.sqlite.JDBC";
		Class.forName(sDriverName);
		
		String dbFilePath = System.getProperty("user.home")
				+ "/.HotelManager/";
		File dbFile = new File(dbFilePath);
		if (dbFile.mkdirs()) {
			System.out.println("Path to " + dbFile.getAbsolutePath()
					+ " created successfully.");
		} else {
			System.out.println("Path was not created.");
		}
		
		Connection con = DriverManager.getConnection("jdbc:sqlite:"
				+ dbFile.getAbsolutePath() + "/testDB.sqlite");
		
		Address addressTableBuilder = new Address(con);
		Person personTableBuilder = new Person(con);
		Room roomTableBuilder = new Room(con);
		
		try {
			addressTableBuilder.createTables();
			personTableBuilder.createTables();
			roomTableBuilder.createTables();
		} finally {
			con.close();
		}

	}

}
