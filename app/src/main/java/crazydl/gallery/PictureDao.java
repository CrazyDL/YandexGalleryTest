package crazydl.gallery;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PictureDao {
    @Query("SELECT * FROM picture")
    List<Picture> getAll();

    @Query("SELECT * FROM picture WHERE filePath = :path")
    Picture getByFilePath(String path);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Picture picture);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Picture> pictures);

    @Update
    void update(Picture picture);

    @Delete
    void delete(Picture picture);

    @Query("DELETE FROM picture")
    public void nukeTable();
}
