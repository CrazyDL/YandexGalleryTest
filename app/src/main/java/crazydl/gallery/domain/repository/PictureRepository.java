package crazydl.gallery.domain.repository;

import crazydl.gallery.domain.repository.model.Picture;
import io.reactivex.Observable;

public interface PictureRepository {
    Observable<Picture> getPictureList();
}
