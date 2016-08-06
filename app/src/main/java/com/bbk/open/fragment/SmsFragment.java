package com.bbk.open.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbk.open.Utils.FilesDao;
import com.bbk.open.Utils.OpenUtils;
import com.bbk.open.Utils.SearchUtils;
import com.bbk.open.adapter.MyAdapter;
import com.bbk.open.globlesearch.R;
import com.bbk.open.model.FileInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmsFragment extends Fragment implements View.OnTouchListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView listView;
    private MyAdapter adapter;
    private List<FileInfo> list = new ArrayList<>();
    private List<FileInfo> result = new ArrayList<>();
    private Context context;
    private TextView tv_tip;


    public SmsFragment() {
        // Required empty public constructor
    }

    public static SmsFragment getInstance() {
        SmsFragment fragment = new SmsFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = SearchUtils.SearchSms(context);
                FilesDao dao = new FilesDao(context);
                for (FileInfo info : list) {
                    dao.add(info);
                }
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment, container, false);
        listView = (ListView) view.findViewById(R.id.lv_file);
        tv_tip = (TextView)view.findViewById(R.id.tv_tip);
        adapter = new MyAdapter(context);
        adapter.setData(result);
        listView.setAdapter(adapter);
        listView.setOnTouchListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        return view;
    }

    public void updateContent(String content) {
        if (TextUtils.isEmpty(content)) {
            result.clear();
        } else {
            FilesDao dao = new FilesDao(context);
            try {
                result = dao.qurry(content, FileInfo.TYPE_SMS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (result.isEmpty()&&!TextUtils.isEmpty(content)){
            tv_tip.setVisibility(View.VISIBLE);
        }else {
            tv_tip.setVisibility(View.GONE);
        }
        adapter.setData(result);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getActivity().getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FilesDao dao = new FilesDao(context);
        FileInfo info = result.get(position);
        info.addCount();
        dao.update(info);
        Intent intent = OpenUtils.openFile(info.getType(), info.getPath(), context);
        context.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(R.array.thread_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileInfo fileInfo = result.get(position);
                if (which == 0) {
                    fileInfo.setTag(FileInfo.TAG_HIDE);
                    Toast.makeText(context, "该短信已被隐藏，可在隐藏空间中恢复", Toast.LENGTH_LONG).show();
                } else {
                    fileInfo.setTag(FileInfo.TAG_DELETE);
                    Toast.makeText(context, "该短信已被删除", Toast.LENGTH_LONG).show();
                }
                result.remove(position);
                FilesDao dao = new FilesDao(context);
                dao.update(fileInfo);
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
        return true;
    }


}
