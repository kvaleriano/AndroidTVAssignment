package com.example.assignmentenrique.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.assignmentenrique.data.Photo;
import com.example.assignmentenrique.model.AssignmentDatabase;

public class PhotoDetailViewModel extends AndroidViewModel {

    private MutableLiveData<Photo> photo;

    public PhotoDetailViewModel(@NonNull Application application, final Integer currentID) {
        super(application);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Photo currentPhoto = AssignmentDatabase.getInstance(getApplication()).photoDao().getPhoto(currentID);
                getPhoto(); //Make sure photo is initialized
                photo.postValue(currentPhoto);
            }
        });
    }

    public LiveData<Photo> getPhoto() {
        if (photo == null) {
            photo = new MutableLiveData<>();
        }
        return photo;
    }

    public void goToNextPhoto() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Photo currentPhoto = photo.getValue();
                Photo newPhoto = AssignmentDatabase.getInstance(getApplication()).photoDao().getPhoto(currentPhoto.getId() + 1);
                if (newPhoto == null) {
                    return;
                }
                photo.postValue(newPhoto);
            }
        });
    }

    public void goToPreviousPhoto() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Photo currentPhoto = photo.getValue();
                Photo newPhoto = AssignmentDatabase.getInstance(getApplication()).photoDao().getPhoto(currentPhoto.getId() - 1);
                if (newPhoto == null) {
                    return;
                }
                photo.postValue(newPhoto);
            }
        });
    }
}
