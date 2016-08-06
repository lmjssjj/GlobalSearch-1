package com.bbk.open.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbk.open.globlesearch.R;
import com.bbk.open.eventbus.MessageEvent;
import com.bbk.open.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * 日期：7.28
 * 功能：AutoCompleteTextView适配器
 * 实现：杨尚臻
 */
public class AutoAdapter extends BaseAdapter implements Filterable {

    private List<String> mLists;
    private Context mContext;
    private boolean refreshFlag = false;

    public AutoAdapter(MainActivity mContext, List<String> mLists) {
        this.mLists = mLists;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int position) {
        return mLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_auto, parent, false);
        TextView tvContent = (TextView) view.findViewById(R.id.auto_tv_adapter);
        ImageView ivCancle = (ImageView) view.findViewById(R.id.cancel_iv_adapter);
        tvContent.setText(mLists.get(position));
        ivCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("getView", mLists.get(position));
                EventBus.getDefault().post(new MessageEvent(mLists.get(position)));
                mLists.remove(mLists.get(position));
                notifyDataSetChanged();
            }
        });
        return view;
    }

    /**
     * 刷新数据
     * @param reLists
     */
    public void refreshAdd(List<String> reLists) {
        if (mLists != null) {
            mLists.clear();
        }
        if (mUnfilteredData != null) {
            mUnfilteredData.clear();
        }
        mLists.addAll(reLists);
        mUnfilteredData.addAll(reLists);
    }

    public void refreshCancle(List<String> reLists) {
        if (mUnfilteredData != null) {
            mUnfilteredData.clear();
        }
        mUnfilteredData.addAll(reLists);
    }

    private ArrayFilter mFilter;
    private ArrayList<String> mUnfilteredData = new ArrayList<>();

    @Override
    public Filter getFilter() {
        mFilter = new ArrayFilter();
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
//
//            if (mLists != null) {
//                mUnfilteredData.clear();
//                mUnfilteredData.addAll(mLists);
//            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<String> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<String> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<String> newValues = new ArrayList<String>(count);

                for (int i = 0; i < count; i++) {
                    String pc = unfilteredValues.get(i);
                    if (pc != null) {

                        if(pc!=null && pc.startsWith(prefixString)){

                            newValues.add(pc);
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //noinspection unchecked
            mLists = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
