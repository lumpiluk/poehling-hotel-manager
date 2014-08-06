/**
 * 
 */
package data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * Manages invoices.
 * @author lumpiluk
 *
 */
public class Invoice extends HotelData {

	/**
	 * Columns in this object's table.
	 * Use getCol(Cols col) to get the respective column name.
	 * @author lumpiluk
	 *
	 */
	protected static enum Cols {
		INDEX, ORDER, ADDRESS, INDIVIDUAL_PRICE_GROSS;
	}
	
	/** invoice ID. */
	private long id;
	
	/** Index of the associated order. */
	private int orderId;
	
	/** links to the order to which this invoice belongs. */
	private Order order;
	
	/**
	 * 
	 */
	public Invoice(final Connection con) {
		super(con);
	}

	@Override
	public boolean fromDbAtIndex(long id) throws NoSuchElementException,
			SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateDb() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertIntoDb() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createTables() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
