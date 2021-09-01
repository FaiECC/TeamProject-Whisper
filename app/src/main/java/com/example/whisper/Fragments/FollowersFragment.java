package com.example.whisper.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whisper.ChoosedUser;
import com.example.whisper.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FollowersFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final ArrayList<Map<String, Object>> list = new ArrayList<>();
    private final String imgpath = "http://click.ecc.ac.jp/ecc/whisper_d/image/";

    private ListView listView;
    SimpleAdapter simpleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_followers,container,false);
        listView = view.findViewById(R.id.followerList);

        simpleAdapter = new SimpleAdapter(getActivity(),getData(), R.layout.follow_item,
                new String[]{"usericon","username","userfollows","userfollowers"},
                new int[]{R.id.imgIconFollow, R.id.tv_follow_name,
                        R.id.tv__number_following, R.id.tv_number_followers});
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(this);
        api();
        return view;
    }


    private void api() {
        list.clear();

        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                //服务端访问地址
                Request request = new Request
                        .Builder()
                        .url("http://click.ecc.ac.jp/ecc/whisper_d/api/showfollower.php?usermailaddress=guojian101@gmail.com").build();
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
                    Log.d("following", "data+++" + resultJsonArray);
                    for (int i = 0; i < resultJsonArray.length(); i++) {
                        //循环遍历，获取json数据中data数组里的内容
                        JSONObject Object = resultJsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        try {
                            String followid = Object.getString("followid");
                            String usericon = Object.getString("usericon");
                            String usermailaddress = Object.getString("usermailaddress");
                            String userwhispers = Object.getString("userwhispers");
                            String username = Object.getString("username");
                            String userprofile = Object.getString("userprofile");
                            String userfollows = Object.getString("userfollows");
                            String userfollowers = Object.getString("userfollowers");
                            String relationship = Object.getString("relationship");
                            Drawable drawable = null;
                            if (!usericon.isEmpty()) {
                                drawable = loadImageFromNetwork(imgpath + usericon);
                            }
                            map.put("followid", followid);
                            map.put("usericon", drawable);
                            map.put("usermailaddress", usermailaddress);
                            map.put("userwhispers", userwhispers);
                            map.put("username", username);
                            map.put("userprofile",userprofile);
                            map.put("userfollows", userfollows);
                            map.put("userfollowers", userfollowers);
                            map.put("relationship", relationship);
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



    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    //list_item.notifyDataSetChanged();
                    simpleAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(getActivity(), "error",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private List<Map<String,Object>> getData(){
//        String [] usernames = {"aaa","bbb","ccc","ddd","eee"};
//        int[] followers = {1,2,3,4,5};
//        int[] following = {5,4,3,2,1};

        //       int images = R.drawable.ic_android_black;
        List<Map<String,Object>> list = new ArrayList<>();
//        for (int i = 0; i <usernames.length;i++){
//            Map map = new HashMap();
//            map.put("image",images);
//            map.put("username",usernames[i]);
//            map.put("following",following[i]);
//            map.put("followers",followers[i]);
        //list.add(map);
        list=this.list;

        return list;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        int values= (int) view.getTag();
//        Map<String, Object> object= list.get(values);


//        Toast.makeText(getActivity(),"choose" + listView.getAdapter().getItem(position),Toast.LENGTH_SHORT).show();
        String text = listView.getAdapter().getItem(position).toString();
        Log.i("position" + position,"username"+text);
        Log.i("userfollows", list.get(position).get("userfollows").toString());

//        String t = (String)object.get("whispername");
//        Log.i("name",t);


        Intent intent = new Intent();
        intent.setClass(getActivity(), ChoosedUser.class);
        intent.putExtra("usericon", list.get(position).get("usericon").toString());
        intent.putExtra("username",list.get(position).get("username").toString());
        intent.putExtra("userfollows",list.get(position).get("userfollows").toString());
        intent.putExtra("userfollowers",list.get(position).get("userfollowers").toString());
        intent.putExtra("userprofile",list.get(position).get("userprofile").toString());
//        intent.putExtra("userwhispers",list.get(position).get("userwhispers").toString());
//        intent.putExtra("whisperconutlikes",list.get(position).get("whisperconutlikes").toString());
        startActivity(intent);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}