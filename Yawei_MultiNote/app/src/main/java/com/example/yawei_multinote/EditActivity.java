package com.example.yawei_multinote;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        editContent.setMovementMethod(new ScrollingMovementMethod());

        Intent i = getIntent();
        if(i.getExtras() != null){
            editTitle.setText(i.getStringExtra("TITLE"));
            editContent.setText(i.getStringExtra("CONTENT"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.Save_menuItem){
            Intent backToMain = new Intent();

            //A note without a title is not allowed to be saved.
            if(editTitle.getText().toString().isEmpty()){
                setResult(-1, backToMain);
                finish();
                Toast.makeText(this,
                        "Untitled Note is not allowed to be saved.",
                        Toast.LENGTH_LONG).show();
            }
            //A note with a title is allowed to be saved.
            else{
                Intent intent = getIntent();
                if(intent.getExtras() != null){ //edit a exist note
                    String title = editTitle.getText().toString();
                    String content = editContent.getText().toString();

                    //user made no changes
                    if(title.equals(intent.getStringExtra("TITLE")) &&
                            content.equals(intent.getStringExtra("CONTENT"))){
                        setResult(-1, backToMain);
                        finish();
                    }
                    //user made changes
                    else{
                        backToMain.putExtra("TITLE",
                                editTitle.getText().toString());
                        backToMain.putExtra("CONTENT",
                                editContent.getText().toString());
                        setResult(0, backToMain);
                        finish();
                    }
                }
                //New Note
                else{
                    backToMain.putExtra("NEW_TITLE",
                            editTitle.getText().toString());
                    backToMain.putExtra("NEW_CONTENT",
                            editContent.getText().toString());
                    setResult(0, backToMain);
                    finish();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        //A note without a title is not allowed to be saved
        if(editTitle.getText().toString().isEmpty()){
            Intent backToMain = new Intent();
            setResult(-1, backToMain);
            Toast.makeText(this,
                    "Untitled Note is not allowed to be saved.",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        //A note with a title is allowed to be saved.
        else{
            Intent intent1 = new Intent();
            Intent intent2 = getIntent();

            //user edit a exist note
            if(intent2.getExtras() != null) {
                String title = editTitle.getText().toString();
                String content = editContent.getText().toString();
                //NO changes
                if (title.equals(intent2.getStringExtra("TITLE"))
                && content.equals(intent2.getStringExtra("CONTENT"))) {
                    setResult(-1, intent1);
                    finish();
                }
                //MAKE changes
                //SHOW dialog box
                else {
                    AlertDialog.Builder mesBox = new AlertDialog.Builder(this);

                    mesBox.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent backToMain = new Intent();
                            Intent intent = getIntent();
                            //Editting An Old Note
                            if(intent.getExtras() != null){
                                String title = editTitle.getText().toString();
                                String content = editContent.getText().toString();
                                backToMain.putExtra("TITLE",
                                        editTitle.getText().toString());
                                backToMain.putExtra("CONTENT",
                                        editContent.getText().toString());
                                setResult(0, backToMain);
                                finish();
                            }
                            //New Note
                            else{
                                backToMain.putExtra("NEW_TITLE",
                                        editTitle.getText().toString());
                                backToMain.putExtra("NEW_CONTENT",
                                        editContent.getText().toString());
                                setResult(0, backToMain);
                                finish();
                            }
                        }
                    });

                    mesBox.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent backToMain = new Intent();
                            setResult(-1, backToMain);
                            finish();
                        }
                    });

                    mesBox.setTitle("Your note is not saved!");
                    mesBox.setMessage("Save '" + editTitle.getText().toString() + "'?");
                    AlertDialog ad = mesBox.create();
                    ad.show();
                }
            }
            else{ //new note added
                AlertDialog.Builder box = new AlertDialog.Builder(this);

                //User wants to save the note
                box.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent backToMain = new Intent();
                        backToMain.putExtra("NEW_TITLE",
                                editTitle.getText().toString());
                        backToMain.putExtra("NEW_CONTENT",
                                editContent.getText().toString());
                        setResult(0, backToMain);
                        finish();
                    }
                });

                box.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent backToMain = new Intent();
                        setResult(-1, backToMain);
                        finish();
                    }
                });

                box.setTitle("Your note is not saved!");
                box.setMessage("Save '" + editTitle.getText().toString() + "'?");
                AlertDialog ad = box.create();
                ad.show();
            }
        }
    }
}
