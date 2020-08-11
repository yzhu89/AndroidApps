package com.example.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {
    private View pa;
    private TextView location;
    private TextView office;
    private TextView name;
    private ImageView partyImage;
    private ImageView photoImage;

    private Intent intent;
    private Official official;
    private static final String TAG = "PhotoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        pa = findViewById(R.id.layout_pa);
        location = (TextView) findViewById(R.id.location_pa);
        office = (TextView) findViewById(R.id.office_pa);
        name = (TextView) findViewById(R.id.name_pa);
        photoImage = (ImageView) findViewById(R.id.photo_pa);
        partyImage = (ImageView) findViewById(R.id.partyImage_pa);

        intent = getIntent();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        official = (Official) intent.getSerializableExtra("official");
        CharSequence ch = intent.getCharSequenceExtra("location");
        location.setText(intent.getCharSequenceExtra("location"));

        office.setText(official.getOfficialName());
        name.setText(official.getName());

        if (official.getParty() != null) {
            Log.d(TAG, "onCreate: " + official.getParty());
            if (official.getParty().equals("Republican Party")
                    ||official.getParty().equals("Republican")){
                pa.setBackgroundColor(Color.RED);
                partyImage.setImageResource(R.drawable.rep_logo);
                partyImage.setVisibility(View.VISIBLE);
            }
            else if (official.getParty().equals("Democratic Party")
                    ||official.getParty().equals("Democratic")) {
                pa.setBackgroundColor(Color.BLUE);
                partyImage.setImageResource(R.drawable.dem_logo);
                partyImage.setVisibility(View.VISIBLE);
            }
            else{
                pa.setBackgroundColor(Color.BLACK);
            }
        }else{
            pa.setBackgroundColor(Color.BLACK);
        }

        if (official.getPhotoUrl() != null){
            Picasso picasso = new Picasso.Builder
                    (this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    final String changedUrl =
                            official.getPhotoUrl().replace("http:", "https:");
                    picasso.load(changedUrl).error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder) .into(photoImage);
                }
            }).build();

            picasso.load(official.getPhotoUrl()).error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder).into(photoImage);
        } else {
            Picasso.get().load(official.getPhotoUrl())
                    .error(R.drawable.brokenimage).placeholder(R.drawable.missing)
                    .into(photoImage);
        }
    }

}
