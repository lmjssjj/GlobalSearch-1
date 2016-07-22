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

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Administrator on 2016/7/20.
 */
public class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private Context context;
    private List<FileInfo> list;
    private static final String[] headersTag = {"", "应用", "联系人", "短信", "音乐", "图片", "视频", "安装包", "压缩包", "TXT", "EXCEL", "HTML", "PDF", "WORD", "PPT"};

    public MyAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<FileInfo> list) {
        this.list = list;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.header_view, null);
            holder.tvHeader = (TextView) convertView.findViewById(R.id.tv_type);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        holder.tvHeader.setText(headersTag[list.get(position).getType()]);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return list.get(position).getType();
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
            holder.ivIcon = (ImageView) view.findViewById(R.id.imageview);
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
            case FileInfo.TYPE_ARCHIVE:
                holder.ivIcon.setImageResource(R.drawable.zip);
                break;
            case FileInfo.TYPE_WORD:
                holder.ivIcon.setImageResource(R.drawable.doc);
                break;
            case FileInfo.TYPE_HTML:
                holder.ivIcon.setImageResource(R.drawable.html);
                break;
            case FileInfo.TYPE_XLS:
                holder.ivIcon.setImageResource(R.drawable.xls);
                break;
            case FileInfo.TYPE_TXT:
                holder.ivIcon.setImageResource(R.drawable.txt);
                break;
            case FileInfo.TYPE_CONTACT:
                holder.ivIcon.setImageResource(R.drawable.contact);
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
        ImageView ivIcon;
    }

    class HeaderViewHolder {
        TextView tvHeader;
    }


}

