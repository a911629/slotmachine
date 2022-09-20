package com.example.slotgame;

public class Score {
    private int current;
    private int bet;
    private int record;

    public void init_score() {
        setCurrent(100);
        setBet(0);
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
        this.record = record;
    }
}
