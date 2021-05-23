package phone.vishnu.todoapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.adapter.RecyclerViewAdapter;
import phone.vishnu.todoapp.fragment.AboutFragment;
import phone.vishnu.todoapp.model.Shelve;
import phone.vishnu.todoapp.receiver.NotificationReceiver;
import phone.vishnu.todoapp.viewmodel.ShelveViewModel;

public class MainActivity extends AppCompatActivity {

    private ShelveViewModel shelveViewModel;
    private RecyclerViewAdapter adapter;
    private BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior;

    private ScrollView scrollView;

    private TextInputEditText titleTIE;
    private TextInputEditText descriptionTIE;

    private Button saveButton;
    private ImageView openIndicator;

    private TimePicker timePicker;
    private DatePicker datePicker;
    private SwitchCompat alarmSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpRecyclerView();

        setUpBottomSheet();

        findViewById(R.id.homeMenuIV).setOnClickListener((v) -> {

            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(item -> {

                int itemId = item.getItemId();
                if (itemId == R.id.id_about) {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        getSupportFragmentManager().beginTransaction().add(R.id.container, AboutFragment.newInstance(), "About").addToBackStack(null).commit();
                    }
                }

                return false;
            });

            popup.inflate(R.menu.menu_menu);
            popup.show();

        });
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }

        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    private void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        shelveViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ShelveViewModel.class);

        shelveViewModel.getAllShelves().observe(this, shelves -> {
            if (shelves.size() == 0) {
                findViewById(R.id.recyclerViewEmptyHintIV).setVisibility(View.VISIBLE);
                findViewById(R.id.recyclerViewEmptyHintTV).setVisibility(View.VISIBLE);

                findViewById(R.id.mainContentLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.BGColor));
                findViewById(R.id.homeAppNameTV).setBackgroundColor(ContextCompat.getColor(this, R.color.BGColor));

            } else {
                findViewById(R.id.recyclerViewEmptyHintIV).setVisibility(View.GONE);
                findViewById(R.id.recyclerViewEmptyHintTV).setVisibility(View.GONE);

                findViewById(R.id.mainContentLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.bottomSheetColorLight));
                findViewById(R.id.homeAppNameTV).setBackgroundColor(ContextCompat.getColor(this, R.color.bottomSheetColorLight));

            }
            adapter.submitList(shelves);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.redColor))
                        .addActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {

                deleteReminder(adapter.getShelve(viewHolder.getAdapterPosition()));
                shelveViewModel.delete(adapter.getShelve(viewHolder.getAdapterPosition()));

                showUndoSnackBar(adapter.getShelve(viewHolder.getAdapterPosition()));

            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener((shelve, id) -> {

            saveButton.setTag(shelve.getId());

            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

            saveButton.setText("Update");

            String timeInMillis = shelve.getDateDue();

            titleTIE.setText(shelve.getTitle());
            descriptionTIE.setText(shelve.getDescription());

            if (!Objects.equals(timeInMillis, "")) {

                alarmSwitch.setChecked(true);
                timePicker.setVisibility(View.VISIBLE);
                datePicker.setVisibility(View.VISIBLE);

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(Long.parseLong(timeInMillis));

                timePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(c.get(Calendar.MINUTE));

                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setUpBottomSheet() {

        ConstraintLayout bottomSheetLayout = findViewById(R.id.bottomSheetContainer);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setPeekHeight(DPtoPX(72));

        scrollView = bottomSheetLayout.findViewById(R.id.bottomSheetScrollView);

        titleTIE = bottomSheetLayout.findViewById(R.id.addTitleTIE);
        descriptionTIE = bottomSheetLayout.findViewById(R.id.addDescriptionTIE);

        saveButton = bottomSheetLayout.findViewById(R.id.bottomSheetButtonSave);
        openIndicator = bottomSheetLayout.findViewById(R.id.bottomSheetOpenSampleIndicator);

        timePicker = bottomSheetLayout.findViewById(R.id.todoAddTimePicker);
        datePicker = bottomSheetLayout.findViewById(R.id.todoAddDatePicker);
        alarmSwitch = bottomSheetLayout.findViewById(R.id.todoAddSwitch);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    hideKeyboard(MainActivity.this);

                    titleTIE.setText("");
                    titleTIE.setError(null);

                    descriptionTIE.setText("");
                    descriptionTIE.setError(null);

                    alarmSwitch.setChecked(false);

                    scrollView.animate().alpha(0);
                    saveButton.animate().alpha(0);

                    openIndicator.animate().rotationBy(180);

                } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {

                    final Calendar calendar = Calendar.getInstance();

                    datePicker.setMinDate(calendar.getTimeInMillis());

                    if (saveButton.getTag() == null) {

                        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY) - 1);
                        timePicker.setMinute(calendar.get(Calendar.MINUTE));

                    }

                    scrollView.animate().alpha(1);
                    saveButton.animate().alpha(1);

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    final Calendar calendar = Calendar.getInstance();

                    datePicker.setMinDate(calendar.getTimeInMillis());

                    if (saveButton.getTag() == null) {

                        timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY) - 1);
                        timePicker.setMinute(calendar.get(Calendar.MINUTE));

                    }

                    scrollView.animate().alpha(1);
                    saveButton.animate().alpha(1);

                    openIndicator.animate().rotationBy(180);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0) {
                    scrollView.animate().alpha(slideOffset);
                    saveButton.animate().alpha(slideOffset);
                }
            }
        });

        alarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                timePicker.setVisibility(View.GONE);
                datePicker.setVisibility(View.GONE);
            } else {
                datePicker.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(v -> {

            String title = Objects.requireNonNull(titleTIE.getText()).toString().trim();
            String description = Objects.requireNonNull(descriptionTIE.getText()).toString().trim();

            if (!title.isEmpty() && !description.isEmpty()) {

                if (saveButton.getTag() != null) {

                    if (-1 == saveButton.getId()) {
                        Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }

                    if (!alarmSwitch.isChecked()) {

                        Shelve shelve = new Shelve(title, description, "");
                        shelve.setId((Integer) saveButton.getTag());
                        shelveViewModel.update(shelve);
                        myAlarm(shelve);

                    } else if (alarmSwitch.isChecked()) {
                        Calendar calendar = Calendar.getInstance();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        }

                        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        calendar.set(Calendar.MONTH, datePicker.getMonth());
                        calendar.set(Calendar.YEAR, datePicker.getYear());

                        Shelve shelve = new Shelve(title, description, String.valueOf(calendar.getTimeInMillis()));
                        shelve.setId((Integer) saveButton.getTag());
                        shelveViewModel.update(shelve);

                        myAlarm(shelve);
                    }

                    showSnackBar("TODO Updated");
                    saveButton.setTag(null);
                    saveButton.setText("Save");

                } else {

                    if (!alarmSwitch.isChecked()) {

                        Shelve shelve = new Shelve(title, description, "");
                        shelveViewModel.insert(shelve);
                        myAlarm(shelve);

                    } else if (alarmSwitch.isChecked()) {
                        Calendar calendar = Calendar.getInstance();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        }

                        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        calendar.set(Calendar.MONTH, datePicker.getMonth());
                        calendar.set(Calendar.YEAR, datePicker.getYear());

                        Shelve shelve = new Shelve(title, description, String.valueOf(calendar.getTimeInMillis()));
                        shelveViewModel.insert(shelve);
                        myAlarm(shelve);
                    }

                    showSnackBar("TODO Added");

                }

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            } else {
                if (title.isEmpty()) {
                    titleTIE.setError("Field Empty");
                    titleTIE.requestFocus();
                } else {
                    descriptionTIE.setError("Field Empty");
                    descriptionTIE.requestFocus();
                }
            }

        });

        openIndicator.setOnClickListener(v -> {

            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        });
    }

    private void deleteReminder(Shelve shelve) {

        if (!"".equals(shelve.getDateDue())) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(shelve.getDateDue()));

            String timeString =
                    String.valueOf(calendar.get(Calendar.MINUTE)) +
                            calendar.get(Calendar.HOUR_OF_DAY) +
                            calendar.get(Calendar.DAY_OF_MONTH) +
                            calendar.get(Calendar.MONTH);


            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("title", shelve.getTitle());
            intent.putExtra("description", shelve.getDescription());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(timeString), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    private void myAlarm(Shelve shelve) {

        if (!Objects.equals(shelve.getDateDue(), "")) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(shelve.getDateDue()));

            if (calendar.getTime().compareTo(new Date()) < 0)
                calendar.add(Calendar.DAY_OF_MONTH, 1);

            String timeString =
                    String.valueOf(calendar.get(Calendar.MINUTE)) +
                            calendar.get(Calendar.HOUR_OF_DAY) +
                            calendar.get(Calendar.DAY_OF_MONTH) +
                            calendar.get(Calendar.MONTH);

            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("title", shelve.getTitle());
            intent.putExtra("description", shelve.getTitle());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(timeString), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Oops! Something went wrong!\nAlarm not set\nTry again", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showUndoSnackBar(Shelve shelveDeleted) {
        showSnackBar("TODO Deleted", "Undo", v -> undoDelete(shelveDeleted), Snackbar.LENGTH_LONG);
    }

    private void showSnackBar(String title, String actionButtonText, View.OnClickListener clickListener, int length) {
        View view = findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(view, title, length);

        if (actionButtonText != null && clickListener != null)
            snackbar.setAction(actionButtonText, clickListener);

        snackbar.show();
    }

    private void showSnackBar(String title) {
        showSnackBar(title, null, null, Snackbar.LENGTH_SHORT);
    }

    private void undoDelete(Shelve shelveDeleted) {
        myAlarm(shelveDeleted);
        shelveViewModel.insert(shelveDeleted);
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View currentFocusedView = activity.getCurrentFocus();
        if (currentFocusedView != null) {
            inputManager.hideSoftInputFromWindow(currentFocusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private int DPtoPX(int DP) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(DP * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}