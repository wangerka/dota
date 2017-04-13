package com.alchemy.prediction;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.name;

/**
 * Created by gejun on 2017/4/13.
 */

public class HeroListActivity extends Activity {
    ListView list;
    List<String> names;
    String[] data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hero_list);
        list = (ListView) findViewById(R.id.list);
        names = new ArrayList<String>();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("hero_info", new String[]{"name"}, null,null,null,null,null);
        if(c != null & c.getCount()>0){
            c.moveToFirst();
            while(c.moveToNext()){
                names.add(c.getString(0));
            }
        }
        c.close();

        data = (String[])names.toArray(new String[names.size()]);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,
                data);

        list.setAdapter(arrayAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(HeroListActivity.this, data[position], Toast.LENGTH_SHORT).show();
                Intent it = getIntent();
                it.putExtra("name", data[position]);
                setResult(RESULT_OK, it);
                finish();
            }
        });
    }
}
