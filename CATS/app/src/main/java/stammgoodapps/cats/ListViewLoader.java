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
    private boolean selecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String TAG = "onCreate";

        Bundle extras = getIntent().getExtras();
        this.selecting = extras.getBoolean("selecting");
//        setContentView(R.layout.contact_list);
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
                        Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, name.getId());
                        selectedContacts.add(contactUri.toString());
                    }
                }

                if (selectedContacts.isEmpty()) {
                    Intent intent = new Intent(ListViewLoader.this, AlertUser.class);
                    intent.putExtra("message", "You did not select any contacts to change!");
                    intent.putExtra("class", "stammgoodapps.cats.ListViewLoader");
                    intent.putExtra("selecting", selecting);
                    ListViewLoader.this.startActivity(intent);
                    return;
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

    public void continueWithChangedPhotos(final ListViewAdapter adapter) {
        Button proceedButton;
        proceedButton = (Button) findViewById(R.id.continue_selection);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<ContactPair> names = adapter.getSelections();
                for (ContactPair name : names) {
                    if (name.getChecked()) {
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
        protected List<ContactPair> doInBackground(String... contacts) throws NullPointerException {
            final String TAG = "populateContactList";

            setContentView(R.layout.contact_list);

            try (Cursor contactIdCursor =
                         ListViewLoader.this.getContentResolver().query(
                                 ContactsContract.Contacts.CONTENT_URI,
                                 PROJECTION,
                                 SELECTION,
                                 null,
                                 SORT_ORDER)) {
                if (contactIdCursor != null && contactIdCursor.getCount() > 0) {
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
                } else {
                    Log.i(TAG, "No contacts were found");
                    contactPair = null;
                }
            } catch (NullPointerException e) {
                Log.e(TAG, "Threw exception: " + e.getMessage());
            }
            return contactPair;
        }

        @Override
        protected void onPreExecute() {
            setContentView(R.layout.progress_bar);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<ContactPair> contactPair) {
            if (selecting) {
                ListViewAdapter adapter = new ListViewAdapter(ListViewLoader.this, contactPair.toArray(new ContactPair[contactPair.size()]), contactPair, true);
                ListView listView = (ListView) findViewById(R.id.contact_list);
                listView.setAdapter(adapter);
                PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
                PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
                mPublisherAdView.loadAd(adRequest);
                proceedButtonAction(adapter);
                cancelButtonAction();
            } else {
                ListViewAdapter adapter = new ListViewAdapter(ListViewLoader.this, contactPair.toArray(new ContactPair[contactPair.size()]), contactPair, false);
                ListView listView = (ListView) findViewById(R.id.contact_list);
                View buttonLayout = findViewById(R.id.button_layout);
                buttonLayout.setVisibility(View.GONE);
                listView.setAdapter(adapter);
                PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
                PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
                mPublisherAdView.loadAd(adRequest);
            }

//            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        }
    }
}