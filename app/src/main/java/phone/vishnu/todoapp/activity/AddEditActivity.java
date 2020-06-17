package phone.vishnu.todoapp.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Objects;

import phone.vishnu.todoapp.R;

public class AddEditActivity extends AppCompatActivity {

    public static final String TITLE_EXTRA = "com.vishnu.shelvestodo.TITLE_STRING";
    public static final String DESCRIPTION_EXTRA = "com.vishnu.shelvestodo.DESCRIPTION_STRING";
    public static final String DUE_DATE_EXTRA = "com.vishnu.shelvestodo.DUE_DATE";
    public static final String ID_EXTRA = "com.vishnu.shelvestodo.ID";
    private FloatingActionButton saveButton;
    private TextInputEditText titleTIE, descriptionTIE;
    private Calendar calendar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        Toolbar toolbar = findViewById(R.id.addEditToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        final TimePicker timePicker = findViewById(R.id.todoAddTimePicker);
        final DatePicker datePicker = findViewById(R.id.todoAddDatePicker);
        final Switch alarmSwitch = findViewById(R.id.todoAddSwitch);
        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    timePicker.setVisibility(View.GONE);
                    datePicker.setVisibility(View.GONE);
                } else {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                }
            }
        });

        titleTIE = findViewById(R.id.addTitleTIE);
        descriptionTIE = findViewById(R.id.addDescriptionTIE);
        saveButton = findViewById(R.id.addNewSaveIV);

        Intent i = getIntent();
        if (i.hasExtra(ID_EXTRA)) {
            toolbar.setTitle("Edit TODO");
            saveButton.setImageResource(R.drawable.ic_edit);

            titleTIE.setText(i.getStringExtra(TITLE_EXTRA));
            descriptionTIE.setText(i.getStringExtra(DESCRIPTION_EXTRA));

            String timeInMillis = i.getStringExtra(DUE_DATE_EXTRA);
            if (!Objects.equals(timeInMillis, "")) {

                alarmSwitch.setChecked(true);
                timePicker.setVisibility(View.VISIBLE);
                datePicker.setVisibility(View.VISIBLE);

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(Long.parseLong(timeInMillis));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
                    timePicker.setMinute(c.get(Calendar.MINUTE));
                }
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            }

        } else {
            toolbar.setTitle("Add New TODO");
            saveButton.setImageResource(R.drawable.add_note);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleTIE.getText().toString().trim();
                String description = descriptionTIE.getText().toString().trim();

                if (isValid(title, description)) {

                    if (!alarmSwitch.isChecked()) {
                        sendData(title, description, "");
                    } else if (alarmSwitch.isChecked()) {
                        calendar = Calendar.getInstance();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        }
                        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        calendar.set(Calendar.MONTH, datePicker.getMonth());
                        calendar.set(Calendar.YEAR, datePicker.getYear());

                        String date = String.valueOf(calendar.getTimeInMillis());
                        sendData(title, description, date);
                    }
                }

            }
        });
    }

    private void sendData(String title, String description, String due) {
        Intent i = new Intent();
        i.putExtra(TITLE_EXTRA, title);
        i.putExtra(DESCRIPTION_EXTRA, description);
        i.putExtra(DUE_DATE_EXTRA, due);

        int id = getIntent().getIntExtra(ID_EXTRA, -1);
        if (-1 != id) {
            i.putExtra(ID_EXTRA, id);
        }

        setResult(RESULT_OK, i);
        finish();
    }

    private boolean isValid(String title, String description) {

        if (title.isEmpty() || description.isEmpty()) {

            if (title.isEmpty())
                titleTIE.setError("Field Empty");

            else if (description.isEmpty())
                descriptionTIE.setError("Field Empty");

            return false;
        }
        return true;
    }
}