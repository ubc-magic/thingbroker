package ca.ubc.magic.thingbroker.config;

public class Constants {
	
	//---------------------------- Message Codes ------------------------------
	public static Integer CODE_OK = 0;
	public static Integer CODE_THING_NOT_FOUND = 1;
	public static Integer CODE_THING_ALREADY_REGISTERED = 2;
	public static Integer CODE_INTERNAL_SERVER_ERROR = 500;
	
	//---------------------------- Message Content ----------------------------
    public static String FOLLOWING_REGISTRATION_SUCCESSFUL_MESSAGE = "Following Registration successful"; 
    public static String UNFOLLOWING_REGISTRATION_SUCCESSFUL_MESSAGE = "Unfollowing Registrations successful";
    public static String THING_NOT_FOUND_MESSAGE = "Thing not found";
    public static String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";
}
