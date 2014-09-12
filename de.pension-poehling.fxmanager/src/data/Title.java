/**
 * 
 */
package data;

import java.sql.Connection;

/**
 * @author lumpiluk
 *
 */
public class Title extends SimpleTableItem {

	private static final String SQL_TABLE_NAME = "titles";
	
	private static final String SQL_COLUMN_NAME = "title";
	
	/**
	 * @param con
	 */
	public Title(Connection con) {
		super(con, SQL_TABLE_NAME, SQL_COLUMN_NAME);
	}

}
