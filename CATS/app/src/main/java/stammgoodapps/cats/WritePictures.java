package stammgoodapps.cats;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;

import java.util.ArrayList;
import java.io.ByteArrayOutputStream;

public class WritePictures extends Activity {

    private Context context = WritePictures.this;
    public Boolean allContacts;
    public Boolean selecting;
    private ArrayList<String> contactList = new ArrayList<>();

    public void setContactList(ArrayList<String> contactList) {
        this.contactList = contactList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String TAG = "WritePictures.onCreate";
        try {
            Bundle extras = getIntent().getExtras();
            this.allContacts = extras.getBoolean("allContacts");
            this.selecting = extras.getBoolean("selecting");
            if (extras.getParcelableArrayList("selectedContacts") == null) {
                setContactList(readPhoneContacts());
            } else {
                setContactList(extras.getStringArrayList("selectedContacts"));
            }

            if (contactList.isEmpty()) {
                Intent intent = new Intent(context, AlertUser.class);
                intent.putExtra("message","It would seem that I can't find any contacts :/");
                intent.putExtra("class", "stammgoodapps.cats.MainActivity");
                context.startActivity(intent);
            } else {
                new UpdatePictures().execute();
            }

//        } catch (InterruptedException | ExecutionException e) {
        } catch (Exception e) {
            Log.e(TAG, "Threw error: " + e.getMessage());
        }
    }

    public byte[] getPhoto() {
        final String TAG = "getPhoto";
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            TypedArray pictures = context.getResources().obtainTypedArray(R.array.loading_images);
            int choice = (int) (Math.random() * pictures.length());
            int imageResource = pictures.getResourceId(choice, R.drawable.cat1);
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
        final Uri contentUri = ContactsContract.Data.CONTENT_URI;
        final String selection;
        String contentUriId = String.valueOf(ContentUris.parseId(rawContactUri));
        String[] selectionArgs = new String[] {contentUriId, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE};

        int photoRow = -1;

        if (allContacts) {
            selection = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                    Data.MIMETYPE + " = ? ";
        } else {
            selection = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                    Data.MIMETYPE + " = ? AND " +
                    ContactsContract.CommonDataKinds.Photo.PHOTO + " IS NULL OR " +
                    ContactsContract.CommonDataKinds.Photo.PHOTO + " = ''";
        }

        Cursor cursor = resolver.query(
                contentUri,
                null,
                selection,
                selectionArgs,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(ContactsContract.Data._ID);

            if (columnIndex == -1) {
                throw new IndexOutOfBoundsException();
            }

            if (columnIndex <= 0) {
                return 0;
            }

            photoRow = cursor.getInt(columnIndex);
            cursor.close();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

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

    public ArrayList<String> readPhoneContacts() {
        final String TAG = "readPhoneContacts";
        final ContentResolver resolver = context.getContentResolver();

        ArrayList<String> contactUris = new ArrayList<>();

        Uri contentUri = Data.CONTENT_URI;
        String[] projection = new String[] {Data.CONTACT_ID};
        String selection = null;

        if (!allContacts) {
            selection = Data.PHOTO_ID + " IS NULL OR " + Data.PHOTO_ID + " = ''";
        }

        try (Cursor contactIdCursor =
                     resolver.query(
                             contentUri,
                             projection,
                             selection,
                             null,
                             null)) {
            if (contactIdCursor != null && contactIdCursor.getCount() > 0) {
                int contactIdColumn = contactIdCursor.getColumnIndex(projection[0]);
                while (contactIdCursor.moveToNext()) {
                    long contactId = contactIdCursor.getLong(contactIdColumn);
                    Uri contactUri = ContentUris.withAppendedId(Data.CONTENT_URI, contactId);
                    contactUris.add(contactUri.toString());
                }
                contactIdCursor.close();
            }

            if (contactIdCursor != null && !contactIdCursor.isClosed()) {
                contactIdCursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Threw error: " + e.getMessage());
            Log.e(TAG, "Threw error: " + e.fillInStackTrace());
            Log.e(TAG, "Threw error: " + e.getCause());
        }
        return contactUris;
    }

    private class UpdatePictures extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... allContacts) {

            final String TAG = "updatePictures";

            try {
                final ContentResolver resolver = context.getContentResolver();
                for (String rawContactUris : contactList) {
                    Uri rawContactUri = Uri.parse(rawContactUris);
                    int photoRow = getPhotoRow(rawContactUri);
                    ContentValues contactContentValues = buildContactContentValues(rawContactUri);
                    if (photoRow > 0) {
                        resolver.update(
                                ContactsContract.Data.CONTENT_URI,
                                contactContentValues,
                                ContactsContract.Data._ID + " = " + photoRow, null);
                    } else if (photoRow < 0) {
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
        protected void onPreExecute() {
            setContentView(R.layout.progress_bar);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClass(WritePictures.this, ListViewLoader.class);
            intent.putExtra("allContacts", false);
            intent.putExtra("selecting", false);
            WritePictures.this.startActivity(intent);
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
        }
    }
}