package ay3524.com.qubagtest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.HashSet;
import java.util.Map;

import ay3524.com.qubagtest.adapter.ItemsAdapter;
import ay3524.com.qubagtest.app.AppConfig;
import ay3524.com.qubagtest.app.AppController;
import ay3524.com.qubagtest.model.Items;

import static android.view.View.GONE;

public class ItemsActivity extends AppCompatActivity implements ItemsAdapter.ListItemClickListener {

    private static final String STATE_TASK = "TASK";
    RecyclerView recyclerView;
    String category;
    private ItemsAdapter adapter;
    private ProgressDialog pDialog;
    ArrayList<Items> itemsArrayList = new ArrayList<>();
    Button proceed;
    LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        if (getIntent().getExtras() != null) {
            category = getIntent().getStringExtra("CATEGORY");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        emptyView = (LinearLayout) findViewById(R.id.emptyview);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        proceed = (Button) findViewById(R.id.checkout);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (savedInstanceState != null) {
            itemsArrayList = savedInstanceState.getParcelableArrayList(STATE_TASK);
            adapter = new ItemsAdapter(itemsArrayList, ItemsActivity.this);
            recyclerView.setAdapter(adapter);
        } else {
            checkInternetBeforeShowingTheList();
        }

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectedItems();
            }
        });
    }

    private void getSelectedItems() {
        HashSet<Items> itemsHashSet = adapter.getHashSet();
        //String text = "";

        if (itemsHashSet.isEmpty()) {
            Toast.makeText(this, "Select items to proceed", Toast.LENGTH_SHORT).show();
        } else {
            ArrayList<Items> itemsArrayList = new ArrayList<>(itemsHashSet);
            Intent i = new Intent(getApplicationContext(), OrderFinalActivity.class);
            i.putExtra("ITEMS", itemsArrayList);
            startActivity(i);
        }
    }

    private void checkInternetBeforeShowingTheList() {
        if (Constants.isConnected(getApplicationContext())) {
            showTheList(category);
        } else {
            emptyView.setVisibility(View.VISIBLE);
            proceed.setVisibility(GONE);
            Toast.makeText(this, "No internet Connection :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTheList(final String category) {
        // Tag used to cancel the request
        String tag_string_req = "req_items";

        pDialog.setMessage("Loading the items...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ITEMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("TAG", "Items List Response: " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Items items = new Items();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        String image_url = jsonObject.getString("image_url");
                        String price = jsonObject.getString("price");
                        String size = jsonObject.getString("size");
                        items.setTitle(name);
                        items.setImage_url(image_url);
                        items.setPrice(price);
                        items.setSize(size);
                        itemsArrayList.add(items);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hideDialog();
                adapter = new ItemsAdapter(itemsArrayList, ItemsActivity.this);
                recyclerView.setAdapter(adapter);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("category", category);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

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
