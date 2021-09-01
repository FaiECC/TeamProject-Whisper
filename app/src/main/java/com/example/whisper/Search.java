package com.example.whisper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.whisper.Adapter.MyTimelineadapter;
import com.example.whisper.Adapter.SearchAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Search extends AppCompatActivity implements SearchAdapter.clickinview,MyTimelineadapter.clickinview {
    private static final ArrayList<Map<String, Object>> list = new ArrayList<>();
    private CircleImageView icon;
    private String whispertime,whisper;
    private String whisperimage;
    protected MyTimelineadapter myTimelineadapter = null;


    private SwipeRefreshLayout refreshLayout;
    private String mailaddress;
    private final String imgpath = "http://click.ecc.ac.jp/ecc/whisper_d/image/";
    protected SearchAdapter searchAdapter = null;
    protected ListView listView;
    private int flag = 0;

    private String search_path;

    private RadioButton s1;
    private RadioButton s2;
    private String key = "";

    private String id,name,profile,whispers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> finish());
        Intent i = getIntent();
        mailaddress = i.getStringExtra("usermailaddress");
        s1 = (RadioButton)findViewById(R.id.RadioBtnUser);
        s2 = (RadioButton)findViewById(R.id.RadioBtnWhisper);
        EditText et_search = findViewById(R.id.ET_Search);



        s1.setOnClickListener(v -> {
            if (s1.isChecked()){
                //search_path=""http://click.ecc.ac.jp/ecc/whisper_d/api/searchuserapi.php?keyword=k&usermailaddress=guojian101@gmail.com";
                //search_path="http://click.ecc.ac.jp/ecc/whisper_d/api/searchuserapi.php?keyword="+key+"&usermailaddress="+mailaddress;
                flag = 1;
            }
        });
        s2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s2.isChecked()){
                   // search_path="http://click.ecc.ac.jp/ecc/whisper_d/api/searchwhisperapi.php?keyword="+key+"&usermailaddress="+mailaddress;
                    flag = 2;
                }
            }
        });

        Button search = findViewById(R.id.BtnSearch);
        search.setOnClickListener(v -> {

            key = et_search.getText().toString();
            api();
        });


//        Button BthSearch = findViewById(R.id.BtnSearch);
//        BthSearch.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "View",
//                Toast.LENGTH_SHORT).show());
//        Intent i = getIntent();
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


    private void api() {
        list.clear();
        listView = findViewById(R.id.listview2);
        if(flag==1){
            searchAdapter = new SearchAdapter(list,getApplicationContext(),
                    Search.this,R.layout.search_item);
            listView.setAdapter(searchAdapter);
        }else{
            myTimelineadapter = new MyTimelineadapter(list,getApplicationContext(),
                    Search.this,R.layout.timeline_item);
            listView.setAdapter(myTimelineadapter);

        }

        setListern();

        if(flag==1) {
            this.search_path = "http://click.ecc.ac.jp/ecc/whisper_d/api/searchuserapi.php?keyword=" + key + "&usermailaddress=" + mailaddress;
        }else {
            this.search_path = "http://click.ecc.ac.jp/ecc/whisper_d/api/searchwhisperapi.php?keyword=" + key + "&usermailaddress=" + mailaddress;
        }

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                    //服务端访问地址
                    Request request = new Request
                            .Builder()
                            .url(search_path).build();

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
                String resultCode = jsonObject.getString("message");
                if (resultCode.equals("データベースの取得に成功した。")) {
                    //获取到json数据中里的data内容
                    JSONArray resultJsonArray = jsonObject.getJSONArray("data");
                    Log.d("Search", "data+++" + resultJsonArray);
                    for (int i = 0; i < resultJsonArray.length(); i++) {
                        //循环遍历，获取json数据中data数组里的内容
                        JSONObject Object = resultJsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        if(flag == 1){

                            try {
                                 id = Object.getString("id");
                                String mailaddress = Object.getString("usermailaddress");
                                String icon = Object.getString("usericon");
                                 name = Object.getString("username");
                                 profile = Object.getString("userprofile");
                                 whispers = Object.getString("userwhispers");
                                String userfollows = Object.getString("userfollowers");
                                String relationship = Object.getString("relationship");
                                Drawable drawable = null;
                                if (!icon.isEmpty()) {
                                    drawable = loadImageFromNetwork(imgpath + icon);
                                }
                                map.put("id", id);
                                map.put("mailaddress", mailaddress);
                                map.put("name", name);
                                map.put("profile", profile);
                                map.put("whispers", whispers);
                                map.put("userfollows", userfollows);
                                map.put("icon", drawable);
                                map.put("relationship", relationship);
                                //保存到ArrayList集合中
                                list.add(map);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }else{

                            try {
                                String whisperId = Object.getString("whisperId");
                                String whisperusericon = Object.getString("whisperusericon");
                                String whispermailaddress = Object.getString("whisperusermailaddress");
                                String whispername = Object.getString("whisperusername");
                                this.whispertime = Object.getString("whispertime");
                                this.whisper = Object.getString("whisper");
                                String whisperimage = Object.getString("whisperimage");
                                String whisperconutlikes = Object.getString("whisperconutlikes");
                                String whisperconutcomments = Object.getString("whisperconutcomments");
                                Drawable drawable = null;
                                if (!whisperimage.isEmpty()) {
                                    this.whisperimage = imgpath + whisperimage;
                                    drawable = loadImageFromNetwork(imgpath + whisperimage);
                                }
                                map.put("whisperId", whisperId);
                                map.put("whisperusericon", whisperusericon);
                                map.put("whispermailaddress", whispermailaddress);
                                map.put("whispername", whispername);
                                map.put("whispertime", whispertime);
                                map.put("whisper", whisper);
                                map.put("whisperimage", drawable);
                                map.put("whisperconutlikes", whisperconutlikes);
                                map.put("whisperconutcomments", whisperconutcomments);
                                map.put("imagepath",imgpath + whisperimage);
                                //保存到ArrayList集合中
                                list.add(map);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    handler.sendEmptyMessageDelayed(1, 30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg2) {

            switch (msg2.what) {
                case 1:
                    if(flag==1){
                        searchAdapter.notifyDataSetChanged();
                    }else{
                       myTimelineadapter.notifyDataSetChanged();
                    }
                    //list_item.notifyDataSetChanged();

                    break;
                case 2:
                    Toast.makeText(Search.this, "error",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    //inner click
    private void setListern(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "点击了第" + position + "条数据",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void click_icon(View view) {
        int values= (int) view.getTag();
        Map<String, Object> object= list.get(values);
        Toast.makeText(getApplicationContext(),"你点击的是"+values +"项"+
                object.get("whisperimage"),Toast.LENGTH_SHORT).show();

    }


    @Override
    public void click_like(View view) {
        int values= (int) view.getTag();
        Map<String, Object> object= list.get(values);
        Toast.makeText(getApplicationContext(),"你点击的是"+values +"项"+
                object.get("whisperconutlikes"),Toast.LENGTH_SHORT).show();
    }

}