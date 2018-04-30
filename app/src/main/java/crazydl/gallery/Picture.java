package crazydl.gallery;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

@Entity
public class Picture {
    @PrimaryKey
    @NonNull
    private String fileName;
    @NonNull
    private String date;

    Picture(@NonNull String fileName, @NonNull String date) {
        this.fileName = fileName;
        this.date = date;
    }

    public void setFileName(@NonNull String fileName) {
        this.fileName = fileName;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDate() {
        return date;
    }
}
