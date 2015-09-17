package stammgoodapps.cats;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class ListViewLoader extends Activity {

    static final String[] PROJECTION = new String[]{
            ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI};

    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        final String TAG = "onCreate";

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        progressBar.setIndeterminate(true);
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);

        Cursor contactIdCursor =
                this.getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI,
                        PROJECTION,
                        SELECTION,
                        null,
                        null);

        try {
            if (contactIdCursor.getCount() > 0) {
                List<ContactPair> contactPair = new ArrayList<>();
                while (contactIdCursor.moveToNext()) {
                    String contactName = contactIdCursor.getString(
                            contactIdCursor.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME));
                    Uri contactPhotoUri = Uri.parse(
                            contactIdCursor.getString(
                                    contactIdCursor.getColumnIndex(
                                            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
                    contactPair.add(new ContactPair(contactName, contactPhotoUri));
                }

                ListViewAdapter adapter = new ListViewAdapter(this, contactPair.toArray(new ContactPair[contactPair.size()]));
                ListView listView = (ListView) findViewById(R.id.contact_list);
                listView.setAdapter(adapter);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            contactIdCursor.close();
            root.removeView(progressBar);
        }
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // Do something when a list item is clicked
//        l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//    }
}