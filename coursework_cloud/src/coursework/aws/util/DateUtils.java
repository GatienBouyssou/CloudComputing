package coursework.aws.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.WebApplicationException;

import coursework.appointments.constants.StatusCodes;

public class DateUtils {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd-hh:mm:ss";
	private static final String TIME_FORMAT = "HH:mm:ss";
	
	/**
	 * check if a string match a date pattern if it does convert it to Date
	 * @param date
	 * @return Date
	 */
	public static Date stringToDate(String date) {
		if(date==null || date.isEmpty())
			return null;
		try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            return df.parse(date);
        } catch (ParseException e) {
            throw new WebApplicationException("Incorrect date format it should have the format " + DATE_FORMAT,StatusCodes.CLIENT_ERROR);
        }
	}
	/**
	 * Convert a string at the format yyyy-MM-dd-hh:mm:ss to a date. If it does not 
	 * have this format raise an exception.
	 * @param dateAndTime
	 * @return a Date with time in it
	 */
	public static Date stringToDateWithTime(String dateAndTime) {
		if(dateAndTime==null || dateAndTime.isEmpty())
			return null;
		try {
            DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
            df.setLenient(false);
            return df.parse(dateAndTime);
        } catch (ParseException e) {
            throw new WebApplicationException("Invalid date or time try the following format " + DATE_TIME_FORMAT ,StatusCodes.CLIENT_ERROR);
        }
	}
	
	/**
	 * 
	 * @param date
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	public static Date concatenateDateAndTime(String date, int hours, int minutes, int seconds) {
		if(date==null || date.isEmpty())
			return null;
		try {
			Date dateParsed = stringToDate(date);
			dateParsed.setHours(hours);
			dateParsed.setMinutes(minutes);
			dateParsed.setSeconds(seconds);
			return dateParsed;
		} catch(Exception e) {
			throw new WebApplicationException("Invalid date or hour/minute try the following format " + DATE_TIME_FORMAT,StatusCodes.CLIENT_ERROR);
		}
		
	}
	
	/**
	 * Format date to string to compare it to the values in the database.
	 * @param date
	 * @return String like "yyyy-mm-ddThh:mm:ss.000Z"
	 */
	public static String dateToStringDB(Date date) {
		// get the date with the format yyyy-mm-dd
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		df.setLenient(false);
		String dateOnly = df.format(date);
		// get the time with the format hh:mm:ss
		df = new SimpleDateFormat(TIME_FORMAT);
		df.setLenient(false);
		return dateOnly + "T" + df.format(date) + ".000Z";
	}
}
