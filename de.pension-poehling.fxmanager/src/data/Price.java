/* 
 * Copyright 2014 Lukas Stratmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
