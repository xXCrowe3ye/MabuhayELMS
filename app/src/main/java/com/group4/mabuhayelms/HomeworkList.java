package com.group4.mabuhayelms;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
public class HomeworkList extends AppCompatActivity {

    TextView tvStatus;
    ListView lvHomeworks;
    ArrayList<String> homework;
    ArrayAdapter<String> adapter1;
    SQLiteDatabase db;
    Cursor c;
    String strId,strName,strStatus;
    String[] idd;
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_list);
        try{
            tvStatus = (TextView)findViewById(R.id.tvStatus);
            lvHomeworks = (ListView)findViewById(R.id.lvHomeworks);


            db = openOrCreateDatabase("app", MODE_PRIVATE, null);

            strId = getIntent().getExtras().getString("id");
            strName = getIntent().getExtras().getString("name");
            strStatus = getIntent().getExtras().getString("status");

            tvStatus.setText(strStatus);

            if(strStatus.equalsIgnoreCase("submitted")){

                c = db.rawQuery("select finish.f_id, finish.c_date, finish.c_time, homework.s_name " +
                        "from finish " +
                        "INNER JOIN homework " +
                        "on finish.h_id = homework.h_id " +
                        "where finish.a_id = '"+strId+"'",null);


                c.moveToFirst();
            }else{

                c = db.rawQuery("select * from homework where a_id = '"+strId+"' and status = '"+strStatus+"'",null);


                c.moveToFirst();
            }
            idd = new String[c.getCount()];
            homework = new ArrayList<>();


            if(c.getCount() > 0){
                do{

                    if(strStatus.equalsIgnoreCase("submitted")){
                        String id = c.getString(c.getColumnIndex("finish.f_id"));
                        String subject = c.getString(c.getColumnIndex("homework.s_name"));
                        String c_date = c.getString(c.getColumnIndex("finish.c_date"));
                        String c_time = c.getString(c.getColumnIndex("finish.c_time"));

                        homework.add(subject+"\t\t\t\t\t\t\t\t\tDue: "+c_date+"("+c_time+")");
                        idd[index] = id;
                    }else {
                        String id = c.getString(c.getColumnIndex("h_id"));
                        String subject = c.getString(c.getColumnIndex("s_name"));
                        String d_date = c.getString(c.getColumnIndex("d_date"));
                        String time = c.getString(c.getColumnIndex("time"));

                        homework.add(subject+"\t\t\t\t\t\t\t\t\tDue: "+d_date+"("+time+")");
                        idd[index] = id;
                    }
                    index++;
                }while (c.moveToNext());
            }
            adapter1 = new ArrayAdapter<String>(HomeworkList.this, android.R.layout.simple_list_item_1,homework);
            lvHomeworks.setAdapter(adapter1);

            if(strStatus.equalsIgnoreCase("submitted") || strStatus.equalsIgnoreCase("late-submitted")){
            }else{
                lvHomeworks.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        int ID = Integer.parseInt(idd[i]);
//                        Toast.makeText(getApplicationContext(), ID+"", Toast.LENGTH_SHORT).show();
                        Intent j = new Intent(getApplicationContext(), Homework.class);
                        j.putExtra("id",strId);
                        j.putExtra("h_id",ID);
                        j.putExtra("status",strStatus);
                        j.putExtra("name",strName);
                        startActivity(j);
                    }
                });
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(getApplicationContext(), Student.class);
        i.putExtra("id",strId);
        i.putExtra("name",strName);
        startActivity(i);
    }
}


