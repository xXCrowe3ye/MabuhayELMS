package com.group4.mabuhayelms;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
public class List extends AppCompatActivity {

    ListView lvList;
    ArrayList<String> list;
    ArrayAdapter<String> adap;
    SQLiteDatabase db;
    TextView listt;

    String type, name;
    String strId,strName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        lvList = (ListView)findViewById(R.id.lvList);
        listt = (TextView)findViewById(R.id.listt);

        db = openOrCreateDatabase("app", MODE_PRIVATE, null);

        type = getIntent().getExtras().getString("type");
        strId = getIntent().getExtras().getString("id");
        strName = getIntent().getExtras().getString("name");

        listt.setText("List of "+type);
        listt.setAllCaps(true);

        Cursor j = db.rawQuery("select * from account where accounttype = '"+type+"'", null);
        j.moveToFirst();

        list = new ArrayList<String>();

        if (j.getCount()>0){
            do{
                name = j.getString(j.getColumnIndex("name"));
                list.add("Name: "+name);
            }while (j.moveToNext());


        }else{

            Toast.makeText(getApplicationContext(), "No "+type+" Available!", Toast.LENGTH_SHORT).show();
        }
        adap = new ArrayAdapter<String>(List.this, android.R.layout.simple_list_item_1, list);
        lvList.setAdapter(adap);
    }
    public void pro(View v){
        finish();
        Intent o = new Intent(getApplicationContext(), Create.class);
        o.putExtra("type", type);
        o.putExtra("id",strId);
        o.putExtra("name",strName);
        startActivity(o);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent o = new Intent(getApplicationContext(), Admin.class);
        o.putExtra("id",strId);
        o.putExtra("name",strName);
        startActivity(o);
    }
}
