package stammgoodapps.cats;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

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

    static final String SORT_ORDER = ContactsContract.Data.DISPLAY_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        final String TAG = "onCreate";
        populateContactList();
    }

    public void populateContactList() {
        final String TAG = "populateContactList";
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
                    long contactId = contactIdCursor.getLong(
                            contactIdCursor.getColumnIndex(
                                    ContactsContract.RawContacts._ID));
                    String contactName = contactIdCursor.getString(
                            contactIdCursor.getColumnIndex(
                                    ContactsContract.Contacts.DISPLAY_NAME));
                    String contactPhotoString = contactIdCursor.getString(
                            contactIdCursor.getColumnIndex(
                                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    if (contactPhotoString != null) {
                        contactPair.add(new ContactPair(contactName, Uri.parse(contactPhotoString), contactId, false));
                    } else {
                        contactPair.add(new ContactPair(contactName, null, contactId, false));
                    }
                }

                ListViewAdapter adapter = new ListViewAdapter(this, contactPair.toArray(new ContactPair[contactPair.size()]), contactPair);
                ListView listView = (ListView) findViewById(R.id.contact_list);
                listView.setAdapter(adapter);
                proceedButtonAction(adapter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Threw exception: " + e.getMessage());
        } finally {
            contactIdCursor.close();
            cancelButtonAction();
            try {
                Thread.sleep(5000, 0);
            } catch (Exception e) {
                Log.e(TAG, "Threw exception: " + e.getMessage());
            }
        }
    }

    public void cancelButtonAction() {
        Button cancelButton;
        cancelButton = (Button) findViewById(R.id.cancel_selection);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void proceedButtonAction(final ListViewAdapter adapter) {
        Button proceedButton;
        proceedButton = (Button) findViewById(R.id.continue_selection);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ContactPair> names = adapter.getSelections();
                for (ContactPair name : names) {
                    Log.e("proceedButtonAction", "the contact pair ======== " + name.getName() + " selected == " + name.getChecked());
                }
            }
        });
    }
}