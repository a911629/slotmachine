package com.example.slotgame;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;
import android.os.MessageQueue.IdleHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程执行代理类，用于统一管理线程
 */
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

    /**
     * 執行異步任務
     */
    public void execute(Runnable task) {
        mExecutor.execute(task);
    }

    /**
     * 取消任務
     */
    public void cancel(final Runnable task) {
        mExecutor.remove(task);
        mSingleAsyncHandler.removeCallbacks(task);
        mMainHandler.removeCallbacks(task);
    }

    /**
     * 銷毀
     */
    public void destroy() {
        mExecutor.shutdownNow();
        mSingleAsyncHandler.removeCallbacksAndMessages(null);
        mMainHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 提交一个Runable到异步线程队列，该异步线程为单队列
     *
     * @param r
     */
    public void runOnAsyncThread(Runnable r) {
        mSingleAsyncHandler.post(r);
    }

    /**
     * 提交一个Runable到异步线程队列，该异步线程为单队列
     *
     * @param r
     * @param delay
     */
    public void runOnAsyncThread(Runnable r, long delay) {
        mSingleAsyncHandler.postDelayed(r, delay);
    }

    /**
     * 提交一个Runable到主线程队列
     *
     * @param r
     */
    public void runOnMainThread(Runnable r) {
        mMainHandler.post(r);
    }

    /**
     * 提交一个Runable到主线程队列
     *
     * @param r
     * @param delay
     */
    public void runOnMainThread(Runnable r, long delay) {
        mMainHandler.postDelayed(r, delay);
    }

    /**
     * 提交一个Runnable到主线程空闲时执行
     *
     * @param r
     */
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
