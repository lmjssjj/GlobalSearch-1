package com.bbk.open.globlesearch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;


import android.widget.AdapterView;
import android.widget.EditText;



import com.bbk.open.Utils.FilesDao;
import com.bbk.open.Utils.OpenUtils;
import com.bbk.open.Utils.SearchUtils;
import com.bbk.open.adapter.MyAdapter;
import com.bbk.open.model.FileInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;


public class MainActivity extends Activity {

    private List<FileInfo> list = new ArrayList<>();
    private List<FileInfo> result = new ArrayList<>();
    private EditText etSearchKey;
    StickyListHeadersListView listView;
    private Context context;
    private MyAdapter adapter;
    private ProgressDialog dialog;

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    dialog.dismiss();
                    break;
                case 2:
                    adapter.setData(result);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<FileInfo> appInfoList = SearchUtils.SearchApps(context);
                List<FileInfo> contactInfoList = SearchUtils.SearchContacts(context);
                List<FileInfo> smsInfoList = SearchUtils.SearchSms(context);
                List<FileInfo> videoInfoList = SearchUtils.SearchVedio(context);
                List<FileInfo> audioInfoList = SearchUtils.SearchAudio(context);
                List<FileInfo> imageInfoList = SearchUtils.SearchImages(context);
                List<FileInfo> pircemealInfoList = SearchUtils.SearchPiecemealInfo(context);
                list.addAll(appInfoList);
                list.addAll(contactInfoList);
                list.addAll(smsInfoList);
                list.addAll(audioInfoList);
                list.addAll(imageInfoList);
                list.addAll(videoInfoList);
                list.addAll(pircemealInfoList);
                FilesDao dao = new FilesDao(context);
                try {
                    dao.delete();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                for (FileInfo info : list) {
                    dao.add(info);
                }
                Message msg = Message.obtain();
                msg.what=1;
                myHandler.sendMessage(msg);
            }
        }).start();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("qq", list.get(i).getType() + list.get(i).getPath());
                Intent intent = OpenUtils.openFile(list.get(i).getType(), list.get(i).getPath(), MainActivity.this);
                startActivity(intent);
            }
        });



    }



    private void init() {
        listView = (StickyListHeadersListView) findViewById(R.id.listview);
        etSearchKey = (EditText) findViewById(R.id.search_key);
        context = MainActivity.this;
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
        adapter = new MyAdapter(context);
        adapter.setData(result);
        listView.setAdapter(adapter);
        etSearchKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(etSearchKey.getText())) {
                    result.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FilesDao dao = new FilesDao(context);
                            try {
                                result = dao.qurry(etSearchKey.getText().toString());
                                Message msg = Message.obtain();
                                msg.what = 2;
                                myHandler.sendMessage(msg);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });


    }





}
