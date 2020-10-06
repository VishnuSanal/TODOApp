package phone.vishnu.todoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.model.Shelve;

public class FavoritesDataAdapter extends ArrayAdapter<Shelve> {

    private final LayoutInflater inflater;
    private final List<Shelve> objects;

    public FavoritesDataAdapter(@NonNull Context context, List<Shelve> objects) {
        super(context, 0, objects);
        this.objects = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Shelve item = objects.get(position);
        View rootView = convertView;
        final ViewHolder viewHolder;

        if (rootView == null) {
            viewHolder = new ViewHolder();
            rootView = inflater.inflate(R.layout.share_single_item, parent, false);

            viewHolder.mainTV = rootView.findViewById(R.id.singleItemTitleTV);
            viewHolder.descTV = rootView.findViewById(R.id.singleItemDescriptionTV);

            rootView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) rootView.getTag();
        }

        viewHolder.mainTV.setText(item.getTitle());
        viewHolder.descTV.setText(item.getDescription());

        return rootView;
    }

    static class ViewHolder {
        TextView mainTV, descTV;
    }
}