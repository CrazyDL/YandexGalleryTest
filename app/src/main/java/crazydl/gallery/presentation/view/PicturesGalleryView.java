package crazydl.gallery.presentation.view;

import com.arellomobile.mvp.MvpView;

import java.util.List;

import crazydl.gallery.domain.model.Picture;

public interface PicturesGalleryView extends MvpView {

    /**
     * Показать статус обновления
     */
    void showRefreshing();

    /**
     * Скрыть статус обновления
     */
    void hideRefreshing();

    /**
     * Добавить картинку в конец адаптера
     *
     * @param picture картика {@link Picture}
     */
    void addPictureToTail(Picture picture);

    /**
     * Добавить список картинок в конец адаптера
     *
     * @param pictures список картинок {@link Picture}
     */
    void addPicturesToTail(List<Picture> pictures);

    /**
     * Открыть картинку в полноэкранном режиме
     */
    void openFullScreenPicture();
}
