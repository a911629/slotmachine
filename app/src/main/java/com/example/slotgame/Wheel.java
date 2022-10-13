package com.example.slotgame;

import android.util.Log;

public class Wheel extends Thread {

//    int[] chance = {18, 18, 18, 15, 15, 10, 6};

    Chance chance;

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

    public Wheel(WheelListener wheelListener, long frameDuration, long startIn, Chance chance) {
        this.wheelListener = wheelListener;
        this.frameDuration = frameDuration;
        this.startIn = startIn;
        this.chance = chance;
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
        currentIndex = (int) (Math.random() * 100);

        Log.d(TAG, "nextImg_r: random " + currentIndex);
        Log.d(TAG, "nextImg_r: random " + chance.sum(0) + " "
                + chance.sum(1) + " " + chance.sum(2) + " "
                + chance.sum(3) + " " + chance.sum(4) + " "
                + chance.sum(5) + " " + chance.sum(6));

        if (currentIndex < chance.sum(0)) {
            Log.d(TAG, "nextImg_r: go 0");
            currentIndex = 0;
        } else if (currentIndex < chance.sum(1)) {
            Log.d(TAG, "nextImg_r: go 1");
            currentIndex = 1;
        } else if (currentIndex < chance.sum(2)) {
            Log.d(TAG, "nextImg_r: go 2");
            currentIndex = 2;
        } else if (currentIndex < chance.sum(3)) {
            Log.d(TAG, "nextImg_r: go 3");
            currentIndex = 3;
        } else if (currentIndex < chance.sum(4)) {
            Log.d(TAG, "nextImg_r: go 4");
            currentIndex = 4;
        } else if (currentIndex < chance.sum(5)) {
            Log.d(TAG, "nextImg_r: go 5");
            currentIndex = 5;
        } else if (currentIndex < chance.sum(6)) {
            Log.d(TAG, "nextImg_r: go 6");
            currentIndex = 6;
        }

        if (currentIndex >= imgs.length) {
            currentIndex = currentIndex % imgs.length;
//            Log.d(TAG, "nextImg2 " + currentIndex);
        }
    }

    //從1到num 的機率總和
    private int add(int num) {
        int sum = 0;
        for (int i = 0; i <= num; i++) {
            sum += chance.chance[i];
        }

        return sum;
    }

    //增加1~3的機率，並減少4~6的機率，以6為最低基準
//    public void add_chance(int num) {
//        int times = 0;
//        for (int i = 0; i < num; i++) {
//            if (chance[3] == 6) {
//                times = times;
//            } else {
//                chance[3] -= 1;
//                times += 1;
//            }
//
//            if (chance[4] == 6) {
//                times = times;
//            } else {
//                chance[4] -= 1;
//                times += 1;
//            }
//
//            if (chance[5] == 6) {
//                times = times;
//            } else {
//                chance[5] -= 1;
//                times += 1;
//            }
//            for (int j = 0; j < times; j++) {
//                chance[mini()] += 1;
//            }
//        }
////            chance[0] += 1;
////            chance[1] += 1;
////            chance[2] += 1;
//        Log.d(TAG, "add_chance: " + chance[0] + "|" + chance[1] + "|" + chance[2]);
//    }

//    private int mini() {
//        if(chance[1] < chance[0])
//            return 1;
//        if(chance[2] < chance[1])
//            return 2;
//        return 0;
//    }

    @Override
    public void run() {
        try {
            Thread.sleep(startIn);
        } catch (InterruptedException e) {
        }

        while (isStarted) {
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
        nextImg_r();
        if (wheelListener != null) {
            wheelListener.newImage(imgs[currentIndex]);
        }
        isStarted = false;
    }
}