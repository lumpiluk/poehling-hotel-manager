/**
 * 
 */
package data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * Manages an order to collect a number of invoices.
 * @author lumpiluk
 *
 */
public class Order extends HotelData {
	
	public Order(Connection con) {
		super(con);
		// TODO Auto-generated constructor stub
	}

	private ArrayList<Invoice> invoices;

	@Override
	public void updateDb() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createTables() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean fromDbAtIndex(long id) throws NoSuchElementException,
			SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void insertIntoDb() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
}
