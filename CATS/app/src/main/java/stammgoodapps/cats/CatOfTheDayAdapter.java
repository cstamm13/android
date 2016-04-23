package stammgoodapps.cats;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CatOfTheDayAdapter extends BaseAdapter {

    private final Activity context;

    private Bitmap mImgs;

    public CatOfTheDayAdapter(Activity context, Bitmap imgs) {
        super();
        this.context = context;
        mImgs = imgs;
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
            view.setTag(R.id.cat_of_the_day_photo, viewHolder.catOfTheDayPhoto);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.catOfTheDayPhoto.setImageBitmap(mImgs);

        return view;
    }

    static class ViewHolder {
        ImageView catOfTheDayPhoto;
    }

}
