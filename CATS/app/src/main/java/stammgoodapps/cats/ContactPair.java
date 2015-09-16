package stammgoodapps.cats;

import android.net.Uri;

public class ContactPair {
    private String name;
    private Uri photo;

    public ContactPair(String name, Uri photo) {
        this.name = name;
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPhoto() {
        return photo;
    }

    public void setPhoto(Uri photo) {
        this.photo = photo;
    }
}
