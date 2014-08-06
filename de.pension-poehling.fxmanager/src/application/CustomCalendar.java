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
package application;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import data.Room;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Displays an overview of the specified month with occupied markers for
 * each room where needed.
 * @author lumpiluk
 *
 */
public class CustomCalendar extends GridPane {

	private Calendar monthToDisplay;
	
	private Iterable<String> rows; // TODO: more general than Room?
	
	/**
	 * Constructor.
	 * @param rowItems 
	 */
	public CustomCalendar(Iterable<String> rowItems) {
		monthToDisplay = new GregorianCalendar();
		rows = rowItems;
		makeDaysHeader();
		updateView();
	}
	
	/**
	 * Sets up the header row
	 */
	private void makeDaysHeader() {
		int daysInMonth = monthToDisplay.getActualMaximum(
				Calendar.DAY_OF_MONTH);
		for (int i = 1; i <= daysInMonth; i++) {
			Label l = new Label(String.valueOf(i));
			this.add(l, i, 0);
		}
	}
	
	/**
	 * Sets the month to be displayed by this calendar pane.
	 * 
	 * @param m Month to display, must be between 0 and 11 (cf. Calendar.MONTH)
	 */
	public void setMonth(int m) {
		monthToDisplay.set(Calendar.MONTH, m);
	}
	
	private void updateView() {
		// remove obsolete markers
		Iterable<Node> children = this.getChildren();
		for (Node child : children) {
			// TODO if child is marker and not some kind of design element then remove...
		}
		// TODO add markers for current month (threaded loading from db?)
	}
	
}
