package com.example.administrator.email;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SendActivity extends AppCompatActivity {

    EditText email_number, email_subject, email_message;
    Button send;String Email;String Password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Intent getIntent = getIntent();
        Email = getIntent.getStringExtra("Email");
        Password = getIntent.getStringExtra("Password");
        send = (Button) findViewById(R.id.send);
        email_number = (EditText) findViewById(R.id.email_send);
        email_message = (EditText) findViewById(R.id.email_message);
        email_subject = (EditText) findViewById(R.id.email_subject);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                         new Thread() {
                             String message = email_message.getText().toString();
                             String subject = email_subject.getText().toString();
                             String number = email_number.getText().toString();
                             PrintWriter output;
                             BufferedReader input;
                             Socket socket = null;
                             public void run() {
                                 try {
                                     socket = new Socket("smtp.163.com", 25);
                                     output = new PrintWriter(new OutputStreamWriter(socket
                                             .getOutputStream()));
                                     input = new BufferedReader(new InputStreamReader(socket
                                             .getInputStream()));
                                     output.println("helo 163");
                                     output.flush();
                                     Log.i(input.readLine(), "info");
                                     //验证登陆
                                     output.println("auth login");
                                     output.flush();
                                     Log.i(input.readLine(), "info");
                                     String username;
                                     //用户名
                                     username = android.util.Base64.encodeToString(Email.getBytes("UTF-8"), Base64.NO_WRAP);
                                     output.println(username);
                                     output.flush();
                                     Log.i(input.readLine(), "info");
                                     //密码
                                     String pass = Password;
                                     pass = android.util.Base64.encodeToString(pass.getBytes("UTF-8"), Base64.NO_WRAP);
                                     output.println(pass);
                                     output.flush();
                                     Log.i(input.readLine(), "info");
                                     String login = input.readLine();
                                     Log.i(login, "info");
                                     //发件人
                                     output.println("mail from: <" + Email + "@163.com>");
                                     output.flush();
                                     Log.i(input.readLine(), "info");
                                     //收件人
                                     output.println("rcpt to: <" + number + ">");
                                     output.flush();
                                     Log.i(input.readLine(), "info");
                                     //内容
                                     output.println("data");
                                     output.flush();
                                     Log.i(input.readLine(), "info");
                                     String con = "From: 网易邮箱<"+Email+"@163.com\r\n";
                                     con += "To: <" + number + ">\r\n";
                                     con = con + "Subject: " + subject + "\r\n";
                                     con = con + "Content-Type: text/plain;charset=\"utf-8\"\r\n";
                                     con = con + "\r\n";
                                     con = con + message + "\r\n";
                                     con = con + ".\r\n";
                                     output.println(con);
                                     output.flush();
                                     Log.i(input.readLine(), "info");
                                     Log.i(input.readLine(), "info");
                                     socket.close();
                                     input.close();
                                     output.close();
                                     System.out.println("Done");
                                 } catch (Exception e) {
                                     System.out.println("Error " + e);
                                 }
                             }
                         }.start();
                         Intent intent = new Intent(SendActivity.this, ReceiveActivity.class);
                         intent.putExtra("Email", Email);
                         intent.putExtra("Password", Password);
                         startActivity(intent);
                     }
                 });
    }
}
