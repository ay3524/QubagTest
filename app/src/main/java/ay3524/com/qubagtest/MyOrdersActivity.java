package ay3524.com.qubagtest;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import ay3524.com.qubagtest.app.AppConfig;
import ay3524.com.qubagtest.app.AppController;

public class MyOrdersActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // session manager
        SessionManager session = new SessionManager(getApplicationContext());
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        listView = (ListView) findViewById(R.id.listview);

        if(Constants.isConnected(getApplicationContext())){
            showOrders(session.getEmail());
        }else{
            Toast.makeText(this, "No Intenet Connection :)", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOrders(final String email) {
        // Tag used to cancel the request
        final String tag_string_req = "req_login";

        pDialog.setMessage("Loading the items ...");
        showDialog();

        final StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ORDERS_SHOW, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Category List Response: " + response);
                HashSet<String> hashset = new HashSet<>();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //Categories categories = new Categories();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("title");
                        String orderTime = jsonObject.getString("orderTime");
                        String formattedDateTime = getDate(Long.parseLong(orderTime));
                        Toast.makeText(MyOrdersActivity.this, orderTime, Toast.LENGTH_SHORT).show();
                        Log.e("TAG",name);
                        hashset.add("Order : "+formattedDateTime);

                        //categories.setTitle(name);
                        //categories.setImage_url(image_url);
                        //categoriesList.add(categories);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideDialog();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(MyOrdersActivity.this,
                        android.R.layout.simple_list_item_1,
                        new ArrayList<>(hashset));
                listView.setAdapter(adapter);
                //adapter = new CategoryAdapter(categoriesList, MainActivity.this);
                //recyclerView.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "My Order Error: " + error.getMessage());
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time*1000L);
        return DateFormat.format("dd-MM-yyyy hh:mm:ss a", cal).toString();
    }
}
