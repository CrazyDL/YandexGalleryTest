package crazydl.gallery.data;

import com.yandex.disk.rest.json.Resource;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import crazydl.gallery.domain.repository.model.Picture;

public class ResourceToPictureConverter {
    private static DateFormat df = new SimpleDateFormat("d MMM", Locale.US);

    public static Picture convertToPicture(Resource resource, String cacheFilePath) {
        return new Picture(cacheFilePath, resource.getName(), df.format(resource.getCreated()));
    }
}
