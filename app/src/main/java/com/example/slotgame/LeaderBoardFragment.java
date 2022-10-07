package com.example.slotgame;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LeaderBoardFragment extends Fragment implements ValueEventListener {
    private static final String TAG = LeaderBoardFragment.class.getSimpleName();
    private List<leaderboard> leads = new ArrayList<leaderboard>();
    private View view;
    private static LeaderBoardFragment instance;
    public RecyclerView recycler;
    JSONObject jsonObject;
    String name;
    int score;
    Date date;

    public List<leaderboard> leaderboards;
//    private Query query;
    private DatabaseReference leaderRef;
    private FirebaseRecyclerAdapter<leaderboard, leaderboardHolder> adapter;
    private leaderboard leader;
    private ValueEventListener DataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            Log.d(TAG, "onDataChange: calvin3 " + snapshot.getValue());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    //    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
// TODO: leaderboard read database
        view = inflater.inflate(R.layout.leader_board, container, false);
//
//        leaderRef = FirebaseDatabase.getInstance().getReference().child("leaderboard");
//        Log.d(TAG, "onCreateView: " + leaderRef.getDatabase());
//        FirebaseDatabase database = leaderRef.getDatabase();
//        database.

        init(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        leaderboards = new ArrayList<leaderboard>();
//        FirebaseDatabase.getInstance().getReference("leaderboard")
//                .child("status")
//                .addValueEventListener(DataListener);
        adapter.startListening();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Log.d(TAG, "onCancelled: ");
    }

    private void init(View view) {
//        Log.d(TAG, "init: ");
        leaderRef = FirebaseDatabase.getInstance().getReference("leaderboard");
        recycler = view.findViewById(R.id.leaderboard);
//        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d(TAG, "init1: " + leaderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaderboard lead = null;
                //這可以印出資料庫完整資料
                Log.d(TAG, "onDataChange: calvin1 " + snapshot.getValue().toString());
                Log.d(TAG, "onDataChange: calvin2 " + snapshot.getChildrenCount());
                for (int i = 1; i <= snapshot.getChildrenCount(); i++) {
                    Log.d(TAG, "onDataChange: " +  snapshot.child(i + "").getValue());
                    try {
                        jsonObject = new JSONObject(snapshot.child(i + "").getValue().toString());
                        Log.d(TAG, "onDataChange: NO." + i + " name:" + jsonObject.getString("name"));
                        lead = new leaderboard(jsonObject.getString("name"),
                                jsonObject.getString("score"),
                                jsonObject.getString("date"),
                                jsonObject.getInt("rank"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    leaderboards.add(lead);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }));
        Query query = FirebaseDatabase.getInstance().getReference("leaderboard").orderByValue();
        FirebaseRecyclerOptions<leaderboard> options = new FirebaseRecyclerOptions.Builder<leaderboard>()
                .setQuery(query, leaderboard.class).build();
//        Log.d(TAG, "init2: " + options.getSnapshots());
        adapter = new FirebaseRecyclerAdapter<leaderboard, leaderboardHolder>(options) {
            @Override
            public void onBindViewHolder(@NonNull leaderboardHolder holder, int position, @NonNull leaderboard lead) {
                super.onBindViewHolder(holder, position);
                Log.d(TAG, "onBindViewHolder: calvin" + lead.getName());

//                holder.rank.setText((position + 1) + "");
                holder.date.setText(lead.getDate());
                holder.score.setText(lead.getScore() + "");
                holder.name.setText(lead.getName());
                Log.d(TAG, "onBindViewHolder: calvin" + lead.getName());
            }

            @NonNull
            @Override
            public leaderboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.leader_info, parent, false);
                return new leaderboardHolder(view);
            }
        };
        recycler.setAdapter(adapter);
//        Log.d(TAG, "init end");
    }

    class leaderboardHolder extends RecyclerView.ViewHolder {
        //        TextView rank;
        TextView name;
        TextView score;
        TextView date;

        public leaderboardHolder(@NonNull View itemView) {
            super(itemView);
//            rank = itemView.findViewById(R.id.leader_rank);
            name = itemView.findViewById(R.id.leader_name);
            score = itemView.findViewById(R.id.leader_score);
            date = itemView.findViewById(R.id.leader_date);
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        leaderboard lead = snapshot.getValue(leaderboard.class);
        Log.d(TAG, "onDataChange: " + lead.getName() + "database" + snapshot.toString());
        FirebaseDatabase.getInstance().getReference("leaderboard")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "onDataChange: calvin go here");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        Log.d(TAG, "onDataChange: ");
    }


    public static LeaderBoardFragment getInstance() {
        if (instance == null)
            instance = new LeaderBoardFragment();
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
