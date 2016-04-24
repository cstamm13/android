package stammgoodapps.cats;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CatOfTheDayAdapter extends BaseAdapter {

    private final Activity context;
    private final List<CatOfDay> catOfDay;

    public CatOfTheDayAdapter(Activity context, List<CatOfDay> catOfDay) {
        super();
        this.context = context;
        this.catOfDay = catOfDay;
    }

    List<CatOfDay> getCatOfDayLink() {
        return catOfDay;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
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
            viewHolder.catOfTheDayName = (TextView) view.findViewById(R.id.cat_of_the_day_name);
            viewHolder.catOfTheDayDescription = (TextView) view.findViewById(R.id.cat_of_the_day_description);
            view.setTag(R.id.cat_of_the_day_photo, viewHolder.catOfTheDayPhoto);
            view.setTag(R.id.cat_of_the_day_name, viewHolder.catOfTheDayName);
            view.setTag(R.id.cat_of_the_day_description, viewHolder.catOfTheDayDescription);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }


        if (catOfDay.get(position).getName() != null) {
            viewHolder.catOfTheDayName.setText(catOfDay.get(position).getName());
        }
        if (catOfDay.get(position).getBitmap() != null) {
            viewHolder.catOfTheDayPhoto.setImageBitmap(catOfDay.get(position).getBitmap());
        }
        if (catOfDay.get(position).getDescription() != null) {
            viewHolder.catOfTheDayDescription.setText(catOfDay.get(position).getDescription());
        }

        return view;
    }

    static class ViewHolder {
        ImageView catOfTheDayPhoto;
        TextView catOfTheDayName;
        TextView catOfTheDayDescription;
    }

}
