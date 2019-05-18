package com.example.assignmentenrique.presentation.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.assignmentenrique.R;
import com.example.assignmentenrique.data.Photo;
import com.example.assignmentenrique.viewmodel.PhotoDetailViewModelFactory;
import com.example.assignmentenrique.viewmodel.PhotoDetailViewModel;

public class PhotoDetailActivity extends FragmentActivity {

    ImageView photoImageView;
    PhotoDetailViewModel photoDetailViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        photoImageView = findViewById(R.id.imageview_photo);

        Intent intent = getIntent();
        int currentID = intent.getIntExtra("CURRENT_ID", 0);

        photoDetailViewModel = ViewModelProviders.of(this, new PhotoDetailViewModelFactory(getApplication(), currentID))
                .get(PhotoDetailViewModel.class);
        photoDetailViewModel.getPhoto().observe(this, new Observer<Photo>() {
            @Override
            public void onChanged(@Nullable Photo photo) {
                Glide.with(PhotoDetailActivity.this)
                        .load(photo.getImageURL())
                        .error(R.drawable.placeholder)
                        .into(photoImageView);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: {
                handled = true;
                photoDetailViewModel.goToPreviousPhoto();
                break;
            }
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                handled = true;
                photoDetailViewModel.goToNextPhoto();
                break;
            }
        }
        return handled || super.onKeyDown(keyCode, event);
    }
}
