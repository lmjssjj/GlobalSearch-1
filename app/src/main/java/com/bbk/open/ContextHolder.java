package com.bbk.open;

import android.content.Context;

/**
 * Created by Administrator on 2016/8/5.
 */
public class ContextHolder {

    static Context ApplicationContext;
    public static void initial(Context context) {
        ApplicationContext = context;
    }
    public static Context getContext() {
        return ApplicationContext;
    }
}
