package com.example.whisper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class Timeline_sub extends AppCompatActivity {
    private String subimage = "";
    private ImageView TV_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_sub);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("詳細");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        Intent i = getIntent();
        String whispername = i.getStringExtra("whispername");
        String whisper = i.getStringExtra("whisper");
        String whispertime = i.getStringExtra("whispertime");
        subimage = i.getStringExtra("imagepath");
        String whisperconutlikes = i.getStringExtra("whisperconutlikes");
        TextView TV_username = findViewById(R.id.subusername);
        TextView TV_whisper = findViewById(R.id.subwhisper);
        TextView TV_time = findViewById(R.id.subwhispertime);
        TextView TV_countlikes =findViewById(R.id.subconutlikes);
        TV_image = findViewById(R.id.subimage);
        TV_username.setText(whispername);
        TV_whisper.setText(whisper);
        TV_time.setText(whispertime);
        TV_countlikes.setText(whisperconutlikes);

        showimage();

    }

    public void showimage() {
        //子线程请求网络,Android4.0以后访问网络不能放在主线程中
        new Thread() {
            private HttpURLConnection conn;

            public void run() {
                // 连接服务器 get 请求 获取图片
                try {
                    //创建URL对象
                    URL url = new URL(subimage);
                    // 根据url 发送 http的请求
                    conn = (HttpURLConnection) url.openConnection();
                    // 设置请求的方式
                    conn.setRequestMethod("GET");
                    //设置超时时间
                    conn.setConnectTimeout(5000);
                    // 得到服务器返回的响应码
                    int code = conn.getResponseCode();
                    //请求网络成功后返回码是200
                    if (code == 200) {
                        //获取输入流
                        InputStream is = conn.getInputStream();
                        //将流转换成Bitmap对象
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        //将更改主界面的消息发送给主线程
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = bitmap;
                        handler_icon.sendMessage(msg);
                    } else {
                        //返回码不等于200 请求服务器失败
                        Message msg = new Message();
                        msg.what = 2;
                        handler_icon.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 2;
                    handler_icon.sendMessage(msg);
                }
                //关闭连接
                conn.disconnect();
            }
        }.start();

    }
    @SuppressLint("HandlerLeak")
    public final Handler handler_icon = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    TV_image.setImageBitmap(bitmap);
                    break;
                case 2:
                    Toast.makeText(Timeline_sub.this, "image_error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}