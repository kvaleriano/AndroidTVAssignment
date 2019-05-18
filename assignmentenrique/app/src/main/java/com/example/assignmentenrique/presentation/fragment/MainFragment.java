package com.example.assignmentenrique.presentation.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.VerticalGridSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.assignmentenrique.widget.CustomTitleView;
import com.example.assignmentenrique.R;
import com.example.assignmentenrique.data.Photo;
import com.example.assignmentenrique.helper.Constants;
import com.example.assignmentenrique.presentation.activity.PhotoDetailActivity;
import com.example.assignmentenrique.presentation.activity.SearchActivity;
import com.example.assignmentenrique.viewmodel.MainViewModel;

import java.lang.reflect.Field;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends VerticalGridSupportFragment {

    private MainViewModel mainViewModel;
    private ArrayObjectAdapter adapter;
    private boolean loadingNewPage = false;
    private static final int SEARCH_REQUEST_CODE = 1;
    private static final int NUMBER_COLUMNS = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUMBER_COLUMNS);
        setGridPresenter(gridPresenter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setTitle(getString(R.string.photo_search));
        ((CustomTitleView)getTitleView()).setSubtitle(getString(R.string.trending_now));

        adapter = new ArrayObjectAdapter(new PhotosPresenter());
        setAdapter(adapter);

        mainViewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);

        //We make sure the database is cleared in order to refresh the data on new sessions.
        mainViewModel.getClearedDatabase().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean == null) {
                    return;
                }
                if(aBoolean) {
                    mainViewModel.getClearedDatabase().removeObservers(getViewLifecycleOwner());
                    mainViewModel.getPhotos().observe(getViewLifecycleOwner(), new Observer<List<Photo>>() {
                        @Override
                        public void onChanged(@Nullable List<Photo> flickrPhotos) {
                            loadingNewPage = false;
                            if(adapter.size() == 0) {
                                adapter.addAll(0, flickrPhotos);
                                return;
                            }
                            Photo initialPhoto = (Photo)adapter.get(0);
                            Photo lastPhoto = (Photo)adapter.get(adapter.size() - 1);
                            for(Photo photo : flickrPhotos) {
                                if(photo.getId() < lastPhoto.getId()) {
                                    adapter.remove(photo);
                                }
                                if(photo.getId() > lastPhoto.getId()) {
                                    adapter.add(photo);
                                }
                            }
                        }
                    });
                }
            }
        });

        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivityForResult(intent, SEARCH_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        addGridScrollListener();
    }

    private void addGridScrollListener() {
        try {
            Class<VerticalGridSupportFragment> VerticalGridFragmentClass = VerticalGridSupportFragment.class;
            Field verticalGridViewHolder = VerticalGridFragmentClass.getDeclaredField("mGridViewHolder");
            verticalGridViewHolder.setAccessible(true);
            VerticalGridPresenter.ViewHolder viewHolder = (VerticalGridPresenter.ViewHolder) verticalGridViewHolder.get(this);
            VerticalGridView gridView = viewHolder.getGridView();
            ViewParent parent = gridView.getParent();
            gridView.addOnScrollListener(scrollListener);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                return;
            }

            //get current last child View
            View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager().getChildCount() - 1);
            //get the bottom
            int lastChildBottom = lastChildView.getBottom();
            // get recyclerView's bottom
            int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
            //get last childview's position
            int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);


            if (lastChildBottom == recyclerBottom && lastPosition == recyclerView.getLayoutManager().getItemCount() - 1 && !loadingNewPage) {
                loadingNewPage = true;
                mainViewModel.getMorePhotos();
                // yes to bottom.
            }
        }
    };

    private class PhotosPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            return new ViewHolder(new ImageCardView(parent.getContext()));
        }
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {

            final Photo photo = (Photo) item;

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), PhotoDetailActivity.class);
                    intent.putExtra("CURRENT_ID", photo.getId());
                    view.getContext().startActivity(intent);
                }
            });

            final ImageCardView view = (ImageCardView) viewHolder.view;
            view.setCardType(BaseCardView.CARD_TYPE_INFO_OVER);
            view.setInfoAreaBackground(getResources().getDrawable(R.drawable.bg_black_gradient));

            ((TextView) view.findViewById(R.id.content_text)).setTextColor(getResources().getColor(R.color.white));

            Display display = getActivity().getWindowManager().getDefaultDisplay();
            final Point size = new Point();
            display.getSize(size);

            view.setMainImageScaleType(ImageView.ScaleType.FIT_CENTER);
            view.getMainImageView().setBackgroundColor(getResources().getColor(R.color.black));
            view.setMainImage(getResources().getDrawable(R.drawable.placeholder));
            int newWidth = (size.x - 100) / NUMBER_COLUMNS;
            view.setMainImageDimensions(newWidth, 300);

            view.setTitleText(photo.getTitle());
            view.setContentText(photo.getSubtitle());
            Glide.with(view.getContext())
                    .load(photo.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.placeholder)
                    .into(view.getMainImageView());
        }
        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK) {
            adapter.clear();
            String query = data.getStringExtra(Constants.QUERY);
            boolean result = data.getBooleanExtra(Constants.RESULT, false);
            int pages = data.getIntExtra(Constants.PAGES, -1);

            if(result) {
                mainViewModel.startHandlingSearch(query, pages);
                ((CustomTitleView)getTitleView()).setSubtitle(getString(R.string.search_results) + " \"" + query + "\"");
            }else {
                ((CustomTitleView)getTitleView()).setSubtitle(getString(R.string.no_search_results) + " \"" + query + "\"");
            }
        }
    }
}