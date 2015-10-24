package stammgoodapps.cats;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

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

    List<ContactPair> contactPair = new ArrayList<>();
    ArrayList<String> selectedContacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        final String TAG = "onCreate";
        try {
            PopulateContactList populateContactList = new PopulateContactList();
            populateContactList.execute().get();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
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
                    if (name.getChecked()) {
                        Log.e("stuff", "the checked name ===== " + name.getName());
                        Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, name.getId());
                        selectedContacts.add(contactUri.toString());
                    }
                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClass(ListViewLoader.this, LoadingScreen.class);
                intent.putExtra("class", "stammgoodapps.cats.WritePictures");
                intent.putExtra("allContacts", true);
                intent.putStringArrayListExtra("selectedContacts", selectedContacts);
                ListViewLoader.this.startActivity(intent);
            }
        });
    }

    private class PopulateContactList extends AsyncTask<String, Void, List<ContactPair>> {

        @Override
        protected List<ContactPair> doInBackground(String... contacts) {
            final String TAG = "populateContactList";

            try (Cursor contactIdCursor =
                         ListViewLoader.this.getContentResolver().query(
                                 ContactsContract.Contacts.CONTENT_URI,
                                 PROJECTION,
                                 SELECTION,
                                 null,
                                 SORT_ORDER)) {
                if (contactIdCursor.getCount() > 0) {
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
                }
            } catch (Exception e) {
                Log.e(TAG, "Threw exception: " + e.getMessage());
            } finally {
                cancelButtonAction();
            }
            return contactPair;
        }

        @Override
        protected void onPostExecute(List<ContactPair> contactPair) {
            ListViewAdapter adapter = new ListViewAdapter(ListViewLoader.this, contactPair.toArray(new ContactPair[contactPair.size()]), contactPair);
            ListView listView = (ListView) findViewById(R.id.contact_list);
            listView.setAdapter(adapter);
            PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
            PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
            mPublisherAdView.loadAd(adRequest);
            proceedButtonAction(adapter);
            cancelButtonAction();
        }
    }
}