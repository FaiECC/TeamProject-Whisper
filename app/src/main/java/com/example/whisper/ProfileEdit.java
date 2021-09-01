package com.example.whisper;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProfileEdit extends AppCompatActivity {
    private Button save,cancel,image_save;
    private EditText et_name,et_profile;
    private CircleImageView icon;
    private String usericon,name,profile,usermailaddress,addate,whispers,follows,followers;
    private final String apipath = "http://click.ecc.ac.jp/ecc/whisper_d/api/showuserinformation.php?usermailaddress=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("情報編集");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
        Intent i = getIntent();
        usericon = i.getStringExtra("usericon");
        usermailaddress = i.getStringExtra("usermailaddress");
        //事例画所有控件
        icon = findViewById(R.id.imageView_timeline_icon);
        et_name = findViewById(R.id.ET_Name);
        et_profile = findViewById(R.id.ET_Profile);
        api();
        image_save = findViewById(R.id.BtnChangeIcon);
        save = findViewById(R.id.BtnSave);
        cancel = findViewById(R.id.BtnCancel);
        //给按钮添加监听事件
        save.setOnClickListener(onClickListenr);
        cancel.setOnClickListener(onClickListenr);

    }
    /**
     * 按钮监听类，处理按钮事件
     */
    private View.OnClickListener onClickListenr = v -> {

        if (v.getId() == R.id.BtnSave) {

        }
        if (v.getId() == R.id.BtnSave) {

        }
        if (v.getId() == R.id.BtnChangeIcon){

        }
    };


    private void api() {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                //服务端访问地址
                Request request = new Request
                        .Builder()
                        .url(apipath+usermailaddress).build();
                Response response = client.newCall(request).execute();
                //得到服务器返回的数据后，调用parseJSONWithJSONObject进行解析
                String responseData = Objects.requireNonNull(response.body()).string();
                parseJSONWithJSONObject(responseData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String jsonData) {
        if (jsonData != null) {
            try {
                JSONObject jsonObject = new JSONObject(jsonData);

                //获取数据中的code值，如果是0则正确
                String resultCode = jsonObject.getString("result");
                if (resultCode.equals("true")) {
                    //获取到json数据中里的data内容
                    JSONArray resultJsonArray = jsonObject.getJSONArray("data");
                    Log.d("Editer", "data+++" + resultJsonArray);
                    //循环遍历，获取json数据中data数组里的内容
                    JSONObject Object = resultJsonArray.getJSONObject(0);
                    //usericon = Object.getString("usericon");
                    name = Object.getString("username");
                    profile = Object.getString("userprofile");
                    addate = Object.getString("userregistereddate");
                    whispers = Object.getString("userwhispers");
                    follows = Object.getString("userfollows");
                    followers = Object.getString("userfollowers");
                    et_profile.setHint(profile);
                    et_name.setHint(name);
                    icon.setImageDrawable(loadImageFromNetwork(usericon));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }






    }

    private Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        if(imageUrl.length()>43){
            try {
                // 可以在这里通过文件名来判断，是否本地有此图片
                drawable = Drawable.createFromStream(
                        new URL(imageUrl).openStream(), "image.jpg");
            } catch (IOException e) {
                Log.d("test", e.getMessage());
            }
            if (drawable == null) {
                Log.d("test", "null drawable");
            } else {
                Log.d("test", "not null drawable");
            }
        }
        return drawable;
    }
//    @SuppressLint("HandlerLeak")
//    public Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg2) {
//
//            switch (msg2.what) {
//                case 1:
//                    //list_item.notifyDataSetChanged();
//                    myTimelineadapter.notifyDataSetChanged();
//                    break;
//                case 2:
//                    Toast.makeText(Timeline.this, "error",
//                            Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
//    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }

//    @SuppressLint("NonConstantResourceId")
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//
//        switch (item.getItemId()) {
//            case R.id.menu_Search:
//                Intent intent = new Intent(this, Search.class);
//                startActivity(intent);
//
//                return true;
//            case R.id.menu_whisper:
//                Intent intent1 = new Intent(this, WhisperUpload.class);
//                startActivity(intent1);
//                return true;
//            case R.id.menu_follow:
//                Intent intent2 = new Intent(this, Followers.class);
//                startActivity(intent2);
//                return true;
////            case R.id.menu_profileEdit:
////                Intent intent3 = new Intent(this, ProfileEdit.class);
////                startActivity(intent3);
////                return true;
//            case R.id.menu_timeline:
//                Intent intent4 = new Intent(this, Timeline.class);
//                startActivity(intent4);
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}