package com.example.assignmentenrique.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.assignmentenrique.data.FlickrFeedItem;
import com.example.assignmentenrique.data.FlickrFeedResult;
import com.example.assignmentenrique.data.FlickrPhoto;
import com.example.assignmentenrique.data.FlickrResult;
import com.example.assignmentenrique.data.Photo;
import com.example.assignmentenrique.model.AssignmentDatabase;
import com.example.assignmentenrique.repository.FlickrRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<Photo>> photos;
    private MutableLiveData<Boolean> showLoad = new MutableLiveData<>();
    private MutableLiveData<Boolean> clearedDatabase = new MutableLiveData<>();
    private FlickrRepository repository = new FlickrRepository();
    private int pages;
    private int currentPage = 0;
    private String query;

    public MainViewModel(@NonNull Application application) {
        super(application);
        photos = AssignmentDatabase.getInstance(application).photoDao().fetchAllPhotos();
        showLoad.setValue(true);
        clearedDatabase.setValue(false);
        getFeedPhotos(true);
    }

    public LiveData<List<Photo>> getPhotos() {
        return photos;
    }

    public LiveData<Boolean> getShowLoad() {
        return showLoad;
    }

    public LiveData<Boolean> getClearedDatabase() {
        return clearedDatabase;
    }

    public void getMorePhotos() {
        if (query == null) {
            showLoad.setValue(true);
            getFeedPhotos(false);
            return;
        }

        if(pages == currentPage) {
            return;
        }

        showLoad.setValue(true);
        currentPage++;
        searchPhotos(currentPage);
    }

    public void startHandlingSearch(String query, int pages) {
        this.query = query;
        this.pages = pages;
        currentPage = 1;
    }

    private void searchPhotos(int page) {
        repository.searchPhotos(query, page, new Callback<FlickrResult>() {
            @Override
            public void onResponse(Call<FlickrResult> call, final Response<FlickrResult> response) {

                if (response.body() == null) {
                    showLoad.setValue(false);
                    return;
                }

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {

                        for(FlickrPhoto flickrPhoto : response.body().photos.photo) {
                            Photo photo = Photo.fromFlickrPhoto(flickrPhoto);
                            AssignmentDatabase.getInstance(getApplication()).photoDao().insertPhoto(photo);
                        }

                        showLoad.postValue(false);
                    }
                });
            }

            @Override
            public void onFailure(Call<FlickrResult> call, Throwable t) {
                t.printStackTrace();
                showLoad.postValue(false);
            }
        });
    }

    private void getFeedPhotos(final boolean clearDatabase) {

        repository.getFeedPhotos(new Callback<FlickrFeedResult>() {
            @Override
            public void onResponse(Call<FlickrFeedResult> call, final Response<FlickrFeedResult> response) {
                if (response.body() == null) {
                    showLoad.setValue(false);
                    return;
                }

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {

                        if (clearDatabase) {
                            AssignmentDatabase.getInstance(getApplication()).photoDao().deleteAll();
                        }

                        for(FlickrFeedItem flickrFeedItem :response.body().items) {
                            Photo photo = Photo.fromFlickrFeedItem(flickrFeedItem);
                            AssignmentDatabase.getInstance(getApplication()).photoDao().insertPhoto(photo);
                        }

                        showLoad.postValue(false);
                        if(clearDatabase) {
                            clearedDatabase.postValue(true);
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<FlickrFeedResult> call, Throwable t) {
                t.printStackTrace();
                showLoad.postValue(false);
            }
        });
    }
}
