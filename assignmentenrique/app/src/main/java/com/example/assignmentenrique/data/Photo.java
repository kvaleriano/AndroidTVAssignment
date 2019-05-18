package com.example.assignmentenrique.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Entity
public class Photo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo
    private String title;
    @ColumnInfo
    private String subtitle;
    @ColumnInfo
    private String imageURL;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public static Photo fromFlickrPhoto(FlickrPhoto flickrPhoto) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yyyy");

        Photo photo = new Photo();
        Date date = new Date(Long.parseLong(flickrPhoto.dateupload) * 1000);
        if(flickrPhoto.url_l != null) {
            photo.setImageURL(flickrPhoto.url_l);
        }else {
            photo.setImageURL(flickrPhoto.url_o);
        }
        photo.setTitle(flickrPhoto.title);
        photo.setSubtitle(flickrPhoto.ownername + " / " + simpleDateFormat.format(date));
        return photo;
    }

    public static Photo fromFlickrFeedItem(FlickrFeedItem flickrFeedItem) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM d yyyy");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));

        Photo photo = new Photo();
        photo.setTitle(flickrFeedItem.title);
        photo.setImageURL(flickrFeedItem.media.url);
        Date date;
        try {
            date = simpleDateFormat2.parse(flickrFeedItem.published);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        int initialParenthesisIndex = flickrFeedItem.author.indexOf("(\"");
        int lastParenthesisIndex = flickrFeedItem.author.indexOf("\")");
        photo.setSubtitle(flickrFeedItem.author.substring(initialParenthesisIndex + 2, lastParenthesisIndex) + " / " + simpleDateFormat.format(date));
        return photo;
    }
}
