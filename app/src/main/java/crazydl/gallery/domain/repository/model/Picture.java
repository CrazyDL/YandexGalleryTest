package crazydl.gallery.domain.repository.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Picture {
    @PrimaryKey
    @NonNull
    private String filePath;
    @NonNull
    private String name;
    @NonNull
    private String date;

    public Picture(@NonNull String filePath, @NonNull String name, @NonNull String date) {
        this.filePath = filePath;
        this.name = name;
        this.date = date;
    }

    public void setFilePath(@NonNull String filePath) {
        this.filePath = filePath;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getName() {
        return name;
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
