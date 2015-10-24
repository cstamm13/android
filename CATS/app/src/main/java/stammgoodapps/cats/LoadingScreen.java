package stammgoodapps.cats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class LoadingScreen extends Activity {

    public void loadIntent() {
        final String TAG = "loadIntent";
        final int WAIT_TIME = 2500;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle extras = getIntent().getExtras();
                    String classname = extras.getString("class");
                    Class<?> clazz = Class.forName(classname);
                    Intent progressIntent = new Intent(LoadingScreen.this.getApplicationContext(), clazz);
                    progressIntent.putExtra("allContacts", extras.getBoolean("allContacts"));
                    progressIntent.putStringArrayListExtra("selectedContacts", extras.getStringArrayList("selectedContacts"));
                    LoadingScreen.this.startActivity(progressIntent);
                    LoadingScreen.this.finish();
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }, WAIT_TIME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_bar);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        loadIntent();
    }
}
