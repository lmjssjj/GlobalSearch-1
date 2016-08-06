package com.bbk.open.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/7/23.
 */
public class CircularAnimUtil {

    public static final long PERFECT_MILS = 618;
    public static final int MINI_RADIUS = 0;
    private static final String TAG = "CircularAnimUtil";
    /**
     * 像四周伸张
     * @param myView
     * @param startRadius
     * @param durationMills
     */
    public static void show(View myView, float startRadius, long durationMills) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            myView.setVisibility(View.VISIBLE);
            return;
        }

        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        int w = myView.getWidth();
        int h = myView.getHeight();

        int finalRadius = (int) Math.sqrt(w * w + h * h) + 1;

        Animator animator = ViewAnimationUtils.createCircularReveal(myView, cx, cy, startRadius, finalRadius);
        myView.setVisibility(View.VISIBLE);
        animator.setDuration(durationMills);
        animator.start();
    }

    /**
     * 由满向中间收缩
     * @param myView
     * @param endRadius
     * @param durationMills
     */
    public static void hide(final View myView, float endRadius, long durationMills) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            myView.setVisibility(View.VISIBLE);
            return;
        }

        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        int w = myView.getWidth();
        int h = myView.getHeight();

        int finalRadius = (int) Math.sqrt(w * w + h * h) + 1;

        Animator animator = ViewAnimationUtils.createCircularReveal(myView, cx, cy, endRadius, finalRadius);
        myView.setVisibility(View.VISIBLE);
        animator.setDuration(durationMills);
        animator.addListener(new  AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animator) {
                myView.setVisibility(View.INVISIBLE);
            }

        });
        animator.start();
    }

    /**
     * 从指定View开始向四周伸张(伸张颜色或图片为colorOrImageRes), 然后进入另一个Activity,
     * 返回至 @thisActivity 后显示收缩动画。
     * @param thisActivity
     * @param intent
     * @param requestCode
     * @param triggerView
     * @param colorImageRes
     * @param durationMills
     */
    public static void startActivityForResult(final Activity thisActivity, final Intent intent, final Integer
                                              requestCode, final Bundle bundle , final View triggerView, int  colorImageRes,
                                              final long durationMills){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            thisActivity.startActivity(intent);
            return;
        }

        //location [0]--->x坐标,location [1]--->y坐标 view.getLocationInWindow(location);
        // 获取在当前窗口内的绝对坐标，getLeft , getTop, getBottom, getRight,  这一组是获取相对在它父窗口里的坐标。
        int[] location = new int[2];
        triggerView.getLocationInWindow(location);
        final int cx = location[0] + triggerView.getWidth() / 2;
        final int cy = location[1] + triggerView.getHeight() / 2;
        Log.e("startActivityForResult", "location[0] = " + location[0] + "  location[1] = " + location[1]);
        final ImageView view = new ImageView(thisActivity);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setImageResource(colorImageRes);
        final ViewGroup decorView = (ViewGroup) thisActivity.getWindow().getDecorView();//，包括标题栏，但不包括状态栏。
        int w = decorView.getWidth();
        int y = decorView.getHeight();
        decorView.addView(view, w, y);

        //计算中心点至view边界的最大距离
        int maxW = Math.max(cx, w - cx);
        int maxY = Math.max(cy, y - cy);
        final int finalRadius = (int) Math.sqrt(maxW*maxW + maxY*maxY) + 1;
        Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        int maxRadius = (int) (Math.sqrt(w*w+y*y) + 1);
        long finalDuration = durationMills;
        if (finalDuration == PERFECT_MILS) {
            double rate = 1d * finalRadius / maxRadius;
            finalDuration = (long)(PERFECT_MILS * rate);
        }
        animator.setDuration(finalDuration);
        animator.addListener(new  AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animator) {
                if (requestCode == null) {
                    thisActivity.startActivity(intent);
                } else if (bundle == null) {
                    thisActivity.startActivityForResult(intent, requestCode);
                } else {
                    thisActivity.startActivityForResult(intent, requestCode, bundle);
                }
                // 默认渐隐过渡动画.
                thisActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                triggerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius, 0);
                        anim.setDuration(durationMills);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                try{
                                    decorView.removeView(view);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        anim.start();
                    }
                }, 1000);
            }

        });
        animator.start();
    }

    public static void startActivityForResult(
            Activity thisActivity, Intent intent, Integer requestCode, View triggerView, int colorOrImageRes) {
        startActivityForResult(thisActivity, intent, requestCode, null, triggerView, colorOrImageRes, PERFECT_MILS);
    }

    public static void startActivity(
            Activity thisActivity, Intent intent, View triggerView, int colorOrImageRes, long durationMills) {
        startActivityForResult(thisActivity, intent, null, null, triggerView, colorOrImageRes, durationMills);
    }

    public static void startActivity(
            Activity thisActivity, Intent intent, View triggerView, int colorOrImageRes) {
        startActivity(thisActivity, intent, triggerView, colorOrImageRes, PERFECT_MILS);
    }

    public static void startActivity(Activity thisActivity, Class<?> targetClass, View triggerView, int colorOrImageRes) {
        startActivity(thisActivity, new Intent(thisActivity, targetClass), triggerView, colorOrImageRes, PERFECT_MILS);
    }

    public static void show(View myView) {
        show(myView, MINI_RADIUS, PERFECT_MILS);
    }

    public static void hide(View myView) {
        hide(myView, MINI_RADIUS, PERFECT_MILS);
    }
}
