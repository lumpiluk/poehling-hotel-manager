/**
 * 
 */
package data;

import java.sql.Connection;

/**
 * @author lumpiluk
 *
 */
public class Country extends SimpleTableItem {

	private static final String SQL_TABLE_NAME = "countries";
	
	private static final String SQL_COLUMN_NAME = "country";
	
	/**
	 * @param con
	 */
	public Country(Connection con) {
		super(con, SQL_TABLE_NAME, SQL_COLUMN_NAME);
	}

}
