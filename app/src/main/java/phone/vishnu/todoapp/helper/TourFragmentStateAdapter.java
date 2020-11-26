package phone.vishnu.todoapp.helper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.fragment.TourSingleFragment;
import phone.vishnu.todoapp.model.TourItem;


public class TourFragmentStateAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    private ArrayList<TourItem> tourItems = new ArrayList<>();

    public TourFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.tourItems = getTourItems();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TourSingleFragment.newInstance(getTourItem(position));
    }

    @Override
    public int getItemCount() {
        return tourItems.size();
    }

    private TourItem getTourItem(int position) {
        return tourItems.get(position);
    }

    private ArrayList<TourItem> getTourItems() {

        ArrayList<TourItem> tourItems = new ArrayList<>();

        tourItems.add(new TourItem(R.drawable.ic_drawing, "Enhance your productivity with us", "A simple To-Do List App\nWould you try?"));
        tourItems.add(new TourItem(R.drawable.ic_import_export, "Import & Export TODOs", "Import & Export TODOs to Storage"));
        tourItems.add(new TourItem(R.drawable.ic_notifications, "Notifications", "Notifications for TODOs"));
        tourItems.add(new TourItem(R.drawable.ic_delete, "Swipe to Delete", "Swipe a TODO to Delete it"));
        tourItems.add(new TourItem(R.drawable.ic_whatshot, "That's it", "Get Started"));

        return tourItems;
    }
}
