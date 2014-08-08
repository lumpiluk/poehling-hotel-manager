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
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Displays an overview of the specified month with occupied markers for
 * each room where needed.
 * Can be CSS-styled using style class custom-calendar.<br />
 * Style class for header: cols-header<br />
 * 
 * @author lumpiluk
 *
 */
public class CustomCalendar extends Region {

	private Calendar monthToDisplay;
	
	private final GridPane grid;
	
	/**
	 * LinkedHashMap to quickly check which row in the GridPane a
	 * CalendarMarker has to go into.
	 */
	private HashMap<String, Integer> rows = 
			new HashMap<String, Integer>(30);
	
	/** List of labels for each row with indices as specified in rows. */
	private ArrayList<Label> rowLabels = new ArrayList<Label>(30);
	
	/** Row Strings as specified in constructor. */
	private Iterable<String> rowItems;
	
	private LinkedList<Label> headerLabels = new LinkedList<Label>();
	
	/** 
	 * Property to determine width of columns other than column 0.
	 * Can be set in CSS via -col-width. See also COL_WIDTH.
	 */
	private StyleableDoubleProperty colWidth = 
			new StyleableDoubleProperty(35d) {
		// cf. example in Javadoc of CssMetaData
		
		@Override
		public String getName() {
			return "colWidth";
		}
		
		@Override
		public Object getBean() {
			return CustomCalendar.this;
		}
		
		@Override
		public CssMetaData<CustomCalendar, Number> getCssMetaData() {
			return COL_WIDTH_META_DATA;
		}
	};
	
	/**
	 * Enables CSS styling of column width via -col-width.
	 */
	private static final CssMetaData<CustomCalendar, Number> COL_WIDTH_META_DATA =
			new CssMetaData<CustomCalendar, Number>("-col-width", 
					StyleConverter.getSizeConverter(), 15d) {

		@Override
		public boolean isSettable(CustomCalendar c) {
			return  c.colWidth == null || !c.colWidth.isBound();
		}

		@Override
		public StyleableProperty<Number> getStyleableProperty(
				CustomCalendar c) {
			return (StyleableProperty<Number>)c.colWidth;
		}
		
	};
	
	/**
	 * Enables CSS styling with custom properties
	 */
	private static final List<CssMetaData<? extends Styleable, ?>> cssMetaDataList; 
	
	static {
	 	List<CssMetaData<? extends Styleable, ?>> temp =
	 			new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
	 	temp.add(COL_WIDTH_META_DATA);
	 	cssMetaDataList = Collections.unmodifiableList(temp);
	 }
	
	/**
	 * Control for displaying a marker in a calendar.
	 * Intended to show over which time period a room has been booked.
	 * Can be styled with CSS via style class calendar-marker.
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
			/*Color cTop = new Color(0.3, 0.3, 0.3, 0.9);
			Color cBottom = new Color(0.35, 0.35, 0.35, 0.9);
			BackgroundFill bgFill = new BackgroundFill(
					new LinearGradient(0.0, 0.0, 0.0, 1.0, true, // TODO: correct values?
							CycleMethod.NO_CYCLE, 
							new Stop(0, cTop), new Stop(1, cBottom)),
					CornerRadii.EMPTY, Insets.EMPTY);
			this.setBackground(new Background(bgFill));*/ // now in css
			
			this.getStyleClass().add("calendar-marker");
			this.setPadding(new Insets(3.5, 5.0, 3.5, 5.0));
			this.setMinWidth(0.0);
			this.setMaxWidth(Double.MAX_VALUE);
		}
		
	}
	
	/**
	 * Constructor.
	 * @param rowItems 
	 * @throws InterruptedException 
	 */
	public CustomCalendar(Iterable<String> rowItems) {
		super();
		this.rowItems = rowItems;
		this.grid = new GridPane();
		this.getChildren().add(grid);
		this.getStyleClass().add("custom-calendar");
		monthToDisplay = new GregorianCalendar();
		makeRowsHeader();
		//makeDaysHeader();
		updateView();
		
		colWidth.addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue observable, Object oldValue,
					Object newValue) {
				makeDaysHeader();				
			}	
		});
	}
	
	 public static List <CssMetaData <? extends Styleable, ? > > getClassCssMetaData() {
		 return cssMetaDataList;
	 }
	 
	 /* @Override
	 public List <CssMetaData <? extends Styleable, ? > > getCssMetaData() {
	     return getClassCssMetaData();
	 }*/
	
	/**
	 * Sets up the header row and sets column constraints for the GridPane.
	 * Column Constraints define the width of each column (hopefully).
	 * TODO: find out if they do ;)
	 * @throws InterruptedException 
	 */
	private void makeDaysHeader() {
		int daysInMonth = monthToDisplay.getActualMaximum(
				Calendar.DAY_OF_MONTH);
		
		headerLabels.clear();
		grid.getColumnConstraints().clear();
		grid.getColumnConstraints().add(new ColumnConstraints()); // no fixed width for header column
		
		ColumnConstraints widthConstraint = new ColumnConstraints(
				colWidth.getValue());
		
		//new Calendar instance to determine weekdays
		Calendar tmpCal = (Calendar)monthToDisplay.clone();
		tmpCal.set(Calendar.DAY_OF_MONTH,
				tmpCal.getActualMinimum(Calendar.DAY_OF_MONTH));
		
		for (int i = 1; i <= daysInMonth; i++) {
			// create header text and label
			String colHeader = Messages.getShortDayOfWeek(
					tmpCal.get(Calendar.DAY_OF_WEEK));
			colHeader += "\n" + i;
			tmpCal.set(Calendar.DAY_OF_MONTH,
					tmpCal.get(Calendar.DAY_OF_MONTH) + 1); // increase day of month

			Label l = new Label(colHeader);
			l.getStyleClass().add("cols-header");
			headerLabels.add(l);
			grid.add(l, i, 0);
			
			// apply constraint
			grid.getColumnConstraints().add(widthConstraint);
		}
	}
	
	private void makeRowsHeader() {
		int i = 0;
		for (String rowString : rowItems) {
			rows.put(rowString, i);
			Label l = new Label(rowString);
			l.getStyleClass().add("rows-header");
			rowLabels.add(l);
			grid.add(l, 0, i + 1);
			i++;
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
	private boolean monthInRange(final Calendar start, final Calendar end) { // TODO FIXME: consider hours, minutes, seconds?
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
