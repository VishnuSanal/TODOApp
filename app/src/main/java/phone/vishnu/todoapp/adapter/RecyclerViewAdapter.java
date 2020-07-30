package phone.vishnu.todoapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

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
//        holder.setIsRecyclable(false);

        Shelve currentShelve = getItem(position);
        holder.titleTV.setText(currentShelve.getTitle());
        holder.descriptionTV.setText(currentShelve.getDescription());
        holder.dueTV.setText(getDueDate(currentShelve.getDateDue()));
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

    public Shelve getShelve(int position) {
        return getItem(position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Shelve shelve, int id);
    }

    class ShelveHolder extends RecyclerView.ViewHolder {
        private ImageView editIV, shareIV, detailsIV, copyIV;
        private TextView titleTV, descriptionTV, dueTV;

        public ShelveHolder(@NonNull View itemView) {
            super(itemView);

            titleTV = itemView.findViewById(R.id.todoTitle);
            descriptionTV = itemView.findViewById(R.id.todoDescription);
            dueTV = itemView.findViewById(R.id.todoDue);

            editIV = itemView.findViewById(R.id.todoEditIV);
            editIV.setColorFilter(R.color.colorAccent);
            editIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onItemClick(getItem(getAdapterPosition()), v.getId());
                }
            });

            shareIV = itemView.findViewById(R.id.todoShareIV);
            shareIV.setColorFilter(R.color.colorAccent);
            shareIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onItemClick(getItem(getAdapterPosition()), v.getId());
                }
            });

            detailsIV = itemView.findViewById(R.id.todoDetailsIV);
            detailsIV.setColorFilter(R.color.colorAccent);
            detailsIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onItemClick(getItem(getAdapterPosition()), v.getId());
                }
            });

            copyIV = itemView.findViewById(R.id.todoCopyIV);
            copyIV.setColorFilter(R.color.colorAccent);
            copyIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        listener.onItemClick(getItem(getAdapterPosition()), v.getId());
                }
            });

        }
    }
}
