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
public class ListT extends AppCompatActivity {
    SQLiteDatabase db;
    ListView lvList;
    ArrayList<String> list;
    ArrayAdapter<String> adap;
    TextView tvList;
    String strId,strName,type;
    String sname, sroom, sdays, stime;
    String h_name, h_date, h_time;
    String [] iD;
    String sid;
    int index = 0,origid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_t);
        try {
            lvList = (ListView)findViewById(R.id.lvList2);
            tvList = (TextView)findViewById(R.id.tvList);


            db = openOrCreateDatabase("app", MODE_PRIVATE, null);

            type = getIntent().getExtras().getString("type");
            strId = getIntent().getExtras().getString("id");
            strName = getIntent().getExtras().getString("name");

            tvList.setText("List of "+type);
            tvList.setAllCaps(true);

            if (type.equalsIgnoreCase("Classes")) {

                list = new ArrayList<>();

                Cursor c = db.rawQuery("select * from subject where a_id = '"+strId+"'",null);
                c.moveToFirst();

                if (c.getCount()>0){
                    do {
                        sname = c.getString(c.getColumnIndex("s_name"));
                        sroom = c.getString(c.getColumnIndex("room"));
                        sdays = c.getString(c.getColumnIndex("days"));
                        stime = c.getString(c.getColumnIndex("time"));

                        Cursor d = db.rawQuery("select  * from class where s_name = '"+sname+"'",null);
                        d.moveToFirst();
                        list.add(sname+"("+d.getCount()+")\t\t\t"+sroom+"\t\t\t"+sdays+"\t\t\t"+stime);

                    }while(c.moveToNext());

                    adap = new ArrayAdapter<String>(ListT.this, android.R.layout.simple_list_item_1,list);
                    lvList.setAdapter(adap);
                }else{
                    Toast.makeText(this, "You haven't created any subject!", Toast.LENGTH_SHORT).show();
                }

            }else if (type.equalsIgnoreCase("Homework")) {

                Cursor c = db.rawQuery("select homework.s_name, homework.d_date, homework.time, homework.instruction " +
                        "from homework " +
                        "INNER JOIN subject " +
                        "on homework.s_name = subject.s_name " +
                        "where subject.a_id = '"+strId+"' " +
                        "group by instruction",null);
                c.moveToFirst();

                iD = new String[c.getCount()];
                list = new ArrayList<>();

                if (c.getCount()>0){
                    do {
                        h_name = c.getString(c.getColumnIndex("homework.s_name"));
                        h_date= c.getString(c.getColumnIndex("homework.d_date"));
                        h_time = c.getString(c.getColumnIndex("homework.time"));
                        String instruct = c.getString(c.getColumnIndex("homework.instruction"));

                        Cursor h = db.rawQuery("select * from class where s_name = '"+h_name+"'",null);//student in a class
                        h.moveToFirst();

                        int student = h.getCount();
                        h.close();

                        Cursor k = db.rawQuery("select * from finish where instruction = '"+instruct+"'",null);//submitted homework
                        k.moveToFirst();

                        int submitted = k.getCount();
                        list.add(h_name+"("+submitted+"/"+student+")\t\t\t\t\t\t\t\t\t\t"+h_date+"("+h_time+")");

                        iD[index] = instruct;
                        ++index;
                    }while(c.moveToNext());

                    adap = new ArrayAdapter<String>(ListT.this, android.R.layout.simple_list_item_1,list);
                    lvList.setAdapter(adap);

                }else{
                    Toast.makeText(this, "You haven't added any homework, yet!", Toast.LENGTH_SHORT).show();
                }
                lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try{

                            String orig = iD[i];


                            Cursor k = db.rawQuery("select * from finish where instruction = '"+orig+"'",null);//submitted homework
                            k.moveToFirst();

                            int submitted = k.getCount();


                            if(submitted == 0){
                                Toast.makeText(getApplicationContext(), "No Student Submit This Homework!", Toast.LENGTH_SHORT).show();
                            }else {
                                finish();
                                Intent a = new Intent(getApplicationContext(), Submitted.class);
                                a.putExtra("h_id", orig);
                                a.putExtra("type", type);
                                a.putExtra("id", strId);
                                a.putExtra("name", strName);
                                startActivity(a);
                            }
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else if(type.equalsIgnoreCase("Subjects")){

                Cursor c = db.rawQuery("select * from subject where a_id = '"+strId+"'",null);
                c.moveToFirst();


                iD = new String[c.getCount()];
                list = new ArrayList<>();

                if (c.getCount()>0){
                    do {
                        sname = c.getString(c.getColumnIndex("s_name"));

                        list.add(sname);
                        iD[index] = sname;
                        index++;

                    }while(c.moveToNext());
                    adap = new ArrayAdapter<String>(ListT.this, android.R.layout.simple_list_item_1,list);
                    lvList.setAdapter(adap);

                    lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            String subj = iD[i];

                            Cursor c = db.rawQuery("select * from homework where s_name = '"+subj+"'",null);
                            c.moveToFirst();

                            if(c.getCount() > 0){
                                finish();
//                                Intent a = new Intent(getApplicationContext(), Grades.class);
//                                a.putExtra("s_name", subj);
//                                a.putExtra("type", type);
//                                a.putExtra("id", strId);
//                                a.putExtra("name", strName);
//                                startActivity(a);
                            }else{
                                Toast.makeText(getApplicationContext(), "No Homework Yet!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(this, "You haven't created any subject!", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void process1(View v){

        if(type.equalsIgnoreCase("Classes")){
            finish();
            Intent i = new Intent(getApplicationContext(), NewClasses.class);
            i.putExtra("type", type);
            i.putExtra("id",strId);
            i.putExtra("name",strName);
            startActivity(i);
        }else if(type.equalsIgnoreCase("Homework")){
            finish();
            Intent i = new Intent(getApplicationContext(), NewHomework.class);
            i.putExtra("type", type);
            i.putExtra("id",strId);
            i.putExtra("name",strName);
            startActivity(i);
        }else if(type.equalsIgnoreCase("Subjects")){
            finish();
            Intent i = new Intent(getApplicationContext(), NewClasses.class);
            i.putExtra("type", type);
            i.putExtra("id",strId);
            i.putExtra("name",strName);
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(getApplicationContext(), Teacher.class);
        i.putExtra("id",strId);
        i.putExtra("name",strName);
        startActivity(i);
    }
}

