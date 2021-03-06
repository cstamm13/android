package stammgoodapps.cats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class LoadingScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras = getIntent().getExtras();
        final String classname = extras.getString("class");
        final String TAG = "loadIntent";

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    Intent progressIntent = new Intent(LoadingScreen.this.getApplicationContext(), Class.forName(classname));
                    progressIntent.putExtra("allContacts", extras.getBoolean("allContacts"));
                    progressIntent.putExtra("selecting", extras.getBoolean("selecting"));
                    progressIntent.putStringArrayListExtra("selectedContacts", extras.getStringArrayList("selectedContacts"));
                    LoadingScreen.this.startActivity(progressIntent);
                    LoadingScreen.this.finish();
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}