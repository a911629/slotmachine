package com.example.slotgame;

import static java.lang.Math.random;

public class Wheel extends Thread {

    private static final String TAG = Wheel.class.getSimpleName();

    interface WheelListener {
        void newImage(int img);
    }

    private static int[] imgs = {R.drawable.class1, R.drawable.class2, R.drawable.class3, R.drawable.class4,
            R.drawable.class5, R.drawable.class6, R.drawable.class7};
    public int currentIndex;
    private WheelListener wheelListener;
    private long frameDuration;
    private long startIn;
    private boolean isStarted;

    public Wheel(WheelListener wheelListener, long frameDuration, long startIn) {
        this.wheelListener = wheelListener;
        this.frameDuration = frameDuration;
        this.startIn = startIn;
        currentIndex = -1;
        isStarted = true;
    }

    public void nextImg() {
        currentIndex++;
//        Log.d(TAG, "nextImg1 " + currentIndex);
        if (currentIndex >= imgs.length) {
            currentIndex = currentIndex % imgs.length;
//            Log.d(TAG, "nextImg2 " + currentIndex);
        }
    }

    public void nextImg_r() {
        currentIndex = (int) random() * 100;
//        Log.d(TAG, "nextImg1 " + currentIndex);
        if (currentIndex >= imgs.length) {
            currentIndex = currentIndex % imgs.length;
//            Log.d(TAG, "nextImg2 " + currentIndex);
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(startIn);
        } catch (InterruptedException e) {
        }

        while(isStarted) {
            try {
                Thread.sleep(frameDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            nextImg();
//            nextImg_r();

            if (wheelListener != null) {
                wheelListener.newImage(imgs[currentIndex]);
            }
        }
    }

    public void stopWheel() {
        isStarted = false;
    }
}