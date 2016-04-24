package stammgoodapps.cats;

import android.graphics.Bitmap;

public class CatOfDay {
    Bitmap bitmap;
    String description;
    String id;
    String name;
    String url;

    public CatOfDay(Bitmap bitmap, String id, String name, String url, String description) {
        this.bitmap = bitmap;
        this.description = description;
        this.id = id;
        this.name = name;
        this.url = url;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getBitmap() {

        return bitmap;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
