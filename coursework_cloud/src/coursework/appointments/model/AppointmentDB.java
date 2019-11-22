package coursework.appointments.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import coursework.appointments.constants.ConfigDB;
import coursework.aws.util.DateUtils;
import coursework.aws.util.DynamoDBUtil;

public class AppointmentDB {
	
	/**
	 * Get all the appointment belonging to a user
	 * @param userId
	 * @return List of appointments
	 */
	public static List<Appointment> getAllAppointments(String userId, Date startDate, Date endDate) {
		DynamoDBMapper db = DynamoDBUtil.getDBMapper(ConfigDB.REGION, ConfigDB.END_POINT);
		
		Map<String, AttributeValue> mapAttrVal = new HashMap<String, AttributeValue>(); 
		mapAttrVal.put(":userId", new AttributeValue(userId));
		
		StringBuilder sbFilter = new StringBuilder();
		
		DynamoDBScanExpression scanExpression=new DynamoDBScanExpression();
		sbFilter.append("userId = :userId");
		if(startDate != null) {
			System.out.println(DateUtils.dateToStringDB(startDate));
			mapAttrVal.put(":startDate", new AttributeValue(DateUtils.dateToStringDB(startDate)));
			sbFilter.append(" and dateAndTime >= :startDate");
		}
		if(endDate != null) {
			System.out.println(DateUtils.dateToStringDB(endDate));
			mapAttrVal.put(":endDate", new AttributeValue(DateUtils.dateToStringDB(endDate)));
			sbFilter.append(" and dateAndTime <= :endDate");
		}
		scanExpression.withFilterExpression(sbFilter.toString());
		scanExpression.withExpressionAttributeValues(mapAttrVal);
		try {
			return db.scan(Appointment.class, scanExpression);
		} catch(Exception e) {
			System.out.println(e);
			throw new WebApplicationException("Sorry couldn't perform the search in the db", 500);
		}
	}
	
	public static void createAppointment(Appointment appointment) {
		DynamoDBMapper db = DynamoDBUtil.getDBMapper(ConfigDB.REGION, ConfigDB.END_POINT);
		
		try {
			db.save(appointment);
		} catch(Exception e) {
			System.out.println(e);
			throw new WebApplicationException("Sorry we could not save your appointment.", 500);
		}
	}
	
}
