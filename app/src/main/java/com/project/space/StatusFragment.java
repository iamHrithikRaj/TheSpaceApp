package com.project.space;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.zip.Inflater;

public class StatusFragment extends Fragment {

    public static StatusFragment newInstance(){
        StatusFragment fragment = new StatusFragment();
        //new MainActivity().onLocationChanged();
        return fragment;
    }

}
