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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    double latitude, longitude;
    TextView climate, txtTemperature, lastUpdate, txtCity, txtTime;
    DownloadTask task;
    JSONInput JsonInput;
    String temp, weatherdescription, city, firstpart, country, apiid, sunRise, sunSet, humidity, icon = null;
    Uri uri;
    int count = 0;
    private static final int SELECTED_PICTURE = 1;
    ImageView editBtn, imageView, background;
    Button forecastBtn, submit;
    EditText search;

    private int REQUEST_CAMERA = 0;
    String userChoosenTask;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    }
                }
                break;
            case 1001:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initial();
                }
                break;
            default:
                break;

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if (intent.getStringExtra("warning") != null) {
            Toast.makeText(this, intent.getStringExtra("warning"), Toast.LENGTH_LONG).show();
        }
        task = new DownloadTask();
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
        initial();
//        forecastBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,ForeCast.class);
//                intent.putExtra("country", country);
//                startActivity(intent);
//            }
//        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
//                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, SELECTED_PICTURE);
            }
        });
    }
//    super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == Activity.RESULT_OK) {
//        if (requestCode == SELECTED_PICTURE && data != null)
//            uri = data.getData();
//            background.setImageURI(uri);
//        else if (requestCode == REQUEST_CAMERA)
//            onCaptureImageResult(data);
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECTED_PICTURE && data != null) {
                uri = data.getData();
                background.setImageURI(uri);
            }
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    protected void onStart() {
        super.onStart();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.i("Test Location", "Latitude: " + latitude + "" + "Longitude: " + longitude);
                firstpart = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude;
                apiid = "&appid=8a9cbc8db325a1ff4287792759faf76c";
                Log.i("test coordinates", "" + latitude + " " + longitude);
                if (count == 0) {
                    task.execute(firstpart + apiid);
                    count++;
                }
                Log.i("test", "task executed");
                Log.i("test", "finished successfully");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            Log.i("ERROR", "NETWORK_PROVIDER NOT AVAILABLE");
            climate.setText("OPEN YOUR INTERNET");
        } else {
            locationListener.onLocationChanged(location);
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
            try {
                JsonInput = new JSONInput(result);
                temp = JsonInput.getJsonObjectFromJsonObject("main", "temp");
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
                Picasso.with(MainActivity.this).load(getImage(icon)).into(imageView);

                Log.i("test", "JSON finished successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onPause() {
        super.onPause();
        SharedPreferences p = this.getSharedPreferences("rial", 0);
        p.edit().putString("image", String.valueOf(uri)).apply();

    }

    public void initial() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        } else {
            SharedPreferences p = this.getSharedPreferences("rial", MODE_PRIVATE);
            String s = p.getString("image", "Image Not Found");
            uri = Uri.parse(s);
            background.setImageURI(uri);
        }

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

    public void submitOnclick(View view) {
        String txt = search.getText().toString();
        Intent intent = new Intent(this, CitySearch.class);
        intent.putExtra("image", uri.toString());
        intent.putExtra("txt", txt);
        startActivity(intent);
    }

//    public static Uri getUri(){
//        return uri;
//    }

    public void next(View view) {
        Intent intent = new Intent(this, ForeCast.class);
        intent.putExtra("Lat", latitude);
        intent.putExtra("Long", longitude);
        intent.putExtra("country", country);
        startActivity(intent);
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(MainActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void galleryIntent(){
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
