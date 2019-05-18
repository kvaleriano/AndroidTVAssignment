package com.example.assignmentenrique.model;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.assignmentenrique.data.Photo;

import java.util.List;

@Dao
public interface PhotoDao {

    @Insert
    Long insertPhoto(Photo photo);

    @Query("SELECT * FROM Photo")
    LiveData<List<Photo>> fetchAllPhotos();

    @Query("SELECT * FROM Photo WHERE id =:photoId")
    Photo getPhoto(int photoId);

    @Query("DELETE from Photo")
    void deleteAll();
}
