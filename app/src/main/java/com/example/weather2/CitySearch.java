package com.example.weather2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CitySearch extends AppCompatActivity {

    double latitude, longitude;
    TextView climate, txtTemperature, lastUpdate, txtCity, txtTime;
    DownloadTask task2;
    JSONInput JsonInput;
    String temp, weatherdescription, city, firstpart, country, apiid, sunRise, sunSet, humidity, icon = null;
    String citySearch = "";
    Uri uri;
    int count = 0;
    ImageView editBtn, imageView, background;
    Button forecastBtn, submit;
    EditText search;
    private static final int SELECTED_PICTURE = 1;
    private int REQUEST_CAMERA = 0;
    String userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);
        Intent intent = getIntent();
        uri = Uri.parse(intent.getStringExtra("image"));
        citySearch = intent.getStringExtra("txt");
        Log.i("CheckCity", citySearch);
        task2 = new DownloadTask();
        lastUpdate = findViewById(R.id.txtLastUpdate);
        climate = findViewById(R.id.txtDescription);
        txtTemperature = findViewById(R.id.txtCelsius);
        txtCity = findViewById(R.id.txtCity);
        txtTime = findViewById(R.id.txtTime);
        imageView = findViewById(R.id.imageView);
        forecastBtn = findViewById(R.id.forecastBtn);
        editBtn = findViewById(R.id.editBtn);
        background = findViewById(R.id.background);
        submit = findViewById(R.id.button2);
        search = findViewById(R.id.search);
        background.setImageURI(uri);
        initial();


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    protected void onStart() {
        super.onStart();
        Log.wtf("Test citySearch", citySearch);
        firstpart = "http://api.openweathermap.org/data/2.5/weather?q=" + citySearch;
        apiid = "&appid=8a9cbc8db325a1ff4287792759faf76c";
        Log.i("Test citySearch", citySearch);
        if (count == 0) {
            task2.execute(firstpart + apiid);
            count++;
        }

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
            if (result == null) {
                Intent intent1 = new Intent(CitySearch.this, MainActivity.class);
                intent1.putExtra("warning", "City Not Found");
                startActivity(intent1);
            }
            try {

                JsonInput = new JSONInput(result);

                Log.i("CheckBug", "check2");
                //String check = JsonInput.toString();

                latitude = Double.parseDouble(JsonInput.getJsonObjectFromJsonObject("coord", "lat"));
                longitude = Double.parseDouble(JsonInput.getJsonObjectFromJsonObject("coord", "lon"));
                temp = JsonInput.getJsonObjectFromJsonObject("main", "temp");
                txtCity.setText("HelloWorld");
                weatherdescription = JsonInput.getJsonObjectFromJsonArray("weather", "description");

                city = JsonInput.getJsonObject("name");
                country = JsonInput.getJsonObjectFromJsonObject("sys", "country");
                String celcius = JSONInput.kelvinToCelcius(temp) + " \u2103";
                sunRise = JsonInput.getJsonObjectFromJsonObject("sys", "sunrise");
                sunSet = JsonInput.getJsonObjectFromJsonObject("sys", "sunset");
                humidity = JsonInput.getJsonObjectFromJsonObject("main", "humidity");
                icon = JsonInput.getJsonObjectFromJsonArray("weather", "icon");
                climate.setText(weatherdescription + ", Humidity: " + humidity + "%");
                txtTemperature.setText(celcius);
                txtCity.setText(city + ", " + country);
                lastUpdate.setText("Last Updated: " + getDateNow());
                txtTime.setText(String.format("Sun rise/ Sun set: " + "%s/%s", unixTimeStampToDateTime(Double.parseDouble(sunRise)), unixTimeStampToDateTime(Double.parseDouble(sunSet))));
                Picasso.with(CitySearch.this).load(getImage(icon)).into(imageView);

                Log.i("test1", "JSON finished successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Log.i("CheckData",weatherdescription);
        }
    }

    public void onPause() {
        super.onPause();
        SharedPreferences p = this.getSharedPreferences("rial", 0);
        p.edit().putString("image", String.valueOf(uri)).apply();

    }

    public void initial() {
        SharedPreferences p = this.getSharedPreferences("rial", 0);
        String s = p.getString("image", "Image Not Found");
        uri = Uri.parse(s);
        background.setImageURI(uri);
    }

    public static String getImage(String icon) {
        return String.format("http://openweathermap.org/img/w/%s.png", icon);
    }

    public static String getDateNow() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String unixTimeStampToDateTime(double unixTimeStamp) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long) unixTimeStamp * 1000);
        return dateFormat.format(date);
    }
//    public void btnClick(View v){
//        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i,SELECTED_PICTURE);
//    }

    //    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK && requestCode == SELECTED_PICTURE && data != null){
//            uri = data.getData();
//            background.setImageURI(uri);
//
//        }
//    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECTED_PICTURE && data != null) {
                uri = data.getData();
                background.setImageURI(uri);
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    public void home(View view) {
        Intent intent1 = new Intent(CitySearch.this, MainActivity.class);
        //intent1.putExtra("image",uri);
        startActivity(intent1);
    }

    public void submitOnclick(View view) {
        String txt = search.getText().toString();
        Intent intent = new Intent(this, CitySearch.class);
        intent.putExtra("image", uri.toString());
        intent.putExtra("txt", txt);
        startActivity(intent);
    }

    public void next(View view) {
        Intent intent = new Intent(this, ForeCast.class);
        intent.putExtra("Lat", latitude);
        intent.putExtra("Long", longitude);
        intent.putExtra("country", country);
        startActivity(intent);
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(CitySearch.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(CitySearch.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void galleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_PICTURE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
            uri = Uri.fromFile(destination);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        background.setImageBitmap(thumbnail);
    }
}
