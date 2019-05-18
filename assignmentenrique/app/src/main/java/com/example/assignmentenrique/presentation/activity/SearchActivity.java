package com.example.assignmentenrique.presentation.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.example.assignmentenrique.R;
import com.example.assignmentenrique.viewmodel.SearchViewModel;

public class SearchActivity extends FragmentActivity {

    private SearchViewModel searchViewModel;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressBar = findViewById(R.id.progressbar);

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);

        searchViewModel.getShowLoad().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean == null) {
                    return;
                }
                if(aBoolean) {
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
