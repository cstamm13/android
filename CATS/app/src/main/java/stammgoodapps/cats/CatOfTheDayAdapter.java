package stammgoodapps.cats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CatOfTheDayAdapter extends Activity {

    private Context context = CatOfTheDayAdapter.this;

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.US);
        return dateFormat.format(calendar.getTime());
    }

    public void getCatOfDay() {

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final String TAG = "getView";
        final ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cat_of_the_day, null);
            viewHolder.catOfTheDayPhoto = (ImageView) view.findViewById(R.id.cat_of_the_day_photo);
            view.setTag(R.id.cat_of_the_day_photo, viewHolder.catOfTheDayPhoto);

        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.catOfTheDayPhoto.setImageURI();
    }

    static class ViewHolder {
        ImageView catOfTheDayPhoto;
    }

}
