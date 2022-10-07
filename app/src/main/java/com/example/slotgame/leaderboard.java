package com.example.slotgame;

public class leaderboard {
    String name;
    String score;
    String date;
    int rank;

    public leaderboard() {
    }

    public leaderboard(String name, String score, String date) {
        this.name = name;
        this.score = score;
        this.date = date;
    }

    public leaderboard(String name, String score, String date, int rank) {
        this.name = name;
        this.score = score;
        this.date = date;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

//    public int getRank() {
//        return rank;
//    }
//
//    public void setRank(int rank) {
//        this.rank = rank;
//    }
}
