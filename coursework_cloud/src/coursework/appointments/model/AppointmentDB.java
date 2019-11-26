package coursework.appointments.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import coursework.appointments.constants.ConfigDB;
import coursework.appointments.constants.StatusCodes;
import coursework.aws.util.DateUtils;
import coursework.aws.util.DynamoDBUtil;

public class AppointmentDB {
	//Getters
	/**
	 * Get all the appointment belonging to a user if the two dates are null.
	 * If the startDate is null get all the appointments before the end date
	 * If the endDate is null get all the appointments after the start date
	 * @param userId
	 * @return List of appointments
	 */
	public static List<Appointment> getAllAppointments(String userId, Date startDate, Date endDate) {
		
		Map<String, AttributeValue> mapAttrVal = new HashMap<String, AttributeValue>();
		mapAttrVal.put(":userId", new AttributeValue(userId));

		StringBuilder sbFilter = new StringBuilder();

		sbFilter.append("userId = :userId");
		if (startDate != null) { // if start date not null get all dates after this date
			mapAttrVal.put(":startDate", new AttributeValue(DateUtils.dateToStringDB(startDate)));
			sbFilter.append(" and dateAndTime >= :startDate");
		}

		if (endDate != null) { // if end date not null get all dates before this date
			mapAttrVal.put(":endDate", new AttributeValue(DateUtils.dateToStringDB(endDate)));
			sbFilter.append(" and dateAndTime <= :endDate");
		}
		
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		scanExpression.withFilterExpression(sbFilter.toString());
		scanExpression.withExpressionAttributeValues(mapAttrVal);

		try {
			DynamoDBMapper db = DynamoDBUtil.getDBMapper(ConfigDB.REGION, ConfigDB.END_POINT);
			return db.scan(Appointment.class, scanExpression);
		} catch (Exception e) {
			System.out.println(e);
			throw new WebApplicationException("Sorry couldn't perform the search in the db", StatusCodes.SERVER_ERROR);
		}
	}
	
	/**
	 * Query the database to find back a particular appointment
	 * @param userId
	 * @param appointmentId
	 * @return an appointment 
	 */
	public static Appointment getAppointmentWithId(String userId, String appointmentId) {		
		Map<String, AttributeValue> mapAttrVal = new HashMap<String, AttributeValue>();
		mapAttrVal.put(":appointmentId", new AttributeValue(appointmentId));
		mapAttrVal.put(":userId", new AttributeValue(userId));
		
		// Find the appointment with same appointmentId and userId
		DynamoDBQueryExpression<Appointment> queryExpression = new DynamoDBQueryExpression<Appointment>();
		queryExpression.withKeyConditionExpression("appointmentId = :appointmentId");
		queryExpression.withFilterExpression("userId = :userId");
		queryExpression.withExpressionAttributeValues(mapAttrVal);
		
		try {
			DynamoDBMapper db = DynamoDBUtil.getDBMapper(ConfigDB.REGION, ConfigDB.END_POINT);
			List<Appointment> listAppointments = db.query(Appointment.class, queryExpression);
			return listAppointments.get(0);
			
		} catch (Exception e) {
			System.out.println(e);
			throw new WebApplicationException("Sorry we could not get this appointment in the database.", StatusCodes.SERVER_ERROR);
		}
	}
	
	// Write into the database
	public static void createAppointment(Appointment appointment) {
		try {
			DynamoDBMapper db = DynamoDBUtil.getDBMapper(ConfigDB.REGION, ConfigDB.END_POINT);
			db.save(appointment);
		} catch (Exception e) {
			System.out.println(e);
			throw new WebApplicationException("Sorry we could not create your appointment.", StatusCodes.SERVER_ERROR);
		}
	}
	
	public static void updateAppointment(Appointment appointment) {
		try {
			DynamoDBMapper db = DynamoDBUtil.getDBMapper(ConfigDB.REGION, ConfigDB.END_POINT);
			db.save(appointment);
		} catch (Exception e) {
			System.out.println(e);
			throw new WebApplicationException("Sorry we could not update your appointment.", StatusCodes.SERVER_ERROR);
		}
	}

	public static void deleteAnAppointmentWithId(String userId, String appointmentId) {

		Appointment appointment = new Appointment(userId, appointmentId);
		try {
			DynamoDBMapper db = DynamoDBUtil.getDBMapper(ConfigDB.REGION, ConfigDB.END_POINT);

			db.delete(appointment);
		} catch (Exception e) {
			System.out.println(e);
			throw new WebApplicationException("Sorry we could not delete this appointment.", StatusCodes.SERVER_ERROR);
		}
	}
}
