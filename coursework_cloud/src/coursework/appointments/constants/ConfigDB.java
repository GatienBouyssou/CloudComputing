package coursework.appointments.constants;

public class ConfigDB {
	public static final String DYNAMODB_TABLE_NAME="coursework-appointments";	//DynamoDB table name
	public static final String DYNAMODB_HASH_KEY="id";						//hash key attribute name in DynamoDB table

	public final static String REGION = "local";
	public final static String END_POINT = "http://localhost:8000";
}
