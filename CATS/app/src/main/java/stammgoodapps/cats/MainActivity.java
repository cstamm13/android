package stammgoodapps.cats;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListenerOnButton();
    }


    public void addListenerOnButton() {
        final WritePictures writePictures = new WritePictures(this);

        Button button;
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        button = (Button) findViewById(R.id.choice_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedButton = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedButton);

                try {
                    switch (radioButton.getId()) {
                        case (R.id.no_photo):
                            writePictures.updatePictures(false);
                            break;
                        case (R.id.all_contacts):
                            writePictures.updatePictures(false);
                            break;
                        case (R.id.select_contacts):
                            writePictures.launchMultiplePhonePicker();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}