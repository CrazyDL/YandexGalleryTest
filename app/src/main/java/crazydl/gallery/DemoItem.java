package crazydl.gallery;


public class DemoItem {
    public static final int VISIBLE = 0;
    public static final int INVISIBLE = 1;
    public static final int HIDE = 2;
    private int dateStatus;
    private final String date;
    private final Integer imageId;

    DemoItem(String date, Integer imageId) {
        this.date = date;
        this.imageId = imageId;
        dateStatus = VISIBLE;
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

    /*public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DemoItem demoItem = (DemoItem) o;
        return imageId.equals(demoItem.imageId) && date.equals(demoItem.date);

    }*/

    @Override
    public int hashCode() {
        return imageId.hashCode();
    }
}
