package com.example.slotgame;

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
    private Query query;
    private FirebaseRecyclerOptions<leaderboard> data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
// TODO: leaderboard read database
        view = inflater.inflate(R.layout.leader_board, container, false);
        recycler = view.findViewById(R.id.leaderboard);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(LeaderBoardFragment.this.getContext()));

//        Query query = FirebaseDatabase.getInstance().getReference("users");

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
        query = FirebaseDatabase.getInstance().getReference("leaderboard").orderByKey();
        data = new FirebaseRecyclerOptions.Builder<leaderboard>()
                .setQuery(query, leaderboard.class).build();
        System.out.print("start");
        System.out.print(data);
        System.out.print("end");
//        Log.d(TAG, "init: data",  data);
        adapter = new FirebaseRecyclerAdapter<leaderboard, leaderboardHolder>(data) {
            @Override
            public void onBindViewHolder(@NonNull leaderboardHolder holder, int position, @NonNull leaderboard lead) {
                super.onBindViewHolder(holder, position);
//                holder.rank.setText(position + "");
                holder.date.setText(lead.getDate());
                holder.score.setText(lead.getScore() + "");
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

//    class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.LeaderHolder> {
//
//
//        @NonNull
//        @Override
//        public LeaderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view =getLayoutInflater().inflate(R.layout.leader_board, parent, false);
//            return new LeaderHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull LeaderHolder holder, int position) {
//            holder.rank.setText(position+1);
//            holder.name.setText();
//        }
//
//        @Override
//        public int getItemCount() {
//            return 0;
//        }
//
//        class LeaderHolder extends RecyclerView.ViewHolder {
//            TextView rank;
//            TextView name;
//            TextView score;
//            TextView date;
//
//            public LeaderHolder(@NonNull View itemView) {
//                super(itemView);
//                this.rank = itemView.findViewById(R.id.leader_rank);
//                this.name = itemView.findViewById(R.id.leader_name);
//                this.score = itemView.findViewById(R.id.leader_score);
//                this.date = itemView.findViewById(R.id.leader_date);
//            }
//        }
//    }
//}

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
