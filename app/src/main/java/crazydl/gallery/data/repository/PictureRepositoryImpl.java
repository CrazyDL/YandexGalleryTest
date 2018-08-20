package crazydl.gallery.data.repository;

import com.yandex.disk.rest.json.Resource;

import java.util.ArrayList;

import crazydl.gallery.Utils;
import crazydl.gallery.data.ResourceToPictureConverter;
import crazydl.gallery.data.YandexDiskApiMapper;
import crazydl.gallery.domain.model.Picture;
import crazydl.gallery.domain.repository.PictureRepository;
import io.reactivex.Observable;


public class PictureRepositoryImpl implements PictureRepository {

    private YandexDiskApiMapper mYandexDiskApiMapper;

    public PictureRepositoryImpl(YandexDiskApiMapper yandexDiskApiMapper) {
        mYandexDiskApiMapper = yandexDiskApiMapper;
    }

    @Override
    public Observable<Picture> getPictureList() {
        return Observable.create(emitter -> {
            ArrayList<Resource> resources = mYandexDiskApiMapper.getPictureList(Utils.PUBLIC_FOLDER_URL);
            for (Resource resource : resources){
                String filePath = mYandexDiskApiMapper.downloadPictureInCache(resource);
                if(filePath != null){
                    emitter.onNext(ResourceToPictureConverter.convertToPicture(resource, filePath));
                }
            }
            emitter.onComplete();
        });
    }
}
