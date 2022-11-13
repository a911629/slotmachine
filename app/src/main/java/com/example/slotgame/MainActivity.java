package com.example.slotgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.common.ChangeEventType;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainActivity extends AppCompatActivity {

    private String VERSION = "Version 0.0.5";
    private static final String TAG = MainActivity.class.getSimpleName();
    private Score score = new Score();
    private TextView score_t;
    private TextView bet_t;
    private TextView Ver;
    private FragmentManager option_board;
    private FragmentManager leader_board;
    private FragmentManager list_board;
    private DialogFragment df_list = new ListDialog();
    private boolean isStarted = false;
    private Chance chance = new Chance();
    private long[] total_score = new long[5];

    private Query query;
    private DatabaseReference leaderRef;
    public RecyclerView recycler;

    public static final Random RANDOM = new Random();
    private int l_time = 0;
    private FirebaseRecyclerAdapter<leaderboard, testHolder> adapter;
    private RecyclerView test;
    private ConstraintLayout lead;
    private int RankNumber = 5;
    Bundle bundle = new Bundle();

    private SlotMachine mSlotMachine;
    private boolean mIsPlaying = false;
    private CopyOnWriteArrayList<Bitmap> bitmaps;

    private int large_score = 0;
    private long count = 5;
    private boolean RewriteLeader = false;
    private AlertDialog.Builder temp;


    public static long randomLong(long lower, long upper) {
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

        init();

        option_board = getSupportFragmentManager();
        leader_board = getSupportFragmentManager();
        list_board = getSupportFragmentManager();

        large_score = score.total();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.new_game:
                new_game(true);
                break;
            case R.id.show_leader_board:
                leader_board();
                break;
            case R.id.show_list:
                list_board();
                break;
            case R.id.quit:
                quit(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void bar(View view) {
        if (score.getBet() == 0)
            return;

        Log.d(TAG, "bar: ");
        score.setRecord(score.getBet());
        Random mRandom = new Random();

        if (score.getBet() + score.getCurrent() > large_score) {
            large_score = score.getBet() + score.getCurrent();
            Log.d(TAG, "bar: large score " + large_score);
        }

        if (score.getBet() != 0) {
            Log.d(TAG, "bar: start");
            if (mIsPlaying) {
                return;
            }
            mIsPlaying = true;

            if (mRandom.nextInt(3) == 0) {
                Log.d(TAG, "bar: 會中獎");

                int rand = mRandom.nextInt(100);

                if (rand < chance.sum(0))
                    mSlotMachine.play(1);
                else if (rand < chance.sum(1))
                    mSlotMachine.play(2);
                else if (rand < chance.sum(2))
                    mSlotMachine.play(3);
                else if (rand < chance.sum(3))
                    mSlotMachine.play(4);
                else if (rand < chance.sum(4))
                    mSlotMachine.play(5);
                else if (rand < chance.sum(5))
                    mSlotMachine.play(6);
                else if (rand < chance.sum(6))
                    mSlotMachine.play(7);

                chance.init();
            } else { //
                Log.d(TAG, "bar: 不會中獎");
                chance.add_chance();
                mSlotMachine.play(-1);
            }
        }
    }

    private void init() {
        mSlotMachine = (SlotMachine) findViewById(R.id.Slotmachine);

        bitmaps = new CopyOnWriteArrayList<Bitmap>();
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.class1));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.class2));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.class3));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.class4));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.class5));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.class6));
        bitmaps.add(BitmapFactory.decodeResource(getResources(), R.drawable.class7));

        mSlotMachine.setData(bitmaps);
        mSlotMachine.setSlotMachineListener(new SlotMachine.SlotMachineListener() {
            @Override
            public void onFinish(int pos01, int pos02, int pos03) {
                mIsPlaying = false;
                if (pos01 == pos02 && pos02 == pos03) {
                    Log.d(TAG, "onFinish: bingo pos: " + pos01 + " cur: " + score.getCurrent());
                    int temp = score.bingo(pos01);
                    if (temp > large_score) {
                        large_score = temp;
                    }
                    Log.d(TAG, "onFinish: large " + large_score);
                }
                score.clean_bet();
                Log.d(TAG, "onFinish: 轉動結束");
                if (score.getCurrent() == 0 && score.getBet() == 0) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Game over")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new_game(true);
                                }
                            }).show();
                }
                refresh_score();
            }

            @Override
            public boolean acceptWinResult(int position) {
                return true;
            }
        });
    }

    public void find_view() {
        final DatabaseReference[] tempRef = new DatabaseReference[1];
        score_t = findViewById(R.id.score);
        bet_t = findViewById(R.id.bet);
        Ver = findViewById(R.id.version);
        Ver.setText(VERSION);

        lead = findViewById(R.id.lead_board);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setReverseLayout(true);

        test = findViewById(R.id.test);
        test.setHasFixedSize(true);
        test.setLayoutManager(lm);

        FirebaseDatabase.getInstance().getReference("leaderboard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() <= 5)
                    count = snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query query = FirebaseDatabase.getInstance().getReference("leaderboard").orderByChild("score").limitToLast(5);

        FirebaseRecyclerOptions<leaderboard> options = new FirebaseRecyclerOptions.Builder<leaderboard>()
                .setQuery(query, leaderboard.class).build();
        adapter = new FirebaseRecyclerAdapter<leaderboard, testHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull testHolder holder, int position, @NonNull leaderboard model) {
                holder.name.setText(model.getName());
                holder.date.setText(model.getDate());
                holder.score.setText(Integer.toString(model.getScore()));
                holder.rank.setText(Integer.toString((int) (count - position)));
            }

            @NonNull
            @Override
            public testHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.leader_info, parent, false);
                return new testHolder(view);
            }

            @Override
            public void onChildChanged(@NonNull ChangeEventType type, @NonNull DataSnapshot snapshot, int newIndex, int oldIndex) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex);
                adapter.notifyDataSetChanged();
            }
        };
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
        if (score.getBet() == 50 || score.getCurrent() == 0)
            return;
        if (score.getCurrent() < 5)
            score.compute(score.getCurrent());
        else if (score.getBet() > 45) {
            score.compute(50 - score.getBet());
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

    public void new_game(boolean save) {
        Log.d(TAG, "reset_game: ");
        if (save == true)
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("New Game")
                    .setMessage("Do you want to save score to leaderboard ?")
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SaveLeaderBoard();
                        }
                    })
                    .setPositiveButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        refresh_score();
        score.init_score();
        large_score = 100;
        refresh_score();
    }

    public void option_board() {
        if (OptionFragment.getInstance().isVisible()) {
            Log.d(TAG, "option_board: close");
            Fragment optionBoardFragment = getSupportFragmentManager().findFragmentByTag("option");
            if (optionBoardFragment != null) {
                option_board.beginTransaction().remove(optionBoardFragment).commitAllowingStateLoss();
            }
        } else {
            Log.d(TAG, "option_board: open");
            option_board.beginTransaction().add(R.id.board, OptionFragment.getInstance(), "option").commitNowAllowingStateLoss();
        }
    }

    public void leader_board() {
        if (lead.getVisibility() == View.GONE) {
            Log.d(TAG, "leader_board: lead open");
            lead.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "leader_board: lead close");
            lead.setVisibility(View.GONE);
        }
        if (OptionFragment.getInstance().isVisible()) {
            Log.d(TAG, "option_board: close");
            Fragment optionBoardFragment = getSupportFragmentManager().findFragmentByTag("option");
            if (optionBoardFragment != null) {
                option_board.beginTransaction().remove(optionBoardFragment).commit();
            }
        }
    }

    public void list_board() {
        if (lead.getVisibility() == View.VISIBLE)
            lead.setVisibility(View.GONE);
        else {
            Log.d(TAG, "list: open list");
            if (OptionFragment.getInstance().isVisible()) {
                Log.d(TAG, "option_board: close");
                Fragment optionBoardFragment = getSupportFragmentManager().findFragmentByTag("option");
                if (optionBoardFragment != null) {
                    option_board.beginTransaction().remove(optionBoardFragment).commit();
                }
            }
            df_list.show(list_board, "list");
        }

    }

    public void SaveLeaderBoard(View view) {
        DatabaseReference leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard").push();
        Date date = new Date(System.currentTimeMillis());
        leaderboard leaderboard = new leaderboard();
        final EditText titleEdit = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Enter name")
                .setView(titleEdit)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = titleEdit.getText().toString();
                        leaderboard add = new leaderboard(name, large_score, new SimpleDateFormat("yyyy-MM-dd").format(date));
                        leaderboardRef.setValue(add);
                    }
                }).setPositiveButton("Cancel", null)
                .show();
    }

    public void SaveLeaderBoard() {
        DatabaseReference leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard").push();
        Date date = new Date(System.currentTimeMillis());
        leaderboard leaderboard = new leaderboard();
        final EditText titleEdit = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Enter name")
                .setView(titleEdit)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = titleEdit.getText().toString();
                        leaderboard add = new leaderboard(name, large_score, new SimpleDateFormat("yyyy-MM-dd").format(date));
                        leaderboardRef.setValue(add);
                    }
                }).setPositiveButton("Cancel", null)
                .show();
    }

    public void ReadLeaderBoard() {
        DatabaseReference leader[] = new DatabaseReference[5];
        for (int i = 1; i <= 5; i++) {
            leader[i - 1] = (DatabaseReference) FirebaseDatabase.getInstance()
                    .getReference("leaderboard")
                    .child(Integer.toString(i));
        }

        leader[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total_score[0] = Long.parseLong(snapshot.child("score").getValue().toString(), 10);
                Log.d(TAG, "onDataChange: 0 | " + total_score[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        leader[1].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total_score[1] = Long.parseLong(snapshot.child("score").getValue().toString(), 10);
                Log.d(TAG, "onDataChange: 1 | " + total_score[1]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        leader[2].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total_score[2] = Long.parseLong(snapshot.child("score").getValue().toString(), 10);
                Log.d(TAG, "onDataChange: 2 | " + total_score[2]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        leader[3].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total_score[3] = Long.parseLong(snapshot.child("score").getValue().toString(), 10);
                Log.d(TAG, "onDataChange: 3 | " + total_score[3]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        leader[4].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total_score[4] = Long.parseLong(snapshot.child("score").getValue().toString(), 10);
                Log.d(TAG, "onDataChange: 4 | " + total_score[4]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void quit(boolean t) {
        final boolean[] q = {false};
        Log.d(TAG, "quit: go");

        temp = new AlertDialog.Builder(MainActivity.this);
        temp.setTitle("Quit Game");
        temp.setMessage("Do you want to save score to leaderboard ?");
        temp.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveLeaderBoard();
                q[0] = true;
            }
        });
        temp.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                q[0] = true;
            }
        });
        temp.create().show();

        Log.d(TAG, "quit: 有欸 " + q[0]);
        if (q[0] == true) {
            Log.d(TAG, "quit: really quit");
        }
    }

    @Override
    protected void onDestroy() {
        if (temp != null) {
            temp.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {

                }
            });
        }

        super.onDestroy();
    }
}
