package com.gc.materialdesigndemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gc.materialdesigndemo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;

public class OwnerListActivity extends Activity {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private TextView productName;
    private TextView manufacturer;
    public static JSONObject jsonObj = null;
    public static JSONArray jArray = null;
    ArrayList<String> items = new ArrayList<String>();
    String contractAddress;
    String currentOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_list);
        productName = (TextView) findViewById(R.id.product);
        manufacturer = (TextView) findViewById(R.id.manufacturer);

        Intent intent = getIntent();
        contractAddress = intent.getStringExtra("contractAddress");
        String response = intent.getStringExtra("response");
        try {
            jsonObj = new JSONObject(response);
            JSONObject productJson = jsonObj.getJSONObject("product");
            String name = productJson.getString("name");
            String code  = productJson.getString("code");
            String owner = productJson.getString("manufacturer");
            productName.setText("Product " + name);
            manufacturer.setText("Manufacturer " + owner);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jArray = jsonObj.getJSONArray("ownerships");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i=0; i < jArray.length(); i++)
        {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                String name = oneObject.getString("owner");
                String date = oneObject.getString("timestamp");
                items.add( name + " (" +  date + ")");
                currentOwner = name;
            } catch (JSONException e) {
                // Oops
            }
        }

        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, items);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(mArrayAdapter);

    }

    public void TransferProduct(View v){
        Intent in = new Intent(OwnerListActivity.this, TransferOwnerActivity.class);
        in.putExtra("contractAddress", contractAddress);
        in.putExtra("owner", currentOwner);
        startActivity(in);
        finish();
    }

}
