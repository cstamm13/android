package stammgoodapps.cats;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonStreamParser;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CatOfTheDayLoader extends Activity {

    List<CatOfDay> list = new ArrayList<>();

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

    public void openCatOfTheDay(final CatOfTheDayAdapter adapter) {
        Button openCatOfDay;
        openCatOfDay = (Button) findViewById(R.id.cat_of_the_day_link);
        openCatOfDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CatOfDay> link = adapter.getCatOfDayLink();
                for (CatOfDay cats : list) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(cats.getUrl()));
                    CatOfTheDayLoader.this.startActivity(intent);
                }
            }
        });
    }

    private class DownloadImageTask extends AsyncTask<String, Void, List<CatOfDay>> {

        String description;
        String id;
        String name;
        String url;
        Bitmap bitmap;

        protected List<CatOfDay> doInBackground(String... urls) {
//            String pictureUrl = "https://s3.amazonaws.com/stamm-cat-of-the-day/" + getDate() + ".jpeg";
            String pictureUrl = "https://s3.amazonaws.com/stamm-cat-of-the-day/04212016.jpeg";
            String jsonUrl = "https://s3.amazonaws.com/stamm-cat-of-the-day/04212016.json";
//            String jsonUrl = "https://s3.amazonaws.com/stamm-cat-of-the-day/" + getDate() + ".json";
            JsonObject jsonObject;
            try {
                InputStream bitmapIn = new java.net.URL(pictureUrl).openStream();
                bitmap = BitmapFactory.decodeStream(bitmapIn);

                InputStream jsonIn = new java.net.URL(jsonUrl).openStream();
                InputStreamReader inputStreamReader = new InputStreamReader(jsonIn);
                JsonReader jsonReader = new JsonReader(inputStreamReader);
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(jsonReader);
                jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("petfinder").getAsJsonObject("pet");
                name = jsonObject.getAsJsonObject("name").get("$t").getAsString();
                id = jsonObject.getAsJsonObject("id").get("$t").getAsString();
                description = jsonObject.getAsJsonObject("description").get("$t").getAsString();
                url = "https://www.petfinder.com/petdetail/" + id;
                CatOfDay catOfDay = new CatOfDay(bitmap, id, name, url, description);
                list.add(catOfDay);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return list;
        }

        protected void onPostExecute(List<CatOfDay> catOfDay) {

            if (catOfDay.isEmpty()) {
                Intent intent = new Intent(CatOfTheDayLoader.this.getApplicationContext(), AlertUser.class);
                intent.putExtra("message","There was an issue getting the Cat Of The Day :(");
                intent.putExtra("class", "stammgoodapps.cats.MainActivity");
                CatOfTheDayLoader.this.startActivity(intent);
                return;
            }

            CatOfTheDayAdapter adapter = new CatOfTheDayAdapter(CatOfTheDayLoader.this, catOfDay);
            ListView listView = (ListView) findViewById(R.id.cat_of_the_day_list_view);
            listView.setAdapter(adapter);
            openCatOfTheDay(adapter);
            PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
            PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
            mPublisherAdView.loadAd(adRequest);
        }
    }
}
