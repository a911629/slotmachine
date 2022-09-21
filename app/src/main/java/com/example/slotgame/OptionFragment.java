package com.example.slotgame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OptionFragment extends Fragment {
    private static OptionFragment instance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.option, container, false);
    }

    public static OptionFragment getInstance() {
        if (instance == null)
            instance = new OptionFragment();
        return instance;
    }
}
