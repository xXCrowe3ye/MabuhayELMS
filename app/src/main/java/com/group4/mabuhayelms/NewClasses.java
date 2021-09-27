package com.group4.mabuhayelms;


import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Build;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.ListView;


import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
public class NewClasses extends AppCompatActivity {

    EditText etSname, etSroom, etStart, etEnd;
    CheckBox cbM, cbT,cbW,cbTH,cbF,cbS;
    ListView lvStudent, lvChosen;
    SQLiteDatabase db;
    ArrayList<String> student, chosen;
    ArrayAdapter<String> adapter1, adapter2;
    Cursor c;
    String sname, sroom, start,end, sid,time;
    String [] iD;
    int index = 0,origid;
    int a;
    String chosenid;
    String name2;
    String ID;
    String combinedtime;
    String strId,strName,type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_classes);

        etSname = (EditText) findViewById(R.id.etSname);
        etSroom = (EditText) findViewById(R.id.etSroom);
        etStart = (EditText) findViewById(R.id.etStart);
        etEnd = (EditText) findViewById(R.id.etEnd);
        lvStudent = (ListView) findViewById(R.id.lvStudent);
        lvChosen = (ListView) findViewById(R.id.lvChosen);
        cbM = (CheckBox) findViewById(R.id.cbM);
        cbT = (CheckBox) findViewById(R.id.cbT);
        cbW = (CheckBox) findViewById(R.id.cbW);
        cbTH = (CheckBox) findViewById(R.id.cbTH);
        cbF = (CheckBox) findViewById(R.id.cbF);
        cbS = (CheckBox) findViewById(R.id.cbS);


        etStart.requestFocus();
        etEnd.requestFocus();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            etStart.setShowSoftInputOnFocus(false);
            etEnd.setShowSoftInputOnFocus(false);
        }

        db = openOrCreateDatabase("app", MODE_PRIVATE, null);

        db.execSQL("create table if not exists student(t_id INTEGER PRIMARY KEY AUTOINCREMENT, a_id VARCHAR)");

        type = getIntent().getExtras().getString("type");
        strId = getIntent().getExtras().getString("id");
        strName = getIntent().getExtras().getString("name");

        student = new ArrayList<>();
        c = db.rawQuery("select * from account where accounttype = 'Student'", null);
        c.moveToFirst();
        iD = new String[c.getCount()];

        if (c.getCount() > 0) {
            do{
                sid = c.getString(c.getColumnIndex("id"));
                sname = c.getString(c.getColumnIndex("name"));
                student.add(sname + "\n");
                iD[index] = sid;
                ++index;
            } while (c.moveToNext());
            adapter1 = new ArrayAdapter<String>(NewClasses.this,
                    android.R.layout.simple_list_item_multiple_choice, student);
            adapter1.notifyDataSetChanged();
            lvStudent.setAdapter(adapter1);
            lvStudent.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        } else {
            Toast.makeText(this, "No student yet!", Toast.LENGTH_SHORT).show();
        }
    }
    public void process3(View v){
        switch (v.getId()){

            case R.id.btnCreate:

                try{

                    String days = "";

                    if (cbM.isChecked()) {
                        days = days + "M-";
                    }
                    if (cbT.isChecked()) {
                        days = days + "T-";
                    }
                    if (cbW.isChecked()) {
                        days = days + "W-";
                    }
                    if (cbTH.isChecked()) {
                        days = days + "TH-";
                    }
                    if (cbF.isChecked()) {
                        days = days + "F-";
                    }
                    if (cbS.isChecked()) {
                        days = days + "S";
                    }

                    combinedtime = start +"-"+end;


                    sname = etSname.getText().toString();
                    sroom = etSroom.getText().toString();


                    if(sname.isEmpty()){
                        etSname.setError("Fill in the Blanks!");


                    }else if(sroom.isEmpty()){
                        etSname.setError(null);
                        etSroom.setError("Fill in the Blanks!");

                    }else if(days.equalsIgnoreCase("")){
                        Toast.makeText(getApplicationContext(), "Set The Alotted Day for The Class!", Toast.LENGTH_SHORT).show();


                    }else if(start == null || end == null){
                        Toast.makeText(getApplicationContext(), "Set The Alotted Time for The Class!", Toast.LENGTH_SHORT).show();

                    }else if(adapter2 == null){
                        Toast.makeText(getApplicationContext(), "Add Students!", Toast.LENGTH_SHORT).show();

                    }else{
                        Cursor f = db.rawQuery("select * from student",null);
                        f.moveToFirst();


                        if (f.getCount() > 0){
                            do {
                                ID =  f.getString(f.getColumnIndex("a_id"));

                                db.execSQL("insert into class (s_name, a_id) values ('"+sname+"','"+ID+"')");

                            }while(f.moveToNext());
                            db.execSQL("insert into subject (s_name, room, days,time,a_id)values('"+sname+"', '"+sroom+"','"+days+"','"+combinedtime+"', '"+strId+"')");//subject

                            AlertDialog.Builder ab = new AlertDialog.Builder(this);
                            ab.setTitle("NOTICE!");
                            ab.setMessage("New Class has been Created!");
                            ab.setCancelable(true);
                            ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    finish();
                                    Intent a = new Intent(getApplicationContext(), ListT.class);
                                    a.putExtra("type", type);
                                    a.putExtra("id",strId);
                                    a.putExtra("name",strName);                        startActivity(a);
                                }
                            });
                            ab.show();


                            db.execSQL("drop table student");

                        }else{
                            Toast.makeText(getApplicationContext(), "Add Student to Your Class!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (Exception e){                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnAdd:
                try{
                    if(adapter2 != null){
                        chosen.clear();
                    }                  SparseBooleanArray select = lvStudent.getCheckedItemPositions();
                    for (int i = 0; i < select.size(); i++){
                        final boolean isChecked = select.valueAt(i);
                        if (isChecked == true){
                            final int position = select.keyAt(i);
                            origid = Integer.parseInt(iD[position]);

                            Cursor c = db.rawQuery("select * from student where a_id = '"+origid+"'",null);
                            c.moveToFirst();
                            if(c.getCount() == 0){


                                db.execSQL("insert into student(a_id) values ('"+origid+"')");
                            }

                        }else if (isChecked == false){
                            final int position = select.keyAt(i);
                            origid = Integer.parseInt(iD[position]);


                            Cursor c = db.rawQuery("select * from student where a_id = '"+origid+"'",null);
                            c.moveToFirst();
                            if(c.getCount() > 0){


                                db.execSQL("delete from student where a_id = '"+origid+"'");
                            }
                        }
                    }
                    Cursor c = db.rawQuery("select account.name " +
                            "from account " +
                            "INNER JOIN student " +
                            "on account.id = student.a_id",null);
                    c.moveToFirst();
                    chosen = new ArrayList<>();

                    if(c.getCount() > 0) {
                        do {
                            name2 = c.getString(c.getColumnIndex("account.name"));
                            chosen.add(name2);
                        } while (c.moveToNext());
                    }
                    adapter2 = new ArrayAdapter<String>(NewClasses.this, android.R.layout.simple_list_item_1, chosen);
                    adapter2.notifyDataSetChanged();
                    lvChosen.setAdapter(adapter2);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void TimePickerDialog(){
        try{
            Calendar c = Calendar.getInstance();
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);

            TimePickerDialog tp = new TimePickerDialog(NewClasses.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    start = i+":"+i1;
                    etStart.setText(start);
                }
            },h,m,true);
            tp.setTitle("Select Starting Time");
            tp.show();
        }catch(Exception n){
            Toast.makeText(this, n.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void TimePickerDialog2(){
        try{
            Calendar c = Calendar.getInstance();
            int h = c.get(Calendar.HOUR_OF_DAY);
            int m = c.get(Calendar.MINUTE);

            TimePickerDialog tp = new TimePickerDialog(NewClasses.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int i, int i1) {
                    end = i+":"+i1;
                    etEnd.setText(end);
                }
            },h,m,true);
            tp.setTitle("Select End Time");
            tp.show();
        }catch(Exception n){
            Toast.makeText(this, n.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    public void time1(View x){
        switch (x.getId()){
            case R.id.etStart:

                TimePickerDialog();
                break;
            case R.id.etEnd:
                TimePickerDialog2();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent a = new Intent(getApplicationContext(), ListT.class);
        a.putExtra("type", type);
        a.putExtra("id",strId);
        a.putExtra("name",strName);
        startActivity(a);
    }
}
