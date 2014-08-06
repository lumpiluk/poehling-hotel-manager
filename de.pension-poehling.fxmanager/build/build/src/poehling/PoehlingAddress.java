/**
 * 
 */
package poehling;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

import data.Address;
import data.HotelData;

/**
 * @author lumpiluk
 *
 */
public class PoehlingAddress extends Address {

	public PoehlingAddress(Connection con) {
		super(con);
	}
	
	protected final String getCol(Cols col) { //TODO: adapt!
		if (col == Cols.ADDED) { return "added"; }
		else if (col == Cols.ADDITION) { return "addition"; }
		else if (col == Cols.CELLPHONE) { return "cellphone"; }
		else if (col == Cols.CHILDREN) { return "children"; }
		else if (col == Cols.CREATED) { return "created"; }
		else if (col == Cols.CREDITOR) { return "creditor"; }
		else if (col == Cols.DEBITOR) { return "debitor"; }
		else if (col == Cols.DECEASED) { return "deceased"; }
		else if (col == Cols.EMAIL) { return "email"; }
		else if (col == Cols.FAX) { return "fax"; }
		else if (col == Cols.FIRST_NAMES) { return "first_names"; }
		else if (col == Cols.FOOD_MEMO) { return "food_memo"; }
		else if (col == Cols.INDEX) { return "id"; }
		else if (col == Cols.MEMO) { return "memo"; }
		else if (col == Cols.OTHER) { return "other"; }
		else if (col == Cols.PEOPLE) { return "people"; }
		else if (col == Cols.PHONE) { return "phone"; }
		else if (col == Cols.POSTBOX) { return "postbox"; }
		else if (col == Cols.POSTBOX_TOWN) { return "postbox_town"; }
		else if (col == Cols.POSTBOX_ZIP) { return "postbox_zip"; }
		else if (col == Cols.PRIVATE) { return "private"; }
		else if (col == Cols.SHORT_COUNTRY) { return "short_country"; }
		else if (col == Cols.STATE) { return "state"; }
		else if (col == Cols.STREET) { return "street"; }
		else if (col == Cols.SURNAMES) { return "surnames"; }
		else if (col == Cols.TITLE) { return "title"; }
		else if (col == Cols.TOWN) { return "town"; }
		else if (col == Cols.WEBSITE) { return "website"; }
		else if (col == Cols.XMAS_LETTER) { return "letter"; }
		else if (col == Cols.ZIP_CODE) { return "zip"; }
		else { throw new NoSuchElementException(); }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getTableName() {
		return "adressen";
	}

	/* (non-Javadoc)
	 * @see data.Address#fromDbAtIndex(long)
	 */
	@Override
	public boolean fromDbAtIndex(long id) throws NoSuchElementException,
			SQLException {
		// TODO Auto-generated method stub
		return super.fromDbAtIndex(id);
	}

	/* (non-Javadoc)
	 * @see data.Address#updateDb()
	 */
	@Override
	public void updateDb() throws SQLException, NullPointerException {
		// TODO Auto-generated method stub
		super.updateDb();
	}

	/**
	 * Inserts row into database as in regular Address.
	 * Make sure to use a different connection before calling this!
	 * {@inheritDoc}
	 */
	@Override
	public void insertIntoDb() throws SQLException {
		super.insertIntoDb();
	}

	/**
	 * Creates tables as in regular Address.
	 * Make sure to use a different connection before calling this!
	 * {@inheritDoc}
	 */
	@Override
	public void createTables() throws SQLException {
		super.createTables();
	}

	/* (non-Javadoc)
	 * @see data.HotelData#getBatch(java.util.List)
	 */
	@Override
	public Iterable<? extends HotelData> getBatch(List<Long> indices)
			throws SQLException {
		// TODO Auto-generated method stub
		return super.getBatch(indices);
	}
	
	
	
	

}
