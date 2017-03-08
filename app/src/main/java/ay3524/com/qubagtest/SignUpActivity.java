package ay3524.com.qubagtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ay3524.com.qubagtest.app.AppConfig;
import ay3524.com.qubagtest.app.AppController;

public class SignUpActivity extends AppCompatActivity {

    TextView linkToLogin;
    private EditText name, email, phone,
            password, re_password, address_line1,
            address_line2;
    private AppCompatButton button;
    private ProgressDialog pDialog;
    private SessionManager session;

    private static final String TAG = SignUpActivity.class.getSimpleName();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        linkToLogin = (TextView) findViewById(R.id.link_login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(SignUpActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }


        name = (EditText) findViewById(R.id.input_name);
        email = (EditText) findViewById(R.id.input_email);
        phone = (EditText) findViewById(R.id.input_mobile);
        password = (EditText) findViewById(R.id.input_password);
        re_password = (EditText) findViewById(R.id.input_reEnterPassword);
        address_line1 = (EditText) findViewById(R.id.input_address1);
        address_line2 = (EditText) findViewById(R.id.input_address2);

        button = (AppCompatButton) findViewById(R.id.btn_signup);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constants.isConnected(getApplicationContext())){
                    signup();
                }else{
                    Toast.makeText(SignUpActivity.this, "No Intenet Connection :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

        linkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });
    }

    private void signup() {
        Log.d("TAG", "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        button.setEnabled(false);

        /*final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();*/

        String nameString = name.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String phoneString = phone.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        //String rePasswordString = re_password.getText().toString().trim();
        String address_line1String = address_line1.getText().toString().trim();
        String address_line2String = address_line2.getText().toString().trim();
        String address = address_line1String;

        if (!address_line2String.isEmpty()) {
            address = address_line1String + "," + address_line2String;
        }


        registerUser(nameString, emailString, passwordString, phoneString, address);

        //String method = "register";

        //BackgroundTask backgroundTask = new BackgroundTask(SignUpActivity.this);
        //backgroundTask.execute(method, nameString, emailString, passwordString, phoneString, address);

    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        button.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String nameString = name.getText().toString().trim();
        String emailString = email.getText().toString().trim();
        String phoneString = phone.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        String rePasswordString = re_password.getText().toString().trim();
        String address_line1String = address_line1.getText().toString().trim();
        //String address_line2String = address_line2.getText().toString().trim();

        if (nameString.isEmpty() || nameString.length() < 3) {
            name.setError("at least 3 characters");
            valid = false;
        } else {
            name.setError(null);
        }

        if (address_line1String.isEmpty()) {
            address_line1.setError("Enter address");
            valid = false;
        } else {
            address_line1.setError(null);
        }


        if (emailString.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("enter a valid email address");
            valid = false;
        } else {
            email.setError(null);
        }

        if (phoneString.isEmpty() || phoneString.length() != 10) {
            phone.setError("Enter Valid Mobile Number");
            valid = false;
        } else {
            phone.setError(null);
        }

        if (passwordString.isEmpty() || passwordString.length() < 6 || passwordString.length() > 10) {
            password.setError("Between 4 and 10 characters");
            valid = false;
        } else {
            password.setError(null);
        }

        if (rePasswordString.isEmpty() || rePasswordString.length() < 6 || rePasswordString.length() > 10 || !(rePasswordString.equals(passwordString))) {
            re_password.setError("Password Do not match");
            valid = false;
        } else {
            re_password.setError(null);
        }

        return valid;
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String name, final String email,
                              final String password, final String phone, final String address) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        session.setLogin(true);
                        //String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user_info");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String verified = user.getString("verified");

                        session.setProfileValues(name,email,verified);

                        Toast.makeText(getApplicationContext(), "User successfully registered", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("phone", phone);
                params.put("address", address);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
