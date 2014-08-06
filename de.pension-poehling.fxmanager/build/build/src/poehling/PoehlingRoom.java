/**
 * 
 */
package poehling;

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
	@Override
	protected final String getCol(Cols col) {
		if (col == Cols.AREA) {
			return "Fläche"; 
		} else if (col == Cols.BALCONY) {
			return "Balkon";
		} else if (col == Cols.FLOOR) {
			return "Etage";
		} else if (col == Cols.INDEX) {
			return "Zimmer";
		} else if (col == Cols.LIFT) {
			return "Aufzug";
		} else if (col == Cols.NAME) {
			return "Zimmer";
		} else if (col == Cols.PHONE) {
			return "Telefondurchwahl";
		} else if (col == Cols.TYPE) {
			return "Typ";
		} else if (col == Cols.VIEW) {
			return "Aussicht";
		} else {
			return super.getCol(col);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTableName() {
		return "zimmer";
	}

	@Override
	public boolean fromDBatIndex(int id)
			throws NoSuchElementException, SQLException {
		return super.fromDBatIndex(id);
		//should already work with Java dynamic binding
	}
	
}
