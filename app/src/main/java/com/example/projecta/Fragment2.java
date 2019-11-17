package com.example.projecta;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Fragment2 extends Fragment {
    Button startBtn,stopBtn,resetBtn,reStartBtn,recordBtn;
    TextView timeText,secondsText,performanceText,goalTimeText;
    LinearLayout content;
    AlertDialog alertDialog;
    Thread timeThread = null;
    Boolean isRunning = true;
    int index;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment2, container, false);

        startBtn = view.findViewById(R.id.start);
        stopBtn = view.findViewById(R.id.stop);
        reStartBtn = view.findViewById(R.id.restart);
        recordBtn = view.findViewById(R.id.record);
        resetBtn = view.findViewById(R.id.reset);
        timeText = view.findViewById(R.id.stopWatch_time);
        secondsText = view.findViewById(R.id.stopWatch_seconds);
        content = view.findViewById(R.id.stopWatch_content);
        performanceText = view.findViewById(R.id.stopWatch_performance);
        goalTimeText = view.findViewById(R.id.stopWatch_goalTime);



        timeText.setText("00:00");
        secondsText.setText("00");

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ArrayList<Data> list = new ArrayList<Data>();
                final ArrayList<String> name = new ArrayList<String>();


                DBHelper helper = new DBHelper(getContext());
                SQLiteDatabase db = helper.getReadableDatabase();
                String sql = "select * from tb_project";
                Cursor cursor = db.rawQuery(sql,null);
                while(cursor.moveToNext()){
                    Data data = new Data();
                    data.name = cursor.getString(1);
                    name.add(data.name);
                    data.date = cursor.getString(2);
                    data.time = cursor.getString(3);
                    data.desc = cursor.getString(4);
                    list.add(data);
                }
                final CharSequence[] items = name.toArray(new String[name.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("수행");
                builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        index = i;
                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        performanceText.setText(list.get(index).name);
                        goalTimeText.setText("목표시간: "+list.get(index).time);
                    }
                });
                builder.setNegativeButton("cansel",null);
                alertDialog =builder.create();
                alertDialog.show();
            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRunning = true;
                view.setVisibility(View.GONE);
                stopBtn.setVisibility(View.VISIBLE);
                recordBtn.setVisibility(View.VISIBLE);
                timeThread = new Thread(new TimeThread());
                timeThread.start();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                isRunning = !isRunning;
                recordBtn.setVisibility(View.GONE);
                reStartBtn.setVisibility(View.VISIBLE);

            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = String.valueOf(timeText.getText());
                String seconds = String.valueOf(secondsText.getText());
                String data =time+":"+seconds;

                DBTimeHelper helper = new DBTimeHelper(getContext());
                SQLiteDatabase db = helper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("time",data);
                db.insert("tb_time",null, values);

                Intent intent = new Intent(getContext(),Popup.class);
                startActivity(intent);
            }
        });

        reStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                isRunning = !isRunning;
                stopBtn.setVisibility(View.VISIBLE);
                recordBtn.setVisibility(View.VISIBLE);
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeText.setText("00:00");
                secondsText.setText("00");
                reStartBtn.setVisibility(View.GONE);
                startBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.VISIBLE);
                timeThread.interrupt();
            }
        });

        return view;
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 3600;
            String result_seconds = String.format("%02d",sec);
            secondsText.setText(result_seconds);
            String result_time = String.format("%02d:%02d", hour,min);
            timeText.setText(result_time);
        }
    };

    public class TimeThread implements Runnable{
        @Override
        public void run() {
            int i = 0;
            while(true){
                while(isRunning){
                    Message msg = new Message();
                    msg.arg1=i++;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        }
    }

}
