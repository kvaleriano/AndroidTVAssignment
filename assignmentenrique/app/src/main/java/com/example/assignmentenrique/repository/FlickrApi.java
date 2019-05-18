package com.example.assignmentenrique.repository;

import com.example.assignmentenrique.data.FlickrFeedResult;
import com.example.assignmentenrique.data.FlickrResult;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlickrApi {
    @GET("feeds/photos_public.gne")
    public Call<FlickrFeedResult> feedPhotos(@Query("format") String format, @Query("nojsoncallback") String noJsonCallback);

    @GET("rest/")
    public Call<FlickrResult> getPhotos(@Query("method") String method, @Query("api_key") String apiKey, @Query("text") String text, @Query("per_page")String perPage,
                          @Query("format") String format, @Query("extras") String extras, @Query("content_type") String contentType, @Query("nojsoncallback") String noJsonCallback,
                                        @Query("page") String page);
}
