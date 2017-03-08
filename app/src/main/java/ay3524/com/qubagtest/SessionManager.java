package ay3524.com.qubagtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

class SessionManager {
	// LogCat tag
	private static String TAG = SessionManager.class.getSimpleName();

	// Shared Preferences
	private SharedPreferences pref;

	private Editor editor;
	private Context _context;

	// Shared pref mode
	private int PRIVATE_MODE = 0;

	// Shared preferences file name
	private static final String PREF_NAME = "QuBagLogin";
	
	private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

	SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	void setLogin(boolean isLoggedIn) {

		editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);

		// commit changes
		editor.commit();

		Log.d(TAG, "User login session modified!");
	}
	void setProfileValues(String name, String email, String verified) {

        editor.putString("name_key", name);
		editor.putString("email_key", email);
        editor.putString("verify_key",verified);

		// commit changes
		editor.commit();

		Log.d(TAG, "User values modified!");
	}

	public String getName(){
        return pref.getString("name_key", "N");
    }

	String getEmail(){
        return pref.getString("email_key", "E");
    }

    String getVerifiedValue(){
        return pref.getString("verify_key", "V");
    }
	
	boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}
}
