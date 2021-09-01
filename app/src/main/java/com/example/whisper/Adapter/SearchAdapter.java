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

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends BaseAdapter {

    //        clickinview clickinview;
    ArrayList<Map<String, Object>> list = new ArrayList<>();
    Context mContext;
    clickinview clickinview;
    int layout;

    public SearchAdapter(ArrayList<Map<String, Object>> list, Context mContext, clickinview clickinview, int layout) {
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


        SearchAdapter.ViewHolder viewHolder = new SearchAdapter.ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, null);
            viewHolder.name = convertView.findViewById(R.id.tv_username);
            viewHolder.profile = convertView.findViewById(R.id.tv_profile);
            viewHolder.whisper = convertView.findViewById(R.id.tv_whispers);
            viewHolder.icon = convertView.findViewById(R.id.imgIcon);
            viewHolder.count = convertView.findViewById(R.id.conutfollow);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (SearchAdapter.ViewHolder) convertView.getTag();
        }


        viewHolder.name.setText(Objects.requireNonNull(list.get(position).get("name")).toString());
        viewHolder.profile.setText(Objects.requireNonNull(list.get(position).get("profile")).toString());
        viewHolder.whisper.setText(Objects.requireNonNull(list.get(position).get("whispers")).toString());
        viewHolder.icon.setImageDrawable((Drawable) list.get(position).get("icon"));
        viewHolder.count.setText(Objects.requireNonNull(list.get(position).get("userfollows")).toString());

        //inner click
        viewHolder.icon.setTag(position);

        viewHolder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickinview.click_icon(v);
            }
        });

        viewHolder.count.setTag(position);
        viewHolder.count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickinview.click_like(v);
            }
        });

        return convertView;
    }


    final static class ViewHolder {
        TextView name;
        TextView profile;
        TextView whisper;
        CircleImageView icon;
        TextView count;
    }

}