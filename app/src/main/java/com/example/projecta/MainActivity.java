package com.example.projecta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {


    private SwitchDateTimeDialogFragment dateTimeFragment;
    BottomNavigationView bottomNavigationView;
    ImageView imageView;
    CustomDialog customDialog;
    FragmentManager fragmentManager=getSupportFragmentManager();
    Fragment1 fragment1= new Fragment1();
    Fragment2 fragment2= new Fragment2();
    FrameLayout frameLayout;
    SQLiteDatabase db;
    EditText name,date,time,desc;
    Context context=this;
    String a,b,c,d;
    TimePickerDialog dialog;
    String d_time;
    //TimePickerDialog.OnTimeSetListener listener;
    View.OnClickListener negativeListener;

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView=findViewById(R.id.bottom_bar);
        imageView=findViewById(R.id.plus);
        frameLayout=findViewById(R.id.frame_layout);

        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout,fragment1).commitAllowingStateLoss();
        DBHelper dbHelper= new DBHelper(MainActivity.this);
        db= dbHelper.getWritableDatabase();
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    "수행 날짜",
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }
        dialog= new TimePickerDialog(this,listener,15,24,true);
        customDialog= new CustomDialog(MainActivity.this,negativeListener);
        dateTimeFragment.setTimeZone(TimeZone.getDefault());
        final SimpleDateFormat myDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 hh시 mm분", java.util.Locale.getDefault());

        // Assign unmodifiable values
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setHighlightAMPMSelection(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2025, Calendar.DECEMBER, 31).getTime());

        // Define new day and month format
        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e("tag", e.getMessage());
        }

        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
                a=myDateFormat.format(date);
                showTimePickDialog();

                //Toast.makeText(getApplicationContext(),a,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNegativeButtonClick(Date date) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateTimeFragment.startAtCalendarView();
                dateTimeFragment.setDefaultDateTime(new GregorianCalendar(2019, Calendar.NOVEMBER, 15, 15, 20).getTime());
                dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);

                customDialog.setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        b=customDialog.getName();
                        c=d_time;
                        d=customDialog.getDesc();
                        Toast.makeText(getApplicationContext(),"수행이 저장되었습니다.",Toast.LENGTH_SHORT).show();
                        dbInsert("tb_project",b,a,c,d);
                        Log.d("name",name.getText().toString());
                        customDialog.dismiss();



                    }
                });
                LayoutInflater inflater = LayoutInflater.from(context);
                View dialogView = inflater.inflate(R.layout.dialog, null);
               // date=dialogView.findViewById(R.id.edit_date);
                name=dialogView.findViewById(R.id.edit_name);
                //time=dialogView.findViewById(R.id.edit_time);
                desc=dialogView.findViewById(R.id.edit_desc);



            }



        });


        negativeListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                switch (menuItem.getItemId()){
                    case R.id.home:
                        transaction.replace(R.id.frame_layout,fragment1).commitAllowingStateLoss();
                        return true;
                    case R.id.timer:
                        transaction.replace(R.id.frame_layout,fragment2).commitAllowingStateLoss();
                        return true;
                }
                return false;
            }
        });

    }

    private void showTimePickDialog() {

        Log.d("start","start1");
        dialog.setMessage("목표 시간 설정");
        dialog.show();

    }


    void dbInsert(String tableName, String name, String date, String time,String desc) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("description", desc);
        // 리턴값: 생성된 데이터의 id
        db.insert(tableName, null, contentValues);


    }

    TimePickerDialog.OnTimeSetListener listener= new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            Log.d("start","start2");
            d_time=i+"시 "+i1+"분";
            customDialog.show();

        }
    };


}
