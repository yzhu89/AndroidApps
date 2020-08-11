package com.example.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

//use Linkify

public class OfficialActivity extends AppCompatActivity {
    private View oa;
    private TextView location;
    private TextView office;
    private TextView name;
    private TextView party;
    private TextView a;
    private TextView address;
    private TextView p;
    private TextView phone;
    private TextView e;
    private TextView email;
    private TextView w;
    private TextView webInfo;


    private ImageView partyImage;
    private ImageButton photoImage;
    private ImageButton facebook;
    private ImageButton twitter;
    private ImageButton googlePlus;
    private ImageButton youtube;

    private int icon_flag = 0;
    private Intent intent;
    private Official official;
    private socialMediaChannel channels;
    private static final String TAG = "OfficialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        oa = findViewById(R.id.oa_mainLayout);
        location = (TextView) findViewById(R.id.location_official);
        office = (TextView) findViewById(R.id.officename_official);
        name = (TextView) findViewById(R.id.name_official);
        party = (TextView) findViewById(R.id.party_official);
        a = (TextView) findViewById(R.id.address_official);
        address = (TextView) findViewById(R.id.addressInfo_official);
        p = (TextView) findViewById(R.id.phone_official);
        phone = (TextView) findViewById(R.id.phoneInfo_official);
        e = (TextView) findViewById(R.id.email_official);
        email = (TextView) findViewById(R.id.emailInfo_official);
        w = (TextView) findViewById(R.id.website_official);
        webInfo = (TextView) findViewById(R.id.webInfo_official);
        photoImage = (ImageButton) findViewById(R.id.photo_pa);
        facebook = (ImageButton) findViewById(R.id.fb_official);
        twitter = (ImageButton) findViewById(R.id.twitter_official);
        googlePlus = (ImageButton) findViewById(R.id.googlePlus_official);
        youtube = (ImageButton) findViewById(R.id.youtube_official);
        partyImage = (ImageView) findViewById(R.id.partyImage_official);

        intent = getIntent();
        location.setText(intent.getCharSequenceExtra("location"));
        official = (Official) intent.getSerializableExtra("official");
        channels = official.getChannel();
        office.setText(official.getOfficialName());
        name.setText(official.getName());
        Log.d(TAG, "onCreate: " + official.getName());

        if (official.getParty() != null) {
            Log.d(TAG, "onCreate: " + official.getParty());
            if (official.getParty().equals("Republican Party")
                    || official.getParty().equals("Republican")){
                oa.setBackgroundColor(Color.RED);
                party.setText('('+official.getParty()+')');
                partyImage.setImageResource(R.drawable.rep_logo);
                icon_flag = 1;
                partyImage.setVisibility(View.VISIBLE);
            }
            else if (official.getParty().equals("Democratic Party")
                    ||official.getParty().equals("Democratic")) {
                party.setText('('+official.getParty()+')');
                oa.setBackgroundColor(Color.BLUE);
                partyImage.setImageResource(R.drawable.dem_logo);
                icon_flag = 2;
                partyImage.setVisibility(View.VISIBLE);
            }
            else{
                oa.setBackgroundColor(Color.BLACK);
            }
        } else{
            oa.setBackgroundColor(Color.BLACK);
        }

        if (official.getAddress() != "") {
            address.setText(official.getAddress());
        }else{
            a.setVisibility(View.GONE);
            address.setVisibility(View.GONE);
        }
        if (official.getPhones() != null){
            phone.setText(official.getPhones());
        }else{
            p.setVisibility(View.GONE);
            phone.setVisibility(View.GONE);
        }
        if (official.getEmails() != null){
            email.setText(official.getEmails());
        }else{
            e.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }
        if (official.getUrls() != null) {
            webInfo.setText(official.getUrls());
        }else{
            w.setVisibility(View.GONE);
            webInfo.setVisibility(View.GONE);
        }

        if (official.getPhotoUrl() != null){
            if(!isConnected()){
                photoImage.setBackgroundResource(R.drawable.brokenimage);
            }else{
                Picasso picasso = new Picasso.Builder
                        (this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                        final String changedUrl =
                                official.getPhotoUrl().replace("http:", "https:");
                        picasso.load(changedUrl).error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder).into(photoImage);
                    }
                }).build();

                picasso.load(official.getPhotoUrl()).error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder).into(photoImage);
            }
        }
        else {
            if(!isConnected()){
                photoImage.setBackgroundResource(R.drawable.brokenimage);
            }else {
                Picasso.get().load(official.getPhotoUrl())
                        .error(R.drawable.brokenimage).placeholder(R.drawable.missing)
                        .into(photoImage);
            }
        }

        if (channels == null) {
            facebook.setVisibility(View.INVISIBLE);
            youtube.setVisibility(View.INVISIBLE);
            twitter.setVisibility(View.INVISIBLE);
            googlePlus.setVisibility(View.INVISIBLE);
        }else{
            if (channels.getFacebookId() == null)
                facebook.setVisibility(View.INVISIBLE);
            if (channels.getYoutubeId() == null)
                youtube.setVisibility(View.INVISIBLE);
            if (channels.getTwitterId() == null)
                twitter.setVisibility(View.INVISIBLE);
            if (channels.getGooglePlusId() == null)
                googlePlus.setVisibility(View.INVISIBLE);
        }

        //Linkify
        Linkify.addLinks(webInfo, Linkify.ALL);
        Linkify.addLinks(phone, Linkify.ALL);
        Linkify.addLinks(address, Linkify.ALL);
        Linkify.addLinks(email, Linkify.ALL);

    }

    //Make Image Button Clickable
    public void photoClick(View v){
        if (official.getPhotoUrl() == null){
            return;
        }
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("official", official);
        intent.putExtra("location", location.getText());
        Log.d("before go to pa", location.getText().toString());
        startActivityForResult(intent, 1);
    }

    public void goToPoliticalWeb(View v){
        if(icon_flag == 1){
            if(isConnected()){
                final String REP_URL = "https://www.gop.com";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(REP_URL));
                startActivity(i);
            }
            else{
                Toast.makeText(this,
                        "Please check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        }else if(icon_flag == 2){
            if(isConnected()){
                final String DEM_URL = "https://democrats.org";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(DEM_URL));
                startActivity(i);
            }
            else{
                Toast.makeText(this,
                        "Please check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            return;
        }


    }

    public void twitterClicked(View v){
        Intent intent = null;
        String name = channels.getTwitterId();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v){
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getChannel().getFacebookId();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.
                    getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + channels.getFacebookId(); }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void youtubeClicked(View v) {
        String name = channels.getYoutubeId();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void googlePlusClicked(View v) {
        String name = channels.getGooglePlusId();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent
                    (Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + name)));
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
}
