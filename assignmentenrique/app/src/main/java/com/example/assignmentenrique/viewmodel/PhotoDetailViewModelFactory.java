package com.example.assignmentenrique.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class PhotoDetailViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private Integer currentID;


    public PhotoDetailViewModelFactory(Application application, Integer currentID) {
        this.application = application;
        this.currentID = currentID;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new PhotoDetailViewModel(application, currentID);
    }
}
