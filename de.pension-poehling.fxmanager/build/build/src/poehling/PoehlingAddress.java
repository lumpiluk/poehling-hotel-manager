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
