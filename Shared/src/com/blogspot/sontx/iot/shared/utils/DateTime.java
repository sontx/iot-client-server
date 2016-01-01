package com.blogspot.sontx.iot.shared.utils;

import java.io.Serializable;
import java.util.Calendar;

public class DateTime implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String[] M_MONTHS_NAME = { "Jan", "Feb", "Mar", 
													"Apr", "May", "Jun", 
													"Jul", "Aug", "Sep",
													"Oct", "Nov", "Dec" };
	private static final String[] M_MONTHS_FULL_NAME = { "January", "February", "March", 
														 "April", "May", "June", 
														 "July", "August", "September", 
														 "October", "November", "December" };
	private static final String[] M_WEEKS = { "1st week", "2nd week", "3rd week" };
	private static final byte[] M_DAYS_OF_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	
	/**
	 * @param day
	 *            day value from 1 to 28, 29, 30 or 31
	 * @param month
	 *            month value between 1 and 12
	 * @param year
	 *            year value greater or equal zero
	 * @return true if given date is valid
	 */
	public static boolean checkValidDate(int day, int month, int year) {
		if (year <= 0 || day <= 0 || month <= 0 || month > 12)
			return false;
		if (month == 2 && day == 29 && isLeap(year))
			return true;
		return M_DAYS_OF_MONTH[month - 1] >= day;
	}

	/**
	 * @param day
	 *            day value from 0 to 28, 29, 30 or 31
	 * @param month
	 *            month value between 1 and 12
	 * @param year
	 *            year value greater or equal zero
	 * @return day of week value from 1 for sunday to 7 for saturday
	 */
	public static int getDayOfWeek(int day, int month, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.YEAR, year);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * @param month
	 *            month value between 1 and 12
	 * @param year
	 *            year value greater or equal zero
	 * @return max day of @param month in @param year
	 */
	public static int getMaxDay(int month, int year) {
		int maxDay = M_DAYS_OF_MONTH[month - 1];
		if (month == 2 && isLeap(year))
			maxDay = 29;
		return maxDay;
	}

	/**
	 * @param month
	 *            month value between 1 and 12
	 * @return full month name
	 */
	public static String getMonthFullName(int month) {
		return M_MONTHS_FULL_NAME[month - 1];
	}

	/**
	 * @param month
	 *            month value between 1 and 12
	 * @return short month name
	 */
	public static String getMonthName(int month) {
		return M_MONTHS_NAME[month - 1];
	}

	/**
	 * @param week
	 *            week value greater zero
	 * @return week name
	 */
	public static String getWeekName(int week) {
		if (week > 3)
			return week + "th week";
		else
			return M_WEEKS[week - 1];
	}

	/**
	 * @param year
	 *            year value greater or equal zero
	 * @return true if @param year is leap year
	 */
	public static boolean isLeap(int year) {
		return (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0));
	}

	/**
	 * @return a DateTime instance of current date time
	 */
	public static DateTime now() {
		return parse(Calendar.getInstance());
	}

	public static DateTime parse(Calendar calendar) {
		DateTime dt = new DateTime();
		dt.mDay = calendar.get(Calendar.DAY_OF_MONTH);
		dt.mMonth = calendar.get(Calendar.MONTH) + 1;
		dt.mYear = calendar.get(Calendar.YEAR);
		dt.mHours = calendar.get(Calendar.HOUR_OF_DAY);
		dt.mMinutes = calendar.get(Calendar.MINUTE);
		dt.mSeconds = calendar.get(Calendar.SECOND);
		dt.mDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		return dt;
	}

	public static DateTime parse(int timeInSeconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInSeconds * 1000L);
		return parse(calendar);
	}

	/**
	 * @return system tick
	 */
	public static long tick() {
		return System.nanoTime();
	}

	private int mDay = 0;

	private int mMonth = 0;

	private int mYear = 0;

	private int mHours = 0;

	private int mMinutes = 0;

	private int mSeconds = 0;

	private int mDayOfWeek = 0;

	/**
	 * Create an instance of DateTime class with default date time value is zero
	 * for all time values and 25/14/1994 for date value
	 */
	public DateTime() {
		mDay = 25;
		mMonth = 12;
		mYear = 1994;
	}

	/**
	 * @param day
	 *            day value from 1 to 28, 29, 30 or 31
	 * @param month
	 *            month value between 1 and 12
	 * @param year
	 *            year value greater or equal zero
	 */
	public DateTime(int day, int month, int year) {
		mDay = day;
		mMonth = month;
		mYear = year;
		mDayOfWeek = getDayOfWeek(day, month, year);
		mHours = 0;
		mMinutes = 0;
		mSeconds = 0;
	}

	/**
	 * @param day
	 *            day value from 1 to 28, 29, 30 or 31
	 * @param month
	 *            month value between 1 and 12
	 * @param year
	 *            year value greater or equal zero
	 * @param hour
	 *            hour value between 0 and 23
	 * @param minute
	 *            minute value between 0 and 59
	 * @param second
	 *            second value between 0 and 59
	 */
	public DateTime(int day, int month, int year, int hour, int minute, int second) {
		mDay = day;
		mMonth = month;
		mYear = year;
		mDayOfWeek = getDayOfWeek(day, month, year);
		mHours = hour;
		mMinutes = minute;
		mSeconds = second;
	}

	/**
	 * @return day value between 1 and 31
	 */
	public int getDay() {
		return mDay;
	}

	/**
	 * @return day of week value. Note start day of week is sunday have value is
	 *         1
	 */
	public int getDayOfWeek() {
		return mDayOfWeek;
	}

	public String getEstimateDateTime() {
		DateTime now = DateTime.now();

		int deltaYears = now.mYear - mYear;
		int deltaMonths = now.mMonth - mMonth;
		int deltaDays = now.mDay - mDay;
		if (deltaYears == 0) {
			if (deltaMonths == 0) {
				if (deltaDays == 0) {
					int deltaHours = now.mHours - mHours;
					int deltaMinutes = now.mMinutes - mMinutes;
					int deltaSeconds = now.mSeconds - mSeconds;
					if (deltaHours == 0) {
						if (deltaMinutes == 0) {
							if (deltaSeconds <= 10)
								return "just now";
							else
								return String.format("just %d seconds", deltaSeconds);
						} else {
							return deltaMinutes == 1 ? "just a minute" : String.format("just %d minutes", deltaMinutes);
						}
					} else {
						return deltaHours == 1 ? "an hour ago" : String.format("%d hours ago", deltaHours);
					}
				} else {
					return deltaDays == 1 ? "yesterday" : String.format("%d days ago", deltaDays);
				}
			} else {
				return deltaMonths == 1 ? "a month ago" : String.format("%d months ago", deltaMonths);
			}
		} else {
			return deltaYears == 1 ? "a year ago" : String.format("%d years ago", deltaYears);
		}
	}

	/**
	 * @return hours value between 0 and 23
	 */
	public int getHours() {
		return mHours;
	}

	/**
	 * @return max day value of month. If month is 1, 3, 5, 7, 8, 10 and 12 then
	 *         max day is 31. If month is 4, 6, 9 and 11 then max day is 30. If
	 *         month is 2 and year is leap year then max day is 29 else max day
	 *         is 28
	 */
	public int getMaxDay() {
		return getMaxDay(mMonth, mYear);
	}

	/**
	 * @return minutes value between 0 and 59
	 */
	public int getMinutes() {
		return mMinutes;
	}

	/**
	 * @return month value between 1 and 12
	 */
	public int getMonth() {
		return mMonth;
	}

	/**
	 * @return seconds value between 0 and 59
	 */
	public int getSeconds() {
		return mSeconds;
	}

	/**
	 * @return year value greater or equal zero
	 */
	public int getYear() {
		return mYear;
	}

	/**
	 * @return true if year is leap year
	 */
	public boolean isLeap() {
		return isLeap(mYear);
	}

	/**
	 * @param mDay
	 *            day value between 1 and 31 if month is 1, 3, 5, 7, 8, 10 and
	 *            12; between 1 and 30 if month is 4, 6, 9 and 11; between 1 and
	 *            28 if month is 2 and isn't leap year; between 1 and 29 if
	 *            month is 2 and is leap year
	 * @return
	 */
	public boolean setDay(int mDay) {
		if (!checkValidDate(mDay, mMonth, mYear))
			return false;
		this.mDay = mDay;
		return true;
	}

	/**
	 * @param mHours
	 *            hours value between 0 and 23, 24 hours is 0 hour
	 * @return true if set hours is successful
	 */
	public boolean setHours(int mHours) {
		if (mHours < 0 || mHours > 23)// h thu 24 la h thu 0
			return false;
		this.mHours = mHours;
		return true;
	}

	/**
	 * @param mMinutes
	 *            minutes value between 0 and 59
	 * @return true if set minutes is successful
	 */
	public boolean setMinutes(int mMinutes) {
		if (mHours < 0 || mMinutes > 60)
			return false;
		this.mMinutes = mMinutes;
		return true;
	}

	/**
	 * @param mMonth
	 *            month value between 1 and 12
	 * @return true if set month is successful
	 */
	public boolean setMonth(int mMonth) {
		if (!checkValidDate(1, mMonth, mYear))
			return false;
		this.mMonth = mMonth;
		return true;
	}

	/**
	 * @param mSeconds
	 *            seconds value between 0 and 59
	 * @return true if set seconds is successful
	 */
	public boolean setSeconds(int mSeconds) {
		if (mSeconds < 0 || mSeconds > 60)
			return false;
		this.mSeconds = mSeconds;
		return true;
	}

	/**
	 * @param mYear
	 *            year value greater or equal zero
	 * @return true if set year is successful
	 */
	public boolean setYear(int mYear) {
		if (!checkValidDate(mDay, mMonth, mYear))
			return false;
		this.mYear = mYear;
		return true;
	}

	/**
	 * @return date string with default format is dd/MM/yyyy
	 */
	public String toDateString() {
		return String.format("%02d/%02d/%d", mDay, mMonth, mYear);
	}

	/**
	 * @return long friendly date string with default format is dd MM yyyy ex:
	 *         25 December 1994
	 */
	public String toLongDateFriendly() {
		String monthName = M_MONTHS_FULL_NAME[mMonth - 1];
		return String.format("%02d %s %d", mDay, monthName, mYear);
	}

	/**
	 * @return short friendly date string with default format is dd MM yyyy ex:
	 *         25 Dec 1994
	 */
	public String toShortDateFriendly() {
		String monthName = M_MONTHS_NAME[mMonth - 1];
		return String.format("%02d %s %d", mDay, monthName, mYear);
	}

	@Override
	public String toString() {
		return toTimeString() + " " + toDateString();
	}

	/**
	 * @return time string with default format is hh:mm:ss
	 */
	public String toTimeString() {
		return String.format("%02d:%02d:%02d", mHours, mMinutes, mSeconds);
	}

	/**
	 * 
	 * @return a integer value present time in seconds
	 */
	public int toInteger() {
		Calendar calendar = Calendar.getInstance();

		calendar.set(Calendar.DAY_OF_MONTH, mDay);
		calendar.set(Calendar.MONTH, mMonth - 1);
		calendar.set(Calendar.YEAR, mYear);
		calendar.set(Calendar.HOUR_OF_DAY, mHours);
		calendar.set(Calendar.MINUTE, mMinutes);
		calendar.set(Calendar.SECOND, mSeconds);

		return (int) (calendar.getTimeInMillis() / 1000L);
	}
}
