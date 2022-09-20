package com.example.slotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Score score = new Score();
    private TextView score_t;
    private TextView bet_t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find_view();
        score.init_score();
        load_score();
    }

    public void find_view() {
        score_t = findViewById(R.id.score);
        bet_t = findViewById(R.id.bet);
    }

    public void load_score() {
        score_t.setText(Integer.toString(score.getCurrent()));
        bet_t.setText(Integer.toString(score.getBet()));
    }
}