package lava.bluepay.com.lavaapp.common;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by bluepay on 2017/10/16.
 */

public class ThreadManager {
    private static Handler SUB_THREAD1_HANDLER;

    private static final Object mMainHandlerLock = new Object();
    private static Handler mUiHandler;
    /**
     * 副线程2
     */
    private static HandlerThread SUB_THREAD2;
    public static Handler getSubThread1Handler() {
        if (SUB_THREAD1_HANDLER == null) {
            synchronized (ThreadManager.class) {
                SUB_THREAD2 = new HandlerThread("SUB2");
                SUB_THREAD2.setPriority(Thread.MIN_PRIORITY);//降低线程优先级
                SUB_THREAD2.start();
                SUB_THREAD1_HANDLER = new Handler(SUB_THREAD2.getLooper());
            }
        }
        return SUB_THREAD1_HANDLER;
    }

    public static Handler getMainHandler(){
        if(mUiHandler == null){
            synchronized (mMainHandlerLock) {
                mUiHandler = new Handler(Looper.getMainLooper());
            }
        }
        return mUiHandler;
    }

    public static void executeOnSubThread1(Runnable run) {
        getSubThread1Handler().post(run);
    }

}
