package com.example.administrator.email;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChoiceActivity extends AppCompatActivity {

    ListView mListView;
    ArrayList<Map<String,Object>> array=new ArrayList<Map<String, Object>>();
    String email[];
    String password[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        mListView = (ListView) findViewById(R.id.choice_list);
        SQLiteDatabase db=openOrCreateDatabase("users.db",MODE_PRIVATE,null);
        Cursor c=db.rawQuery("select email,password from users",null);
        email=new String[c.getCount()];
        password=new String[c.getCount()];
        //c.moveToNext();
        int i=0;
        while(c.moveToNext()){
            Map<String, Object> item = new HashMap<String, Object>();
            array.add(item);
            email[i]=c.getString(c.getColumnIndex("email"));
            item.put("email","邮箱号: "+email[i]+"@163.com");
            password[i]=c.getString(c.getColumnIndex("password"));
            i++;
        }
        //定义一个SimpleAdapter
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), array, R.layout.list_choice,
                new String[]{"email"},
                new int[]{R.id.choice_email});
        //设置mListView的适配器为adapter
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new OnItemClickListenerImpl());
    }

    private class OnItemClickListenerImpl implements AdapterView.OnItemClickListener {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent intent = new Intent(ChoiceActivity.this, MainActivity.class);
            intent.putExtra("Email", email[position]);
            intent.putExtra("Password",password[position]);
            setResult(1001,intent);
            finish();
        }
    }
}
