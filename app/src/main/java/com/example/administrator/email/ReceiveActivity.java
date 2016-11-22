package com.example.administrator.email;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReceiveActivity extends AppCompatActivity {

    PrintWriter output;
    BufferedReader input;
    Socket socket = null;
    Handler handler = null;
    ArrayList<Map<String, Object>> arraylist = new ArrayList<Map<String, Object>>();
    ListView mListView;
    SimpleAdapter adapter;
    String message[] = new String[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        handler=new Handler();
        mListView = (ListView) findViewById(R.id.show_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent getIntent = getIntent();
        final String Email = getIntent.getStringExtra("Email");
        final String Password = getIntent.getStringExtra("Password");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiveActivity.this, SendActivity.class);
                intent.putExtra("Email", Email);
                intent.putExtra("Password", Password);
                startActivity(intent);
            }
        });
        new Thread() {
            public void run() {
                try {
                    socket = new Socket("pop3.163.com", 110);
                    output = new PrintWriter(new OutputStreamWriter(socket
                            .getOutputStream()));
                    input = new BufferedReader(new InputStreamReader(socket
                            .getInputStream()));
                    Log.i(input.readLine(), "info");
                    //用户名
                    output.println("USER " + Email);
                    output.flush();
                    Log.i(input.readLine(), "info");
                    //密码
                    output.println("PASS " + Password);
                    output.flush();
                    Log.i(input.readLine(), "info");
                    //获取数量
                    output.println("STAT");
                    output.flush();
                    String get_sum = input.readLine();
                    int sum = Integer.parseInt(get_sum.substring(get_sum.indexOf(" ") + 1, get_sum.lastIndexOf(" ")));
                    //获取信息
                    String email;
                    String time;
                    String subject;
                    int j = 0;
                    int min;
                    if(sum-10<1) min=1;
                    else min=sum-10;
                    for (int i = sum; i > min; i--) {
                        output.println("retr " + i);
                        output.flush();
                        Log.i(input.readLine(),"info");
                        input.readLine();
                        Map<String, Object> item = new HashMap<String, Object>();
                        arraylist.add(item);
                        String read;
                        read = input.readLine();
                        email="";subject="";time="";
                        while (read.length() != 1 && !read.equals(".")){
                            if(read.indexOf("From")==0&&email.length()==0)
                                email=new String(read);
                            if(read.indexOf("Subject")==0&&subject.length()==0)
                                subject=new String(read);
                            if(read.contains("(CST)")&&time.length()==0)
                                time=new String(read);
                            message[j] += read;
                            read = input.readLine();
                        }
                        item.put("email", email);
                        item.put("time", time);
                        item.put("subject", subject);
                        j++;
                    }
                } catch (Exception e) {
                    System.out.println("Error " + e);
                }
                handler.post(runnableUi);
            }
        }.start();
        mListView.setOnItemClickListener(new OnItemClickListenerImpl());
    }

    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            //定义一个SimpleAdapter
            adapter = new SimpleAdapter(getBaseContext(), arraylist, R.layout.list_layout,
                    new String[]{"email", "time", "subject"},
                    new int[]{R.id.list_email, R.id.list_time, R.id.list_subject,});
            //设置mListView的适配器为adapter
            mListView.setAdapter(adapter);
        }
    };
    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {


        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = new Intent(ReceiveActivity.this, ShowActivity.class);
            intent.putExtra("Message", message[position]);
            startActivity(intent);

        }
    }
}
