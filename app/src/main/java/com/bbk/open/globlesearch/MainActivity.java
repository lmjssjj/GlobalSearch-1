package com.bbk.open.globlesearch;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bbk.open.fragment.ApkFragment;
import com.bbk.open.fragment.AppFragment;
import com.bbk.open.fragment.AudioFragment;
import com.bbk.open.fragment.ContactFragment;
import com.bbk.open.fragment.DocFragment;
import com.bbk.open.fragment.ImageFragment;
import com.bbk.open.fragment.SmsFragment;
import com.bbk.open.fragment.VideoFragment;
import com.lhh.apst.library.AdvancedPagerSlidingTabStrip;

import java.sql.SQLException;


public class MainActivity extends AppCompatActivity implements TextWatcher, View.OnClickListener {

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
    private AdvancedPagerSlidingTabStrip tabStrip;
    private EditText et_searchkey;
    private Button bt_cancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        init();

    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpage1r);
        tabStrip = (AdvancedPagerSlidingTabStrip) findViewById(R.id.ta1bs);
        et_searchkey = (EditText) findViewById(R.id.tv_searchkey);
        bt_cancle = (Button) findViewById(R.id.bt_cancel);

    }

    private void init() {
        viewPager.setOffscreenPageLimit(8);
        adapter = new FragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
        tabStrip.setViewPager(viewPager);
        et_searchkey.addTextChangedListener(this);
        bt_cancle.setOnClickListener(this);
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
                ((AppFragment) object).updateContent(et_searchkey.getText().toString());
            } else if (object instanceof ContactFragment) {
                ((ContactFragment) object).updateContent(et_searchkey.getText().toString());
            } else if (object instanceof SmsFragment) {
                ((SmsFragment) object).updateContent(et_searchkey.getText().toString());
            } else if (object instanceof ImageFragment) {
                ((ImageFragment) object).updateContent(et_searchkey.getText().toString());
            } else if (object instanceof AudioFragment) {
                ((AudioFragment) object).updateContent(et_searchkey.getText().toString());
            } else if (object instanceof VideoFragment) {
                ((VideoFragment) object).updateContent(et_searchkey.getText().toString());
            } else if (object instanceof DocFragment) {
                ((DocFragment) object).updateContent(et_searchkey.getText().toString());
            } else if (object instanceof ApkFragment) {
                ((ApkFragment) object).updateContent(et_searchkey.getText().toString());
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
}