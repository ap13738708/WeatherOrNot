package com.example.weather2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

/**
 * Created by User on 25/11/2017.
 */

public class Animation_001_Layout extends View implements Runnable {

    Bitmap cloud_bm,cloud_bm2,question_bm;
    int cloud_x,cloud2_x,cloud_y;
    int x1,x2;
    int point_x,point_y;
    int sweep ;
    int radius;
    int elongate,elongate2;
    Thread T1,T2,T3;
    Path myPath;
    public Animation_001_Layout(Context context) {
        super(context);
        setBackgroundResource(R.drawable.intro);
        cloud_x=30;
        cloud2_x= 1000;
        cloud_y=700;
        x1=5;
        x2=2;
        sweep=0;
        radius = 150;
        elongate =0;
        elongate2=0;

        if (T1 == null) {
            T1 = new Thread(this);
            T1.start();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cloud_bm = BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
        cloud_bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.cloud);
        question_bm = BitmapFactory.decodeResource(getResources(), R.drawable.question);

        Paint blue = new Paint();
        blue.setColor(Color.parseColor("#163676"));
        blue.setStyle(Paint.Style.STROKE);
        blue.setStrokeWidth(40);

        myPath = new Path();
        Path line = new Path();
        Path line2= new Path();


        if(cloud_x!=cloud2_x) {

            cloud_x += x1;
            canvas.drawBitmap(cloud_bm, cloud_x, cloud_y, null);

            cloud2_x -= x1;
            canvas.drawBitmap(cloud_bm, cloud2_x, cloud_y, null);
        }

        else if(sweep<=270){
            //canvas.drawBitmap(question_bm, canvas.getWidth()/2-40, canvas.getHeight()/2-150, null);
            point_x=canvas.getWidth()/2;
            point_y=canvas.getHeight()/2-150;
            sweep+=3;

            final RectF oval = new RectF();
            oval.set(point_x - radius, point_y - radius-20, point_x+ radius, point_y+ radius+20);
            myPath.arcTo(oval, 180, sweep, true);
            canvas.drawPath(myPath,blue);
        }

        else if(elongate<=150) {
            point_x = canvas.getWidth() / 2;
            point_y = canvas.getHeight() / 2 - 150;

            final RectF oval = new RectF();
            oval.set(point_x - radius, point_y - radius - 20, point_x + radius, point_y + radius + 20);
            myPath.arcTo(oval, 180, 270, true);
            canvas.drawPath(myPath, blue);

            if (elongate < 150) {
                line.moveTo(point_x, point_y + radius);
                line.lineTo(point_x, point_y + radius + elongate);
                canvas.drawPath(line, blue);
            }
        }
        else {
            final RectF oval = new RectF();
            oval.set(point_x - radius, point_y - radius-20, point_x+ radius, point_y+ radius+20);
            myPath.arcTo(oval, 180, sweep, true);
            canvas.drawPath(myPath, blue);
            line.moveTo(point_x, point_y + radius);
            line.lineTo(point_x, point_y + radius + elongate);
            canvas.drawPath(line, blue);
            line2.moveTo(point_x, point_y + radius + elongate+50);
            line2.lineTo(point_x, point_y + radius + elongate+50+elongate2);
            canvas.drawPath(line2, blue);
            ((Animation) getContext()).startAct();
        }


    }
    public int getElongate(){
        return elongate2 ;
    }

    @Override
    public void run() {
        while (Thread.currentThread() == T1){
            if (cloud_x==cloud2_x) {

                T2 = new Thread(this);
                T2.start();
                break;
            }
            //
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            postInvalidate();
        }

        while (Thread.currentThread() == T2){

            if(sweep>=270&&elongate<=150){
                elongate+=1;
            }

            if(elongate >150) {
                T3 = new Thread(this);
                T3.start();
                break;
            }

            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            postInvalidate();
        }

        while (Thread.currentThread() == T3) {
            elongate2+=1;
            if (elongate2>50) break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
            postInvalidate();

        }
        Log.i("check", String.valueOf(elongate2));

    }
}

