package com.example.assignmentenrique.presentation.fragment;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.SearchSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.ObjectAdapter;

import com.example.assignmentenrique.helper.Constants;
import com.example.assignmentenrique.viewmodel.SearchViewModel;

public class CustomSearchFragment extends SearchSupportFragment
        implements SearchSupportFragment.SearchResultProvider {

    private SearchViewModel searchViewModel;
    private String query;
    private ArrayObjectAdapter adapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        searchViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);

        searchViewModel.getPages().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer == null) {
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(Constants.QUERY, query);
                intent.putExtra(Constants.PAGES, integer);
                if (integer.equals(SearchViewModel.SEARCH_FAILED_PAGES)) {
                    intent.putExtra(Constants.RESULT, false);
                }else {
                    intent.putExtra(Constants.RESULT, true);
                }

                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new ArrayObjectAdapter(new ListRowPresenter());

        setSearchResultProvider(this);
    }

    @Override
    public ObjectAdapter getResultsAdapter() {
        return adapter;
    }

    @Override
    public boolean onQueryTextChange(String newQuery) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(this.query != null) {
            return false;
        }
        this.query = query;
        searchViewModel.searchPhotos(query);
        return false;
    }
}
