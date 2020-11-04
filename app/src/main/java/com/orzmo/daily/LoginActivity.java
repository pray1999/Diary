package com.orzmo.daily;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.orzmo.daily.core.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Button login;//登录按钮
    private Button cancel;//取消按钮
    private EditText adminEdit;//用户名输入框
    private EditText pwdEdit;//密码输入框
    protected CheckBox savePwd;//是否保存密码复选框
    private CheckBox showPwd;//显示或隐藏密码复选框




    public static DatabaseHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        将背景图与状态栏融合到一起，只支持Android5.0以上的版本
//        if(Build.VERSION.SDK_INT>=21){
//            View decorView = getWindow().getDecorView();
//            //布局充满整个屏幕
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            ///设置状态栏为透明
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//
//        }
        setContentView(R.layout.activity_login);
        //获取各组件或对象的实例
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        login = (Button)findViewById(R.id.login);
        cancel = (Button)findViewById(R.id.cancel);
        adminEdit = (EditText)findViewById(R.id.admin);
        pwdEdit = (EditText)findViewById(R.id.pwd);
        savePwd = (CheckBox)findViewById(R.id.save_pwd);
        showPwd = (CheckBox)findViewById(R.id.show_pwd);



        //获取当前“是否保存密码”的状态
        final boolean isSave = pref.getBoolean("save_pwd",false);
        //当“是否保存密码”均选时，从Shared Preferences对象中读出保存的内容，并显示出来
        if(isSave){
            String account = pref.getString("account","");
            String pwd = pref.getString("pwd","");
            adminEdit.setText(account);
            pwdEdit.setText(pwd);
            //把光标移到文本末尾处
            adminEdit.setSelection(adminEdit.getText().length());
            pwdEdit.setSelection(pwdEdit.getText().length());
            savePwd.setChecked(true);
        }
        //用户点击登录时的处理事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = adminEdit.getText().toString();
                String pwd = pwdEdit.getText().toString();
                //用户名和密码正确
                if(account.equals("july")&&pwd.equals("july")){
                    editor=pref.edit();
                    //“是否保存密码”勾选
                    if(savePwd.isChecked()){
                        editor.putBoolean("save_pwd",true);
                        editor.putString("account",account);
                        editor.putString("pwd",pwd);
                    }else{
                        editor.clear();
                    }
                    //提交完成数据存储
                    editor.apply();
                    //显示登陆成功并跳转到主界面活动
                    Toast.makeText(LoginActivity.this,"登录成功！",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    //结束当前活动
                    finish();
                }
                //用户名或当前密码错误
                else {
                    Toast.makeText(LoginActivity.this,"用户名或密码错误，请重新登录！",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //用户点击“显示密码”复选框
        showPwd.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(showPwd.isChecked()){
//                    pwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showOrHide(pwdEdit,true);
                }else{
//                    pwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showOrHide(pwdEdit,false);

                }
            }
        });


    }
    //当用户离开活动时，检测是否勾选“记住密码”，若勾选，则保存用户输入的用户名及密码
    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor = pref.edit();
        String account = adminEdit.getText().toString();
        String pwd = pwdEdit.getText().toString();
        if(savePwd.isChecked()){
            editor.putBoolean("save_pwd",true);
            editor.putString("account",account);
            editor.putString("pwd",pwd);
        }else{
            editor.clear();
        }
        editor.apply();
    }
    //显示或隐藏密码
    public void showOrHide(EditText pwdEdit,boolean isShow){
        //记住光标开始的位置
        int pos = pwdEdit.getSelectionStart();
        if(isShow){
            pwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            pwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        pwdEdit.setSelection(pos);
    }




}