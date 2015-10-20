package stammgoodapps.cats;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class WritePictures extends Activity {

    private Context context = WritePictures.this;
    private Boolean allContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String TAG = "WritePictures.onCreate";
        Bundle extras = getIntent().getExtras();
        this.allContacts = extras.getBoolean("allContacts");
        try {
            UpdatePictures updatePictures = new UpdatePictures();
            updatePictures.execute(allContacts).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Threw error: " + e.getMessage());
        }
    }

    public byte[] getPhoto() {
        final String TAG = "getPhoto";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            TypedArray pictures = context.getResources().obtainTypedArray(R.array.loading_images);
            int choice = (int) (Math.random() * pictures.length());
            int imageResource = pictures.getResourceId(choice, R.drawable.cats2);
            pictures.recycle();
            Bitmap picture = BitmapFactory.decodeResource(context.getResources(), imageResource);
            picture.compress(Bitmap.CompressFormat.PNG, 0, stream);
        } catch (Exception e) {
            Log.e(TAG, "Threw error: " + e.getMessage());
        }
        return stream.toByteArray();
    }

    public int getPhotoRow(Uri rawContactUri) {

        final ContentResolver resolver = context.getContentResolver();
        int photoRow = -1;
        final String SELECTION;
        final Uri PROJECTION = ContactsContract.Data.CONTENT_URI;
        if (allContacts) {
            SELECTION = ContactsContract.Data.RAW_CONTACT_ID + " == " +
                    ContentUris.parseId(rawContactUri) + " AND " +
                    Data.MIMETYPE + "=='" +
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
        } else {
            SELECTION = ContactsContract.Data.RAW_CONTACT_ID + " == " +
                    ContentUris.parseId(rawContactUri) + " AND " +
                    Data.MIMETYPE + "=='" +
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + " AND " +
                    ContactsContract.CommonDataKinds.Photo.PHOTO + " IS NULL OR " +
                    ContactsContract.CommonDataKinds.Photo.PHOTO + " = ?'";
        }

        Cursor cursor = resolver.query(
                PROJECTION,
                null,
                SELECTION,
                null,
                null);

        int columnIndex = cursor.getColumnIndex(ContactsContract.Data._ID);
        if (columnIndex == -1) {
            throw new IndexOutOfBoundsException();
        }
        if (cursor.moveToFirst()) {
            photoRow = cursor.getInt(columnIndex);
        }
        cursor.close();

        return photoRow;

    }

    public ContentValues buildContactContentValues(Uri rawContactUri) {
        ContentValues ops = new ContentValues();
        byte[] photo = getPhoto();
        ops.put(ContactsContract.Data.RAW_CONTACT_ID,
                ContentUris.parseId(rawContactUri));
        ops.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
        ops.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photo);
        ops.put(ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
        return ops;
    }

    private class UpdatePictures extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... allContacts) {

            final String TAG = "updatePictures";

            try {
                final ContentResolver resolver = context.getContentResolver();
                ArrayList<Uri> rawContactUris = readPhoneContacts();

                for (Uri rawContactUri : rawContactUris) {
                    int photoRow = getPhotoRow(rawContactUri);
                    ContentValues contactContentValues = buildContactContentValues(rawContactUri);
                    if (photoRow >= 0) {
                        resolver.update(
                                ContactsContract.Data.CONTENT_URI,
                                contactContentValues,
                                ContactsContract.Data._ID + " = " + photoRow, null);
                    } else {
                        resolver.insert(
                                ContactsContract.Data.CONTENT_URI,
                                contactContentValues);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Threw error: " + e.getMessage());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //load next view, but figure out what that is first
        }
    }

    public ArrayList<Uri> readPhoneContacts() {
        final ContentResolver resolver = context.getContentResolver();
        final String TAG = "WritePictures.readPhoneContacts";
        ArrayList<Uri> contactUris = new ArrayList<>();
        try (Cursor contactIdCursor =
                     resolver.query(
                             ContactsContract.RawContacts.CONTENT_URI,
                             null,
                             null,
                             null,
                             null)) {
            if (contactIdCursor.getCount() > 0) {
                int contactIdColumn = contactIdCursor.getColumnIndex(ContactsContract.RawContacts._ID);
                while (contactIdCursor.moveToNext()) {
                    long contactId = contactIdCursor.getLong(contactIdColumn);
                    Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, contactId);
                    contactUris.add(contactUri);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Threw error: " + e.getMessage());
        }
        return contactUris;
    }
}
