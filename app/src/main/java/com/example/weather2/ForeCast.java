package com.example.weather2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class ForeCast extends AppCompatActivity {

    double latitude, longtitude;
    TextView txtCity, lastUpdate;
    DownloadTask task;
    JSONInput JsonInput;
    String city,firstpart, apiid, weather, description, humidity, temp, celcius , detail, icon, imageLink = null;
    String country = "";
    ListView mListView;
    ArrayList<String> text, images ;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fore_cast);
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("Lat", 0);
        longtitude = intent.getDoubleExtra("Long",0);
        country = intent.getStringExtra("country");
        task = new DownloadTask();
        lastUpdate = findViewById(R.id.lastUpdate);
        txtCity =  findViewById(R.id.txtCity);
        mListView = findViewById(R.id.listView);
        if (getSupportActionBar() != null)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStart(){
        super.onStart();
        txtCity.setText("Loading.....");
        Log.i("test coordinates0", "" + latitude + " " + longtitude);
        firstpart = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longtitude;
        apiid = "&appid=8a9cbc8db325a1ff4287792759faf76c";
        Log.i("test coordinates0", "" + latitude + " " + longtitude);
        if (count == 0) {
            task.execute(firstpart + apiid);
            count++;
        }
        Log.i("test", "task executed");
        Log.i("test", "finished successfully");

    }
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (true) {
                    if (data == -1) {
                        break;
                    }
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JsonInput = new JSONInput(result);
                JsonInput.setJsonArray("list");


                text = new ArrayList<String>();
                images = new ArrayList<String>();

                country = JsonInput.getJsonObjectFromJsonObject("city","country");
                city = JsonInput.getJsonObjectFromJsonObject("city","name");
                for (int i = 0;i< JsonInput.getArrayListSize();i++) {
                    weather = JsonInput.getJsonObjectFromJsonArray("list", "dt_txt", i);
                    description = JsonInput.getJsonFromArrayList("weather", "description", i);
                    humidity = JsonInput.getJsonFromArrayListFromObject("main", "humidity", i);
                    temp = JsonInput.getJsonFromArrayListFromObject("main", "temp", i);
                    celcius = JSONInput.kelvinToCelcius(temp) + " \u2103";
                    icon = JsonInput.getJsonFromArrayList("weather", "icon", i);
                    imageLink = getImage(icon);
                    detail = weather + ":" + "\n" + "\t\t\t" + description + ", Humidity: " + humidity + "%" + ", " + celcius;
                    images.add(imageLink);
                    text.add(detail);
                    Log.i("check", weather + "  " +i);
                }

                CustomAdaptor customAdaptor = new CustomAdaptor();
                mListView.setAdapter(customAdaptor);


                txtCity.setText(city + ", " + country);
                lastUpdate.setText("Last Updated: "+getDateNow());
                Log.i("test", "JSON finished successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getDateNow(){
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public static String unixTimeStampToDateTime(double unixTimeStamp) {
        DateFormat dateFormat =  new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long)unixTimeStamp*1000);
        return dateFormat.format(date);
    }
    public static String getImage(String icon){
        return String.format("http://openweathermap.org/img/w/%s.png",icon);
    }

    class CustomAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return text.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.customlayout, null);

            ImageView mImageView = view.findViewById(R.id.imageView);
            TextView mTextView = view.findViewById(R.id.textView);

            Picasso.with(ForeCast.this).load(images.get(position)).into(mImageView);
            mTextView.setText(text.get(position));

            return view;
        }
    }
}
