package com.example.assignmentenrique.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.assignmentenrique.data.FlickrPhoto;
import com.example.assignmentenrique.data.FlickrResult;
import com.example.assignmentenrique.data.Photo;
import com.example.assignmentenrique.model.AssignmentDatabase;
import com.example.assignmentenrique.repository.FlickrRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends AndroidViewModel {

    public static final Integer SEARCH_FAILED_PAGES = -1;
    private MutableLiveData<Integer> pages = new MutableLiveData<>();
    private MutableLiveData<Boolean> showLoad = new MutableLiveData<>();
    private FlickrRepository repository = new FlickrRepository();

    public SearchViewModel(@NonNull Application application) {
        super(application);
        showLoad.setValue(false);
    }

    public LiveData<Integer> getPages() {
        return pages;
    }

    public LiveData<Boolean> getShowLoad() {
        return showLoad;
    }

    public void searchPhotos(final String query) {
        showLoad.setValue(true);
        repository.searchPhotos(query, 1, new Callback<FlickrResult>() {
            @Override
            public void onResponse(Call<FlickrResult> call, final Response<FlickrResult> response) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {

                        AssignmentDatabase.getInstance(getApplication()).photoDao().deleteAll();

                        if (response.body() == null) {
                            pages.postValue(SEARCH_FAILED_PAGES);
                            return;
                        }

                        List<FlickrPhoto> flickrPhotos = response.body().photos.photo;
                        if(flickrPhotos.size() == 0) {
                            pages.postValue(SEARCH_FAILED_PAGES);
                            return;
                        }

                        for(FlickrPhoto flickrPhoto : flickrPhotos) {
                            Photo photo = Photo.fromFlickrPhoto(flickrPhoto);
                            AssignmentDatabase.getInstance(getApplication()).photoDao().insertPhoto(photo);
                        }

                        pages.postValue(Integer.valueOf(response.body().photos.pages));
                    }
                });
            }

            @Override
            public void onFailure(Call<FlickrResult> call, Throwable t) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        AssignmentDatabase.getInstance(getApplication()).photoDao().deleteAll();
                        pages.postValue(SEARCH_FAILED_PAGES);
                    }
                });
                t.printStackTrace();
            }
        });
    }
}