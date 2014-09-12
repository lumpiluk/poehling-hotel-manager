/**
 * 
 */
package data;

import java.sql.Connection;

/**
 * @author lumpiluk
 *
 */
public class Flag extends SimpleTableItem {

	private static final String SQL_TABLE_NAME = "flags";
	
	private static final String SQL_COLUMN_NAME = "name";
	
	/**
	 * @param con
	 */
	public Flag(Connection con) {
		super(con, SQL_TABLE_NAME, SQL_COLUMN_NAME);
	}

}
