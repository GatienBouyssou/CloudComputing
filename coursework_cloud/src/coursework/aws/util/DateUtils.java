package coursework.aws.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.WebApplicationException;

public class DateUtils {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd-hh:mm:ss";
	private static final String TIME_FORMAT = "HH:mm:ss";
	
	public static Date stringToDate(String date) {
		if(date==null || date.isEmpty())
			return null;
		try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            return df.parse(date);
        } catch (ParseException e) {
            throw new WebApplicationException("Incorrect date format " + date,400);
        }
	}
	
	public static Date stringToDateWithTime(String date) {
		if(date==null || date.isEmpty())
			return null;
		try {
            DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT);
            df.setLenient(false);
            return df.parse(date);
        } catch (ParseException e) {
            throw new WebApplicationException("Incorrect date format " + date,400);
        }
	}
	
	public static Date concatenateDateAndTime(String date, int hours, int minutes, int seconds) {
		Date dateParsed = stringToDate(date);
		dateParsed.setHours(hours);
		dateParsed.setMinutes(minutes);
		dateParsed.setSeconds(seconds);
		return dateParsed;
	}
	
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
