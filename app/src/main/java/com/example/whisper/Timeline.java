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
            //模拟网络请求需要3000毫秒，请求完成，设置setRefreshing 为false，停止刷新
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
                //服务端访问地址
                Request request = new Request
                        .Builder()
                        .url(timeline).build();
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
                    Log.d("Timeline", "data+++" + resultJsonArray);
                    for (int i = 0; i < resultJsonArray.length(); i++) {
                        //循环遍历，获取json数据中data数组里的内容
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
                            //保存到ArrayList集合中
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
        Toast.makeText(getApplicationContext(),"你点击的是"+values +"项"+
                count+"whisperId：" + id
                + "whisperusermailaddress" + usermailaddress,
                Toast.LENGTH_SHORT).show();

    }



    private void like(String urlStr) {
        //要传递的数据
        //urlStr = "https://click.ecc.ac.jp/ecc/whisper_d/api/likeadd.php?usermailaddress=guojian101@gmail.com&whisperId=79;
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

                Log.d("like", "data+++" +msg);


                Intent intent = new Intent();
            }
            //关闭数据流
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
        //子线程请求网络,Android4.0以后访问网络不能放在主线程中
        new Thread() {
            private HttpURLConnection conn;

            public void run() {
                // 连接服务器 get 请求 获取图片
                try {
                    //String imgpath ="http://click.ecc.ac.jp/ecc/whisper_d/image/guojian101@gmail.com.jpg";
                    //创建URL对象
                    URL url = new URL(usericon);
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


//    private void apilike(String urlStr) {
//        //要传递的数据
//        //urlStr = "http://click.ecc.ac.jp/ecc/whisper_d/api/login.php?usermailaddress="+email+"&"+"password="+pass;
//        try {
//            URL url = new URL(urlStr);
//            //获得连接
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(3000);
//            conn.setReadTimeout(3000);
//
//            //获得输入流
//            InputStream in = conn.getInputStream();
//
//            //创建一个2048字节数的缓冲区
//            byte[] buffer = new byte[2048];
//
//            //在输入流中读取数据并存放到缓冲字节数组中
//            in.read(buffer);
//            //将字节转换成字符串
//
//            String msg = new String(buffer);
//
//            JSONObject jo = new JSONObject(msg);
//
//            //关闭数据流
//            in.close();
//        } catch (Exception e) {
//            showDialog(Integer.parseInt(e.getMessage()));
//        }
//    }




//    //listview适配器
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
//            //viewHolder.whispericon.setText("icon："+list.get(position).get("whispericon").toString());
//            //viewHolder.whispermailaddress.setText("mailaddress："+list.get(position).get("whispermailaddress").toString());
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