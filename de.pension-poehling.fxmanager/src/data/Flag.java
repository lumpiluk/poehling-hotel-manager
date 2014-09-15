/**
 * 
 */
package data;

import java.sql.Connection;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @author lumpiluk
 *
 */
public class Flag extends SimpleTableItem {

	private static final String SQL_TABLE_NAME = "flags";
	
	private static final String SQL_COLUMN_NAME = "name";
	
	private String dbString = "";
	
	/**
	 * @param con
	 */
	public Flag(Connection con) {
		super(con, SQL_TABLE_NAME, SQL_COLUMN_NAME);
		this.valueProperty().addListener((observable, oldValue, newValue) -> {
			dbString = "| " + newValue.toLowerCase() + " |";
		});
	}
	
	public String toDbString() {
		return dbString;
	}

}
