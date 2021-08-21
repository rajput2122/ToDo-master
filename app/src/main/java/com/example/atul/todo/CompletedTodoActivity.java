package com.example.atul.todo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class CompletedTodoActivity extends AppCompatActivity {

    private static final String TAG = "CompletedTodoActivity";
    private DatabaseHelper databaseHelper;
    private ArrayList<DataModel> items;
    private ItemAdapter itemsAdopter;
    private ListView itemsListView;
    private FloatingActionButton fab;
    private ToggleButton toggleTheme;
    private SharedPref sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = new SharedPref(this);

        //load theme preference
        if (sharedPreferences.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_todo);

        //set custom action bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        //toggle to change theme and save uer preference
        toggleTheme = findViewById(R.id.themeActionButton);
        if (sharedPreferences.loadNightModeState()) {
            toggleTheme.setChecked(true);
        }
        toggleTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.setNightModeState(true);
                } else {
                    sharedPreferences.setNightModeState(false);
                }
                restartApp();
            }
        });

        databaseHelper = new DatabaseHelper(this);
        fab = findViewById(R.id.fab);
        itemsListView = findViewById(R.id.itemsList);

        populateListView();
        onFabClick();
        hideFab();

    }

    //on theme change refresh activity
    private void restartApp() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
        Log.d(TAG, "restartApp: Changed theme successfully");
    }

    //Populate listView with data from database
    private void populateListView() {
        try {
            items = databaseHelper.getAllDataFromCompleted();
            Log.d(TAG, "populateListView: Displaying data in list view" + items.size());
            if(items.size()==0){
                //initialise and set empty listView
                TextView empty = findViewById(R.id.emptyTextView);
                empty.setText(Html.fromHtml(getString(R.string.listEmptyText)));
                FrameLayout emptyView = findViewById(R.id.emptyView);
                itemsListView.setEmptyView(emptyView);
            }else{
                itemsAdopter = new ItemAdapter(this, items);
                itemsListView.setAdapter(itemsAdopter);
                itemsAdopter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Hide fab on list scroll
    private void hideFab() {
        itemsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    fab.show();
                }else{
                    fab.hide();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    //On floating button click open dialog
    private void onFabClick() {
        try {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showAddDialog();
                    Log.d(TAG, "onFabClick: Opened edit dialog");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}