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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Displays an overview of the specified month with occupied markers for
 * each room where needed.
 * @author lumpiluk
 *
 */
public class CustomCalendar extends GridPane {

	private Calendar monthToDisplay;
	
	/**
	 * LinkedHashMap to quickly check which row in the GridPane a
	 * CalendarMarker has to go into.
	 */
	private HashMap<String, Integer> rows = 
			new HashMap<String, Integer>(30);
	
	/** List of labels for each row with indices as specified in rows. **/
	private ArrayList<Label> rowLabels = new ArrayList<Label>(30);
	
	private LinkedList<Label> headerLabels = new LinkedList<Label>();
	
	/**
	 * Control for displaying a marker in a calendar.
	 * Intended to show over which time period a room has been booked.
	 * @author lumpiluk
	 *
	 */
	public class CalendarMarker extends Label {
		
		public CalendarMarker() {
			this("", null);
		}

		public CalendarMarker(String text) {
			this(text, null);
		}
		
		public CalendarMarker(String text, Node graphic) {
			super(text, graphic);
			
			// set background
			Color cTop = new Color(0.3, 0.3, 0.3, 0.9);
			Color cBottom = new Color(0.35, 0.35, 0.35, 0.9);
			BackgroundFill bgFill = new BackgroundFill(
					new LinearGradient(0.0, 0.0, 0.0, 1.0, true, // TODO: correct values?
							CycleMethod.NO_CYCLE, 
							new Stop(0, cTop), new Stop(1, cBottom)),
					CornerRadii.EMPTY, Insets.EMPTY);
			this.setBackground(new Background(bgFill));
			
			this.setPadding(new Insets(3.5, 5.0, 3.5, 5.0));
			this.setMinWidth(0.0);
			this.setMaxWidth(Double.MAX_VALUE);
			// TODO: implement color change on hover in css
		}
		
	}
	
	/**
	 * Constructor.
	 * @param rowItems 
	 */
	public CustomCalendar(Iterable<String> rowItems) {
		monthToDisplay = new GregorianCalendar();
		int i = 0;
		for (String rowString : rowItems) {
			rows.put(rowString, i);
			rowLabels.add(new Label(rowString));
			i++;
		}
		makeDaysHeader();
		updateView();
	}
	
	/**
	 * Sets up the header row and column constraints for the GridPane.
	 * Column Constraints define the width of each column (hopefully).
	 * TODO: find out if they do ;)
	 */
	private void makeDaysHeader() {
		int daysInMonth = monthToDisplay.getActualMaximum(
				Calendar.DAY_OF_MONTH);
		
		headerLabels.clear();
		for (int i = 1; i <= daysInMonth; i++) {
			headerLabels.add(new Label(String.valueOf(i)));
			this.add(headerLabels.getLast(), i, 0);
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
	
	/**
	 * Checks whether the currently displayed month is within the specified
	 * date range. This method is used in placeMarker(...).
	 * @param start
	 * @param end
	 * @return true if current month is within range
	 */
	private boolean monthInRange(final Calendar start, final Calendar end) {
		Calendar firstDay = (Calendar)monthToDisplay.clone();
		Calendar lastDay = (Calendar)monthToDisplay.clone();
		firstDay.set(Calendar.DAY_OF_MONTH, 
				firstDay.getActualMinimum(Calendar.DAY_OF_MONTH));
		lastDay.set(Calendar.DAY_OF_MONTH,
				lastDay.getActualMinimum(Calendar.DAY_OF_MONTH));
		return (start.after(lastDay) && end.after(lastDay)) ||
				(start.before(firstDay) && end.before(lastDay));
	}
	
	/**
	 * Places a marker for the specified date range.
	 * @param start Beginning of date range shown by new marker.
	 * @param end End of date range shown by new marker.
	 * @throws IllegalArgumentException if date range from start to end does
	 * not overlap with the currently displayed month.
	 */
	public void placeMarker(final Calendar start, final Calendar end)
			throws IllegalArgumentException {
		if (monthInRange(start, end)) {
			throw new IllegalArgumentException();
		}
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
