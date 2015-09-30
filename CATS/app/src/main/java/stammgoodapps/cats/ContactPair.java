package stammgoodapps.cats;

import android.net.Uri;

public class ContactPair {
    private String name;
    private Uri photo;
    private long id;
    private boolean checked;

    public ContactPair(String name, Uri photo, long id, boolean checked) {
        this.name = name;
        this.photo = photo;
        this.id = id;
        this.checked = checked;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
