package com.gc.materialdesigndemo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesigndemo.R;
import com.google.zxing.Result;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesigndemo.R;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;



public class TransferOwnerActivity extends Activity implements ZXingScannerView.ResultHandler {

    public static final String TAG = TransferOwnerActivity.class.getCanonicalName();

    public static String response = null;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private ZXingScannerView mScannerView;
    public String contractAddr;
    public String currentOwner;

    //@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_owner);
        Intent intent = getIntent();
        contractAddr = intent.getStringExtra("contractAddress");
        currentOwner = intent.getStringExtra("owner");
    }

    void post(String data) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("newOwner", data);
            obj.put("code", contractAddr);
            obj.put("owner",  currentOwner);
            String url = "http://10.196.9.105:8080/change-ownership/";
            RequestBody body = RequestBody.create(JSON, obj.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String jsonData = response.body().string();
                Intent i = new Intent(TransferOwnerActivity.this, OwnerListActivity.class);
                i.putExtra("response", jsonData);
                i.putExtra("contractAddress", data);
                startActivity(i);
                finish();
                Log.i(TAG, "Json Data: " + jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
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
        TransferOwnerActivity.this.post(rawResult.getText());
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
        mScannerView.stopCamera();   // Stop camera on pause<br />
    }

}
