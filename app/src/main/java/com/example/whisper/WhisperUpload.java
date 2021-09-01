package com.example.whisper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class WhisperUpload extends AppCompatActivity {
    Button BtnCancel, BtnUpload;
    TextInputLayout EtUpload;
    private String whisper = "";
    private String urlStr = "http://click.ecc.ac.jp/ecc/whisper_d/whisperadd.php?mailaddress=";
    private String usermailaddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whisper_upload);
        Intent i = getIntent();
        this.usermailaddress = i.getStringExtra("usermailaddress") + "&whisper=";
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Whisper");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());

        BtnCancel = findViewById(R.id.BtnCancel);
        BtnUpload = findViewById(R.id.BtnUpload);
        EtUpload = findViewById(R.id.ETUpload);

        BtnCancel.setOnClickListener(v -> EtUpload.getEditText().setText(""));

        BtnUpload.setOnClickListener(v -> {
            whisper = EtUpload.getEditText().getText().toString();
            urlStr += usermailaddress + whisper;
            new Thread(() -> api(urlStr)).start();
        });
    }


    private void api(String urlStr) {
        //要传递的数据
        try {
            URL url = new URL(urlStr);
            //获得连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            //获得输入流
            InputStream in = conn.getInputStream();

            //创建一个512字节数的缓冲区
            byte[] buffer = new byte[512];

            //在输入流中读取数据并存放到缓冲字节数组中
            in.read(buffer);
            //将字节转换成字符串

            String msg = new String(buffer);

            JSONObject jo = new JSONObject(msg);
            if (jo.getString("message").equals("whisper success")) {
                showDialog("投稿成功");
            } else {
                showDialog("投稿失敗");
            }
            //关闭数据流
            in.close();
        } catch (Exception e) {
            showDialog(e.getMessage());
        }
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//
//        switch (item.getItemId()) {
//            case R.id.menu_Seach:
//                Intent intent = new Intent(this, Search.class);
//                startActivity(intent);
//
//                return true;
//            case R.id.menu_whisper:
//                Intent intent1 = new Intent(this,WhisperUpload.class);
//                startActivity(intent1);
//                return true;
//            case R.id.menu_myprofile:
//                Intent intent2 = new Intent(this,Followers.class);
//                startActivity(intent2);
//                return true;
//            case R.id.menu_profileEdit:
//                Intent intent3 = new Intent(this, ProfileEdit.class);
//                startActivity(intent3);
//                return true;
//            case R.id.menu_timeline:
//                Intent intent4 = new Intent(this,Timeline.class);
//                startActivity(intent4);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    /**
     * メッセージwindow
     *
     * @param msg
     */
    private void showDialog(final String msg) {

        //回到主线程，否则拿不到msg
        WhisperUpload.this.runOnUiThread(() -> {
            try {
                Log.d("LOGCAT", msg);
                AlertDialog.Builder builder = new AlertDialog.Builder(WhisperUpload.this);
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
}