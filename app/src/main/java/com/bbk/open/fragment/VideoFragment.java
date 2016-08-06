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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbk.open.Utils.FilesDao;
import com.bbk.open.Utils.OpenUtils;
import com.bbk.open.Utils.SearchUtils;
import com.bbk.open.adapter.ImageAdapter;
import com.bbk.open.adapter.MyAdapter;
import com.bbk.open.globlesearch.R;
import com.bbk.open.model.FileInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends Fragment implements View.OnTouchListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private GridView gridView;
    private ImageAdapter adapter;
    private List<FileInfo> list = new ArrayList<>();
    private List<FileInfo> result = new ArrayList<>();
    private Context context;
    private TextView tv_tip;


    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment getInstance() {
        VideoFragment fragment = new VideoFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = SearchUtils.SearchVedio(context);
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
        View view = inflater.inflate(R.layout.fragment_gridview, container, false);
        gridView = (GridView) view.findViewById(R.id.gridView);
        tv_tip = (TextView)view.findViewById(R.id.tv_tip);
        adapter = new ImageAdapter(context);
        adapter.setData(result);
        gridView.setAdapter(adapter);
        gridView.setOnTouchListener(this);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        return view;
    }

    public void updateContent(String content) {
        if (TextUtils.isEmpty(content)) {
            result.clear();
        } else {
            FilesDao dao = new FilesDao(context);
            try {
                result = dao.qurry(content, FileInfo.TYPE_VIDEO);
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
        Intent intent = OpenUtils.openFile(info.getType(), info.getPath(), context);
        if (intent != null) {
            info.addCount();
            dao.update(info);
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "该视频已被删除", Toast.LENGTH_LONG).show();
            FileInfo fileInfo = result.get(position);
            fileInfo.setType(FileInfo.TAG_DELETE);
            result.remove(position);
            dao.update(fileInfo);
            adapter.notifyDataSetChanged();
        }
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
                    Toast.makeText(context, "该视频已被隐藏，可在隐藏空间中恢复", Toast.LENGTH_LONG).show();
                } else {
                    fileInfo.setTag(FileInfo.TAG_DELETE);
                    Toast.makeText(context, "该视频已被删除", Toast.LENGTH_LONG).show();
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
