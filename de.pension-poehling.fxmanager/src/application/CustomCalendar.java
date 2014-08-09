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

import util.DateComparator;
import javafx.beans.property.DoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * Displays an overview of the specified month with occupied markers for
 * each room where needed.
 * Can be CSS-styled using style class custom-calendar.<br />
 * Style class for header: cols-header<br />
 * 
 * @author lumpiluk
 * @param <M> should be an Object implementing the toString() method.
 * Will be used to tell clicked markers apart. In this case a Booking will be
 * used.
 */
public class CustomCalendar<M> extends Control {

	private static final double DEFAULT_COLUMN_WIDTH = 35d;
	
	private Calendar monthToDisplay;
	
	private final GridPane grid;
	
	/**
	 * LinkedHashMap to quickly check which row in the GridPane a
	 * CalendarMarker has to go into.
	 */
	private HashMap<String, Integer> rows = 
			new HashMap<String, Integer>(30);
	
	/** List of labels for each row with indices as specified in rows. */
	private ArrayList<Label> rowLabels = new ArrayList<Label>(30); // TODO: make property (also for headers etc)
	
	/** Row Strings as specified in constructor. */
	private Iterable<String> rowItems;
	
	private LinkedList<Label> headerLabels = new LinkedList<Label>();
	
	// Styleable CSS Properties ***********************************************
	// (would've never gotten this to work without the help of this source file: https://github.com/HanSolo/JFX8CustomControls/blob/master/src/jfx8controls/csspseudoclass/MyCtrl.java)
	/** 
	 * Property to determine width of columns other than column 0.
	 * Can be set in CSS via -col-width. See also COL_WIDTH.
	 */
	private StyleableDoubleProperty colWidth;
	
	public final double getColumnWidth() {
		return columnWidthProperty().get();
	}
	
	public final void setColumnWidth(double value) {
		columnWidthProperty().set(value);
	}
	
	public final DoubleProperty columnWidthProperty() {
		if (colWidth == null) {
			colWidth = new StyleableDoubleProperty(DEFAULT_COLUMN_WIDTH) {
				@Override 
				public String getName() {	return "colWidth"; }
				
				@Override 
				public Object getBean() {	return CustomCalendar.this;	}
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				@Override 
				public CssMetaData getCssMetaData() { 
					return StyleableProperties.COL_WIDTH_META_DATA;
				}
				
				@Override
				public void invalidated() {
					makeDaysHeader();
				}
			};
		}
		return colWidth;
	}
	
	// CSS Meta Data **********************************************************
	private static class StyleableProperties {
		
		private static final CssMetaData<CustomCalendar, Number> COL_WIDTH_META_DATA =
				new CssMetaData<CustomCalendar, Number>("-col-width", 
						StyleConverter.getSizeConverter(), DEFAULT_COLUMN_WIDTH) {

			@Override
			public boolean isSettable(CustomCalendar c) {
				return  c.colWidth == null || !c.colWidth.isBound();
			}

			@Override
			public StyleableProperty<Number> getStyleableProperty(
					CustomCalendar c) {
				return (StyleableProperty<Number>)c.colWidth;
			}
			
			@Override
			public Number getInitialValue(CustomCalendar c) {
				return c.getColumnWidth();
			}
			
		};
		
		private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES; 
		
		static {
		 	List<CssMetaData<? extends Styleable, ?>> temp =
		 			new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
		 	temp.add(COL_WIDTH_META_DATA);
		 	STYLEABLES = Collections.unmodifiableList(temp);
		}
	}
	
	public static List <CssMetaData <? extends Styleable, ? > > getClassCssMetaData() {
		return StyleableProperties.STYLEABLES;
	}
	 
	@Override
	public List <CssMetaData <? extends Styleable, ? > > getControlCssMetaData() {
		return getClassCssMetaData();
	}
	
	
	/**
	 * Control for displaying a marker in a calendar.
	 * Intended to show over which time period a room has been booked.
	 * Can be styled with CSS via style class calendar-marker.
	 * @author lumpiluk
	 *
	 */
	public class CalendarMarker extends Label {
		
		private Calendar start = monthToDisplay;
		private Calendar end = monthToDisplay;
		private M comparator; // TODO: use this or sth else for click event or not at all...
		
		private String text; // TODO: property?
		
		/**
		 * Constructor.
		 * @param comparator should be an Object implementing the toString()
		 * method. Will be used to tell clicked markers apart. In this case a
		 * Booking will be used. Null is not allowed.
		 * @param start
		 * @param end
		 * @throws IllegalArgumentException
		 */
		public CalendarMarker(String text, Calendar start, Calendar end,
				int rowIndex) throws IllegalArgumentException {
			this(text, null);
			this.text = text;
			
			if (!DateComparator.monthInRange(monthToDisplay, start, end)) {
				throw new IllegalArgumentException();
			}
			
			this.getStyleClass().add("calendar-marker");
			this.setPadding(new Insets(3.5, 5.0, 3.5, 5.0));
			this.setMinWidth(0.0);
			this.setMaxWidth(Double.MAX_VALUE);
			
			int startCol = getColIndexForDayOfMonth(start);
			int endCol = getColIndexForDayOfMonth(end);
			
			// define margins
			double leftMargin = 0d, rightMargin = 0d;
			if (!DateComparator.monthAfter(monthToDisplay, end)) {
				rightMargin = getColumnWidth() / 2d;
			}
			if (!DateComparator.monthBefore(monthToDisplay, start)) {
				leftMargin = getColumnWidth() / 2d;
			}
			Insets margin = new Insets(0d, rightMargin, 0.0, leftMargin);
			
			
			grid.add(this, startCol, rowIndex);
			GridPane.setColumnSpan(this, endCol - startCol + 1);
			GridPane.setMargin(this, margin);
			installTooltip();
		}
		
		private void installTooltip() {
			Tooltip t = new Tooltip(text);
			Tooltip.install(this, t);
		}
		
		@SuppressWarnings("unused")
		private CalendarMarker() {
			this("", null);
		}

		@SuppressWarnings("unused")
		private CalendarMarker(String text) {
			this(text, null);
		}
		
		private CalendarMarker(String text, Node graphic) {
			super(text, graphic);
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
		this.getStyleClass().setAll("custom-calendar");
		monthToDisplay = new GregorianCalendar();
		makeRowsHeader();
		makeDaysHeader();
		updateView();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Skin createDefaultSkin() {
		return new CustomCalendarSkin(this);
	}
	
	/**
	 * Return the path to the CSS file so things are set up right
	 * @see javafx.scene.control.Control#getUserAgentStylesheet()
	 */
	@Override
	protected String getUserAgentStylesheet() {
		return getClass().getResource("/CustomCalendar.css").toExternalForm();
	}
	
	/**
	 * @param day
	 * @return the index of the grid column for the specified day.
	 */
	protected final int getColIndexForDayOfMonth(Calendar day) {
		if (DateComparator.monthAfter(monthToDisplay, day)) {
			return monthToDisplay.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		if (DateComparator.monthBefore(monthToDisplay, day)) {
			return monthToDisplay.getActualMinimum(Calendar.DAY_OF_MONTH); // hopefully 1 for column 1, not 0
		}
		return day.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Used to enable styling of e.g. headers for a particular day of the week.
	 * E.g. via class .weekday-sunday -> make text red
	 * @param dayOfWeek
	 * @return
	 * @throws IllegalArgumentException
	 */
	private static String getCssDayOfWeek(int dayOfWeek) 
			throws IllegalArgumentException {
		switch (dayOfWeek) {
		case Calendar.MONDAY: return "monday";
		case Calendar.TUESDAY: return "tuesday";
		case Calendar.WEDNESDAY: return "wednesday";
		case Calendar.THURSDAY: return "thursday";
		case Calendar.FRIDAY: return "friday";
		case Calendar.SATURDAY: return "saturday";
		case Calendar.SUNDAY: return "sunday";
		default: throw new IllegalArgumentException();			
		}
	}
	
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
				getColumnWidth());
		
		//new Calendar instance to determine weekdays
		Calendar tmpCal = (Calendar)monthToDisplay.clone();
		tmpCal.set(Calendar.DAY_OF_MONTH,
				tmpCal.getActualMinimum(Calendar.DAY_OF_MONTH));
		
		for (int i = 1; i <= daysInMonth; i++) {
			// create header text and label
			String colHeader = Messages.getShortDayOfWeek(
					tmpCal.get(Calendar.DAY_OF_WEEK));
			colHeader += "\n" + i; 
			

			Label l = new Label(colHeader);
			l.getStyleClass().add("cols-header");
			l.getStyleClass().add("weekday-"
					+ getCssDayOfWeek(tmpCal.get(Calendar.DAY_OF_WEEK)));
			headerLabels.add(l);
			grid.add(l, i, 0);
			
			// apply constraint
			grid.getColumnConstraints().add(widthConstraint);
			
			tmpCal.add(Calendar.DATE, 1); // increase day of month
		}
	}
	
	private void makeRowsHeader() {
		int i = 0;
		for (String rowString : rowItems) {
			rows.put(rowString, i + 1);
			Label l = new Label(rowString);
			l.getStyleClass().add("rows-header");
			l.setMaxWidth(Double.MAX_VALUE);
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
	 * Places a marker for the specified date range.
	 * @param start Beginning of date range shown by new marker.
	 * @param end End of date range shown by new marker.
	 * @param rowIndex
	 * @param text
	 * @throws IllegalArgumentException if date range from start to end does
	 * not overlap with the currently displayed month.
	 */
	public void placeMarker(String text, int rowIndex, final Calendar start,
			final Calendar end)	throws IllegalArgumentException,
			NullPointerException {
		if (!DateComparator.monthInRange(monthToDisplay, start, end)) {
			throw new IllegalArgumentException();
		}
		// TODO: make list!
		CalendarMarker m = new CalendarMarker(text, start, end,
			rowIndex);

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
