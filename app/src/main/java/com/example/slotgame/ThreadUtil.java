package com.example.slotgame;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadUtil {

    private final static String ASYNC_THREAD_NAME = "single-async-thread";

    private static ThreadUtil sInstance;

    private ThreadPoolExecutor mExecutor;

    private HandlerThread mSingleAsyncThread;
    private Handler mSingleAsyncHandler;
    private Handler mMainHandler;
    private MessageQueue mMsgQueue;

    private ThreadUtil() {
        mExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);

        mSingleAsyncThread = new HandlerThread(ASYNC_THREAD_NAME);
        mSingleAsyncThread.start();
        mSingleAsyncHandler = new Handler(mSingleAsyncThread.getLooper());

        mMainHandler = new Handler(Looper.getMainLooper());

        if (Looper.getMainLooper() == Looper.myLooper()) {
            mMsgQueue = Looper.myQueue();
        } else {
            Object queue = null;
            try {
                queue = ReflectUtil.getValue(Looper.getMainLooper(), "mQueue");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (queue instanceof MessageQueue) {
                mMsgQueue = (MessageQueue) queue;
            } else {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        mMsgQueue = Looper.myQueue();
                    }
                });
            }
        }
    }

    public static ThreadUtil getInstance() {
        if (sInstance == null) {
            sInstance = new ThreadUtil();
        }
        return sInstance;
    }

    public void execute(Runnable task) {
        mExecutor.execute(task);
    }

    public void cancel(final Runnable task) {
        mExecutor.remove(task);
        mSingleAsyncHandler.removeCallbacks(task);
        mMainHandler.removeCallbacks(task);
    }

    public void destroy() {
        mExecutor.shutdownNow();
        mSingleAsyncHandler.removeCallbacksAndMessages(null);
        mMainHandler.removeCallbacksAndMessages(null);
    }

    public void runOnAsyncThread(Runnable r) {
        mSingleAsyncHandler.post(r);
    }

    public void runOnAsyncThread(Runnable r, long delay) {
        mSingleAsyncHandler.postDelayed(r, delay);
    }

    public void runOnMainThread(Runnable r) {
        mMainHandler.post(r);
    }

    public void runOnMainThread(Runnable r, long delay) {
        mMainHandler.postDelayed(r, delay);
    }

    public void runOnIdleTime(final Runnable r) {
        IdleHandler handler = new IdleHandler() {

            @Override
            public boolean queueIdle() {
                r.run();
                return false;
            }
        };
        mMsgQueue.addIdleHandler(handler);
    }
}
