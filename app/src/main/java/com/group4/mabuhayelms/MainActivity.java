package com.group4.mabuhayelms;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    EditText etUsername,etPassword;
    SQLiteDatabase db;
    Cursor c;
    AlertDialog.Builder ab;

    String strUsername,strPassword;
    String strAccounttype,strId,strName,strValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            etUsername = (EditText)findViewById(R.id.etUsername);
            etPassword = (EditText)findViewById(R.id.etPassword);


            db = openOrCreateDatabase("app", MODE_PRIVATE, null);

            db.execSQL("create table if not exists account(id INTEGER PRIMARY KEY AUTOINCREMENT, accounttype VARCHAR, name VARCHAR, username VARCHAR, password VARCHAR, v_date VARCHAR)");

            db.execSQL("create table if not exists subject(s_id INTEGER PRIMARY KEY AUTOINCREMENT, s_name VARCHAR, room VARCHAR, days VARCHAR, time VARCHAR, a_id VARCHAR)");

            db.execSQL("create table if not exists class(c_id INTEGER PRIMARY KEY AUTOINCREMENT, s_name VARCHAR, a_id VARCHAR)");

            db.execSQL("create table if not exists homework(h_id INTEGER PRIMARY KEY AUTOINCREMENT, a_id VARCHAR, s_name VARCHAR, instruction VARCHAR, attachment VARCHAR, d_date VARCHAR, time VARCHAR, status VARCHAR, score VARCHAR, item VARCHAR)");

            db.execSQL("create table if not exists finish(f_id INTEGER PRIMARY KEY AUTOINCREMENT, h_id VARCHAR, a_id VARCHAR, instruction VARCHAR, answer VARCHAR, attachment VARCHAR, c_date VARCHAR, c_time VARCHAR, score VARCHAR, item VARCHAR)");

            Cursor c = db.rawQuery("select * from account",null);
            c.moveToFirst();

            if(c.getCount() == 0){
                db.execSQL("insert into account(accounttype,name,username,password) values ('admin','Administrator','admin','@dmin')");
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    public void login(View v){
        try{
            strUsername = etUsername.getText().toString();
            strPassword = etPassword.getText().toString();


            c = db.rawQuery("select * from account where username = '"+strUsername+"' and password = '"+strPassword+"'",null);
            c.moveToFirst();

            if(c.getCount() > 0){
                strAccounttype = c.getString(c.getColumnIndex("accounttype"));
                strId = c.getString(c.getColumnIndex("id"));
                strName = c.getString(c.getColumnIndex("name"));
                strValid = c.getString(c.getColumnIndex("v_date"));
                if(strAccounttype.equalsIgnoreCase("admin")){
                    ab = new AlertDialog.Builder(this);
                    ab.setCancelable(true);
                    ab.setTitle("NOTICE");
                    ab.setMessage("Welcome to eHMS, "+strName+"!");
                    ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {

                            Intent i = new Intent(getApplicationContext(), Admin.class);
                            i.putExtra("id",strId);
                            i.putExtra("name",strName);
                            startActivity(i);
                        }
                    });
                    ab.show();
                }else if(strAccounttype.equalsIgnoreCase("teacher")){
                    ab = new AlertDialog.Builder(this);
                    ab.setCancelable(true);
                    ab.setTitle("NOTICE");
                    ab.setMessage("Welcome to eHMS, "+strName+"!");
                    ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {

                            Intent i = new Intent(getApplicationContext(), Teacher.class);
                            i.putExtra("id",strId);
                            i.putExtra("name",strName);
                            startActivity(i);
                        }
                    });
                    ab.show();
                }else if(strAccounttype.equalsIgnoreCase("student")){

                    String c_date = new java.text.SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());

                    if(strValid.equalsIgnoreCase(c_date)){
                        ab = new AlertDialog.Builder(this);
                        ab.setCancelable(true);
                        ab.setTitle("NOTICE");
                        ab.setMessage("Account is Expired!");
                        ab.show();
                    }else {
                        ab = new AlertDialog.Builder(this);
                        ab.setCancelable(true);
                        ab.setTitle("NOTICE");
                        ab.setMessage("Welcome to eHMS, " + strName + "!");
                        ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {

                                Intent i = new Intent(getApplicationContext(), Student.class);
                                i.putExtra("id", strId);
                                i.putExtra("name", strName);
                                startActivity(i);
                            }
                        });
                        ab.show();
                    }
                }
            }else{
                ab = new AlertDialog.Builder(this);
                ab.setCancelable(true);
                ab.setTitle("NOTICE");
                ab.setMessage("Wrong Username or Password!");
                ab.show();
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
