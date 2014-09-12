/**
 * 
 */
package data;

import java.sql.Connection;

/**
 * @author lumpiluk
 *
 */
public class State extends SimpleTableItem {

	private static final String SQL_TABLE_NAME = "states";
	
	private static final String SQL_COLUMN_NAME = "state";
	
	/**
	 * @param con
	 */
	public State(Connection con) {
		super(con, SQL_TABLE_NAME, SQL_COLUMN_NAME);
	}

}
