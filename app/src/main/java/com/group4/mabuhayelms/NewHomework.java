package com.group4.mabuhayelms;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class NewHomework extends AppCompatActivity {

    SQLiteDatabase db;
    EditText etInstruct, etDue, etDuet, etItems;
    Spinner spClass;
    Button btnAttach;
    TextView tvAttach;
    String sname, sdue, sduet, sinstruct;
    String strId, strName, type, strAttachment = "";
    String encodedImage;
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_homework);
        etInstruct = (EditText) findViewById(R.id.etInstruct);
        etDue = (EditText) findViewById(R.id.etDue);
        etDuet = (EditText) findViewById(R.id.etDuet);
//        etItems = (EditText) findViewById(R.id.etItems);
        spClass = (Spinner) findViewById(R.id.spClass);
        btnAttach = (Button) findViewById(R.id.btnAttach);
        tvAttach = (TextView) findViewById(R.id.tvAttach);


        type = getIntent().getExtras().getString("type");
        strId = getIntent().getExtras().getString("id");
        strName = getIntent().getExtras().getString("name");

        db = openOrCreateDatabase("app", MODE_PRIVATE, null);
        etDue.requestFocus();
        etDuet.requestFocus();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            etDue.setShowSoftInputOnFocus(false);
            etDuet.setShowSoftInputOnFocus(false);
        }
        Cursor c = db.rawQuery("select * from subject", null);
        c.moveToFirst();
        ArrayList cls = new ArrayList();
        if (c.getCount() > 0) {
            do {
                sname = c.getString(c.getColumnIndex("s_name"));
                cls.add(sname);
            } while (c.moveToNext());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewHomework.this, android.R.layout.simple_spinner_dropdown_item, cls);
            spClass.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No Subjects Available!", Toast.LENGTH_SHORT).show();
        }
        etDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog();
            }
        });
        etDuet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimePickerDialog();
            }
        });
        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();

                Cursor name = getContentResolver().query(uri, null, null, null, null);

                int nameIndex = name.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                name.moveToFirst();
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                imageTop.setImageBitmap(bm);
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, bo);
                    byte[] b = bo.toByteArray();

                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String pic = name.getString(nameIndex);

                tvAttach.setText(pic);

                tvAttach.setVisibility(View.VISIBLE);
                btnAttach.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void process6(View v) {
        try {
            sinstruct = etInstruct.getText().toString().trim();
            String subject = spClass.getSelectedItem().toString();
            strAttachment = encodedImage;
            String items = etItems.getText().toString();

            if (sinstruct.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Add Instruction in Your Homework1", Toast.LENGTH_SHORT).show();

            } else if (items.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Set Total no. Items!", Toast.LENGTH_SHORT).show();

            } else if (sdue == null || sduet == null) {
                Toast.makeText(getApplicationContext(), "Set the Deadline!", Toast.LENGTH_SHORT).show();

            } else {
                Cursor c = db.rawQuery("select * from class where s_name= '" + subject + "'", null);
                c.moveToFirst();
                if (c.getCount() > 0) {
                    do {
                        String a_id = c.getString(c.getColumnIndex("a_id"));

                        db.execSQL("insert into homework (s_name,a_id,attachment, instruction, d_date, time, status,item)values ('" + subject + "','" + a_id + "','" + strAttachment + "','" + sinstruct + "', '" + sdue + "', '" + sduet + "', 'incoming','" + items + "')");
                    } while (c.moveToNext());

                    AlertDialog.Builder ab = new AlertDialog.Builder(this);
                    ab.setTitle("NOTICE!");
                    ab.setMessage("New Homework has been Created!");
                    ab.setCancelable(true);
                    ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                            finish();
                            Intent a = new Intent(getApplicationContext(), ListT.class);
                            a.putExtra("type", type);
                            a.putExtra("id", strId);
                            a.putExtra("name", strName);
                            startActivity(a);
                        }
                    });
                    ab.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Can't Create Homework No Subject Available!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void DatePickerDialog() {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(NewHomework.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                int months = i1 + 1;
                sdue = months + "-" + i2 + "-" + i;
                etDue.setText(sdue);

            }
        }, y, m, d);
        dp.setTitle("Select Date of Homework");
        dp.show();
    }

    public void TimePickerDialog() {
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);


        TimePickerDialog tp = new TimePickerDialog(NewHomework.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                sduet = i + ":" + i1;
                etDuet.setText(sduet);

            }
        }, h, m, true);
        tp.setTitle("Select Time of Homework");
        tp.show();
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

