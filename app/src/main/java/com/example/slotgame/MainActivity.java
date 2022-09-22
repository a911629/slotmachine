package com.example.slotgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Score score = new Score();
    private TextView score_t;
    private TextView bet_t;
    private FragmentManager option_board;
    private FragmentManager leader_board;
    private FragmentManager list_board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find_view();
        score.init_score();
        refresh_score();

        option_board = getSupportFragmentManager();
        leader_board = getSupportFragmentManager();
        list_board = getSupportFragmentManager();


    }

    public void bar(View view) {
        Log.d(TAG, "bar: ");
        score.setRecord(7);

        // TODO:
        // score.setRecord(score.getBet());
    }

    public void find_view() {
        score_t = findViewById(R.id.score);
        bet_t = findViewById(R.id.bet);
    }

    public void refresh_score() {
        score_t.setText(Integer.toString(score.getCurrent()));
        bet_t.setText(Integer.toString(score.getBet()));
    }

    public void record_bet(View view) {
        Log.d(TAG, "record_bet: ");
        score.record_bet();
        refresh_score();
    }

    public void clear_bet(View view) {
        Log.d(TAG, "clean_bet: ");

        score.add_back();
        refresh_score();
    }

    public void add_bet1(View view) {
        Log.d(TAG, "add_bet1: ");
        refresh_score();

        if (score.getCurrent() < 1)
            return;
        if (score.getBet() > 49)
            return;

        score.compute(1);
        refresh_score();
    }

    public void minus_bet1(View view) {
        Log.d(TAG, "minus_bet1: ");
        refresh_score();
        if (score.getBet() < 1)
            return;

        score.compute(-1);
        refresh_score();
    }

    public void add_bet5(View view) {
        Log.d(TAG, "add_bet5: ");
        refresh_score();
        if (score.getCurrent() < 5)
            return;
        if (score.getBet() > 45)
            return;
        score.compute(5);
        refresh_score();
    }

    public void minus_bet5(View view) {
        Log.d(TAG, "add_bet5: ");
        refresh_score();
        if (score.getBet() < 5)
            return;
        score.compute(-5);
        refresh_score();
    }

    public void add_bet10(View view) {
        Log.d(TAG, "add_bet10: ");
        refresh_score();
        if (score.getCurrent() < 10)
            return;
        if (score.getBet() > 40)
            return;
        score.compute(10);
        refresh_score();
    }

    public void minus_bet10(View view) {
        Log.d(TAG, "add_bet10: ");
        refresh_score();
        if (score.getBet() < 10)
            return;
        score.compute(-10);
        refresh_score();
    }

    public void new_game(View view) {
        Log.d(TAG, "reset_game: ");
        score.init_score();
        refresh_score();
        option_board(view);
    }

    public void option_board(View view) {
        if (OptionFragment.getInstance().isVisible()) {
            Log.d(TAG, "option_board: close");
            Fragment optionBoardFragment = getSupportFragmentManager().findFragmentByTag("option");
            if (optionBoardFragment != null) {
                option_board.beginTransaction().remove(optionBoardFragment).commit();
            }
        } else {
            Log.d(TAG, "option_board: open");
            option_board.beginTransaction().add(R.id.board, OptionFragment.getInstance(), "option").commit();
        }
    }

    public void leader_board(View view) {
        readLeaderBoardInfo();

        if (LeaderBoardFragment.getInstance().isVisible()) {
            Log.d(TAG, "leader_board: close");
            Fragment leaderBoardFragment = getSupportFragmentManager().findFragmentByTag("leader");

            if (leaderBoardFragment != null) {
                leader_board.beginTransaction().remove(leaderBoardFragment).commit();
            }
        } else {
            Log.d(TAG, "leader_board: open");
            if (OptionFragment.getInstance().isVisible()) {
                Log.d(TAG, "option_board: close");
                Fragment optionBoardFragment = getSupportFragmentManager().findFragmentByTag("option");
                if (optionBoardFragment != null) {
                    option_board.beginTransaction().remove(optionBoardFragment).commit();
                }
            }
            leader_board.beginTransaction().add(R.id.board, LeaderBoardFragment.getInstance(), "leader").commit();
        }
    }

    public void list_board(View view) {
        if (ListFragment.getInstance().isVisible()) {
            Log.d(TAG, "list_board close");
            Fragment listBoardFragment = getSupportFragmentManager().findFragmentByTag("list");
            if (listBoardFragment != null) {
                list_board.beginTransaction().remove(listBoardFragment).commit();
            }
        } else {
            Log.d(TAG, "list: open list");
            if (OptionFragment.getInstance().isVisible()) {
                Log.d(TAG, "option_board: close");
                Fragment optionBoardFragment = getSupportFragmentManager().findFragmentByTag("option");
                if (optionBoardFragment != null) {
                    option_board.beginTransaction().remove(optionBoardFragment).commit();
                }
            }
            list_board.beginTransaction().add(R.id.board, ListFragment.getInstance(), "list").commit();
        }
    }

    private void readLeaderBoardInfo() {
        //TODO: read datebase
    }


    public void quit(View view) {
        Log.d(TAG, "quit: go");
        finish();
    }
}