package com.example.stockwatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{

    //initialize
    private final String MESSAGE_TITLE = "No Internet Connection";
    private final String MESSAGE_CONTENT =
            "Stocks Cannot Be Updated Without A Network Connection";
    private SwipeRefreshLayout swiper;
    private ArrayList<String[]> dbStockList;
    private DatabaseHandler dbHandler;
    private RecyclerView recyclerView;
    private final List<Stock> stockList = new ArrayList<>();
    private StockAdapter stockAdapter;
    private HashMap<String, String> stockMap;
    private int stockPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        recyclerView = findViewById(R.id.recycler);
        stockAdapter = new StockAdapter(this, stockList);

        recyclerView.setAdapter(stockAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHandler = new DatabaseHandler(MainActivity.this);
        dbStockList = dbHandler.loadStockInfo();
        if(isConnected()){
            for(int i=0; i<dbStockList.size(); i++){
                new DownloadTask(this, -1).execute(new String[] {dbStockList.get(i)[0],
                        dbStockList.get(i)[1]});
            }
            new InfoLoaderTask(this).execute();
        }
        else{ //Not Connected
            for(int i=0; i < dbStockList.size(); i++){
                Stock currStock = new Stock(dbStockList.get(i)[0],
                        dbStockList.get(i)[1],0,0,0);
                stockList.add(currStock);
            }
        }
        stockAdapter.notifyDataSetChanged();

    }

    //Menu Item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Show the meanu icon
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.Add_menu){
            //We need to check if the App connected to the internet
            if(isConnected()){
                //Add stocks
                LayoutInflater layoutInflater = LayoutInflater.from(this);
                final View v = layoutInflater.inflate(R.layout.dialog, null);
                //Message box
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(v);
                builder.setTitle("Stock Selection");
                builder.setMessage("Please Enter A Stock Symbol:  ");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText userInput = v.findViewById(R.id.userInput);
                        String inputSymbol = userInput.getText().toString();
                        search(inputSymbol);
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
            }else{
                //App have not connected to the internet
                AlertDialog.Builder message =
                        new AlertDialog.Builder(MainActivity.this);//is this OK?
                message.setTitle(MESSAGE_TITLE);
                message.setMessage(MESSAGE_CONTENT);
                AlertDialog ad = message.create();
                ad.show();
            }
        }
        return super.onOptionsItemSelected(item);
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

    public void search(String inputText){
        if(!inputText.isEmpty()){   //User typed something
            for(int i=0; i < stockList.size(); i++){ //check the stocklist first
                if(stockList.get(i).getSymbol().equals(inputText)){ //already exists
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Duplicate Stock");
                    alert.setMessage("Stock Symbol " + inputText + " is already displayed.");
                    AlertDialog alertDialog = alert.create();
                    alertDialog.show();
                    return;
                }
            }

            //We check the substrings in userInput
            final List<String> list = new ArrayList<String>();

            for(Map.Entry<String,String> entry: stockMap.entrySet()){
                String symbol = entry.getKey();
                String name = entry.getValue();

                if(symbol.contains(inputText) || name.contains(inputText)){
                    list.add(symbol);
                }
            }

            if(list.size() > 0){
                final CharSequence[] listPrompt = new CharSequence[list.size()];
                for(int i=0; i< list.size(); i++){
                    listPrompt[i] = list.get(i) + " - " + stockMap.get(list.get(i));
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Make a selection");
                builder.setItems(listPrompt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DownloadTask(MainActivity.this, -1).
                                execute(new String[]{list.get(which),
                                        stockMap.get(list.get(which))});
                        dbHandler.addStock(list.get(which), stockMap.get(list.get(which)));
                    }
                });

                builder.setNegativeButton("NEVERMIND", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                AlertDialog ad = builder.create();
                ad.show();
            }
            else{ //NOT FOUND
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Symbol Not Found: " + inputText);
                builder.setMessage("Data for stock symbol");

                AlertDialog ad = builder.create();
                ad.show();
            }
        }
    }

    public void doRefresh(){
        if(isConnected()){
            for(int i=0; i < stockList.size(); i++){
                new DownloadTask(this, i).execute
                        (new String[] {stockList.get(i).getSymbol(),
                                stockList.get(i).getCompany()});
            }
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(MESSAGE_TITLE);
            builder.setMessage(MESSAGE_CONTENT);
            AlertDialog ad = builder.create();
            ad.show();
        }
        stockAdapter.notifyDataSetChanged();
        swiper.setRefreshing(false);
    }

    @Override
    public void onClick(View v) {
        if(isConnected()){
            stockPos = recyclerView.getChildAdapterPosition(v);
            String tempSymbol = stockList.get(stockPos).getSymbol();
            String url = "https://www.marketwatch.com/investing/stock/" + tempSymbol;

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(MESSAGE_TITLE);
            builder.setMessage(MESSAGE_CONTENT);
            AlertDialog ad = builder.create();
            ad.show();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int pos = recyclerView.getChildLayoutPosition(v);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stockList.remove(pos);
                stockAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        TextView s = v.findViewById(R.id.symbol);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + s.getText().toString() + " ?");
        AlertDialog ad = builder.create();
        ad.show();
        stockAdapter.notifyDataSetChanged();;
        return true;

    }

    public void generateHashMap(HashMap<String,String> HM){
        this.stockMap = HM;
    }

    public void addStock(Stock s){
        stockList.add(s);
        stockAdapter.notifyDataSetChanged();
    }

    public void updateStock(Stock s, int pos){
        stockList.remove(pos);
        stockList.add(pos, s);
        stockAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume(){
        dbHandler.dumpDbToLog();
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        dbHandler.shutDown();
        super.onDestroy();
    }

}
