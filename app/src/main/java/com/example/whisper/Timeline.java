package com.example.whisper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.whisper.Adapter.MyTimelineadapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Timeline extends AppCompatActivity implements MyTimelineadapter.clickinview  {
    private static final ArrayList<Map<String, Object>> list = new ArrayList<>();
    private CircleImageView icon;
    private String whisper;
    private String whispertime;

    private SwipeRefreshLayout refreshLayout;
    private String usermailaddress;
    private String timeline;
    private String usericon;
    private final String imgpath = "http://click.ecc.ac.jp/ecc/whisper_d/image/";
    private String whisperimage;
    protected MyTimelineadapter myTimelineadapter = null;
    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite_timeline);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(() -> {
            //????????????????????????3000??????????????????????????????setRefreshing ???false???????????????
            new Handler().postDelayed(() -> {
                api();
                refreshLayout.setRefreshing(false);
            }, 200);
        });
        icon = findViewById(R.id.profile_image);
        Intent i = getIntent();
        this.timeline = i.getStringExtra("timelinepass");
        this.usermailaddress = i.getStringExtra("whisperusermailaddress");
        String username = i.getStringExtra("username");
        this.usericon = (this.imgpath + i.getStringExtra("usericon"));
        TextView tv = findViewById(R.id.username);
        tv.setText(username);
        api();
        //new Thread(() -> icon.setImageDrawable(loadImageFromNetwork(usericon))).start();
        showicon();
    }

    private Drawable loadImageFromNetwork(String imageUrl) {
        Drawable drawable = null;
        if(imageUrl.length()>43){
            try {
                // ??????????????????????????????????????????????????????????????????
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

    //private Mybaseadapter list_item;

    private void api() {
        list.clear();
        listView = findViewById(R.id.listview);
        myTimelineadapter = new MyTimelineadapter(list,getApplicationContext(),
                Timeline.this,R.layout.timeline_item);
        listView.setAdapter(myTimelineadapter);
        setListern();
        //list_item = new Mybaseadapter();
        //v.setAdapter(list_item);

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                //?????????????????????
                Request request = new Request
                        .Builder()
                        .url(timeline).build();
                Response response = client.newCall(request).execute();
                //??????????????????????????????????????????parseJSONWithJSONObject????????????
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

                //??????????????????code???????????????0?????????
                String resultCode = jsonObject.getString("message");
                if (resultCode.equals("?????????????????????????????????????????????")) {
                    //?????????json???????????????data??????
                    JSONArray resultJsonArray = jsonObject.getJSONArray("data");
                    Log.d("Timeline", "data+++" + resultJsonArray);
                    for (int i = 0; i < resultJsonArray.length(); i++) {
                        //?????????????????????json?????????data??????????????????
                        JSONObject Object = resultJsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
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
                            //?????????ArrayList?????????
                            list.add(map);
                        } catch (Exception e) {
                            e.printStackTrace();
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
                    //list_item.notifyDataSetChanged();
                    myTimelineadapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(Timeline.this, "error",
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
                Toast.makeText(getApplicationContext(), "????????????" + position + "?????????",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void click_icon(View view) {
        int values= (int) view.getTag();
        Map<String, Object> object= list.get(values);
        Toast.makeText(getApplicationContext(),"???????????????"+values +"???"+
                object.get("whisper"),Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setClass(Timeline.this, Timeline_sub.class);
        intent.putExtra("whisper", (String) object.get("whisper"));
        intent.putExtra("whispertime", (String) object.get("whispertime"));
        intent.putExtra("imagepath", (String) object.get("imagepath"));
        intent.putExtra("whispername",(String)object.get("whispername"));
        intent.putExtra("whisperconutlikes",(String)object.get("whisperconutlikes"));
        startActivity(intent);



    }


    @Override
    public void click_like(View view) {
        int values = (int) view.getTag();
        Map<String, Object> object= list.get(values);
        String id,count;
        id =  object.get("whisperId").toString();
        count = object.get("whisperconutlikes").toString();
        String urlStr = "http://click.ecc.ac.jp/ecc/whisper_d/api/likeadd.php?usermailaddress="+usermailaddress+"&whisperId="+id;
        new Thread(() -> like(urlStr)).start();
        Toast.makeText(getApplicationContext(),"???????????????"+values +"???"+
                count+"whisperId???" + id
                + "whisperusermailaddress" + usermailaddress,
                Toast.LENGTH_SHORT).show();

    }



    private void like(String urlStr) {
        //??????????????????
        //urlStr = "https://click.ecc.ac.jp/ecc/whisper_d/api/likeadd.php?usermailaddress=guojian101@gmail.com&whisperId=79;
        try {
            URL url = new URL(urlStr);
            //????????????
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            //???????????????
            InputStream in = conn.getInputStream();

            //????????????2048?????????????????????
            byte[] buffer = new byte[2048];

            //????????????????????????????????????????????????????????????
            in.read(buffer);
            //???????????????????????????

            String msg = new String(buffer);

            JSONObject jo = new JSONObject(msg);
            if (jo.getString("result").equals("true")) {

                Log.d("like", "data+++" +msg);


                Intent intent = new Intent();
            }
            //???????????????
            in.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent();
                intent.setClass(Timeline.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_Search:
                Intent intent2 = new Intent();
                intent2.putExtra("usermailaddress",usermailaddress);
                intent2.setClass(Timeline.this, Search.class);
                startActivity(intent2);
                return true;
            case R.id.menu_whisper:
                Intent intent3 = new Intent();
                intent3.putExtra("usermailaddress", this.usermailaddress);
                intent3.setClass(Timeline.this, WhisperUpload.class);
                startActivity(intent3);
                //finish();
                return true;
            case R.id.menu_follow:
                Intent intent4 = new Intent();
                intent4.setClass(Timeline.this, Follow.class);
                startActivity(intent4);
                //finish();
                return true;
            case R.id.menu_profileEdit:
                Intent intent5 = new Intent();
                intent5.putExtra("usericon",usericon);
                intent5.putExtra("usermailaddress",usermailaddress);
                intent5.setClass(Timeline.this, ProfileEdit.class);
                startActivity(intent5);
                // finish();
                return true;
        }
        return false;
    }

        @SuppressLint("HandlerLeak")
    public final Handler handler_icon = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    icon.setImageBitmap(bitmap);
                    break;
                case 2:
                    Toast.makeText(Timeline.this, "image_error", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void showicon() {
        //?????????????????????,Android4.0??????????????????????????????????????????
        new Thread() {
            private HttpURLConnection conn;

            public void run() {
                // ??????????????? get ?????? ????????????
                try {
                    //String imgpath ="http://click.ecc.ac.jp/ecc/whisper_d/image/guojian101@gmail.com.jpg";
                    //??????URL??????
                    URL url = new URL(usericon);
                    // ??????url ?????? http?????????
                    conn = (HttpURLConnection) url.openConnection();
                    // ?????????????????????
                    conn.setRequestMethod("GET");
                    //??????????????????
                    conn.setConnectTimeout(5000);
                    // ?????????????????????????????????
                    int code = conn.getResponseCode();
                    //?????????????????????????????????200
                    if (code == 200) {
                        //???????????????
                        InputStream is = conn.getInputStream();
                        //???????????????Bitmap??????
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        //?????????????????????????????????????????????
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = bitmap;
                        handler_icon.sendMessage(msg);
                    } else {
                        //??????????????????200 ?????????????????????
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
                //????????????
                conn.disconnect();
            }
        }.start();

    }


//    private void apilike(String urlStr) {
//        //??????????????????
//        //urlStr = "http://click.ecc.ac.jp/ecc/whisper_d/api/login.php?usermailaddress="+email+"&"+"password="+pass;
//        try {
//            URL url = new URL(urlStr);
//            //????????????
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(3000);
//            conn.setReadTimeout(3000);
//
//            //???????????????
//            InputStream in = conn.getInputStream();
//
//            //????????????2048?????????????????????
//            byte[] buffer = new byte[2048];
//
//            //????????????????????????????????????????????????????????????
//            in.read(buffer);
//            //???????????????????????????
//
//            String msg = new String(buffer);
//
//            JSONObject jo = new JSONObject(msg);
//
//            //???????????????
//            in.close();
//        } catch (Exception e) {
//            showDialog(Integer.parseInt(e.getMessage()));
//        }
//    }




//    //listview?????????
//    public class Mybaseadapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return list.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @SuppressLint("InflateParams")
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ViewHolder viewHolder = new ViewHolder();
//
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(R.layout.timeline_item, null);
//                //viewHolder.whispericon = (TextView) convertView.findViewById(R.id.whispericon);
//                //viewHolder.whispermailaddress = (TextView) convertView.findViewById(R.id.whispermailaddress);
//                viewHolder.whispername = convertView.findViewById(R.id.tv_name);
//                viewHolder.whispertime = convertView.findViewById(R.id.tv_time);
//                viewHolder.whisper = convertView.findViewById(R.id.tv_content);
//                viewHolder.whisperimage = convertView.findViewById(R.id.imgIcon);
//                viewHolder.whispername = convertView.findViewById(R.id.tv_name);
//                viewHolder.whispercountlikes = convertView.findViewById(R.id.conutlikes);
//                //viewHolder.whisperconutcomments ;
//                convertView.setTag(viewHolder);
//
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//
//            //viewHolder.whispericon.setText("icon???"+list.get(position).get("whispericon").toString());
//            //viewHolder.whispermailaddress.setText("mailaddress???"+list.get(position).get("whispermailaddress").toString());
//            viewHolder.whispername.setText(Objects.requireNonNull(list.get(position).get("whispername")).toString());
//            viewHolder.whispertime.setText(Objects.requireNonNull(list.get(position).get("whispertime")).toString());
//            viewHolder.whisper.setText(Objects.requireNonNull(list.get(position).get("whisper")).toString());
//            viewHolder.whisperimage.setImageDrawable((Drawable) list.get(position).get("whisperimage"));
//            viewHolder.whispercountlikes.setText(Objects.requireNonNull(list.get(position).get("whisperconutlikes")).toString());
//            return convertView;
//        }
//    }
//
//    public final static class ViewHolder {
////        TextView whisperId;
////        TextView whispericon;
////        TextView whispermailaddress;
//        TextView whispername;
//        TextView whispertime;
//        TextView whisper;
//        CircleImageView whisperimage;
//        TextView whispercountlikes;
////        TextView whisperconutcomments;
//    }
}