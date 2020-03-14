package com.example.yawei_multinote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{

    private RecyclerView recycler;
    private NoteAdapter noteAdapter;
    //Any number of notes are allowed (including no notes at all)
    private List<Note> noteList = new ArrayList<>();
    private int notesNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recycler);
        noteAdapter = new NoteAdapter(noteList, this);
        recycler.setAdapter(noteAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        this.loadJSONFile();

        if(noteList.size() > 0){
            getSupportActionBar().setTitle(getString(R.string.app_name)
                    + "(" + noteList.size() + ")");
        }
        noteAdapter.notifyDataSetChanged();

        //Make some data - not always needed - used to fill list
//        for (int i = 0; i < 15; i++) {
//            noteList.add(new Note());
//       }
    }

    //Menu Item
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.Add_menuItem:
                Intent toEdit = new Intent(this, EditActivity.class);
                startActivityForResult(toEdit, 1);
                return super.onOptionsItemSelected(item);

            case R.id.About_menuItem:
                Intent toAbout = new Intent(this, AboutActivity.class);
                startActivity(toAbout);
                return super.onOptionsItemSelected(item);

            default:
                Toast.makeText(this,
                        "Unknown Menu Item: " + item.getTitle(),
                        Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v){
        notesNumber = recycler.getChildLayoutPosition(v);
        Intent editNote = new Intent(this, EditActivity.class);

        Note currNote = noteList.get(notesNumber);
        editNote.putExtra("TITLE", currNote.getTitle());
        editNote.putExtra("CONTENT", currNote.getText());
        startActivityForResult(editNote, 2);
        noteAdapter.notifyDataSetChanged();
    }

    // From OnLongClickListener, delete note or not
    //Show Dialog Box
    @Override
    public boolean onLongClick(View v){
        final int pos = recycler.getChildLayoutPosition(v);
        AlertDialog.Builder box = new AlertDialog.Builder(this);

        box.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //go back to main activity
            }
        });

        box.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noteList.remove(pos); //delete the note
                if(noteList.size() > 0){
                    getSupportActionBar().setTitle(getString(R.string.app_name)
                            + "(" + noteList.size() + ")");
                    noteAdapter.notifyDataSetChanged();//important
                }
                else{
                    getSupportActionBar().setTitle(getString(R.string.app_name));
                    noteAdapter.notifyDataSetChanged();
                }
            }
        });

        TextView title = v.findViewById(R.id.title);
        box.setTitle("Delete Note '" + title.getText().toString() + "'?");
        AlertDialog ad = box.create();
        ad.show();
        noteAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "The back button was pressed - Bye!",
                Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    //Jason File
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent receivedData){
        super.onActivityResult(requestCode, resultCode, receivedData);

        switch(requestCode){
            case 1:
                if(resultCode == 0){
                    String noteTitle = receivedData.getStringExtra("NEW_TITLE");
                    String noteContent = receivedData.getStringExtra("NEW_CONTENT");

                    noteList.add(0, new Note(noteTitle, noteContent, getPresentTime()));
                    getSupportActionBar().setTitle(getString(R.string.app_name) + "(" +
                            noteList.size() + ")");
                    noteAdapter.notifyDataSetChanged();
                }
                break;

            case 2:
                if(resultCode == 0){
                    String noteTitle = receivedData.getStringExtra("TITLE");
                    String noteContent = receivedData.getStringExtra("CONTENT");
                    noteList.remove(notesNumber);
                    noteList.add(0, new Note(noteTitle, noteContent, getPresentTime()));
                    getSupportActionBar().setTitle(getString(R.string.app_name) + "(" +
                            noteList.size() + ")");
                    noteAdapter.notifyDataSetChanged();
                }
                else if(resultCode == -1){
                    notesNumber = -1;
                }
                break;
        }
    }

//    private void loadJSONFile(){
//        try{
//            InputStream input = getApplicationContext().openFileInput("Note");
//            StringBuilder sb = new StringBuilder();
//            BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(input, "UTF-8"));
//            String s;
//
//            while((s = bufferedReader.readLine()) != null) {
//                sb.append(s);
//            }
//
//            JSONArray jsonArray = new JSONArray(sb.toString());
//
//            for(int i=0; i<jsonArray.length(); i++){
//                JSONObject noteObject = jsonArray.getJSONObject(i);
//
//                String noteTitle = noteObject.getString("noteTitle");
//                String noteContent = noteObject.getString("noteConent");
//                String lastSaveDate = noteObject.getString("lastSaveDate");
//                noteList.add(new Note(noteTitle, noteContent, lastSaveDate));
//            }
//            noteAdapter.notifyDataSetChanged();
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//        catch (JSONException e){
//            e.printStackTrace();
//        }
//    }
//
//    private void writeJSONFile(){
//        try{
//            FileOutputStream output = getApplicationContext().openFileOutput("Note", Context.MODE_PRIVATE);
//
//            JsonWriter JW = new JsonWriter(new OutputStreamWriter(output, "UTF-8"));
//            JW.setIndent("  ");
//            JW.beginArray();
//
//            for(int i=0; i<noteList.size(); i++){
//                JW.beginObject();
//
//                JW.name("noteTitle").value(noteList.get(i).getTitle());
//                JW.name("noteContent").value(noteList.get(i).getText());
//                JW.name("lastSaveDate").value(noteList.get(i).getLastUpdateTime());
//
//                JW.endObject();
//            }
//            JW.endArray();
//            JW.close();
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    private void loadJSONFile(){
        try{
            InputStream inputStream = getApplicationContext().openFileInput("NOTE");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,
                    "UTF-8"));
            StringBuilder stringBuilder = new StringBuilder();
            String s;
            while((s = bufferedReader .readLine()) != null) {
                stringBuilder.append(s);
            }

            JSONArray jsonArray = new JSONArray(stringBuilder.toString());
            for(int i=0; i<jsonArray.length(); i++){

                JSONObject noteObject = jsonArray.getJSONObject(i);

                String noteTitle = noteObject.getString("noteTitle");
                String noteText = noteObject.getString("noteText");
                String lastSaveDate = noteObject.getString("lastSaveDate");

                noteList.add(new Note(noteTitle, noteText, lastSaveDate));
            }

            noteAdapter.notifyDataSetChanged();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void writeJSONFile(){
        try{
            FileOutputStream outputStream = getApplicationContext().openFileOutput(
                    "NOTE", Context.MODE_PRIVATE);

            JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(outputStream,
                    "UTF-8"));
            jsonWriter.setIndent("  ");
            jsonWriter.beginArray();

            for(int i=0; i<noteList.size(); i++){
                jsonWriter.beginObject();
                jsonWriter.name("noteTitle").value(noteList.get(i).getTitle());
                jsonWriter.name("noteText").value(noteList.get(i).getText());
                jsonWriter.name("lastSaveDate").value(noteList.get(i).getLastUpdateTime());
                jsonWriter.endObject();
            }
            jsonWriter.endArray();
            jsonWriter.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    //get date and time
    public static String getPresentTime(){
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat lastUpdateDate = new SimpleDateFormat("EEE MMM d, h:mm a");
        return lastUpdateDate.format(date);
    }
    //onPause
    @Override
    protected void onPause(){
        this.writeJSONFile();
        super.onPause();
    }
}
