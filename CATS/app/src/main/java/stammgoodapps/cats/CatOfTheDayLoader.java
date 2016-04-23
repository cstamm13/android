package stammgoodapps.cats;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CatOfTheDayLoader extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cat_of_the_day_list_view);
        final String TAG = "CatOfDayViewAdapter.onCreate";
        try {
            DownloadImageTask downloadImageTask = new DownloadImageTask();
            downloadImageTask.execute();
        } catch (Exception e) {
            Log.e(TAG, "Threw error: " + e.getMessage());
        }
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy", Locale.US);
        return dateFormat.format(calendar.getTime());
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
//                        String urldisplay = "https://s3.amazonaws.com/stamm-cat-of-the-day/" + getDate() + ".jpeg";
            String urldisplay = "https://s3.amazonaws.com/stamm-cat-of-the-day/04212016.jpeg";
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            CatOfTheDayAdapter adapter = new CatOfTheDayAdapter(CatOfTheDayLoader.this, result);
            ListView listView = (ListView) findViewById(R.id.cat_of_the_day_list_view);
            listView.setAdapter(adapter);
            PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
            PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
            mPublisherAdView.loadAd(adRequest);
        }
    }
}
