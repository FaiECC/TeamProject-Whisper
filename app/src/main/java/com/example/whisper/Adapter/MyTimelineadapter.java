package com.example.whisper.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.whisper.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyTimelineadapter extends BaseAdapter {

//        clickinview clickinview;
    ArrayList<Map<String, Object>> list = new ArrayList<>();
    Context mContext;
    clickinview clickinview;
    int layout;

    public MyTimelineadapter(ArrayList<Map<String, Object>> list, Context mContext, clickinview clickinview, int layout) {
        this.list = list;
        this.mContext = mContext;
        this.clickinview = clickinview;
        this.layout = layout;
    }

    public interface clickinview
    {
        public void click_icon(View view);
        public void click_like(View view);
    }

    @Override
    public int getCount() {
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        Timeline.ViewHolder viewHolder = new Timeline.ViewHolder();

        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.timeline_item, null);
            //viewHolder.whispericon = (TextView) convertView.findViewById(R.id.whispericon);
            //viewHolder.whispermailaddress = (TextView) convertView.findViewById(R.id.whispermailaddress);
            viewHolder.whispername = convertView.findViewById(R.id.tv_name);
            viewHolder.whispertime = convertView.findViewById(R.id.tv_time);
            viewHolder.whisper = convertView.findViewById(R.id.tv_content);
            viewHolder.whisperimage = convertView.findViewById(R.id.imgIcon);
            viewHolder.whispername = convertView.findViewById(R.id.tv_name);
            viewHolder.whispercountlikes = convertView.findViewById(R.id.conutlikes);
            viewHolder.like = convertView.findViewById(R.id.img_great);
            //viewHolder.whisperconutcomments ;
            convertView.setTag(viewHolder);

        } else {
//            viewHolder = (Timeline.ViewHolder) convertView.getTag();
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        //viewHolder.whispericon.setText("icon："+list.get(position).get("whispericon").toString());
//        //viewHolder.whispermailaddress.setText("mailaddress："+list.get(position).get("whispermailaddress").toString());
//        viewHolder.whispername.setText(Objects.requireNonNull(list.get(position).get("whispername")).toString());
//        viewHolder.whispertime.setText(Objects.requireNonNull(list.get(position).get("whispertime")).toString());
//        viewHolder.whisper.setText(Objects.requireNonNull(list.get(position).get("whisper")).toString());
//        viewHolder.whisperimage.setImageDrawable((Drawable) list.get(position).get("whisperimage"));
//        viewHolder.whispercountlikes.setText(Objects.requireNonNull(list.get(position).get("whisperconutlikes")).toString());
        //viewHolder.whispericon.setText("icon："+list.get(position).get("whispericon").toString());
            //viewHolder.whispermailaddress.setText("mailaddress："+list.get(position).get("whispermailaddress").toString());
            viewHolder.whispername.setText(Objects.requireNonNull(list.get(position).get("whispername")).toString());
            viewHolder.whispertime.setText(Objects.requireNonNull(list.get(position).get("whispertime")).toString());
            viewHolder.whisper.setText(Objects.requireNonNull(list.get(position).get("whisper")).toString());
            viewHolder.whisperimage.setImageDrawable((Drawable) list.get(position).get("whisperimage"));
            viewHolder.whispercountlikes.setText(Objects.requireNonNull(list.get(position).get("whisperconutlikes")).toString());

        //inner click
        viewHolder.whisperimage.setTag(position);

        viewHolder.whisperimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickinview.click_icon(v);
            }
        });

//        viewHolder.whispercountlikes.setTag(position);
//        viewHolder.whispercountlikes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                clickinview.click_like(v);
//            }
//        });
        viewHolder.like.setTag(position);
        viewHolder.like.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            clickinview.click_like(v);
        }

        });

        return convertView;
    }


    final static class ViewHolder {
//        TextView whisperId;
//        TextView whispericon;
//        TextView whispermailaddress;
        TextView whispername;
        TextView whispertime;
        TextView whisper;
        CircleImageView whisperimage;
        TextView whispercountlikes;
        ImageButton like;
//        TextView whisperconutcomments;
    }

}

