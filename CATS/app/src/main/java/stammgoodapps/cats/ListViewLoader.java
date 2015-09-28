package stammgoodapps.cats;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class ListViewLoader extends Activity {

    private Button cancelButton;
    private Button proceedButton;

    static final String[] PROJECTION = new String[]{
            ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI};

    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

    static final String SORT_ORDER = ContactsContract.Data.DISPLAY_NAME;

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
                        SORT_ORDER);

        try {
            if (contactIdCursor.getCount() > 0) {
                List<ContactPair> contactPair = new ArrayList<>();
                while (contactIdCursor.moveToNext()) {
                    String contactName = contactIdCursor.getString(
                            contactIdCursor.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME));
                    String contactPhotoString = contactIdCursor.getString(
                            contactIdCursor.getColumnIndex(
                                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    if (contactPhotoString != null) {
                        contactPair.add(new ContactPair(contactName, Uri.parse(contactPhotoString)));
                    } else {
                        contactPair.add(new ContactPair(contactName, null));
                    }
                }

                ListViewAdapter adapter = new ListViewAdapter(this, contactPair.toArray(new ContactPair[contactPair.size()]));
                ListView listView = (ListView) findViewById(R.id.contact_list);
                listView.setAdapter(adapter);
            }

        } catch (Exception e) {
            Log.e(TAG, "Threw exception: " + e.getMessage());
        } finally {
            contactIdCursor.close();
            root.removeView(progressBar);
            addCancelButton();
        }
    }

    public void addCancelButton() {
        cancelButton = (Button) findViewById(R.id.cancel_selection);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // Do something when a list item is clicked
//        l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//    }
}