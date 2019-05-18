package com.example.assignmentenrique.repository;

import com.example.assignmentenrique.data.FlickrFeedResult;
import com.example.assignmentenrique.data.FlickrResult;
import com.example.assignmentenrique.helper.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FlickrRepository {

    private FlickrApi flickrApi;

    public FlickrRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.FLICKR_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        flickrApi = retrofit.create(FlickrApi.class);
    }

    public void searchPhotos(String query, int page, Callback<FlickrResult> callback) {
        Call<FlickrResult> call = flickrApi.getPhotos(Constants.FLICKR_SEARCH, Constants.FLICKR_KEY, query, "20", "json",
                "url_l,url_o,owner_name,date_upload", "1", "1", String.valueOf(page));
        call.enqueue(callback);
    }

    public void getFeedPhotos(Callback<FlickrFeedResult> callback) {
        Call<FlickrFeedResult> call = flickrApi.feedPhotos("json", "1");
        call.enqueue(callback);
    }
}
