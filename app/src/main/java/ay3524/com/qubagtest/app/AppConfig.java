package ay3524.com.qubagtest.app;

import ay3524.com.qubagtest.Constants;

public class AppConfig {
	// Server user login url
	public static String URL_LOGIN = Constants.IP_ADDRESS.concat("/login.php");

	// Server user register url
	public static String URL_REGISTER = Constants.IP_ADDRESS.concat("/register.php");

	// Verify user status url
	public static String URL_VERIFY_STATUS = Constants.IP_ADDRESS.concat("/check_verify_status.php");

	// Category list url
	public static String URL_CATEGORY = Constants.IP_ADDRESS.concat("/categories.php");

	// Items list url
	public static String URL_ITEMS = Constants.IP_ADDRESS.concat("/items.php");

	// Order list url
	public static String URL_ORDERS = Constants.IP_ADDRESS.concat("/orders.php");

    //Show Order list url
    public static String URL_ORDERS_SHOW = Constants.IP_ADDRESS.concat("/show_orders.php");

}
