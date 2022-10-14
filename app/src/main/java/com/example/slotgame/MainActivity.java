package com.example.slotgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Score score = new Score();
    private TextView score_t;
    private TextView bet_t;
    private FragmentManager option_board;
    private FragmentManager leader_board;
    private FragmentManager list_board;
    private boolean isStarted = false;
    private Wheel wheel1, wheel2, wheel3;
    private ImageView slot1, slot2, slot3;
    private Chance chance = new Chance();

//    private Wheel wheel1 = new Wheel(new Wheel.WheelListener() {
//        @Override
//        public void newImage(final int img) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    slot1.setImageResource(img);
//                }
//            });
//        }
//    }, 50, randomLong(150, 400));
//
//    private Wheel wheel2 = new Wheel(new Wheel.WheelListener() {
//        @Override
//        public void newImage(final int img) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    slot2.setImageResource(img);
//                }
//            });
//        }
//    }, 50, randomLong(150, 400));
//
//    private Wheel wheel3 = new Wheel(new Wheel.WheelListener() {
//        @Override
//        public void newImage(final int img) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    slot3.setImageResource(img);
//                }
//            });
//        }
//    }, 50, randomLong(150, 400));

    //recyclerview test
    private Query query;
    private DatabaseReference leaderRef;
    //    private FirebaseRecyclerAdapter<leaderboard, leaderboard> adapter;
    public RecyclerView recycler;

    //    private RecyclerView recyclerView;
//    private FirebaseRecyclerAdapter<leaderboard, leaderboardHolder> adapter;

    public static final Random RANDOM = new Random();
    private int l_time = 0;
    private FirebaseRecyclerAdapter<leaderboard, testHolder> adapter;
    private RecyclerView test;
    @SuppressLint("WrongViewCast")
    private ConstraintLayout lead;

    public static long randomLong(long lower, long upper) {
//        return (long) (RANDOM.nextDouble() * (upper - lower));
        return lower + (long) (RANDOM.nextDouble() * (upper - lower));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find_view();
        score.init_score();
        chance.init();
        refresh_score();

        option_board = getSupportFragmentManager();
        leader_board = getSupportFragmentManager();
        list_board = getSupportFragmentManager();
    }

    public void bar(View view) {
        Log.d(TAG, "bar: ");
//        score.setRecord(7);
        if (score.getBet() != 0) {
            if (isStarted) {
                Log.d(TAG, "bar: go stop");
                wheel1.stopWheel();
                wheel2.stopWheel();
                wheel3.stopWheel();

                if (wheel1.currentIndex == wheel2.currentIndex && wheel2.currentIndex == wheel3.currentIndex
                        && wheel1.currentIndex == wheel3.currentIndex) {
                    score.bingo(wheel1.currentIndex);
                    chance.init();
                } else {
                    chance.add_chance();
                    Log.d(TAG, "bar: not bingo");
                }
                score.clean_bet();
                isStarted = false;
                if (score.isEmpty()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("遊戲結束")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            } else {
                Log.d(TAG, "bar: go start");
                wheel1 = new Wheel(new Wheel.WheelListener() {
                    @Override
                    public void newImage(final int img) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slot1.setImageResource(img);
                            }
                        });
                    }
                }, 50, randomLong(150, 400), chance);
                wheel1.start();

                wheel2 = new Wheel(new Wheel.WheelListener() {
                    @Override
                    public void newImage(final int img) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slot2.setImageResource(img);
                            }
                        });
                    }
                }, 50, randomLong(150, 400), chance);
                wheel2.start();

                wheel3 = new Wheel(new Wheel.WheelListener() {
                    @Override
                    public void newImage(final int img) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slot3.setImageResource(img);
                            }
                        });
                    }
                }, 50, randomLong(150, 400), chance);
                wheel3.start();

//            test.set_record();
                isStarted = true;
            }
        }
        refresh_score();
        // TODO:
        // score.setRecord(score.getBet());
    }

    @SuppressLint("WrongViewCast")
    public void find_view() {
        score_t = findViewById(R.id.score);
        bet_t = findViewById(R.id.bet);

        slot1 = (ImageView) findViewById(R.id.slot1);
        slot2 = (ImageView) findViewById(R.id.slot2);
        slot3 = (ImageView) findViewById(R.id.slot3);

        lead = findViewById(R.id.lead_board);

        // 10.14 測試開始 成功
        test = findViewById(R.id.test);
        test.setHasFixedSize(true);
        test.setLayoutManager(new LinearLayoutManager(this));
        Query query = FirebaseDatabase.getInstance().getReference("leaderboard").orderByValue();
        FirebaseRecyclerOptions<leaderboard> options = new FirebaseRecyclerOptions.Builder<leaderboard>()
                .setQuery(query, leaderboard.class).build();
        adapter = new FirebaseRecyclerAdapter<leaderboard, testHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull testHolder holder, int position, @NonNull leaderboard model) {
                holder.name.setText(model.getName());
                holder.date.setText(model.getDate());
                holder.score.setText(model.getScore());
                holder.rank.setText(model.getRank());
            }

            @NonNull
            @Override
            public testHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.leader_info, parent, false);
                return new testHolder(view);
            }
        };
        test.setAdapter(adapter);
    }

    class testHolder extends RecyclerView.ViewHolder {
        TextView rank;
        TextView name;
        TextView score;
        TextView date;

        public testHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.leader_rank);
            name = itemView.findViewById(R.id.leader_name);
            score = itemView.findViewById(R.id.leader_score);
            date = itemView.findViewById(R.id.leader_date);
        }
    }
    // 10.14 測試結束

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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
//        if (score.getCurrent() < 5)
//            return;
        if (score.getBet() == 50 || score.getCurrent() == 0)
            return;
        if (score.getCurrent() < 5)
            score.compute(score.getCurrent());
        else if (score.getBet() > 45) {
            score.compute(50 - score.getBet());
//            return;
        } else {
            score.compute(5);
        }
        refresh_score();
    }

    public void minus_bet5(View view) {
        Log.d(TAG, "minus_bet5: ");
        refresh_score();
        if (score.getBet() == 0)
            return;

        if (score.getBet() < 5) {
            score.compute(-1 * score.getBet());
        } else
            score.compute(-5);
        refresh_score();
    }

    public void add_bet10(View view) {
        Log.d(TAG, "add_bet10: ");
        refresh_score();
        if (score.getBet() == 50 || score.getCurrent() == 0)
            return;
        if (score.getCurrent() < 10)
            score.compute(score.getCurrent());
        else if (score.getBet() > 40) {
            score.compute(50 - score.getBet());
//            return;
        } else {
            score.compute(10);
        }
        refresh_score();
    }

    public void minus_bet10(View view) {
        Log.d(TAG, "minus_bet10: ");
        refresh_score();
        if (score.getBet() == 0)
            return;

        if (score.getBet() < 10)
            score.compute(-1 * score.getBet());
        else
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

        if(lead.getVisibility() == View.GONE)
            lead.setVisibility(View.VISIBLE);
        else
            lead.setVisibility(View.GONE);

//        if (LeaderBoardFragment.getInstance().isVisible()) {
//            Log.d(TAG, "leader_board: close");
//            lead.setSystemUiVisibility(0);
//            Fragment leaderBoardFragment = getSupportFragmentManager().findFragmentByTag("leader");
//
//            if (leaderBoardFragment != null) {
//                leader_board.beginTransaction().remove(leaderBoardFragment).commit();
//            }
//        } else {
//            Log.d(TAG, "leader_board: open");
//            lead.setSystemUiVisibility(1);
//            if (OptionFragment.getInstance().isVisible()) {
//                Log.d(TAG, "option_board: close");
//                Fragment optionBoardFragment = getSupportFragmentManager().findFragmentByTag("option");
//                if (optionBoardFragment != null) {
//                    option_board.beginTransaction().remove(optionBoardFragment).commit();
//                }
//            }
//            leader_board.beginTransaction().add(R.id.board, LeaderBoardFragment.getInstance(), "leader").commit();
//        }
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