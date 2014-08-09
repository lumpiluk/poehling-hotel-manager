package util;

import java.util.Calendar;

public class DateComparator {

	/**
	 * @param a date to compare to
	 * @param b date to compare
	 * @return true if b is in the next day after a or later
	 */
	public static final boolean dayAfter(Calendar a, Calendar b) {
		int monthA = a.get(Calendar.MONTH);
		int monthB = b.get(Calendar.MONTH);
		int yearA = a.get(Calendar.YEAR);
		int yearB = b.get(Calendar.YEAR);
		int dayA = a.get(Calendar.DAY_OF_MONTH);
		int dayB = b.get(Calendar.DAY_OF_MONTH);
		return yearB > yearA || (yearB == yearA && monthB > monthA) ||
				(yearB == yearA && monthB == monthA && dayB > dayA);
	}
	
	/**
	 * @param a date to compare to
	 * @param b date to compare
	 * @return true if b is in the next month after a or later
	 */
	public static final boolean monthAfter(Calendar a, Calendar b) {
		int monthA = a.get(Calendar.MONTH);
		int monthB = b.get(Calendar.MONTH);
		int yearA = a.get(Calendar.YEAR);
		int yearB = b.get(Calendar.YEAR);
		return yearB > yearA || (yearB == yearA && monthB > monthA);
	}
	
	/**
	 * @param a date to compare to
	 * @param b date to compare
	 * @return true if b is in the day before a or earlier
	 */
	public static final boolean dayBefore(Calendar a, Calendar b) {
		return dayAfter(b, a);
	}
	
	/**
	 * @param a date to compare to
	 * @param b date to compare
	 * @return true if b is in the month before a or earlier
	 */
	public static final boolean monthBefore(Calendar a, Calendar b) {
		return monthAfter(b, a);
	}
	
	/**
	 * Checks whether the currently displayed month is within the specified
	 * date range. This method is used in placeMarker(...).
	 * @param start
	 * @param end
	 * @return true if current month is within range
	 */
	public static final boolean monthInRange(
			final Calendar check, final Calendar start, final Calendar end) {
		Calendar firstDay = (Calendar)check.clone();
		Calendar lastDay = (Calendar)check.clone();
		firstDay.set(Calendar.DAY_OF_MONTH, 
				firstDay.getActualMinimum(Calendar.DAY_OF_MONTH));
		lastDay.set(Calendar.DAY_OF_MONTH,
				lastDay.getActualMinimum(Calendar.DAY_OF_MONTH));
		return (dayAfter(lastDay, start) && dayAfter(lastDay, end)) ||
				(dayBefore(firstDay, start) && dayBefore(lastDay, end));
	}
	
}
