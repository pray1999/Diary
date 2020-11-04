package com.orzmo.daily;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orzmo.daily.core.Daily;
import com.orzmo.daily.core.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SendActivity extends AppCompatActivity {
    private static final String TAG = "SendActivity";
    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;
    private boolean isEdit = false;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_page);
        DatabaseHelper databaseHelper = new DatabaseHelper(this,"daily_db",null,1);
        this.db = databaseHelper.getWritableDatabase();

        this.id = String.valueOf(getIntent().getStringExtra("id"));

        if (!this.id.equals("null")) {
            getData(this.id);
            this.isEdit = true;
        }

        Button addImgButton = (Button) findViewById(R.id.button_addimg);
        Button sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDaily();
            }
        });

        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        EditText inputUsername = findViewById(R.id.input_username);

        this.sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        inputUsername.setText(username);
    }

    private void sendDaily() {
        EditText editTitle = findViewById(R.id.input_title);
        EditText editContent = findViewById(R.id.input_content);
        EditText editUsername = findViewById(R.id.input_username);

        if (String.valueOf(editTitle.getText()).equals("") || String.valueOf(editContent.getText()).equals("")) {
            Toast toast = Toast.makeText(SendActivity.this, "标题或者内容为空", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (String.valueOf(editUsername.getText()).equals("")) {
            Toast toast = Toast.makeText(SendActivity.this, "作者为空", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", String.valueOf(editUsername.getText()));
        editor.commit();

        Date date = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        ContentValues values = new ContentValues();
        values.put("username", String.valueOf(editUsername.getText()));
        values.put("title", String.valueOf(editTitle.getText()));
        values.put("content", String.valueOf(editContent.getText()));
        values.put("datetime", formatter.format(date));

        if(this.isEdit){
            db.update("daily", values, "id = ?", new String[] { this.id });
        }else {
            this.db.insert("daily",null,values);
        }



        Toast toast = Toast.makeText(SendActivity.this, "更新/发布成功！", Toast.LENGTH_LONG);
        toast.show();

        Intent intent = new Intent(SendActivity.this, MainActivity.class);
        startActivity(intent);




    }

    private void getData(String id) {
        Cursor cursor = db.query("daily", new String[]{"title","content","datetime","username"}, "id=?", new String[] {id}, null, null, null);
        //利用游标遍历所有数据对象
        //为了显示全部，把所有对象连接起来，放到TextView中
        Daily daily = new Daily();
        while(cursor.moveToNext()){
            System.out.println(cursor.getString(cursor.getColumnIndex("title")));
            daily.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            daily.setContent(cursor.getString(cursor.getColumnIndex("content")));
            daily.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            daily.setDatetime(cursor.getString(cursor.getColumnIndex("datetime")));
        }
        EditText editTitle = findViewById(R.id.input_title);
        EditText editContent = findViewById(R.id.input_content);
        editTitle.setText(daily.getTitle());
        editContent.setText(daily.getContent());

    }
}
