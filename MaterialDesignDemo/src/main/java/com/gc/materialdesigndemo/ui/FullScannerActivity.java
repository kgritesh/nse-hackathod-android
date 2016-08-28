package com.gc.materialdesigndemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesigndemo.R;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FullScannerActivity extends Activity implements ZXingScannerView.ResultHandler {

    private static final String TAG = FullScannerActivity.class.getCanonicalName();
    public static String response = null;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_scanner);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    void post(String data) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("code", data);
            String url = "http://10.196.9.105:8080/check-validity/";
            RequestBody body = RequestBody.create(JSON, obj.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String jsonData = response.body().string();
                Log.i(TAG, "Json Data: " + jsonData);
                Intent i = new Intent(FullScannerActivity.this, OwnerListActivity.class);
                i.putExtra("response", jsonData);
                i.putExtra("contractAddress", data);
                startActivity(i);
                finish();

//                JSONObject product = result.getJSONObject("product");
//                String productName = product.getString("name");
//                String productionCode = product.getString("code");
//                Log.i(TAG, productName + " " + productionCode);
//                JSONArray ownerships = result.getJSONArray("ownerships");
//                for(int i = 0; i < ownerships.length(); ++i) {
//                    JSONObject ownership = ownerships.getJSONObject(i);
//                    String owner = ownership.get
//                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();

            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void handleResult(final Result rawResult) {
        // Do something with the result here</p>
        Log.e("handler", rawResult.getText()); // Prints scan results<br />
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)</p>
        // show the scanner result into dialog box.
        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan_Result");
        builder.setMessage(rawResult.getText());
        AlertDialog alert1 = builder.create();
        alert1.show();*/

        this.post(rawResult.getText());
    }

    public void QrScanner(View view){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view<br />
        setContentView(mScannerView);
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.<br />
        mScannerView.startCamera();         // Start camera<br />
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();   // Stop camera on pause<br />
        }
    }
}
