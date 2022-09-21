package com.example.slotgame;

public class Score {
    private int current;
    private int bet;
    private int record;

    public Score() {
        current = 100;
        bet = 0;
        record = 0;
    }

    public void init_score() {
        setCurrent(100);
        setBet(0);
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
        this.record = record;
    }
}
