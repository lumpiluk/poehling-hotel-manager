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
package application.customControls;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import util.DateComparator;
import util.Messages;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * Displays an overview of the specified month with occupied markers for
 * each room where needed.
 * Can be CSS-styled using style class custom-calendar.<br />
 * Style class for header: cols-header<br />
 * 
 * @author lumpiluk
 */
public class CustomCalendar extends Control {

	private static final double DEFAULT_COLUMN_WIDTH = 35d;
	
	private Calendar monthToDisplay;
	
	private final GridPane grid;
	
	/** List of labels for each row with indices as specified in rows. */
	private ArrayList<Label> rowLabels = new ArrayList<Label>(30); // TODO: make property (also for headers etc)
	
	/** Row Strings as specified in constructor. */
	private List<String> rowItems;
	
	private LinkedList<Label> headerLabels = new LinkedList<Label>();
	
	private ArrayList<ArrayList<Pane>> backgroundPanes = new
			ArrayList<ArrayList<Pane>>(10);
	
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
		
		private ObjectProperty<Calendar> start;
		private ObjectProperty<Calendar> end;
		private IntegerProperty rowIndex;
		
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
			this.rowIndex = new SimpleIntegerProperty(rowIndex);
			
			this.getStyleClass().add("calendar-marker");
			this.setPadding(new Insets(3.5, 5.0, 3.5, 5.0));
			this.setMinWidth(0.0);
			this.setMaxWidth(Double.MAX_VALUE);
			
			this.start = new SimpleObjectProperty<Calendar>(start);
			this.end = new SimpleObjectProperty<Calendar>(end);
			setPropertyListeners();
			dateRangeChanged();
			installTooltip();
		}
		
		private void dateRangeChanged() {
			if (!DateComparator.monthInRange(monthToDisplay, getStart(), getEnd())) {
				throw new IllegalArgumentException();
			}
			grid.getChildren().remove(this); // remove marker from grid if possible
			
			int startCol = getColIndexForDayOfMonth(getStart());
			int endCol = getColIndexForDayOfMonth(getEnd());
			
			// define margins
			double leftMargin = 0d, rightMargin = 0d;
			if (!DateComparator.monthAfter(monthToDisplay, getEnd())) {
				rightMargin = getColumnWidth() / 2d;
			}
			if (!DateComparator.monthBefore(monthToDisplay, getStart())) {
				leftMargin = getColumnWidth() / 2d;
			}
			Insets margin = new Insets(0d, rightMargin, 1d, leftMargin);
			
			//add to grid
			grid.add(this, startCol, rowIndex.get());
			GridPane.setColumnSpan(this, endCol - startCol + 1);
			GridPane.setMargin(this, margin);
		}
		
		private void setPropertyListeners() {
			
			rowIndex.addListener(
					(ChangeListener<Number>) (observable, oldValue, newValue) -> {
				if ((Integer)newValue < 1 ||
						(Integer)newValue > rowItems.size()) {
					throw new IllegalArgumentException();
				}
				GridPane.setRowIndex(CalendarMarker.this, (Integer)newValue);
			});
			
			start.addListener(
					(ChangeListener<Calendar>) (observable, oldValue, newValue) -> {
				dateRangeChanged();
			});
			
			end.addListener(
					(ChangeListener<Calendar>) (observable, oldValue, newValue) -> {
				dateRangeChanged();
			});
			
		}
		
		// getters and setter for row index
		public int getRowIndex() {
			return this.rowIndex.get();
		}
		
		public void setRowIndex(final int value) {
			this.rowIndex.set(value);
		}
		
		public IntegerProperty rowIndexProperty() {
			return rowIndex;
		}
		
		// getters and setter for start date
		public Calendar getStart() {
			return start.get();
		}
		
		public void setStart(final Calendar value) {
			start.set(value);
		}
		
		public ObjectProperty<Calendar> startProperty() {
			return start;
		}
		
		// getters and setter for end date
		public Calendar getEnd() {
			return end.get();
		}
		
		public void setEnd(final Calendar value) {
			end.set(value);
		}
		
		public ObjectProperty<Calendar> endProperty() {
			return end;
		}
		
		private void installTooltip() {
			Tooltip t = new Tooltip(this.getText());
			Tooltip.install(this, t);
		}
		
		public void removeFromGrid() {
			grid.getChildren().remove(this);
		}
		
		// privatized constructors from Label; don't need these
		@SuppressWarnings("unused")
		private CalendarMarker() { this("", null);	}
		@SuppressWarnings("unused")
		private CalendarMarker(String t) { this(t, null); }
		private CalendarMarker(String t, Node g) { super(t, g);	}
		
	}
	
	/**
	 * Constructor.
	 * @param rowItems 
	 * @throws InterruptedException 
	 */
	public CustomCalendar(List<String> rowItems) {
		super();
		this.rowItems = rowItems;
		this.grid = new GridPane();
		this.getChildren().add(grid);
		this.getStyleClass().setAll("custom-calendar");
		setMonth(new GregorianCalendar());
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
	
	private void makeBackgroundRects() {
		int daysInMonth = monthToDisplay.getActualMaximum(
				Calendar.DAY_OF_MONTH);
		// remove old panes if existing
		for (ArrayList<Pane> r : backgroundPanes) {
			for (Pane p : r) {
				grid.getChildren().remove(p);
			}
		}
		backgroundPanes.clear();
		
		for (int r = 0; r <= rowItems.size(); r++) {
			ArrayList<Pane> panesRow = new ArrayList<Pane>(32);
			backgroundPanes.add(panesRow);
			
			//new Calendar instance to determine weekdays
			Calendar tmpCal = (Calendar)monthToDisplay.clone();
			tmpCal.set(Calendar.DAY_OF_MONTH,
					tmpCal.getActualMinimum(Calendar.DAY_OF_MONTH));
			
			for (int c = 0; c <= daysInMonth; c++) {
				Pane p = new Pane();
				p.setMouseTransparent(true);
				p.setCache(false);
				
				// add CSS style classes
				p.getStyleClass().add("bg");
				
				if (r == 0) { p.getStyleClass().add("bg-col-header"); }
				if (c == 0) { p.getStyleClass().add("bg-row-header"); }
				
				if (r % 2 == 0 && r != 0 && c != 0) {
					p.getStyleClass().add("bg-odd-row");
				} else if (r != 0 && c != 0) {
					p.getStyleClass().add("bg-even-row");
				}
				
				if (c > 0) {
					p.getStyleClass().add("bg-weekday-"	+ getCssDayOfWeek(
							tmpCal.get(Calendar.DAY_OF_WEEK)));
					if (DateComparator.isToday(tmpCal)) {
						p.getStyleClass().add("bg-today");
					}
				}
				
				// add cell background pane to row list and to the grid
				panesRow.add(p);
				grid.add(p, c, r);
				
				if (c > 0) {
					tmpCal.add(Calendar.DATE, 1); // increase day of month
				}
			}
		}
	}
	
	/**
	 * Sets up the header row and sets column constraints for the GridPane.
	 * Column Constraints define the width of each column.
	 * @throws InterruptedException 
	 */
	private void makeDaysHeader() {
		int daysInMonth = monthToDisplay.getActualMaximum(
				Calendar.DAY_OF_MONTH);
		
		for (Label l : headerLabels) {
			grid.getChildren().remove(l);
		}
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
			l.getStyleClass().add("header-weekday-"
					+ getCssDayOfWeek(tmpCal.get(Calendar.DAY_OF_WEEK)));
			l.setMaxWidth(Double.MAX_VALUE);
			l.setAlignment(Pos.TOP_CENTER);
			headerLabels.add(l);
			grid.add(l, i, 0);
			
			// apply constraint
			grid.getColumnConstraints().add(widthConstraint);
			
			tmpCal.add(Calendar.DATE, 1); // increase day of month
		}
	}
	
	private void makeRowsHeader() {
		for (Label l : rowLabels) {
			grid.getChildren().remove(l);
		}
		rowLabels.clear();
		
		int i = 0;
		for (String rowString : rowItems) {
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
	public void setMonth(Calendar m) {
		monthToDisplay = m;
		makeBackgroundRects();
		makeRowsHeader();
		makeDaysHeader();
		updateView();
	}
	
	/**
	 * Places a marker for the specified date range.
	 * @param start Beginning of date range shown by new marker.
	 * @param end End of date range shown by new marker.
	 * @param rowIndex
	 * @param text
	 * @return TODO
	 * @throws IllegalArgumentException if date range from start to end does
	 * not overlap with the currently displayed month.
	 */
	public CalendarMarker createMarker(String text, int rowIndex,
			final Calendar start, final Calendar end)
			throws IllegalArgumentException, NullPointerException {
		
		if (!DateComparator.monthInRange(monthToDisplay, start, end)) {
			throw new IllegalArgumentException();
		}
		return new CalendarMarker(text, start, end,
			rowIndex);
	}
	
	private void updateView() {
		// remove obsolete markers
		//Iterable<Node> children = this.getChildren();
		//for (Node child : children) {
			// TODO if child is marker and not some kind of design element then remove...
		//}
		// TODO add markers for current month (threaded loading from db?)
	}
	
}
