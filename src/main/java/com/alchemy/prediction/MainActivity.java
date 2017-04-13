package com.alchemy.prediction;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.name;
import static java.lang.System.in;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    String url = "http://www.dotamax.com/hero/rate/";
    Button updateDB,hero1,hero2,hero3,hero4,hero5,hero6,hero7,hero8,hero9,hero10;
    DBHelper dbHelper;
    int currentSelect;
    String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateDB = (Button)findViewById(R.id.update_db);
        updateDB.setOnClickListener(this);
        hero1 = (Button)findViewById(R.id.hero1);
        hero2 = (Button)findViewById(R.id.hero2);
        hero3 = (Button)findViewById(R.id.hero3);
        hero4 = (Button)findViewById(R.id.hero4);
        hero5 = (Button)findViewById(R.id.hero5);
        hero6 = (Button)findViewById(R.id.hero6);
        hero7 = (Button)findViewById(R.id.hero7);
        hero8 = (Button)findViewById(R.id.hero8);
        hero9 = (Button)findViewById(R.id.hero9);
        hero10 = (Button)findViewById(R.id.hero10);
        hero1.setOnClickListener(this);
        hero2.setOnClickListener(this);
        hero3.setOnClickListener(this);
        hero4.setOnClickListener(this);
        hero5.setOnClickListener(this);
        hero6.setOnClickListener(this);
        hero7.setOnClickListener(this);
        hero8.setOnClickListener(this);
        hero9.setOnClickListener(this);
        hero10.setOnClickListener(this);
        dbHelper = new DBHelper(this);
    }

    public void updateDB(){
        ParseHtmlTask task = new ParseHtmlTask();
        task.execute(url);
    }


    class ParseHtmlTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            return Net.parseURL(url);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Document allHeros = Jsoup.parse(s);
            Elements heros = allHeros.select("tr");
            for(Element hero : heros){
                Document anHero = Jsoup.parse(hero.toString());
                Elements heroName = anHero.select(".hero-name-list");
                String name = heroName.text();

                if(TextUtils.isEmpty(heroName.text())) continue;

                Element imgData = anHero.select("img").first();
                String img = imgData.attr("src");

                Elements heroDate = anHero.select("div");
                String rate = heroDate.get(0).text();
                String counts = heroDate.get(2).text();
//                for(Element data : heroDate){
//                    Log.i("gejun","date = "+data.text());
//                }
//                Log.i("gejun","heroRate = "+heroRate.text());
//                Elements heroCount = anHero.select("");
                if(isHeroExists(name)){
                    update(name, img, rate, counts);
                } else {
                    insert(name, img, rate, counts);
                }
//                Log.i("gejun","heroName = "+heroName.text()+", heroRate = "+heroDate.get(0).text()+", heroCount = "+heroDate.get(2).text()+", img = "+i);
            }
        }
    }

    public boolean isHeroExists(String name){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query("hero_info", null, "name=?",new String[]{name},null,null,null);
        if(c != null && c.getCount()>0) {
            c.close();
            return true;
        }
        return false;
    }

    public void insert(String name, String img, String rate, String counts){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",name);
        values.put("counts",counts);
        values.put("rate",rate);
        values.put("img",img);
        db.insert("hero_info", null, values);
    }

    public void update(String name, String img, String rate, String counts){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("counts",counts);
        values.put("rate",rate);
        values.put("img",img);
        db.update("hero_info", values, "name=?", new String[]{name});
    }

    public void selectHero(int id){
        currentSelect = id;
        Intent it = new Intent(this, HeroListActivity.class);
        startActivityForResult(it, 0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.update_db:
                updateDB();
                break;
            case R.id.hero1:
            case R.id.hero2:
            case R.id.hero3:
            case R.id.hero4:
            case R.id.hero5:
            case R.id.hero6:
            case R.id.hero7:
            case R.id.hero8:
            case R.id.hero9:
            case R.id.hero10:
                selectHero(id);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            currentName = data.getStringExtra("name");
//            Log.i("gejun","name = " + name);
            new HeroImgTask().execute(currentName);
        }
    }

    class HeroImgTask extends AsyncTask<String, Integer, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            InputStream in=null;
            String name = params[0];
            Cursor c = dbHelper.getReadableDatabase().query("hero_info", null, "name=?", new String[]{name}, null, null,null);
            if(c!= null&&c.getCount()>0){
                c.moveToFirst();
                int rateIndex = c.getColumnIndex("rate");
                int countsIndex = c.getColumnIndex("counts");
                int imgIndex = c.getColumnIndex("img");
                String rate = c.getString(rateIndex);
                String img = c.getString(imgIndex);
                String counts = c.getString(countsIndex);

                Log.i("gejun","img = " + img);
                in = Net.doUrl("http://content.52pk.com/files/100623/2230_102437_1_lit.jpg");
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmap;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
//            Bitmap bitmap = BitmapFactory.decodeStream(in);
            BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);

            switch(currentSelect){
                case R.id.hero1:
                    hero1.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero1.setBackground(bd);
                    hero1.setText(currentName);
                    break;
                case R.id.hero2:
                    hero2.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero2.setBackground(bd);
                    hero2.setText(currentName);
                    break;
                case R.id.hero3:
                    hero3.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero3.setBackground(bd);
                    hero3.setText(currentName);
                    break;
                case R.id.hero4:
                    hero4.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero4.setBackground(bd);
                    hero4.setText(currentName);
                    break;
                case R.id.hero5:
                    hero5.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero5.setBackground(bd);
                    hero5.setText(currentName);
                    break;
                case R.id.hero6:
                    hero6.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero6.setBackground(bd);
                    hero6.setText(currentName);
                    break;
                case R.id.hero7:
                    hero7.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero7.setBackground(bd);
                    hero7.setText(currentName);
                    break;
                case R.id.hero8:
                    hero8.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero8.setBackground(bd);
                    hero8.setText(currentName);
                    break;
                case R.id.hero9:
                    hero9.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero9.setBackground(bd);
                    hero9.setText(currentName);
                    break;
                case R.id.hero10:
                    hero10.setCompoundDrawablesWithIntrinsicBounds(null,(Drawable)bd,null,null);
                    hero10.setBackground(bd);
                    hero10.setText(currentName);
                    break;

            }
        }
    }
}
