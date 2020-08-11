package com.example.knowyourgovernment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private RecyclerView recycler;
    private List<Official> officialList = new ArrayList<Official>();
    private OfficialAdapter officialAdapter;

    private static TextView locationTextView;
    private static MainActivity mainActivity;
    private TextView notConnected;

    private static int MY_LOCATION_REQUEST_CODE_ID = 318;
    private LocationManager locationManager;
    private Criteria criteria;
    String address = "";
    String preLoc = null;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView
        recycler = (RecyclerView) findViewById(R.id.recycler);
        officialAdapter = new OfficialAdapter(officialList, this);
        recycler.setAdapter(officialAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        //Location
        locationTextView = (TextView) findViewById(R.id.location_official);
        mainActivity = this;
        /*****************************Set Location***************************/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        // gps
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);


        notConnected = (TextView) findViewById(R.id.no_Internet_Warning);

        if (isConnected()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_LOCATION_REQUEST_CODE_ID);
            } else {
                    address = setLocation();
                    new AsyncInfoLoader(mainActivity).execute(address);
            }
            Log.d(TAG, "onCreate: Address = " + address);

        } else {
            notConnected.setVisibility(View.VISIBLE);
        }

        //TEST: Make some data - not always needed - used to fill list
//        for (int i = 0; i < 20; i++) {
//            officialList.add(new Official());
//            Log.d(TAG, "onCreate: " + officialList.get(0));
//        }

    }

    /*****************************Set Location Part***************************/
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull
            String[] permissions, @NonNull
                    int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                address = setLocation();
                new AsyncInfoLoader(mainActivity).execute(address);
                return;
            }
        }
        Toast.makeText(this, "NO Permission", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingPermission")
    private String setLocation() {
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location currentLocation = locationManager.getLastKnownLocation(bestProvider);

        if (currentLocation != null) {
            String location = String.format(Locale.getDefault(),
                    "%.4f, %.4f", currentLocation.getLatitude(),
                    currentLocation.getLongitude());
            String s = doLatLon(location);
            Log.d(TAG, "setLocation: " + location);
            return s;
        } else {
            Toast.makeText(this, "Location Unavailable", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /*****************************Geocoder Part***************************/
    public String doLatLon(String location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;
            String loc = location;
            if (loc.trim().isEmpty()) {
                Toast.makeText(this,
                        "Enter Lat & Lon coordinates first!", Toast.LENGTH_LONG).show();
                return null;
            }
            String[] latLon = loc.split(",");
            double lat = Double.parseDouble(latLon[0]);
            double lon = Double.parseDouble(latLon[1]);

            addresses = geocoder.getFromLocation(lat, lon, 1);
            String zipCode = addresses.get(0).getPostalCode();
            String address = zipCode;
            Log.d(TAG, "onCreate: " + zipCode);
            return address;

        } catch (IOException e) {
            Toast.makeText(this, e.getMessage().toUpperCase(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return null;
    }

    //Menu Item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());

        switch (item.getItemId()) {
            case R.id.about_Item:
                Intent toAbout = new Intent(this, AboutActivity.class);
                //Intent intent = new Intent(this, OfficialActivity.class);
                toAbout.putExtra("Address", address);
                startActivityForResult(toAbout, 2);
                //startActivity(toAbout);
                return true;

            case R.id.search_Item:
                searchFunction();

            default:
                Toast.makeText(this,
                        "Unknown Menu Item: " + item.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }

    //user input location
    private void searchFunction() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View v = layoutInflater.inflate(R.layout.dialog, null);

        //Message box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setMessage("Enter a City or a Zip code");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText userInput = (EditText) v.findViewById(R.id.userInput);
                String input = userInput.getText().toString();

                new AsyncInfoLoader(mainActivity).execute(input);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        AlertDialog ad = builder.create();
        ad.show();
    }

    public void clearOfficialList() {
        officialList.clear();
    }

    @Override
    public void onClick(View v) {
        int pos = recycler.getChildAdapterPosition(v);
        Intent intent = new Intent(this, OfficialActivity.class);
        intent.putExtra("location", locationTextView.getText().toString());
        intent.putExtra("official", officialList.get(pos));
        startActivityForResult(intent, 1);
    }

    private boolean isConnected() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setLocationText(String location) {
        locationTextView.setText(location);
    }

    public void updateData(ArrayList<Official> cList) {
        if (cList != null) {
            officialList.addAll(cList);
        }
        officialAdapter.notifyDataSetChanged();
    }

}

