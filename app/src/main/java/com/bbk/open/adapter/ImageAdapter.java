package com.bbk.open.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbk.open.globlesearch.R;
import com.bbk.open.model.FileInfo;
import com.bumptech.glide.Glide;

import java.util.List;


/**
 * Created by Administrator on 2016/7/20.
 */
public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<FileInfo> mList;

    public ImageAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<FileInfo> list) {
        this.mList = list;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.image_item_view, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.image_item_name);
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.image_item_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(mList.get(position).getName());
        Glide.with(mContext)
                .load(mList.get(position).getPath())
                .centerCrop()
                .placeholder(R.drawable.default_icon)
                .crossFade()
                .into(holder.ivIcon);
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        ImageView ivIcon;
    }
}


