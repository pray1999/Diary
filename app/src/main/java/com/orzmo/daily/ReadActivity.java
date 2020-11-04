package com.orzmo.daily;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orzmo.daily.core.Daily;
import com.orzmo.daily.core.DatabaseHelper;

public class ReadActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Daily daily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.check_daily);
        DatabaseHelper databaseHelper = new DatabaseHelper(this,"daily_db",null,1);
        this.db = databaseHelper.getWritableDatabase();
        String id = String.valueOf(getIntent().getStringExtra("id"));

        Cursor cursor = db.query("daily", new String[]{"title","content","datetime","username", "id"}, "id=?", new String[] {id}, null, null, null);
        //利用游标遍历所有数据对象
        //为了显示全部，把所有对象连接起来，放到TextView中
        while(cursor.moveToNext()){
            this.daily = new Daily();
            daily.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            daily.setContent(cursor.getString(cursor.getColumnIndex("content")));
            daily.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            daily.setDatetime(cursor.getString(cursor.getColumnIndex("datetime")));
            daily.setId(cursor.getInt(cursor.getColumnIndex("id")));
        }

        TextView titleView = (TextView)findViewById(R.id.check_textview_title);
        TextView contentView = (TextView)findViewById(R.id.check_textview_content);

        titleView.setText(daily.getTitle());
        contentView.setText(daily.getContent());

        Button button = (Button) findViewById(R.id.button_edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReadActivity.this, SendActivity.class);
                intent.putExtra("id", String.valueOf(daily.getId()));
                startActivity(intent);
            }
        });

        Button delete = (Button) findViewById(R.id.button_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.delete("daily","id = ?",new String[]{String.valueOf(daily.getId())});
                Toast toast = Toast.makeText(ReadActivity.this, "删除成功！", Toast.LENGTH_LONG);
                toast.show();

                Intent intent = new Intent(ReadActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
