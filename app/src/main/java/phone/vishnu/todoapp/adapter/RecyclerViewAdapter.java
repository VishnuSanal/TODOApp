package phone.vishnu.todoapp.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.Random;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.model.Shelve;

public class RecyclerViewAdapter extends ListAdapter<Shelve, RecyclerViewAdapter.ShelveHolder> {

    private OnItemClickListener listener;

    public RecyclerViewAdapter() {
        super(new DiffUtil.ItemCallback<Shelve>() {
            @Override
            public boolean areItemsTheSame(@NonNull Shelve oldItem, @NonNull Shelve newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Shelve oldItem, @NonNull Shelve newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                        oldItem.getDescription().equals(newItem.getDescription()) &&
                        oldItem.getDateDue().equals(newItem.getDateDue());
            }
        });
    }

    @NonNull
    @Override
    public ShelveHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item, parent, false);
        return new ShelveHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ShelveHolder holder, int position) {
        Shelve currentShelve = getItem(position);

        holder.colorView.setBackground(new ColorDrawable(getCardBGColor()));
        holder.titleTV.setText(currentShelve.getTitle());
        holder.dueTV.setText(getDueDate(currentShelve.getDateDue()));
    }

    private int getCardBGColor() {

        String[] colorArray = new String[]{
                "#e57373",
                "#f06292",
                "#ba68c8",
                "#9575cd",
                "#7986cb",
                "#64b5f6",
                "#4fc3f7",
                "#4dd0e1",
                "#4db6ac",
                "#81c784",
                "#aed581",
                "#dce775",
                "#fff176",
                "#ffd54f",
                "#ffb74d",
                "#ff8a65",
                "#a1887f"
        };

        return Color.parseColor(colorArray[new Random().nextInt(colorArray.length - 1)]);
    }

    public Shelve getShelve(int position) {
        return getItem(position);
    }

    private String getDueDate(String dueDate) {
        if (!dueDate.equals("")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(dueDate.trim()));
            dueDate = calendar.get(Calendar.HOUR_OF_DAY) + " : " +
                    calendar.get(Calendar.MINUTE) + " - " +
                    calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                    (calendar.get(Calendar.MONTH) + 1);
            return dueDate;
        } else return "Not Set";
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Shelve shelve, int id);
    }

    class ShelveHolder extends RecyclerView.ViewHolder {
        private final TextView titleTV;
        private final TextView dueTV;
        private final View colorView;

        public ShelveHolder(@NonNull View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.todoTitle);
            dueTV = itemView.findViewById(R.id.todoDue);
            colorView = itemView.findViewById(R.id.todoSampleColorView);

            itemView.findViewById(R.id.todoCardView).setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onItemClick(getItem(getAdapterPosition()), v.getId());
            });
        }
    }
}
