package com.example.whisper.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.whisper.R;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowListAdapter extends BaseAdapter {
    List<Map<String,Object>> list;
    //添加反射器
    LayoutInflater inflater;

    public FollowListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    //传入数据集合
    public void setList(List<Map<String,Object>> list){
        this.list = list;
    }

    @Override
    public int getCount() {
        //show nothing if null
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //反射listview布局，后面null先默认如此
        View view = inflater.inflate(R.layout.follow_item,null);

        //获取空间位置
        CircleImageView logo = (CircleImageView)view.findViewById(R.id.imgIconFollow);
        TextView username = (TextView)view.findViewById(R.id.tv_follow_name);
        TextView following = (TextView)view.findViewById(R.id.tv__number_following);
        TextView followers = (TextView)view.findViewById(R.id.tv_number_followers);


        //logo.setImageResource((Integer)map.get("image"));
        username.setText((String)list.get(position).get("username"));
        //viewHolder.whisperimage.setImageDrawable((Drawable) list.get(position).get("whisperimage"));
//        logo.setImageDrawable((Drawable) list.get(position).get("usericon"));
        following.setText("フォロー中数" + list.get(position).get("userfollows").toString());
        followers.setText("フォロワー数" + list.get(position).get("userfollowers").toString());
        return view;
    }
}
