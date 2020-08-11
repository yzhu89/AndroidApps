package com.example.newsgateway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.Source;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    //DrawerLayout variables
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    //Receiver
    private NewsReceiver newsReceiver;
    private ArrayList<NewsSource> sourceList = new ArrayList<>();
    //Fragment
    private List<Fragment> fragmentsList;
    //Adapter
    private ArrayAdapter<NewsSource> newsSourceArrayAdapter;
    private MainPageAdapter pageAdapter;
    //ViewPager
    private ViewPager pager;

    //Intent filter
    static final String REQUEST = "REQUEST_ARTICLES";
    static final String RESPONSE = "RESPONSE_ARTICLES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Download the news sources
        Intent intent = new Intent(this, NewsService.class);
        startService(intent); //need to destroy

        newsReceiver = new NewsReceiver();
        IntentFilter filter_response = new IntentFilter(RESPONSE);
        IntentFilter filter_request = new IntentFilter(REQUEST);
        registerReceiver(newsReceiver, filter_response);
        registerReceiver(newsReceiver, filter_request);

        //Set Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.drawer_list);

        newsSourceArrayAdapter =
                new ArrayAdapter<>(this, R.layout.drawer_list_item, sourceList);
        drawerList.setAdapter(newsSourceArrayAdapter);
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        drawerToggle.syncState();

        //Aet Fragments
        fragmentsList = new ArrayList<Fragment>();
        pageAdapter = new MainPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);

        //Load the data
        new AsyncSourceTask(this).execute("");

        if (savedInstanceState != null){
            fragmentsList = (List<Fragment>) savedInstanceState.getSerializable("fList");
            setTitle(savedInstanceState.getString("title"));
            pageAdapter.notifyDataSetChanged();
            for (int i = 0; i< pageAdapter.getCount(); i++)
                pageAdapter.notifyChangeInPosition(i);
        }
    }

    //Drawer and Menu
    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;

        if (item.toString().equals("all")){
            new AsyncSourceTask(this).execute("");
        }
        else{
            new AsyncSourceTask(this).execute(item.toString());
        }
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    //When User select items in Menu
    private void selectItem(int position) {
        setTitle(sourceList.get(position).getName());

        Intent requestIntent = new Intent();
        requestIntent.setAction(MainActivity.REQUEST);
        requestIntent.putExtra("SOURCE", sourceList.get(position).getId());
        sendBroadcast(requestIntent);

        drawerLayout.closeDrawer(drawerList);
    }

    //Add source to the Array adapter
    public void addSource(NewsSource newsSource){
        sourceList.add(newsSource);
        newsSourceArrayAdapter.notifyDataSetChanged();
    }

    //Clear the Source List
    public void clearSource(){
        sourceList.clear();
    }

    private class MainPageAdapter extends FragmentPagerAdapter{
        private long base = 0;
        public MainPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        @Override
        public long getItemId(int position) {
            return base + position;
        }

        public void notifyChangeInPosition(int n) {
            base += getCount() + n;
        }
    }

    public class NewsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;

            switch (action){
                case RESPONSE:
                    if(intent.getSerializableExtra("articles") != null) {
                        ArrayList<Article> articles =
                                (ArrayList<Article>) intent.getSerializableExtra("articles");
                        fragmentsList.clear();
                        for (int i = 0; i < articles.size(); i++) {
                            fragmentsList.add(ArticleFragment.newInstance(articles.get(i)));
                            pageAdapter.notifyChangeInPosition(i);
                        }
                        pageAdapter.notifyDataSetChanged();
                        pager.setCurrentItem(0);
                    }

                default:
                    Log.d(TAG, "onReceive: Unknown broadcast received");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("fList", (Serializable) fragmentsList);
        outState.putString("title",getTitle().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(this, NewsService.class);
        stopService(intent);
        super.onDestroy();
    }

}

