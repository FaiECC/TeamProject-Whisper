package com.example.whisper;

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
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;

public class Registration extends MainActivity {
    TextInputLayout name, password,password2,mailaddress;
    //TextView
    TextView headTitle,subTitle;
    //EditText name, password, password2, mailaddress;
    Button registerBtn,backToLogin;;
    private String usermailaddress, username, userpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setTitle("登録");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(view -> finish());

        //ボタンやエディター
        name = findViewById(R.id.userEdittext);
        mailaddress = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        password2 = findViewById(R.id.passwordEditText2);
        registerBtn = findViewById(R.id.buttonRegister);
        backToLogin = findViewById(R.id.forSignUp);

        //Hooks text view
        headTitle = findViewById(R.id.createAc);
        subTitle = findViewById(R.id.sign2start);
//        //ボタンlintenr
//        registerBtn.setOnClickListener(onClickListenr);
        // back to login
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this, MainActivity.class);

                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(headTitle, "transition_text_title");
                pairs[1] = new Pair<View, String>(subTitle, "transition_text_subTitle");
                pairs[2] = new Pair<View, String>(name, "transition_username");
                pairs[3] = new Pair<View, String>(password, "transition_password");
                pairs[4] = new Pair<View, String>(registerBtn, "transition_next_btn");
                pairs[5] = new Pair<View, String>(backToLogin, "transition_login_sign");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Registration.this, pairs);
                    startActivity(intent, options.toBundle());
                }
            }
        });
    }

    /**
     * 按钮监听类，处理按钮事件
     */
    public void registerUser(View view){

        if( !validateUsername() | !validateEmail() | !validatePassword() | !validatePasswordConf()){
            return;
        }
        this.usermailaddress = mailaddress.getEditText().getText().toString();
        String pass = password.getEditText().getText().toString();
        String pass2 = password2.getEditText().getText().toString();
        this.username = name.getEditText().getText().toString();
        //要开启一个新的线程，主线程无法获得僌输入流
        //mainスレッドがデータを受け取れないから、新しいスレッドで
        if (pass.equals(pass2)){
            this.userpassword = pass;
            new Thread(() -> api("http://click.ecc.ac.jp/ecc/whisper_d/api/registration.php?usermailaddress="+this.usermailaddress+"&"+"&username="+this.username+"&"+"password="+this.userpassword)).start();
        }else{
            showDialog("パスワードが一致しません。");
        }

    }

    /**
     * メッセージウィンドウ
     *
     * @param msg
     */
    private void showDialog(final String msg) {

        //回到主线程，否则拿不到msg
        //mainスレッドに戻る、しないとmsgを受け取れない
        Registration.this.runOnUiThread(() -> {
            try {
                Log.d("LOGCAT", msg);
                AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
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
        try {
            URL url = new URL(urlStr);
            //获得连接
            //コネクター
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            //获得输入流
            //データを受け取る
            InputStream in = conn.getInputStream();

            //创建一个2048字节数的缓冲区
            //２０４８byteのバッファ
            byte[] buffer = new byte[2048];

            //在输入流中读取数据并存放到缓冲字节数组中
            //バッファの内容を読む
            in.read(buffer);

            //将字节转换成字符串
            //ストリングに転換

            String msg = new String(buffer);

//            JSONObject jo = new JSONObject(msg);
//                if(jo.getString("result").equals("success")){
//                    Intent intent = new Intent();
//                    intent.setClass(registration.this,Timeline.class);
//                    startActivity(intent);
//                    registration.this.finish();
//                }else if (jo.getString("message").equals("please check your mailaddress")){
//                    showDialog("メールアドレスエラー");
//
//                }else if (jo.getString("message").equals("This mailaddress is new")) {
//                    showDialog("未登録メールアドレス");
//                }else if(jo.getString("message").equals("wrong-password")){
//                    showDialog("パスワードエラー");
//
//                }

            showDialog(msg);
            //showDialog(jo.getString("result"));
            //showDialog(jo.toString());

            //关闭数据流
            in.close();
        } catch (Exception e) {
            showDialog(e.getMessage());
        }
    }

    ////////// 入力エラーをチェックする

    //Weak Password Pattern
    private static final Pattern weakPwdPattern = Pattern.compile("[a-zA-Z0-9]" +".{6,}");
    //private static final Pattern weakPwdPattern = Pattern.compile("^" + "(?=.*[a-zA-Z])" + "(?=.*[@#$%^&+=])" + "(?=\\s+$)" + ".{6,}" + "$");


    private Boolean validateUsername(){
        String val = name.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\Z";

        if (val.isEmpty()){
            name.setError("入力が必要");
            return false;
        }else if(val.length()>=15){
            name.setError("ユーザー名は15文字以下");
            return false;
        }else if(!val.matches(noWhiteSpace)){
            name.setError("空白文字が入力できません");
            return false;
        }else{
            name.setError(null);
            return true;
        }
    }

    private Boolean validateEmail(){
        String val = mailaddress.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()){
            mailaddress.setError("入力が必要");
            return false;
        }else if(!val.matches(emailPattern)){
            mailaddress.setError("メールアドレス確認できません");
            return false;
        }else{
            mailaddress.setError(null);
            return true;
        }
    }

    private Boolean validatePassword(){
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()){
            password.setError("入力が必要");
            return false;
        }else if(!weakPwdPattern.matcher(val).matches()) {
            password.setError("Password is too weak");
            return false;
        }else{
            password.setError(null);
            return true;
        }
    }

    private Boolean validatePasswordConf(){
        String cmf = password2.getEditText().getText().toString();
        String pwd = password2.getEditText().getText().toString();

        if (cmf.isEmpty()){
            password2.setError("入力が必要");
            return false;
        }else if(!cmf.equals(pwd)) {
            password2.setError("パスワードが間違い");
            return false;
        }else{
            password2.setError(null);
            return true;
        }
    }
}