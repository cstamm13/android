package stammgoodapps.cats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class LoadingScreen extends Activity {

    private final int WAIT_TIME = 2500;

    public void loadIntent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle extras = getIntent().getExtras();
                    String classname = extras.getString("class");
                    Class<?> clazz = Class.forName(classname);
                    Intent progressIntent = new Intent(LoadingScreen.this.getApplicationContext(), clazz);
                    LoadingScreen.this.startActivity(progressIntent);
                    LoadingScreen.this.finish();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
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
