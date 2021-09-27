package com.group4.mabuhayelms;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class Homework extends AppCompatActivity {

    TextView tvSubject, tvDetails, tvAttachment, tvDate;
    EditText etAnswer;
    Button btnAttachment;
    SQLiteDatabase db;
    ImageView imageView;

    String strId, strName, strStatus;
    String strSubject, strInstruction, strAttachment = "", strAnswer,strItem;
    String d_date, time, c_time, c_date;
    int h_id;

    String encodedImage,attachment="";
    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);
        try{
            tvSubject = (TextView) findViewById(R.id.tvSubject);
            tvDetails = (TextView) findViewById(R.id.tvDetails);
            tvAttachment = (TextView) findViewById(R.id.tvAttachment);
            tvDate = (TextView) findViewById(R.id.tvDate);
            imageView = (ImageView)findViewById(R.id.imageView);
            etAnswer = (EditText) findViewById(R.id.etAnswer);
            btnAttachment = (Button) findViewById(R.id.btnAttachment);


            db = openOrCreateDatabase("app", MODE_PRIVATE, null);

            strId = getIntent().getExtras().getString("id");
            strName = getIntent().getExtras().getString("name");
            strStatus = getIntent().getExtras().getString("status");
            h_id = getIntent().getExtras().getInt("h_id");
//        Toast.makeText(getApplicationContext(), h_id+"", Toast.LENGTH_SHORT).show();

            Cursor c = db.rawQuery("select * from homework where h_id = '" + h_id + "'", null);
            c.moveToFirst();
            if (c.getCount() > 0) {
                strSubject = c.getString(c.getColumnIndex("s_name"));
                strInstruction = c.getString(c.getColumnIndex("instruction"));
                d_date = c.getString(c.getColumnIndex("d_date"));
                time = c.getString(c.getColumnIndex("time"));
                attachment = c.getString(c.getColumnIndex("attachment"));
                strItem = c.getString(c.getColumnIndex("item"));

                tvSubject.setText(strSubject);
                tvDetails.setText("Instructions: " + strInstruction + "\nTotal No. of Items: "+strItem+"\nDue: " + d_date + "(" + time + ")");

                c_date = new java.text.SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());


                String time = new java.text.SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                String[] time1 = time.split(":");
                int hour = Integer.parseInt(time1[0]);
                int minute = Integer.parseInt(time1[1]);

                c_time = hour + 8 + ":" + minute;

                tvDate.setText("Date: " + c_date + "(" + c_time + ")");

                if(attachment == null){
                }else{
                    imageView.setVisibility(View.VISIBLE);

                    byte [] b = Base64.decode(attachment, Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(b, 0,b.length);
                    imageView.setImageBitmap(decodedImage);
                }
            }
        }catch (Exception e){

            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
    public void process8(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit11:
                try{
                    strAnswer = etAnswer.getText().toString();
                    strAttachment = encodedImage;


                    if(strAnswer.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Answer Your Homework!", Toast.LENGTH_SHORT).show();
                    }else{

                        db.execSQL("insert into finish (h_id,a_id,instruction,answer,attachment,c_date,c_time,item) values ('"+h_id+"','"+strId+"','"+strInstruction+"','"+strAnswer+"','"+strAttachment+"','"+c_date+"','"+c_time+"','"+strItem+"')");

                        if(strStatus.equalsIgnoreCase("late")){

                            db.execSQL("update homework set status = 'late-submitted' where h_id = '"+h_id+"' and a_id = '"+strId+"'");

                        }else{


                            db.execSQL("update homework set status = 'submitted' where h_id = '" + h_id + "' and a_id = '" + strId + "'");
                        }

                        AlertDialog.Builder ab = new AlertDialog.Builder(this);
                        ab.setTitle("NOTICE!");
                        ab.setMessage("Homework Completed!");
                        ab.setCancelable(true);
                        ab.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Intent i = new Intent(getApplicationContext(), Student.class);
                                i.putExtra("id",strId);
                                i.putExtra("name",strName);
                                startActivity(i);
                            }
                        });
                        ab.show();
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnAttachment:
                chooseImage();
                break;
        }
    }
    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data !=null  && data.getData() != null){

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
                tvAttachment.setText(pic);
                tvAttachment.setVisibility(View.VISIBLE);
                btnAttachment.setVisibility(View.GONE);
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
        i.putExtra("id", strId);
        i.putExtra("name", strName);
        startActivity(i);
    }
}

