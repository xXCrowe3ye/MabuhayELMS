package com.group4.mabuhayelms;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
public class Admin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvName2;
    SQLiteDatabase db;
    Intent i;

    String strId,strName;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        try {
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

            tvName2 = (TextView) nv.findViewById(R.id.tvName2);

            db = openOrCreateDatabase("app", MODE_PRIVATE, null);

            strId = getIntent().getExtras().getString("id");
            strName = getIntent().getExtras().getString("name");
            tvName2.setText(strName);


            Cursor c = db.rawQuery("select * from account where accounttype = 'Teacher'", null);
            c.moveToFirst();

            Menu menu = navigationView.getMenu();
            MenuItem nav_camera = menu.findItem(R.id.nav_camera2);
            nav_camera.setTitle("Teacher (" + c.getCount() + ")");

            Cursor k = db.rawQuery("select * from account where accounttype = 'Student'", null);
            k.moveToFirst();

            MenuItem nav_gallery = menu.findItem(R.id.nav_gallery2);
            nav_gallery.setTitle("Student (" + k.getCount() + ")");

        }catch (Exception e){
            Toast.makeText(this, ""+e.toString(), Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.admin, menu);
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
        if (id == R.id.nav_camera2) {
            type = "Teacher";
            finish();
            i = new Intent(getApplicationContext(), List.class);
            i.putExtra("type", type);
            i.putExtra("id",strId);
            i.putExtra("name",strName);
            startActivity(i);
        } else if (id == R.id.nav_gallery2) {
            type = "Student";
            finish();
            i = new Intent(getApplicationContext(), List.class);
            i.putExtra("type", type);
            i.putExtra("id",strId);
            i.putExtra("name",strName);
            startActivity(i);
        } else if (id == R.id.nav_slideshow2) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
