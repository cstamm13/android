package stammgoodapps.cats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
        addListenerOnButton();
    }


    public void addListenerOnButton() {
        Button button;
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        button = (Button) findViewById(R.id.choice_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedButton = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedButton);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClass(MainActivity.this, LoadingScreen.class);

                try {
                    switch (radioButton.getId()) {
                        case (R.id.no_photo):
                            intent.putExtra("class", "stammgoodapps.cats.WritePictures");
                            intent.putExtra("allContacts", false);
                            intent.putExtra("selecting", false);
                            MainActivity.this.startActivity(intent);
                            break;
                        case (R.id.all_contacts):
                            intent.putExtra("class", "stammgoodapps.cats.WritePictures");
                            intent.putExtra("allContacts", true);
                            intent.putExtra("selecting", false);
                            MainActivity.this.startActivity(intent);
                            break;
                        case (R.id.select_contacts):
                            intent.putExtra("class", "stammgoodapps.cats.ListViewLoader");
                            intent.putExtra("allContacts", false);
                            intent.putExtra("selecting", true);
                            MainActivity.this.startActivity(intent);
                            break;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Threw Exception: " + e.getMessage());
                }
            }
        });

    }


    protected void onAction() {
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
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.cat_of_the_day:
                intent.setClass(MainActivity.this, CatOfTheDayLoader.class);
                MainActivity.this.startActivity(intent);
                return true;
            case R.id.about:
                intent.setClass(MainActivity.this, AboutLoader.class);
                MainActivity.this.startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}