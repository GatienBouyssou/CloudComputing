package coursework.aws.util;

import javax.ws.rs.WebApplicationException;
import coursework.appointments.constants.StatusCodes;

public class FormValidator {
	
	/**
	 * Check all the values of the creation form but the date that is checked separately because it returns a date.
	 * Throws an error when dows not match the conditions. 
	 * @param sessionUserId
	 * @param title
	 * @param date
	 * @param hours
	 * @param minutes
	 * @param owner
	 * @param durationHour
	 * @param durationMin
	 * @param description
	 */
	public static void checkAppointmentForm(String sessionUserId, String title, String date, 
			int hours, int minutes, String owner, int durationHour, int durationMin, String description) {
		if (sessionUserId.isEmpty()) {
			throw new WebApplicationException("Sorry you need to provide the user id before creating any appointments", StatusCodes.CLIENT_ERROR);
		}
		
		if(isHoursValid(hours) || isMinutesValid(minutes)) {
			throw new WebApplicationException("The time entered is not valid.", StatusCodes.CLIENT_ERROR);
		}
		
		if(durationHour < 0 || isMinutesValid(durationMin)) {
			throw new WebApplicationException("The duration is not valid.", StatusCodes.CLIENT_ERROR);
		}
		
		if(title.isEmpty() || containsSpecialCharacter(title)) { // Check special characters because dynamo db does not support them
			throw new WebApplicationException("Sorry your title is null or contains special characters other than .-_", StatusCodes.CLIENT_ERROR);
		}
		
		if(owner.isEmpty() || containsSpecialCharacter(owner)) {
			throw new WebApplicationException("Sorry the owner name is null or contains special characters other than .-_", StatusCodes.CLIENT_ERROR);
		}
		
		if(containsSpecialCharacter(description)) {
			throw new WebApplicationException("Sorry you can't use special characters in your description", StatusCodes.CLIENT_ERROR);
		}
	}
	
	public static boolean isMinutesValid(int minutes) {
		return minutes < 0 || minutes > 59;
	}
	
	public static boolean isHoursValid(int hours) {
		return hours < 0 || hours >23;
	}
	
	public static boolean containsSpecialCharacter(String s) {
	    return (s == null) ? false : s.matches("(.*)[^A-Za-z0-9-._](.*)");
	}
}
