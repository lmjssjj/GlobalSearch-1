package com.bbk.open.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.bbk.open.ContextHolder;
import com.bbk.open.activity.MainActivity;
import com.bbk.open.eventbus.MessageEvent;
import com.bbk.open.globlesearch.R;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/8/5.
 */
public class AdvancedAutoCompleteTextView extends AutoCompleteTextView {

    private Drawable mRightDrawable;
    private Drawable mTopDrawable;
    private boolean isHasFocus;
    private Drawable rightDrawable;
    private Context mContext;

    public AdvancedAutoCompleteTextView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public AdvancedAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdvancedAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init(){
        //getCompoundDrawables:
        //Returns drawables for the left, top, right, and bottom borders.
        Drawable [] drawables=this.getCompoundDrawables();
        //取得right位置的Drawable
        //即我们在布局文件中设置的android:drawableRight
        mRightDrawable=drawables[2];
        mTopDrawable = drawables[1];
        rightDrawable = mRightDrawable;
        //设置焦点变化的监听
        this.setOnFocusChangeListener(new FocusChangeListenerImpl());
        //设置EditText文字变化的监听
        this.addTextChangedListener(new TextWatcherImpl());
        //初始化时让右边clean图标不可见
        //setClearDrawableVisible(false);
        setTopInVisible(false);
    }


    /**
     * 当手指抬起的位置在clean的图标的区域
     * 我们将此视为进行清除操作
     * getWidth():得到控件的宽度
     * event.getX():抬起时的坐标(改坐标是相对于控件本身而言的)
     * getTotalPaddingRight():clean的图标左边缘至控件右边缘的距离
     * getPaddingRight():clean的图标右边缘至控件右边缘的距离
     * 于是:
     * getWidth() - getTotalPaddingRight()表示:
     * 控件左边到clean的图标左边缘的区域
     * getWidth() - getPaddingRight()表示:
     * 控件左边到clean的图标右边缘的区域
     * 所以这两者之间的区域刚好是clean的图标的区域
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                boolean isClean =(event.getX() > (getWidth() - getTotalPaddingRight()))&&
                        (event.getX() < (getWidth() - getPaddingRight()));
                if (rightDrawable == mTopDrawable) {
                    if (isClean) {
                        setText("");
                    }
                } else {
                    if (isClean) {
                        mListener.startVoice();
                    }
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setTopInVisible(boolean topInVisible) {
        setCompoundDrawables(getCompoundDrawables()[0],null,
                getCompoundDrawables()[2],getCompoundDrawables()[3]);
    }

    private class FocusChangeListenerImpl implements OnFocusChangeListener{
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            isHasFocus=hasFocus;
            if (isHasFocus) {
                boolean isVisible=getText().toString().length()>=1;
                setClearDrawableVisible(isVisible);
            } else {
                setClearDrawableVisible(false);
            }
        }

    }

    //当输入结束后判断是否显示右边clean的图标
    private class TextWatcherImpl implements TextWatcher {
        @Override
        public void afterTextChanged(Editable s) {
            boolean isVisible=getText().toString().length()>=1;
            setClearDrawableVisible(isVisible);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,int count) {

        }

    }

    //隐藏或者显示右边clean的图标
    protected void setClearDrawableVisible(boolean isVisible) {
        if (isVisible) {
            rightDrawable = mTopDrawable;
        } else {
            rightDrawable = mRightDrawable;
        }
        //使用代码设置该控件left, top, right, and bottom处的图标
        setCompoundDrawables(getCompoundDrawables()[0],getCompoundDrawables()[1],
                rightDrawable,getCompoundDrawables()[3]);
    }

    public interface startVoiceLister{
        void startVoice();
    }
    private startVoiceLister mListener;

    public void setVoiceListener(startVoiceLister Listener) {
        mListener = Listener;
    }
}
