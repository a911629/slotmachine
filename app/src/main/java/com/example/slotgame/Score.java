package com.example.slotgame;

import android.util.Log;

public class Score {
    private static final String TAG = Score.class.getSimpleName();
    private int current;
    private int bet;
    private int record;

    public void bingo(int cur) {
//        load_score();
        switch (cur) {
            case 0:
            case 1:
            case 2:
                setCurrent(getCurrent() + getBet() * 3);
                Log.d(TAG, "bingo1: " + getCurrent() + getBet());
//                current += bet * 3;
                break;
            case 3:
            case 4:
                setCurrent(getCurrent() + getBet() * 5);
                Log.d(TAG, "bingo2: " + getCurrent() + getBet());
//                current += bet * 5;
                break;
            case 5:
                setCurrent(getCurrent() + getBet() * 9);
                Log.d(TAG, "bingo3: " + getCurrent() + getBet());
//                current += bet * 9;
                break;
            case 6:
                setCurrent(getCurrent() + getBet() * 17);
                Log.d(TAG, "bingo4: " + getCurrent() + getBet());
//                current += bet * 17;
                break;
            default:
//                bet = 0;
//                setBet(0);
                break;
        }
        setRecord(getBet());
        clean_bet();
//        record = bet;
//        set_score();
    }

    public void clean_bet() {
        Log.d(TAG, "clean_bet: ");
//        load_score();
        setBet(0);
//        set_score();
    }

    public Score() {
        current = 100;
        bet = 0;
        record = 0;
    }

    public void init_score() {
        setCurrent(100);
        setBet(0);
        setRecord(0);
    }

    public void compute(int in) {
        current -= in;
        bet += in;
    }

    public void record_bet() {
        if (bet != 0 || current < record)
            return;
        current -= record;
        bet += record;
    }

    public void add_back() {
        current += bet;
        bet = 0;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getRecord() {
        return record;
    }

    public void setRecord(int record) {
        Log.d(TAG, "setRecord: " + record);
        this.record = record;
    }

    public boolean isEmpty() {
        if (current == 0 && bet == 0)
            return true;
        else
            return false;
    }

    public void load_score() {

    }
}
