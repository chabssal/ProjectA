package com.example.projecta;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Popup extends Activity {
    ArrayList<String> list;
    MyAdapter myAdapter;
    RecyclerView recyclerView;
    Button okBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        recyclerView = findViewById(R.id.slideRecycler);
        okBtn = findViewById(R.id.ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        list = new ArrayList<String>();
        DBTimeHelper helper = new DBTimeHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from tb_time";
        Cursor cursor = db.rawQuery(sql,null);
        while(cursor.moveToNext()){
            String time = cursor.getString(1);
            list.add(time);
        }

        myAdapter = new MyAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);



    }
    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        ArrayList<String> list;

        MyAdapter(ArrayList<String> list){this.list = list;}
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_item,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String text = list.get(position);
            holder.timeView.setText(text);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView timeView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeView = itemView.findViewById(R.id.time_textView);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

}
