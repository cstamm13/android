package stammgoodapps.cats;

import android.app.Activity;
import android.content.Context;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WritePictures writePictures = new WritePictures(this);
        ArrayList<Uri> contactIds;

        try {
            switch (R.id.which_contacts) {
                case (R.id.no_photo):
                    contactIds = writePictures.readPhoneContacts();
                    Log.d(TAG, "contactIds = " + contactIds);
                    writePictures.updatePictures(contactIds, false);
                    Log.d(TAG, "Wrote pictures");
                    break;
                case (R.id.all_contacts):
                    contactIds = writePictures.readPhoneContacts();
                    Log.d(TAG, "contactIds = " + contactIds);
                    writePictures.updatePictures(contactIds, false);
                    Log.d(TAG, "Wrote pictures");
                    break;
                case (R.id.select_contacts):
                    
            }
        } catch (Exception e) {
            Log.e(TAG, "Threw Exception: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}