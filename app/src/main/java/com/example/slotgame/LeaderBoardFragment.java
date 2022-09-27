package com.example.slotgame;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

public class LeaderBoardFragment extends Fragment {
    private static final String TAG = LeaderBoardFragment.class.getSimpleName();
    private View view;
    private static LeaderBoardFragment instance;
    public RecyclerView recycler;
    private FirebaseRecyclerAdapter<leaderboard, leaderboardHolder> adapter;

    String name;
    int score;
    Date date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
// TODO: leaderboard read database
//        view = inflater.inflate(R.layout.leader_board, container, false);
//        recycler = view.findViewById(R.id.leaderboard);
//        recycler.setHasFixedSize(true);
//        recycler.setLayoutManager(new LinearLayoutManager(LeaderBoardFragment.this.getContext()));
//        init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        adapter.startListening();
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onStop() {
        super.onStop();
//        adapter.stopListening();
        Log.d(TAG, "onStop: ");
    }

    private void init() {
        Query query = FirebaseDatabase.getInstance().getReference("leaderboard").child("leaderboard");
        FirebaseRecyclerOptions<leaderboard> data = new FirebaseRecyclerOptions.Builder<leaderboard>()
                .setQuery(query, leaderboard.class).build();
         adapter = new FirebaseRecyclerAdapter<leaderboard, leaderboardHolder>(data) {
            @Override
            public void onBindViewHolder(@NonNull leaderboardHolder holder, int position, @NonNull leaderboard lead) {
                super.onBindViewHolder(holder, position);
                holder.rank.setText(Integer.toString(position));
                holder.date.setText(lead.getDate());
                holder.score.setText(Integer.toString(lead.getScore()));
                holder.name.setText(lead.getName());
            }

            @Override
            public int getItemCount() {
                return 5;
            }

            @NonNull
            @Override
            public leaderboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.leader_info, parent, false);
                return new leaderboardHolder(view);
            }
        };
        recycler.setAdapter(adapter);
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
