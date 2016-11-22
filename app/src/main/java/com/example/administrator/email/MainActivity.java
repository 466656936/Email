package com.example.administrator.email;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity{

    EditText name,password,subject,send_email,message;
    TextView houzhui;
    TextView warn;
    Button send,choice;//登录，选择
    Handler handler = null;
    boolean success;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db=openOrCreateDatabase("users.db",MODE_PRIVATE,null);
        db.execSQL("create table if not exists users(email text,password text)");
        success=false;
        handler = new Handler();
        name= (EditText) findViewById(R.id.email_name);
        password= (EditText) findViewById(R.id.password);
        send= (Button) findViewById(R.id.login);
        choice= (Button) findViewById(R.id.choice);
        houzhui=(TextView) findViewById(R.id.houzhui);
        warn= (TextView) findViewById(R.id.warn);
        houzhui.setText("@163.com");
        choice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Cursor c=db.rawQuery("select * from users",null);
                if(c.getCount()==0)
                    Toast.makeText(getApplicationContext(),"没有登录记录",Toast.LENGTH_SHORT);
                else {
                    Intent get = new Intent();
                    get.setClass(MainActivity.this, ChoiceActivity.class);
                    startActivityForResult(get, 1000);
                }
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    PrintWriter output;
                    BufferedReader input;
                    Socket socket = null;
                    public void run(){
                        try {
                            socket = new Socket("smtp.163.com", 25);
                            output = new PrintWriter(new OutputStreamWriter(socket
                                    .getOutputStream()));
                            input = new BufferedReader(new InputStreamReader(socket
                                    .getInputStream()));
                            output.println("helo 163");
                            output.flush();
                            Log.i(input.readLine(),"info");
                            //验证登陆
                            output.println("auth login");
                            output.flush();
                            Log.i(input.readLine(),"info");
                            //用户名
                            String username = name.getText().toString();
                            username=android.util.Base64.encodeToString(username.getBytes("UTF-8"), Base64.NO_WRAP);
                            output.println(username);
                            output.flush();
                            Log.i(input.readLine(),"info");
                            //密码
                            String pass = password.getText().toString();
                            pass=android.util.Base64.encodeToString(pass.getBytes("UTF-8"), Base64.NO_WRAP);
                            output.println(pass);
                            output.flush();
                            Log.i(input.readLine(),"info");
                            String login=input.readLine();
                            Log.i(login,"info");
                            if(login.contains("235")) success=true;
                            else warn.setText("邮箱号或密码输入错误！");
                        } catch (Exception e) {
                            System.out.println("Error " + e);
                        }
                        if(success) handler.post(runnableUi);
                    }
                }.start();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1000&&resultCode==1001){
            name.setText(data.getStringExtra("Email"));
            password.setText(data.getStringExtra("Password"));
        }
    }
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }*/

    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            Cursor c=db.rawQuery("select * from users where email='"+name.getText().toString()+"'",null);
            if(c.getCount()==0)
                db.execSQL("insert into users(email,password) values('"+name.getText().toString()+"','"+password.getText().toString()+"')");
            Intent intent = new Intent(MainActivity.this, ReceiveActivity.class);
            intent.putExtra("Email", name.getText().toString());
            intent.putExtra("Password", password.getText().toString());
            startActivity(intent);
        }
    };
}