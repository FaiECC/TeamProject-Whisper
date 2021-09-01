package com.example.whisper;

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

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChoosedUser extends AppCompatActivity {

    //show icon for intent
    private String useicon = "";
    private ImageView TV_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosed_user);

        //intentから値をget
        Intent i = getIntent();
        String username = i.getStringExtra("username");
        String userfollows = i.getStringExtra("userfollows");
        String userfollowers = i.getStringExtra("userfollowers");
        String userprofile = i.getStringExtra("userprofile");
        useicon = i.getStringExtra("usericon");

        //レイアウトの値をget
        TextView TV_username = findViewById(R.id.TV_follow_username);
        TextView TV_useprofile = findViewById(R.id.TV_follow_profile);
        TextView TV_userfollows = findViewById(R.id.TV_follow_following);
        TextView TV_userfollowers = findViewById(R.id.TV_follow_followers);

        TV_icon = findViewById(R.id.imageView_follow_icon);

        //画面に値をset
        TV_username.setText(username);
        TV_useprofile.setText(userprofile);
        TV_userfollows.setText("フォロー中：" + userfollows);
        TV_userfollowers.setText("フォロワー数：" + userfollowers);
//        showimage();

    }

    public void showimage() {
        //子线程请求网络,Android4.0以后访问网络不能放在主线程中
        new Thread() {
            private HttpURLConnection conn;

            public void run() {
                // 连接服务器 get 请求 获取图片
                try {
                    //创建URL对象
                    URL url = new URL(useicon);
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
                    TV_icon.setImageBitmap(bitmap);
                    break;
                case 2:
                    Toast.makeText(ChoosedUser.this, "image_error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}