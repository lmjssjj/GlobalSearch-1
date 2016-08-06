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
            case FileInfo.TYPE_APP+100:
                bitmap = PhotoHandle.byteToBitmap(list.get(i).getNoSearchInfo());
                holder.ivIcon.setImageBitmap(bitmap);
                break;
            case FileInfo.TYPE_IMAGE:
            case FileInfo.TYPE_IMAGE+100:
                if (list.get(i).getNoSearchInfo().equals("")) {
                    holder.ivIcon.setImageResource(R.drawable.image);
                } else {
                    bitmap = BitmapFactory.decodeFile(list.get(i).getNoSearchInfo());
                    holder.ivIcon.setImageBitmap(bitmap);
                }
                break;
            case FileInfo.TYPE_VIDEO:
            case FileInfo.TYPE_VIDEO+100:
                if (list.get(i).getNoSearchInfo().equals("")) {
                    holder.ivIcon.setImageResource(R.drawable.video);
                } else {
                    bitmap = BitmapFactory.decodeFile(list.get(i).getNoSearchInfo());
                    holder.ivIcon.setImageBitmap(bitmap);
                }
                break;
            case FileInfo.TYPE_AUDIO:
            case FileInfo.TYPE_AUDIO+100:
                holder.ivIcon.setImageResource(R.drawable.audio);
                break;
            case FileInfo.TYPE_PDF:
            case FileInfo.TYPE_PDF+100:
                holder.ivIcon.setImageResource(R.drawable.pdf);
                break;
            case FileInfo.TYPE_WORD:
            case FileInfo.TYPE_WORD+100:
                holder.ivIcon.setImageResource(R.drawable.doc);
                break;
            case FileInfo.TYPE_XLS:
            case FileInfo.TYPE_XLS+100:
                holder.ivIcon.setImageResource(R.drawable.xls);
                break;
            case FileInfo.TYPE_TXT:
            case FileInfo.TYPE_TXT+100:
                holder.ivIcon.setImageResource(R.drawable.txt);
                break;
            case FileInfo.TYPE_CONTACT:
            case FileInfo.TYPE_CONTACT+100:
                int index = list.get(i).getName().indexOf("|");
                holder.tvName.setText(list.get(i).getName().substring(0, index));
                holder.ivIcon.setTextAndColor(list.get(i).getName().substring(0, 1), R.color.btn_bg_pressed_color);
                break;
            case FileInfo.TYPE_SMS:
            case FileInfo.TYPE_SMS+100:
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

