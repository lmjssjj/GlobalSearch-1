package com.bbk.open.globlesearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bbk.open.Utils.OpenUtils;
import com.bbk.open.Utils.SearchUtils;
import com.bbk.open.model.FileInfo;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private List<FileInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        List<FileInfo> appInfoList = SearchUtils.SearchApps(this);
        List<FileInfo> contactInfoList = SearchUtils.SearchContacts(this);
        List<FileInfo> smsInfoList = SearchUtils.SearchSms(this);
        List<FileInfo> videoInfoList = SearchUtils.SearchVedio(this);
        List<FileInfo> audioInfoList = SearchUtils.SearchAudio(this);
        List<FileInfo> imageInfoList = SearchUtils.SearchImages(this);
        List<FileInfo> pircemealInfoList = SearchUtils.SearchPiecemealInfo(this);
        list.addAll(appInfoList);
        list.addAll(contactInfoList);
        list.addAll(smsInfoList);
        list.addAll(videoInfoList);
        list.addAll(audioInfoList);
        list.addAll(imageInfoList);
        list.addAll(pircemealInfoList);
        ListView listView = (ListView) findViewById(R.id.listview);
        MyAdapter adapter =new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = OpenUtils.openFile(list.get(i).getType(),list.get(i).getPath(),MainActivity.this);
                startActivity(intent);
            }
        });


    }

    class MyAdapter extends BaseAdapter {
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
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout,null);
                viewHolder.textView = (TextView)view.findViewById(R.id.textView);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textView.setText(list.get(i).getName());
            return view;
        }
    }

    class ViewHolder {
        TextView textView;
    }

//    class MyAdapter extends BaseAdapter implements StickyListHeadersAdapter{
//
//        @Override
//        public View getHeaderView(int position, View convertView, ViewGroup parent) {
//            return null;
//        }
//
//        @Override
//        public long getHeaderId(int position) {
//            return 0;
//        }
//
//        @Override
//        public int getCount() {
//            return 0;
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            return null;
//        }
//    }


}
