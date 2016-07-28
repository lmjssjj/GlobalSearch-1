package com.bbk.open.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbk.open.Utils.PhotoHandle;
import com.bbk.open.globlesearch.R;
import com.bbk.open.model.FileInfo;

import java.util.List;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Administrator on 2016/7/20.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<FileInfo> list;


    public MyAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<FileInfo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Bitmap bitmap;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_view, null);
            holder.tvName = (TextView) view.findViewById(R.id.name);
            holder.ivIcon = (AvatarImageView) view.findViewById(R.id.imageview);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (TextUtils.isEmpty(list.get(i).getName())) {
            holder.tvName.setText(list.get(i).getSearchInfo());
        } else {
            holder.tvName.setText(list.get(i).getName());
        }
        switch (list.get(i).getType()) {
            case FileInfo.TYPE_APP:
                bitmap = PhotoHandle.byteToBitmap(list.get(i).getNoSearchInfo());
                holder.ivIcon.setImageBitmap(bitmap);
                break;
            case FileInfo.TYPE_IMAGE:
                if (list.get(i).getNoSearchInfo().equals("")) {
                    holder.ivIcon.setImageResource(R.drawable.image);
                } else {
                    bitmap = BitmapFactory.decodeFile(list.get(i).getNoSearchInfo());
                    holder.ivIcon.setImageBitmap(bitmap);
                }
                break;
            case FileInfo.TYPE_VIDEO:
                if (list.get(i).getNoSearchInfo().equals("")) {
                    holder.ivIcon.setImageResource(R.drawable.video);
                } else {
                    bitmap = BitmapFactory.decodeFile(list.get(i).getNoSearchInfo());
                    holder.ivIcon.setImageBitmap(bitmap);
                }
                break;
            case FileInfo.TYPE_AUDIO:
                holder.ivIcon.setImageResource(R.drawable.audio);
                break;
            case FileInfo.TYPE_PDF:
                holder.ivIcon.setImageResource(R.drawable.pdf);
                break;
            case FileInfo.TYPE_WORD:
                holder.ivIcon.setImageResource(R.drawable.doc);
                break;
            case FileInfo.TYPE_XLS:
                holder.ivIcon.setImageResource(R.drawable.xls);
                break;
            case FileInfo.TYPE_TXT:
                holder.ivIcon.setImageResource(R.drawable.txt);
                break;
            case FileInfo.TYPE_CONTACT:
                int index = list.get(i).getName().indexOf("|");
                holder.tvName.setText(list.get(i).getName().substring(0, index));
                holder.ivIcon.setTextAndColor(list.get(i).getName().substring(0, 1), R.color.btn_bg_pressed_color);
                break;
            case FileInfo.TYPE_SMS:
                holder.ivIcon.setImageResource(R.drawable.sms);
                break;
            default:
                holder.ivIcon.setImageResource(R.drawable.default_icon);
        }
        return view;
    }

    class ViewHolder {
        TextView tvName;
        AvatarImageView ivIcon;
    }


}

