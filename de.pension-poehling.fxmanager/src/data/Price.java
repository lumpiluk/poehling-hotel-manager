/**
 * 
 */
package data;

/**
 * Stores a price with values for gross, net and taxes.
 * @author lumpiluk
 *
 */
public class Price {
	
	//TODO: fix redundancies
	
	private double gross, net, taxVal;
	
	/**
	 * 
	 */
	public Price() {
		setGross(0);
		setNet(0);
		setTaxVal(0);
	}
	
	/**
	 * 
	 * @param gross
	 * @param net
	 * @param taxVal
	 */
	public Price(final double gross, final double net, final double taxVal) {
		this.setGross(gross);
		this.setNet(net);
		this.setTaxVal(taxVal);
	}

	/**
	 * @return the gross value
	 */
	public double getGross() {
		return gross;
	}

	/**
	 * @param gross the gross value
	 */
	public void setGross(double gross) {
		this.gross = gross;
	}

	/**
	 * @return the net value
	 */
	public double getNet() {
		return net;
	}

	/**
	 * @param net the net value
	 */
	public void setNet(double net) {
		this.net = net;
	}

	/**
	 * @return the tax value (not in percent)
	 */
	public double getTaxVal() {
		return taxVal;
	}

	/**
	 * @param taxVal the tax value (not in percent)
	 */
	public void setTaxVal(double taxVal) {
		this.taxVal = taxVal;
	}

	/**
	 * 
	 * @return tax in percent, calculated with the quotient of taxVal and net
	 */
	public double getTaxPercent() {
		return getTaxVal() / getNet() * 100.0;
	}
	
}
