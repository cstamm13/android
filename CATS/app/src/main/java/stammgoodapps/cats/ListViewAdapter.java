package stammgoodapps.cats;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends ArrayAdapter<ContactPair> {

    private final Activity context;
    private final ContactPair[] pairs;

    public ListViewAdapter(Activity context, ContactPair[] contacts) {
        super(context, R.layout.contact_list, contacts);
        this.context = context;
        this.pairs = contacts;
    }

    static class ViewHolder {
        TextView contactName;
        ImageView contactPhoto;
        CheckBox checkBox;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_list_entry, null);
            viewHolder.contactName = (TextView) view.findViewById(R.id.current_contact_name);
            viewHolder.contactPhoto = (ImageView) view.findViewById(R.id.current_contact_photo);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.contact_list_checkbox);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.contactName.setText(pairs[position].getName());
        viewHolder.contactPhoto.setImageURI(pairs[position].getPhoto());
        viewHolder.checkBox.setClickable(true);

        return view;
    }
}
