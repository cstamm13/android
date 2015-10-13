package stammgoodapps.cats;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<ContactPair> {

    private final Activity context;
    List<ContactPair> list = new ArrayList<>();

    public ListViewAdapter(Activity context, ContactPair[] contacts, List<ContactPair> list) {
        super(context, R.layout.contact_list, contacts);
        this.context = context;
        this.list = list;
    }

    List<ContactPair> getSelections() {
        return list;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_list_entry, null);
            viewHolder.contactName = (TextView) view.findViewById(R.id.current_contact_name);
            viewHolder.contactPhoto = (ImageView) view.findViewById(R.id.current_contact_photo);
            viewHolder.checkBox = (CheckBox) view.findViewById(R.id.contact_list_checkbox);
            viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton checkBoxView, boolean isChecked) {
                    int getPosition = (Integer) checkBoxView.getTag();
                    list.get(getPosition).setChecked(checkBoxView.isChecked());
                }
            });

            view.setTag(viewHolder);
            view.setTag(R.id.current_contact_name, viewHolder.contactName);
            view.setTag(R.id.current_contact_photo, viewHolder.contactPhoto);
            view.setTag(R.id.contact_list_checkbox, viewHolder.checkBox);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.checkBox.setTag(position);

        if (list.get(position).getName() != null) {
            viewHolder.contactName.setText(list.get(position).getName());
        }
        if (list.get(position).getPhoto() != null) {
            viewHolder.contactPhoto.setImageURI(list.get(position).getPhoto());
        } else {
            viewHolder.contactPhoto.setImageURI(null);
        }

        viewHolder.checkBox.setChecked(list.get(position).getChecked());

        return view;
    }

    static class ViewHolder {
        TextView contactName;
        ImageView contactPhoto;
        CheckBox checkBox;
    }
}
