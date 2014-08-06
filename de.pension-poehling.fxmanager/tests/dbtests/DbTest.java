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
