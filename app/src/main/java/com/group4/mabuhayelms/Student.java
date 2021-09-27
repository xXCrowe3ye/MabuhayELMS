package com.group4.mabuhayelms;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
public class Student extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvName;
    SQLiteDatabase db;
    Cursor c,k;
    String strId,strName;
    int late,incoming,submitted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nv = navigationView.getHeaderView(0);
        tvName = (TextView)nv.findViewById(R.id.tvName);

        db = openOrCreateDatabase("app", MODE_PRIVATE, null);

        strId = getIntent().getExtras().getString("id");
        strName = getIntent().getExtras().getString("name");
        tvName.setText(strName);

        Cursor c = db.rawQuery("select * from homework where a_id = '"+strId+"'",null);
        c.moveToFirst();

        Menu menu = navigationView.getMenu();
        MenuItem nav_camera = menu.findItem(R.id.nav_camera);

        nav_camera.setTitle("Homework ("+c.getCount()+")");
        try{

            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
            String c_date = new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm", Locale.getDefault()).format(new Date());

            Cursor d = db.rawQuery("select * from homework where status = 'incoming' and a_id = '"+strId+"'",null);
            d.moveToFirst();

            if(d.getCount() > 0){
                do{
                    String d_date = d.getString(d.getColumnIndex("d_date"));
                    String time = d.getString(d.getColumnIndex("time"));
                    String h_id = d.getString(d.getColumnIndex("h_id"));

                    String d_day = d_date+" "+time;
                    Date dd_day = null;
                    Date cc_day =null;
                    try {
                        dd_day = formatter.parse(d_day);
                        cc_day = formatter.parse(c_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    Toast.makeText(getApplicationContext(), cc_day+"-------"+dd_day, Toast.LENGTH_SHORT).show();
                    if(cc_day.after(dd_day)){
                        db.execSQL("update homework set status = 'late' where h_id = '"+h_id+"' and a_id = '"+strId+"'");
                    }
                }while (c.moveToNext());
            }
//            k = db.rawQuery("select * from finish where a_id = '" + strId + "'", null);
//            k.moveToFirst();
//
//            submitted = k.getCount();

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.student, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            try {

                AlertDialog.Builder ab = new AlertDialog.Builder(this);
                ab.setCancelable(true);
                LayoutInflater li = getLayoutInflater();
                View cv = li.inflate(R.layout.homework_status, null);
                ab.setView(cv);
                final ListView lvStatus = (ListView) cv.findViewById(R.id.lvStatus);
                String[] status = {"late", "incoming","submitted","late-submitted"};

                for (int j = 0; j < 4; j++) {
                    c = db.rawQuery("select * from homework where status = '" + status[j] + "' and a_id = '" + strId + "'", null);
                    c.moveToFirst();


                    if (status[j].equalsIgnoreCase("late")) {
                        late = c.getCount();

                    } else if (status[j].equalsIgnoreCase("incoming")) {
                        incoming = c.getCount();


                    } else if (status[j].equalsIgnoreCase("submitted")) {
                        submitted += c.getCount();
                        Toast.makeText(getApplicationContext(), "YEY", Toast.LENGTH_SHORT).show();

                    } else if (status[j].equalsIgnoreCase("late-submitted")) {
                        submitted += c.getCount();
                    }
                }
                Toast.makeText(getApplicationContext(), late+"-"+incoming+"-"+submitted, Toast.LENGTH_SHORT).show();
                ArrayList<String> statuss = new ArrayList<>();
                statuss.add("\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+late + " Late Submission");
                statuss.add("\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+incoming + " Incoming");
                statuss.add("\t\t\t\t\t\t\t\t\t\t\t\t\t\t"+submitted + " Submitted");
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Student.this, android.R.layout.simple_list_item_1, statuss);
                lvStatus.setAdapter(adapter1);

                lvStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String strStatus;
                        if(i == 0){
                            if(late == 0){
                                Toast.makeText(getApplicationContext(), "No Late Homework!", Toast.LENGTH_SHORT).show();
                            }else{
                                strStatus = "late";
                                Intent j = new Intent(getApplicationContext(), HomeworkList.class);
                                j.putExtra("id",strId);
                                j.putExtra("status",strStatus);
                                j.putExtra("name",strName);
                                startActivity(j);
                            }
                        }else if(i == 1){


                            if(incoming == 0){
                                Toast.makeText(getApplicationContext(), "No Incoming Homework!", Toast.LENGTH_SHORT).show();


                            }else{
                                strStatus = "incoming";
                                Intent j = new Intent(getApplicationContext(), HomeworkList.class);
                                j.putExtra("id",strId);
                                j.putExtra("status",strStatus);
                                j.putExtra("name",strName);
                                startActivity(j);
                            }
                        }else if(i == 2){


                            if(submitted == 0){
                                Toast.makeText(getApplicationContext(), "No Submitted Homework!", Toast.LENGTH_SHORT).show();
                            }else{
                                strStatus = "submitted";
                                Intent j = new Intent(getApplicationContext(), HomeworkList.class);
                                j.putExtra("id",strId);
                                j.putExtra("status",strStatus);
                                j.putExtra("name",strName);
                                startActivity(j);
                            }
                        }
                    }
                });
                ab.show();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "1"+e.toString(), Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_gallery) {
            String ct = "Classes";
            Intent i = new Intent(getApplicationContext(), StudentCT.class);
            i.putExtra("id",strId);
            i.putExtra("name",strName);
            i.putExtra("ct",ct);
            startActivity(i);


        } else if (id == R.id.nav_slideshow) {
            String ct = "Teachers";
            Intent i = new Intent(getApplicationContext(), StudentCT.class);
            i.putExtra("id",strId);
            i.putExtra("name",strName);
            i.putExtra("ct",ct);
            startActivity(i);


        } else if (id == R.id.nav_manage) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

