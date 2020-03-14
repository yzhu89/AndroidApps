package com.example.converter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //initial variables
    private static double MI_TO_KM = 1.60934;
    private static double KM_TO_MI = 0.621371;
    private EditText inputValue;
    private TextView outputValue;
    private TextView scrollHistory;
    private TextView inputTitle;
    private TextView outputTitle;
    private double factor = MI_TO_KM;
    private String prefix = "Mi to Km: ";
    private Double input;
    private SharedPreferences myPrefs;
    private RadioGroup rg;
    private String outputResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set scrollable history view
        scrollHistory = findViewById(R.id.historyContent);
        scrollHistory.setMovementMethod(new ScrollingMovementMethod());
        inputValue = (EditText)findViewById(R.id.userInputValue);
        outputValue = findViewById(R.id.result);
        inputTitle = findViewById(R.id.inputTitle);
        outputTitle = findViewById(R.id.outputTiltle);
        //Shared Preference
        myPrefs = getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        String myData = myPrefs.getString("CONVERT_HISTORY", "......");
        ((TextView) findViewById(R.id.historyContent)).setText(myData);
    }
    private String s;
    public void picked(View v){
        rg = findViewById(R.id.radioGroup);
        int id = rg.getCheckedRadioButtonId();

        switch (id){
            case R.id.radioButtonMiToKm:
                s = "MitoKm";
                inputTitle.setText("Miles Value:");
                outputTitle.setText("Kilometers Value:");
                factor = MI_TO_KM;
                prefix = "Mi to Km: ";
                break;

            case R.id.radioButtonKmToMi:
                s = "KmtoMi";
                inputTitle.setText("Kilometers Value:");
                outputTitle.setText("Miles Value:");
                factor = KM_TO_MI;
                prefix = "Km to Mi: ";
                break;
        }

        Log.d(TAG, "picked: you picked: " + s);
    }

    public void covert(View v){
        //get the user input value
        if(inputValue.getText().toString().trim().equalsIgnoreCase("")){
            inputValue.setError("Please Enter numbers in here");
        }
        input = Double.parseDouble(inputValue.getText().toString());
        Double result = input * factor;
        outputResult = String.format("%.1f", result);
        outputValue.setText(outputResult);

        //add history
        String history = scrollHistory.getText().toString();
        history = prefix + input + "==>"
                   + outputResult +"\n" + history;
        scrollHistory.setText(history);
        inputValue.setText("");

        //Shared preference
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("CONVERT_HISTORY", history);
        prefsEditor.apply();

    }

    //clear output and history
    public void clearHistory(View v){
        String clean = scrollHistory.getText().toString();
        String output = outputValue.getText().toString();
        clean = "";
        output = "";
        scrollHistory.setText(clean);
        outputValue.setText(output);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString("CONVERT_HISTORY", clean);
        prefsEditor.apply();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("HISTORY", scrollHistory.getText().toString());
        outState.putDouble("VALUE", input);
        outState.putString("INPUT_TITLE", String.valueOf(inputTitle.getText()));
        outState.putString("OUTPUT_TITLE", String.valueOf(outputTitle.getText()));
        outState.putDouble("FACTOR",factor);
        outState.putString("PREFIX", prefix);
        outState.putString("RESULT", outputResult);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollHistory.setText((savedInstanceState.getString("HISTORY")));
        input = savedInstanceState.getDouble("VALUE");
        inputTitle.setText(savedInstanceState.getString("INPUT_TITLE"));
        outputTitle.setText(savedInstanceState.getString("OUTPUT_TITLE"));
        factor = savedInstanceState.getDouble("FACTOR");
        prefix = savedInstanceState.getString("PREFIX");
        outputValue.setText(savedInstanceState.getString("RESULT"));
    }
}
