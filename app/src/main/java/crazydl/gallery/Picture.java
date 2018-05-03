package crazydl.gallery;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Picture {
    @PrimaryKey
    @NonNull
    private String filePath;
    @NonNull
    private String date;

    Picture(@NonNull String filePath, @NonNull String date) {
        this.filePath = filePath;
        this.date = date;
    }

    public void setFilePath(@NonNull String filePath) {
        this.filePath = filePath;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getFilePath() {
        return filePath;
    }

    @NonNull
    public String getDate() {
        return date;
    }
}
