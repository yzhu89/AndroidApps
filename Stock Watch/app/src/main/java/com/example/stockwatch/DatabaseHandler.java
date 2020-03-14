package com.example.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHandler";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    // DB Name
    private static final String DATABASE_NAME = "StockDB";
    // DB Table Name
    private static final String TABLE_NAME = "StockTable";
    //DB Columns
    private static final String NAME = "Name";
    private static final String SYMBOL = "Symbol";

    private SQLiteDatabase database;

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    SYMBOL + " TEXT not null unique, " +
                    NAME + " TEXT not null)";

    public DatabaseHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //get stock info and return back
    public ArrayList<String[]> loadStockInfo(){
        Log.d(TAG, "loadStockInfo: Start Load");
        ArrayList<String[]> stockArrayList = new ArrayList<>();

        Cursor cursor = database.query(
            TABLE_NAME, // The table to query
            new String[]{SYMBOL, NAME}, // The columns to return
            null,
            null,
            null,
            null,
            null);

        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);

                stockArrayList.add(new String[]{symbol, name});
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStockInfo: Done load");
        return stockArrayList;
    }

    public void addStock(String symbol, String name){
        Log.d(TAG, "addStock: Adding " + symbol);
        ContentValues values = new ContentValues();
        values.put(SYMBOL, symbol);
        values.put(NAME, name);


        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addStock: add stock" + key);
    }

    void dumpDbToLog() {
        Cursor cursor = database.query(TABLE_NAME,
                new String[]{SYMBOL, NAME},
                null,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String name = cursor.getString(1);

                Log.d(TAG, "dumpDbToLog: SYMBOL: " + symbol + " NAME: " + name);
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    public void deleteStock(String symbol){
        Log.d(TAG, "deleteStock: " + symbol);
        //Delete a stock from the database
        int cnt = database.delete(TABLE_NAME, SYMBOL + " = ?", new String[]{symbol});
        Log.d(TAG, "deleteStock: Deleted = " + cnt);
    }
    void shutDown() {
        database.close();
    }

}
