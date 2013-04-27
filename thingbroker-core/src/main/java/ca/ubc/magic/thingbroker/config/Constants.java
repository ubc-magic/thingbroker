package ca.ubc.magic.thingbroker.config;

public class Constants {

	// ---------------------------- Message Codes ------------------------------
	public static Integer CODE_OK = 0;
	public static Integer CODE_THING_NOT_FOUND = 1;
	public static Integer CODE_THING_ALREADY_REGISTERED = 2;
	public static Integer CODE_SENT_EVENT_TO_NON_EXISTENT_THING = 3;
	public static Integer CODE_REQUESTER_NOT_INFORMED = 4;
	public static Integer CODE_REQUESTER_NOT_REGISTERED = 5;
	public static Integer CODE_EVENT_NOT_FOUND = 6;
	public static Integer CODE_EVENT_DATA_NOT_FOUND = 7;
	public static Integer CODE_INVALID_EVENT_INFO_FIELD = 8;
	public static Integer CODE_SERVER_TIMESTAMP_NOT_PROVIDED = 9;
	public static Integer CODE_SERVER_TIMESTAMP_OUTDATED = 10;
	public static Integer CODE_BAD_APPLICATION_ID = 11;
	public static Integer CODE_INTERNAL_ERROR = 500;
	
	
	// ---------------------------- Configuration Variables ---------------------
	public static Integer REAL_TIME_EVENTS_WAITING_TIME = 15; //in seconds
	
}
