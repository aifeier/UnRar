package com.ai.cwf.unrar;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Created at 陈 on 2017/5/17.
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

public class ToastUtils {
    private static Handler mHandler;
    private static Toast mToast;

    public static void show(CharSequence msg) {
        show(msg, false);
    }

    public static void show(final CharSequence msg, final boolean timeLong) {
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (mHandler != null)
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mToast == null) {
                        mToast = Toast.makeText(App.getInstance(), msg, timeLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                    } else {
                        mToast.setDuration(timeLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                        mToast.setText(msg);
                    }
                    mToast.show();
                }
            });
    }

}
