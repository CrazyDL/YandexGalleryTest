package crazydl.gallery;

public class PictureItem {
    private int dateStatus;
    private final String date;
    private final Integer imageId;

    PictureItem(String date, Integer imageId) {
        this.date = date;
        this.imageId = imageId;
    }
    String getDate() {
        return date;
    }

    public Integer getImageId() {
        return imageId;
    }

    public int getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(int dateStatus) {
        this.dateStatus = dateStatus;
    }

    @Override
    public int hashCode() {
        return imageId.hashCode();
    }
}
