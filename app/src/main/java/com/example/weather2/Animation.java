package com.example.weather2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Animation extends AppCompatActivity {
    Animation_001_Layout animation01_LayoutView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        animation01_LayoutView = new Animation_001_Layout(this);
        setContentView(animation01_LayoutView);
    }

    public void startAct() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
