package com.group4.mabuhayelms;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
public class Submitted extends AppCompatActivity {
    SQLiteDatabase db;
    ListView lvAll;
    TextView tvSubject;
    ArrayList<String> homework;
    ArrayAdapter<String> adapter1;
    AlertDialog.Builder ab;

    String studentid, status,a_id;
    String strId,strName,type;
    String stat;
    String[] idd;
    int index = 0;
    String subid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted);
        try{
            lvAll = (ListView) findViewById(R.id.lvAll);
            tvSubject = (TextView) findViewById(R.id.tvSubject);


            db = openOrCreateDatabase("app", MODE_PRIVATE, null);

            subid = getIntent().getExtras().getString("h_id");
            type = getIntent().getExtras().getString("type");
            strId = getIntent().getExtras().getString("id");
            strName = getIntent().getExtras().getString("name");

            Cursor c = db.rawQuery("select homework.status, homework.a_id, homework.s_name, account.name " +
                    "from homework " +
                    "INNER JOIN account " +
                    "ON homework.a_id = account.id " +
                    "where homework.instruction = '"+subid+"'" ,null);
            c.moveToFirst();

            String subject = c.getString(c.getColumnIndex("homework.s_name"));


            idd = new String[c.getCount()];
            tvSubject.setText(subject);
            homework = new ArrayList<>();
            if (c.getCount()>0){
                do {
                    studentid = c.getString(c.getColumnIndex("account.name"));
                    status = c.getString(c.getColumnIndex("homework.status"));
                    a_id = c.getString(c.getColumnIndex("homework.a_id"));


                    if (status.equals("late-submitted")) {
                        stat = "Late";
                        homework.add(studentid+"\t\t\t\t\t\t\t\t\t\t\t"+stat);
                        idd[index] = a_id;
                        index++;

                    } else if (status.equals("submitted")){
                        stat = "On Time";
                        homework.add(studentid+"\t\t\t\t\t\t\t\t\t\t\t"+stat);
                        idd[index] = a_id;
                        index++;
                    }
                }while (c.moveToNext());
            }
            adapter1 = new ArrayAdapter<>(Submitted.this, android.R.layout.simple_list_item_1, homework);
            lvAll.setAdapter(adapter1);
            lvAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try{
                        final int origid = Integer.parseInt(idd[position]);
                        ab = new AlertDialog.Builder(Submitted.this);
                        ab.setCancelable(true);
                        LayoutInflater li = getLayoutInflater();
                        View cv = li.inflate(R.layout.homework_answer, null);
                        ab.setView(cv);
                        TextView tvADetails = (TextView)cv.findViewById(R.id.tvADetails);
                        ImageView image = (ImageView)cv.findViewById(R.id.image);
//                        final EditText etScore = (EditText)cv.findViewById(R.id.etScore);
//                        Button btnScore = (Button)cv.findViewById(R.id.btnScore);

                        Cursor c = db.rawQuery("select finish.f_id, finish.answer, finish.attachment, finish.score, finish.item, account.name " +
                                "from finish " +
                                "INNER JOIN account " +
                                "on finish.a_id = account.id " +
                                "where finish.a_id = '"+origid+"' and finish.instruction = '"+subid+"'",null);
                        c.moveToFirst();
                        String answer = c.getString(c.getColumnIndex("finish.answer"));
                        String name = c.getString(c.getColumnIndex("account.name"));
                        String attach = c.getString(c.getColumnIndex("finish.attachment"));
                        final int item = c.getInt(c.getColumnIndex("finish.item"));
                        String score = c.getString(c.getColumnIndex("finish.score"));
                        final String f_id = c.getString(c.getColumnIndex("finish.f_id"));


                        if(attach.equalsIgnoreCase("")){
                            attach = "N/A";
                            tvADetails.setText("Student Name: "+name+"\n\nItems: "+item+"\nAnswer: \n\n"+answer+"\n\nAttachment: "+attach);
                        }else{
                            image.setVisibility(View.VISIBLE);
                            tvADetails.setText("Student Name: "+name+"\n\nItems: "+item+"\nAnswer: \n\n"+answer+"\n\nAttachment: ");
                            byte [] b = Base64.decode(attach, Base64.DEFAULT);
                            Bitmap decodedImage = BitmapFactory.decodeByteArray(b, 0,b.length);
                            image.setImageBitmap(decodedImage);
                        }
                        if(score != null){
//                            etScore.setVisibility(View.GONE);
//                            btnScore.setVisibility(View.GONE);
//                            tvADetails.setText("Student Name: "+name+"\n\nScore: "+score+" out of "+item+"\nAnswer: \n\n"+answer+"\n\nAttachment: ");
                        }
                        final AlertDialog ac = ab.create();
//                        btnScore.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                int score = Integer.parseInt(etScore.getText().toString());
//
//
//                                if(item < score){
//
//                                    Toast.makeText(getApplicationContext(), "Score must not exceed the Total No. of Items", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    db.execSQL("update homework set score = '"+score+"' where a_id = '"+origid+"' and instruction = '"+subid+"'");
//                                    db.execSQL("update finish set score = '"+score+"' where f_id = '"+f_id+"'");
//                                    Toast.makeText(getApplicationContext(), "You give a score! ", Toast.LENGTH_SHORT).show();
//                                    ac.dismiss();
//                                }
//                            }
//                        });
                        ac.show();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent a = new Intent(getApplicationContext(), ListT.class);
        a.putExtra("type", type);
        a.putExtra("id", strId);
        a.putExtra("name", strName);
        startActivity(a);
    }
}

