package com.bbk.open.activity;


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.bbk.open.ContextHolder;
import com.bbk.open.Utils.FilesDao;
import com.bbk.open.Utils.SearchUtils;
import com.bbk.open.adapter.AutoAdapter;
import com.bbk.open.eventbus.MessageEvent;
import com.bbk.open.fragment.ApkFragment;
import com.bbk.open.fragment.AppFragment;
import com.bbk.open.fragment.AudioFragment;
import com.bbk.open.fragment.ContactFragment;
import com.bbk.open.fragment.DocFragment;
import com.bbk.open.fragment.ImageFragment;
import com.bbk.open.fragment.SmsFragment;
import com.bbk.open.fragment.VideoFragment;
import com.bbk.open.globlesearch.R;
import com.bbk.open.model.FileInfo;
import com.bbk.open.view.AdvancedAutoCompleteTextView;
import com.bbk.open.view.CircularAnimUtil;
import com.example.captain_miao.grantap.CheckPermission;
import com.example.captain_miao.grantap.listeners.PermissionListener;
import com.github.clans.fab.FloatingActionMenu;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.shamanland.fab.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;


public class MainActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener, AdvancedAutoCompleteTextView.startVoiceLister {

    private AppFragment appFragment;
    private ContactFragment contactFragment;
    private SmsFragment smsFragment;
    private VideoFragment videoFragment;
    private ImageFragment imageFragment;
    private AudioFragment audioFragment;
    private DocFragment docFragment;
    private ApkFragment apkFragment;
    private ViewPager viewPager;
    private FragmentAdapter adapter;
    private PagerSlidingTabStrip tabStrip;
    private AdvancedAutoCompleteTextView auto_searchkey;
    private Button bt_search_web;
    private FloatingActionButton fab_hide;

    private FloatingActionMenu menuYellow;
    private com.github.clans.fab.FloatingActionButton fabHide;
    private com.github.clans.fab.FloatingActionButton fabScan;
    private List<FloatingActionMenu> menus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        initView();
        init();
        sendNotification();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpage1r);
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab);
        auto_searchkey = (AdvancedAutoCompleteTextView) findViewById(R.id.tv_searchkey);
        bt_search_web = (Button) findViewById(R.id.bt_search_web);
       // fab_hide = (FloatingActionButton)findViewById(R.id.fab);
        mParentLayout = (LinearLayout) findViewById(R.id.layout_all);
        initAutoComplete("history", auto_searchkey);

        menuYellow = (FloatingActionMenu) findViewById(R.id.menu_yellow);
        fabHide = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab1);
        fabScan = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab_scan);
    }

    private void init() {
        viewPager.setOffscreenPageLimit(8);
        adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabStrip.setIndicatorColorResource(R.color.home_bar_text_push);
        tabStrip.setIndicatorHeight(8);
        tabStrip.setUnderlineHeight(0);
        tabStrip.setViewPager(viewPager);
        auto_searchkey.addTextChangedListener(this);
        bt_search_web.setOnClickListener(this);
//        fab_hide.setOnClickListener(this);

        fabHide.setOnClickListener(this);
        fabScan.setOnClickListener(this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_search_web:
                Uri uri = Uri.parse("http://www.baidu.com.cn/s?wd=" + auto_searchkey.getText().toString() + "&cl=3");
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                break;
//            case R.id.fab:
//                CircularAnimUtil.startActivity(this, new Intent(this, HideActivity.class), fab_hide, R.color.fag_bg);
//                break;
            case R.id.fab1:
                CircularAnimUtil.startActivity(this, new Intent(this, HideActivity.class), fabHide, R.color.fag_bg);
                break;
            case R.id.fab_scan:
                ScanActivity.startScanActivity(MainActivity.this);
                break;
        }

    }

    /**
     * 开启监听
     */
    @Override
    public void startVoice() {
        String[] systemAlertWindowPermission = new String[]{Manifest.permission.RECORD_AUDIO};
        CheckPermission
                .from(this)
                .setPermissions(systemAlertWindowPermission)
                .setRationaleConfirmText("Request SYSTEM_ALERT_WINDOW")
                .setDeniedMsg("The SYSTEM_ALERT_WINDOW Denied")
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        if (isrDialog==null) {
                            isrDialog = new RecognizerDialog(MainActivity.this, null);
                            isrDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                            isrDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
                            /*
                             * 设置引擎目前支持五种 ”sms”：普通文本转写 “poi”：地名搜索 ”vsearch”：热词搜索
                             * ”video”：视频音乐搜索 ”asr”：命令词识别
                             */
                            isrDialog.setListener(recoListener);
                        }
                        isrDialog.show();
                        auto_searchkey.setText("");
                    }

                    @Override
                    public void permissionDenied() {
                    }
                }).check();


    }


    public class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= 0 && position < 8) {
                switch (position) {
                    case 0:
                        appFragment = AppFragment.getInstance();
                        return appFragment;
                    case 1:
                        contactFragment = ContactFragment.getInstance();
                        return contactFragment;
                    case 2:
                        smsFragment = SmsFragment.getInstance();
                        return smsFragment;
                    case 3:
                        imageFragment = ImageFragment.getInstance();
                        return imageFragment;
                    case 4:
                        audioFragment = AudioFragment.getInstance();
                        return audioFragment;
                    case 5:
                        videoFragment = VideoFragment.getInstance();
                        return videoFragment;
                    case 6:
                        docFragment = DocFragment.getInstance();
                        return docFragment;
                    case 7:
                        apkFragment = ApkFragment.getInstance();
                        return apkFragment;
                    default:
                        break;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof AppFragment) {
                ((AppFragment) object).updateContent(auto_searchkey.getText().toString());
            } else if (object instanceof ContactFragment) {
                ((ContactFragment) object).updateContent(auto_searchkey.getText().toString());
            } else if (object instanceof SmsFragment) {
                ((SmsFragment) object).updateContent(auto_searchkey.getText().toString());
            } else if (object instanceof ImageFragment) {
                ((ImageFragment) object).updateContent(auto_searchkey.getText().toString());
            } else if (object instanceof AudioFragment) {
                ((AudioFragment) object).updateContent(auto_searchkey.getText().toString());
            } else if (object instanceof VideoFragment) {
                ((VideoFragment) object).updateContent(auto_searchkey.getText().toString());
            } else if (object instanceof DocFragment) {
                ((DocFragment) object).updateContent(auto_searchkey.getText().toString());
            } else if (object instanceof ApkFragment) {
                ((ApkFragment) object).updateContent(auto_searchkey.getText().toString());
            }
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position >= 0 && position < 8) {
                switch (position) {
                    case 0:
                        return "应用";
                    case 1:
                        return "联系人";
                    case 2:
                        return "短信";
                    case 3:
                        return "图片";
                    case 4:
                        return "音乐";
                    case 5:
                        return "视频";
                    case 6:
                        return "文档";
                    case 7:
                        return "安装包";
                    default:
                        break;
                }
            }
            return null;
        }
    }

    //************************* 杨尚臻 ************************//
    public static final String SEARCH_HISTORY = "search_history";
    private List<String> mOriginalValues;
    private AutoAdapter autoAdapter;
    //根布局
    private LinearLayout mParentLayout;

    /**
     * 初始化autoTextView
     * @param history
     * @param auto
     */
    private void initAutoComplete(String history, final AdvancedAutoCompleteTextView auto) {
        initSearchHistory();
        autoAdapter = new AutoAdapter(this, mOriginalValues);
        auto.setAdapter(autoAdapter);
        auto.setDropDownHeight(350);
        auto.setBackgroundColor(getResources().getColor(R.color.colorGray));
        auto.setThreshold(1);
        auto.setVoiceListener(this);
        auto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView view = (AutoCompleteTextView) v;
                if (hasFocus) {
                    view.showDropDown();
                }
            }
        });

        mParentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = mParentLayout.getRootView().getHeight()- mParentLayout.getHeight();
                //不显示软键盘
                if(heightDiff < 100){
                    if (!TextUtils.isEmpty(auto_searchkey.getText().toString().trim()) && !mOriginalValues.contains(auto_searchkey.getText().toString().trim())) {
                        Log.e("mOriginalValues", auto_searchkey.getText().toString().trim());
                        mOriginalValues.add(auto_searchkey.getText().toString().trim());
                        saveSearchHistory();
                        autoAdapter.refreshAdd(mOriginalValues);
                    }
                }

            }
        });
    }

    /**
     * 读取历史搜索记录
     */
    public void initSearchHistory() {
        SharedPreferences sp = getSharedPreferences(
                MainActivity.SEARCH_HISTORY, 0);
        String longhistory = sp.getString(MainActivity.SEARCH_HISTORY, "");
        String[] hisArrays = longhistory.split(",");
        mOriginalValues = new ArrayList<String>();
        if (hisArrays.length == 1) {
            return;
        }
        for (int i = 0; i < hisArrays.length; i++) {
            Log.e("initSearchHistory", hisArrays[i]);
            mOriginalValues.add(hisArrays[i]);
        }
    }

    /**
     * 保存历史记录
     */
    private void saveSearchHistory() {
        String text = auto_searchkey.getText().toString().trim();
        if (text.length() < 1) {
            return;
        }
        SharedPreferences sp = getSharedPreferences(MainActivity.SEARCH_HISTORY, 0);
        String longhistory = sp.getString(MainActivity.SEARCH_HISTORY, "");
        String[] tmpHistory = longhistory.split(",");
        ArrayList<String> history = new ArrayList<String>(
                Arrays.asList(tmpHistory));
        if (history.size() > 0) {
            int i;
            for (i = 0; i < history.size(); i++) {
                if (text.equals(history.get(i))) {
                    history.remove(i);
                    break;
                }
            }
            history.add(0, text);
        }
        if (history.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < history.size(); i++) {
                sb.append(history.get(i) + ",");
                Log.e("saveSearchHistory", history.get(i));
            }
            sp.edit().putString(MainActivity.SEARCH_HISTORY, sb.toString()).commit();
        } else {
            sp.edit().putString(MainActivity.SEARCH_HISTORY, text + ",").commit();
        }
    }

    /**
     * 删除历史记录
     * @param value
     */
    private void deleteSearchHistory(String value) {
        SharedPreferences sp = getSharedPreferences(MainActivity.SEARCH_HISTORY, 0);
        String longhistory = sp.getString(MainActivity.SEARCH_HISTORY, "");
        String[] tmpHistory = longhistory.split(",");
        ArrayList<String> history = new ArrayList<String>(
                Arrays.asList(tmpHistory));
        sp.edit().clear().commit();
        if (history.size() > 0) {
            int i;
            StringBuilder sb = new StringBuilder();
            for (i = 0; i < history.size(); i++) {
                if (value.equals(history.get(i))) {
                    Log.e("deleteSearchHistory", history.get(i));
                    history.remove(i);
                } else {
                    sb.append(history.get(i) + ",");
                }
            }
            sp.edit().putString(MainActivity.SEARCH_HISTORY, sb.toString()).commit();
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void deleteEventBus(MessageEvent message) {
        Log.e("deleteEventBus", message.name);
        deleteSearchHistory(message.name);
        autoAdapter.refreshCancle(mOriginalValues);
    }


    //************************* 杨尚臻 ************************//

    //************************* 王家成 ************************//

    private String result = null;
    RecognizerDialog isrDialog;

    // 语言识别监听器，有两个方法
    RecognizerDialogListener recoListener = new RecognizerDialogListener() {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
        }

    };
    /**
     * 解析方法
     * @param results
     */
    private void printResult(RecognizerResult results) {
        JsonParser jsonParser = new JsonParser();
        String text = jsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String,String> mIatResults=new HashMap<String,String>();
        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
            Log.d("wjc", "EditText内容为：" + resultBuffer);
        }
        result = resultBuffer.toString();
        auto_searchkey.append(resultBuffer);
//        tvShow.setText(resultBuffer);
//        int index=et_searchkey.getSelectionStart();   //获取Edittext光标所在位置
//        if (!resultBuffer.equals("")) {//判断输入框不为空，执行删除
//            et_searchkey.getText().delete(index-1,index);
//        }
        Log.d("wjc", "EditText内容为：" + auto_searchkey.getText());
    }

    /**
     * Json结果解析类
     */
    class JsonParser {

        String parseIatResult(String json) {
            StringBuffer ret = new StringBuffer();
            if(TextUtils.isEmpty(json))
                return "";
            try {
                JSONTokener tokener = new JSONTokener(json);
                JSONObject joResult = new JSONObject(tokener);

                JSONArray words = joResult.getJSONArray("ws");
                for (int i = 0; i < words.length(); i++) {
                    // 转写结果词，默认使用第一个结果
                    JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                    JSONObject obj = items.getJSONObject(0);
                    ret.append(obj.getString("w"));
//              如果需要多候选结果，解析数组其他字段
//              for(int j = 0; j < items.length(); j++)
//              {
//                  JSONObject obj = items.getJSONObject(j);
//                  ret.append(obj.getString("w"));
//              }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ret.toString();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(null != isrDialog){
            isrDialog.cancel();
        }
    }

    public static final int NOTIFY_ID = 8080;

    public void sendNotification() {
        Resources res = getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.search_notification);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(
                this);
        notifyBuilder.setContentTitle("GlobalSearch");
        notifyBuilder.setContentText("点击进入GlobalSearch！");
        notifyBuilder.setSmallIcon(R.drawable.search_notification);
        notifyBuilder.setWhen(System.currentTimeMillis());
        notifyBuilder.setOngoing(true);//不能滑动删除
        notifyBuilder.setLargeIcon(bmp);
        notifyBuilder.setPriority(NotificationCompat.PRIORITY_MAX);//通知栏优先级设置为最高
        notifyBuilder.setTicker("Hi,Notification is here");
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //点击通知跳转
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        PendingIntent contentIntent = PendingIntent.getActivity(
                MainActivity.this,
                R.string.app_name,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFY_ID, notifyBuilder.build());
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // 构建Notification对象
//        Notification.Builder mBuilder = new Notification.Builder(this);
//        mBuilder.setSmallIcon(R.drawable.search_notification).setTicker("GlobalSearch已经启动！");
//        // 自定义RemoteViews对象
//       RemoteViews views = new RemoteViews(getPackageName(),R.layout.layout_notification);
//        // 给views中的按钮添加点击意图
//        Intent intentApp = new Intent(NOTIFICATION_INTENT_APP);
//        PendingIntent piApp = PendingIntent.getBroadcast(this, 1,
//                intentApp, PendingIntent.FLAG_UPDATE_CURRENT);
//        views.setOnClickPendingIntent(R.id.btn_app,piApp);

        //************************* 王家成 ************************//
    }
}