package ay3524.com.qubagtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ay3524.com.qubagtest.app.AppConfig;
import ay3524.com.qubagtest.app.AppController;
import ay3524.com.qubagtest.model.Items;

public class OrderFinalActivity extends AppCompatActivity {


    Button myorders;
    ArrayList<Items> itemsArrayList;
    LinearLayout ll;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_final);
        if (getIntent().getExtras() != null) {
            itemsArrayList = getIntent().getParcelableArrayListExtra("ITEMS");
        }
        // Session manager
        session = new SessionManager(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        myorders = (Button) findViewById(R.id.my_orders);
        ll = (LinearLayout) findViewById(R.id.finalcartlayout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        myorders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MyOrdersActivity.class);
                startActivity(i);
                finish();
            }
        });

        if (Constants.isConnected(getApplicationContext())) {
            putItemsInOrdersList();
        } else {
            ll.setVisibility(View.GONE);
            Toast.makeText(this, "No Internet Connection!!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void putItemsInOrdersList() {
        showDialog();
        String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
        for (int i = 0; i < itemsArrayList.size(); i++) {
            String title = itemsArrayList.get(i).getTitle();
            String price = itemsArrayList.get(i).getPrice();
            String size = itemsArrayList.get(i).getSize();
            putSingleItemInDatabase(title, price, size,timeStamp);
        }
    }

    private void putSingleItemInDatabase(final String title, final String price, final String size,final String timeStamp) {
        // Tag used to cancel the request
        String tag_string_req = "req_orders";

        pDialog.setMessage("Loading ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ORDERS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Orders Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(), "Ordered Successfully", Toast.LENGTH_LONG).show();

                    } else {

                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("ERROR",e.getMessage());
                }

                hideDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Order Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("price", price);
                params.put("size", size);
                params.put("orderTime",timeStamp);
                params.put("email",session.getEmail());
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                navigateUpTo(intent);
                return true;
            }
        return super.onOptionsItemSelected(item);
    }
}
