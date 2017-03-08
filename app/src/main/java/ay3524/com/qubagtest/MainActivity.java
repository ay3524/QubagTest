package ay3524.com.qubagtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ay3524.com.qubagtest.adapter.CategoryAdapter;
import ay3524.com.qubagtest.app.AppConfig;
import ay3524.com.qubagtest.app.AppController;
import ay3524.com.qubagtest.model.Categories;

public class MainActivity extends AppCompatActivity implements CategoryAdapter.ListItemClickListener {

    private static final String STATE_TASK = "TASK";
    private SessionManager session;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    LinearLayout emptyView;
    TextView firstText, secondText, thirdText;
    Button retry;
    ArrayList<Categories> categoriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        firstText = (TextView) findViewById(R.id.mainText);
        secondText = (TextView) findViewById(R.id.subtitleText);
        thirdText = (TextView) findViewById(R.id.lastText);
        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        emptyView = (LinearLayout) findViewById(R.id.emptyview);
        retry = (Button) findViewById(R.id.button);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (savedInstanceState != null) {
            categoriesList = savedInstanceState.getParcelableArrayList(STATE_TASK);
            adapter = new CategoryAdapter(categoriesList, MainActivity.this);
            recyclerView.setAdapter(adapter);
        } else {
            verifyingTheUser();

        }
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyingTheUser();
            }
        });


    }

    private void verifyingTheUser() {
        if (session.getVerifiedValue().equals("0")) {
            if (Constants.isConnected(getApplicationContext())) {

                checkVerificationStatus(session.getEmail());
                //emptyView.setVisibility(View.GONE);
            } else {
                //subtitle_text.setText(R.string.there_might_be_some_problem_with_internet_connection);
                firstText.setText(R.string.problem_internet);
                secondText.setText(R.string.try_checking);
                thirdText.setVisibility(View.GONE);
                retry.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.VISIBLE);
            }
        } else if (session.getVerifiedValue().equals("1")) {
            displayTheList();
        }
    }

    private void checkVerificationStatus(final String email) {

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Verifying Your Status ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VERIFY_STATUS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        ///String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user_info");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String verified = user.getString("verified");

                        session.setProfileValues(name, email, verified);
                        //textView.setText("");
                        //textView.setText(session.getName() + "\n" + session.getEmail() + "\n" + session.getVerifiedValue());
                        Toast.makeText(MainActivity.this, name + "\n" + email + "\n" + verified, Toast.LENGTH_SHORT).show();
                        // Launch main activity
                        //displayTheList();
                        if (session.getVerifiedValue().equals("1")) {
                            displayTheList();
                        } else {
                            Toast.makeText(MainActivity.this, "You are not verified user", Toast.LENGTH_SHORT).show();
                            firstText.setText(R.string.not_verify);
                            secondText.setText(R.string.time_taken);
                            thirdText.setVisibility(View.VISIBLE);
                            retry.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            //Show you are not verified layout
                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("JSONError", e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                //params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void displayTheList() {
        Toast.makeText(this, "Displaying the list", Toast.LENGTH_SHORT).show();

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Loading the items ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_CATEGORY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Catefory List Response: " + response);

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Categories categories = new Categories();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        Log.e("TAG",name);
                        String image_url = jsonObject.getString("image_url");
                        categories.setTitle(name);
                        categories.setImage_url(image_url);
                        categoriesList.add(categories);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideDialog();
                adapter = new CategoryAdapter(categoriesList, MainActivity.this);
                recyclerView.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        });

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

    private void logoutUser() {
        session.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_orders) {
            startActivity(new Intent(getApplicationContext(),MyOrdersActivity.class));
            return true;
        }
        if(id == R.id.action_log_out){
            logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent i = new Intent(getApplicationContext(), ItemsActivity.class);
        i.putExtra("CATEGORY",categoriesList.get(clickedItemIndex).getTitle());
        startActivity(i);
    }
}
