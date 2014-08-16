/**
 * 
 */
package dbimport.poehling;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import data.Room;

/**
 * @author lumpiluk
 *
 */
public class PoehlingRoom extends Room {

	
	
	public PoehlingRoom(Connection con) {
		super(con);
	}

	/**
	 * {@inheritDoc}
	 */
	/*@Override
	protected String getTableName() {
		return "zimmer";
	}*/

	//@Override
	/*public boolean fromDBatIndex(int id)
			throws NoSuchElementException, SQLException {
		return super.fromDBatIndex(id);
		//should already work with Java dynamic binding
	}*/
	
}
