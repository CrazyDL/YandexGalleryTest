package crazydl.gallery.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import crazydl.gallery.domain.model.Picture;

@Database(entities = {Picture.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    public abstract PictureDao pictureDao();
}
