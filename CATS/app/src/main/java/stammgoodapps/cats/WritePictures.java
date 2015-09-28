package stammgoodapps.cats;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.os.RemoteException;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class WritePictures extends Application {

    private Context context;

    public WritePictures (Context context) {
        this.context = context;
    }

    public byte[] getPhoto() {
        final String TAG = "getPhoto";
        /////make this better somehow
        int imageResource = R.drawable.first;
        Bitmap picture = BitmapFactory.decodeResource(context.getResources(), imageResource);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public int getPhotoRow(Uri rawContactUri, boolean allContacts) {

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

    public void updatePictures(boolean allContacts)
            throws RemoteException, OperationApplicationException, IndexOutOfBoundsException {
        final ContentResolver resolver = context.getContentResolver();
        final String TAG = "updatePictures";
        ArrayList<Uri> rawContactUris = readPhoneContacts();

        for (Uri rawContactUri : rawContactUris) {

            int photoRow = getPhotoRow(rawContactUri, allContacts);
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
    }

    public ArrayList<Uri> readPhoneContacts() {
        final ContentResolver resolver = context.getContentResolver();
        final String TAG = "readPhoneContacts";
        ArrayList<Uri> contactUris = new ArrayList<>();
        Cursor contactIdCursor =
                        resolver.query(
                                ContactsContract.RawContacts.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);
        try {
            if (contactIdCursor.getCount() > 0) {
                int contactIdColumn = contactIdCursor.getColumnIndex(ContactsContract.RawContacts._ID);
                while (contactIdCursor.moveToNext()) {
                    long contactId = contactIdCursor.getLong(contactIdColumn);
                    Uri contactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, contactId);
                    contactUris.add(contactUri);
                }
            }
        } finally {
            contactIdCursor.close();
        }
        return contactUris;
    }

    public void launchMultiplePhonePicker() {
        final String TAG = "PhonePicker";
        Intent intent = new Intent(context, ListViewLoader.class);
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, true);
        context.startActivity(intent);
    }
}
