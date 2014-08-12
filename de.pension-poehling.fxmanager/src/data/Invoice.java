/**
 * 
 */
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * Manages invoices.
 * @author lumpiluk
 *
 */
public class Invoice extends HotelData {

	private static final String SQL_TABLE_NAME = "invoices";
	
	private static final String SQL_DELETE = "DELETE FROM " + SQL_TABLE_NAME
			+ " WHERE index = ?";
	
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
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteFromDb() throws SQLException {
		try(PreparedStatement stmt = con.prepareStatement(SQL_DELETE)) {
			stmt.setLong(1, getId());
		}
	}


	@Override
	public void createTables() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
