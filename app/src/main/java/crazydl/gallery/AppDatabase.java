package crazydl.gallery;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Picture.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract PictureDao pictureDao();
}
