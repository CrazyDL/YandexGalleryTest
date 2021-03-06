package crazydl.gallery.domain.interactor;

import crazydl.gallery.domain.repository.PictureRepository;
import crazydl.gallery.domain.model.Picture;
import io.reactivex.Observable;


public class PictureInteractor implements Interactor {

    private PictureRepository pictureRepository;

    public PictureInteractor(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public Observable<Picture> getPictureList(){
        return pictureRepository.getPictureList();
    }
}
