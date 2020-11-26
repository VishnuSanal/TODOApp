package phone.vishnu.todoapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.model.TourItem;


public class TourSingleFragment extends Fragment {

    public TourSingleFragment() {
    }

    public static TourSingleFragment newInstance(TourItem tourItem) {
        Bundle args = new Bundle();

        args.putInt("tourImg", tourItem.getImgId());
        args.putString("tourTitle", tourItem.getTitle());
        args.putString("tourDescription", tourItem.getDesc());

        TourSingleFragment fragment = new TourSingleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_tour_single, container, false);

        Bundle args = getArguments();
        ((ImageView) i.findViewById(R.id.tourImage)).setImageResource(args.getInt("tourImg"));
        ((TextView) i.findViewById(R.id.tourTitle)).setText(args.getString("tourTitle"));
        ((TextView) i.findViewById(R.id.tourDescription)).setText(args.getString("tourDescription"));

        return i;
    }
}