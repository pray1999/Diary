package com.orzmo.daily;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.orzmo.daily.core.Daily;
import com.orzmo.daily.core.DailyAdapter;
import com.orzmo.daily.core.DatabaseHelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.R.layout.*;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<Daily> dailyList = new ArrayList<Daily>();
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DatabaseHelper databaseHelper = new DatabaseHelper(this,"daily_db",null,1);
        this.db = databaseHelper.getWritableDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SendActivity.class);
                startActivity(intent);
            }
        });

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        DatabaseHelper databaseHelper = new DatabaseHelper(this,"daily_db",null,1);
        this.db = databaseHelper.getWritableDatabase();

        initDailys();
        DailyAdapter dailyAdapter = new DailyAdapter(MainActivity.this, R.layout.item, dailyList);
        ListView listView = (ListView) this.findViewById(R.id.list_view);
        listView.setAdapter(dailyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Daily daily = dailyList.get(i);
                Intent intent = new Intent(MainActivity.this, ReadActivity.class);
                intent.putExtra("id", String.valueOf(daily.getId()));
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar content_main clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        SharedPreferences sharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        if (username.equals("")) {
            username = "July";
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.commit();

//        initDailys();

    }
    private void initDailys() {
        this.dailyList.clear();
        Cursor cursor = db.query("daily", new String[]{"title","content","datetime","username","id"}, null, null, null, null, "datetime desc");
        //利用游标遍历所有数据对象
        //为了显示全部，把所有对象连接起来，放到TextView中
        while(cursor.moveToNext()){
            Daily daily = new Daily();
            daily.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            daily.setContent(cursor.getString(cursor.getColumnIndex("content")));
            daily.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            daily.setDatetime(cursor.getString(cursor.getColumnIndex("datetime")));
            daily.setId(cursor.getInt(cursor.getColumnIndex("id")));
            dailyList.add(daily);
        }
        cursor.close();
        db.close();
    }
}
