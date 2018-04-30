package crazydl.gallery;


import android.arch.persistence.room.Entity;

public class Picture {
    private final String date;
    private final Integer imageId;

    Picture(String date, Integer imageId) {
        this.date = date;
        this.imageId = imageId;
    }

    String getDate() {
        return date;
    }

    public Integer getImageId() {
        return imageId;
    }

}
