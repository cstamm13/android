package stammgoodapps.cats;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        Log.e(TAG, "Setting content view");
//        setContentView(R.layout.contact_list_entry);
        Log.e(TAG, "Created, now starting the progress bar");

//        ProgressBar progressBar = new ProgressBar(this);
//        progressBar.setLayoutParams(new ActionBar.LayoutParams(
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                ActionBar.LayoutParams.WRAP_CONTENT,
//                Gravity.CENTER));
//        progressBar.setIndeterminate(true);
//        getListView().setEmptyView(progressBar);

        // Must add the progress bar to the root of the layout
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
//        root.addView(progressBar);

        Cursor contactIdCursor =
                this.getContentResolver().query(
                        ContactsContract.Contacts.CONTENT_URI,
                        PROJECTION,
                        SELECTION,
                        null,
                        null);
//        root.removeView(progressBar);

        try {
            if (contactIdCursor.getCount() > 0) {
                List<ContactPair> contactPair = new ArrayList<>();
                while (contactIdCursor.moveToNext()) {
                    Log.e(TAG, "starting while");
                    String contactName = contactIdCursor.getString(contactIdCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Uri contactPhotoUri = Uri.parse(contactIdCursor.getString(contactIdCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)));
                    contactPair.add(new ContactPair(contactName, contactPhotoUri));

                }
//                    Log.e(TAG, "contact name = " + contactName);
//                    Log.e(TAG, "contact photo uri = " + contactPhotoUri);
//                    Log.e(TAG, "setting list view adapter");
//                    ListViewAdapter listViewAdapter = new ListViewAdapter(this, new String[] {contactName}, new Uri[] {contactPhotoUri});
//                    Log.e(TAG, "list view adapter ==== " + listViewAdapter);
//                    root.addView(listViewAdapter.getView(0, null, root));
                ListViewAdapter adapter = new ListViewAdapter(this, contactPair.toArray(new ContactPair[contactPair.size()]));
                ListView listView = (ListView) findViewById(R.id.contact_list);
                Log.e(TAG, "find view by id ====== " + findViewById(R.id.contact_list));
                listView.setAdapter(adapter);


            }

            Log.e(TAG, "done");
//                    listView.setAdapter(listViewAdapter);
        } finally {
            contactIdCursor.close();
//            root.removeView(progressBar);
        }
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        // Do something when a list item is clicked
//        l.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//    }
}