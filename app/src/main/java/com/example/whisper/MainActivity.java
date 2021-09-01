package com.example.whisper;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    private Button loginBtn, registration,admin;
    private TextView headTitle, subTitle;
    private TextInputLayout mailaddress, password;
    private String usermailaddress = "";
    private String userpassword = "";

    @SuppressLint({"CutPasteId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setTitle("Whisper");

        //事例画所有控件
        mailaddress = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);

        loginBtn = findViewById(R.id.buttonLogin);
        registration = findViewById(R.id.buttonRegister);
        headTitle= findViewById(R.id.title);
        subTitle = findViewById(R.id.subtitle);
        //声明用到的组件
        Button loginBtn = findViewById(R.id.buttonLogin);
        Button registrationBtn = findViewById(R.id.buttonRegister);

        //给按钮添加监听事件
        loginBtn.setOnClickListener(onClickListenr);
        registrationBtn.setOnClickListener(onClickListenr);
    }

    /**
     * 按钮监听类，处理按钮事件
     */
    private View.OnClickListener onClickListenr = v -> {
        if (v.getId() == R.id.buttonLogin) {
            //要开启一个新的线程，主线程无法获得僌输入流
            if(!validateUsername() | !validatePassword()) {
                return;
            }else{
                this.usermailaddress = mailaddress.getEditText().getText().toString().trim();
                this.userpassword = password.getEditText().getText().toString().trim();
                new Thread(() -> api("http://click.ecc.ac.jp/ecc/whisper_d/api/login.php?usermailaddress=" + this.usermailaddress + "&" + "password=" + this.userpassword)).start();
            }
        }
        if (v.getId() == R.id.buttonRegister) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Registration.class);
            Pair[] pairs = new Pair[6];
            pairs[0] = new Pair<View,String>(headTitle,"transition_text_title");
            pairs[1] = new Pair<View,String>(subTitle,"transition_text_subTitle");
            pairs[2] = new Pair<View,String>(mailaddress,"transition_username");
            pairs[3] = new Pair<View,String>(password,"transition_password");
            pairs[4] = new Pair<View,String>(loginBtn,"transition_next_btn");
            pairs[5] = new Pair<View,String>(registration,"transition_login_sign");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        }
    };

    /**
     * 自定义一个消息提示窗口
     *
     * @param msg
     */
    private void showDialog(final String msg) {

        //回到主线程，否则拿不到msg
        MainActivity.this.runOnUiThread(() -> {
            try {
                Log.d("LOGCAT", msg);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(msg).setCancelable(false).setPositiveButton("确定",
                        (dialog, id) -> {

                        });
                AlertDialog alert = builder.create();
                alert.show();
            } catch (Exception e) {
                showDialog(e.getMessage());
            }
        });
    }

    private void api(String urlStr) {
        //要传递的数据
        //urlStr = "http://click.ecc.ac.jp/ecc/whisper_d/api/login.php?usermailaddress="+email+"&"+"password="+pass;
        try {
            URL url = new URL(urlStr);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            //获得输入流
            InputStream in = conn.getInputStream();

            //创建一个2048字节数的缓冲区
            byte[] buffer = new byte[2048];

            //在输入流中读取数据并存放到缓冲字节数组中
            in.read(buffer);
            //将字节转换成字符串

            String msg = new String(buffer);

            JSONObject jo = new JSONObject(msg);
            if (jo.getString("result").equals("true")) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Timeline.class);
                intent.putExtra("usericon", jo.getString("usericon"));
                intent.putExtra("username", jo.getString("username"));
                intent.putExtra("whisperusermailaddress", usermailaddress);
                String timelinepass = "http://click.ecc.ac.jp/ecc/whisper_d/api/timeline.php?usermailaddress=";
                intent.putExtra("timelinepass", timelinepass + usermailaddress);
                startActivity(intent);
                MainActivity.this.finish();
            } else if (jo.getString("message").equals("please check your mailaddress")) {
                showDialog("正しいメールアドレスを入力してください。");

            } else if (jo.getString("message").equals("This mailaddress is new")) {
                showDialog("このメールアドレスが未登録です。");
            } else if (jo.getString("message").equals("wrong-password")) {
                showDialog("パスワードが違います。");
            }
            //关闭数据流
            in.close();
        } catch (Exception e) {
            showDialog(e.getMessage());
        }
    }

    //Validate Username　Emailが入力されるかチェックする
    private Boolean validateUsername(){
        String val = mailaddress.getEditText().getText().toString();

        if (val.isEmpty()){
            mailaddress.setError("メールを入力下さい");
            return false;
        }else {
            mailaddress.setError(null);
            mailaddress.setErrorEnabled(false);
            return true;
        }
    }

    // Validate Password　パスワードが入力されるかチェックする
    private Boolean validatePassword(){
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()){
            password.setError("パスワードを入力下さい");
            return false;
        }else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
}