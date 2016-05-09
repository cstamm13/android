package stammgoodapps.cats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlertUser extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String TAG = "AlertUser";
        final Bundle extras = getIntent().getExtras();
        final String classname = extras.getString("class");
        final String message = extras.getString("message");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Oh no!");
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Intent intent = new Intent(AlertUser.this.getApplicationContext(), Class.forName(classname));
                    AlertUser.this.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        alertDialog.show();
    }

}
