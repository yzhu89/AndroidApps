package com.example.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class AboutActivity extends AppCompatActivity {
    private MainActivity mainActivity;
    private String apiURL = "https://developers.google.com/civic-information/";
    private static final String TAG = "AboutActivity";
    private final String MESSAGE_TITLE = "No Internet Connection";
    private final String MESSAGE_CONTENT =
            "Stocks Cannot Be Updated Without A Network Connection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tv = findViewById(R.id.aboutApi);
        tv.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );

        Intent intent = getIntent();
        if (intent.hasExtra("Address")) {
            String text = intent.getStringExtra("Address");
            Log.d(TAG, "onCreate: " + text);
        }
    }

    public void click(View v) {
        if(isConnected()){
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(apiURL));
            startActivity(i);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(AboutActivity.this);
            builder.setTitle(MESSAGE_TITLE);
            builder.setMessage(MESSAGE_CONTENT);
            AlertDialog ad = builder.create();
            ad.show();
        }
    }

    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public void backToMain(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.putExtra(Intent.EXTRA_TEXT, OfficialActivity.class.getSimpleName());
        startActivity(intent);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 101) {
//            if (resultCode == RESULT_OK) {
//                String first = data.getStringExtra("X");
//                String second = data.getStringExtra("Y");
//                Toast.makeText(this,
//                        "X: " + first + ", Y: " + second,
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
//            }
//
//        } else {
//            Log.d(TAG, "onActivityResult: Request Code " + requestCode);
//        }
//    }
}
