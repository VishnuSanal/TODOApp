package phone.vishnu.todoapp.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.database.Database;
import phone.vishnu.todoapp.fragments.AboutFragment;
import phone.vishnu.todoapp.helpers.NotificationReceiver;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQ_CODE = 1;
    private static final String TAG = "vishnu";
    private TextInputEditText e1;
    private ListView lv;
    private String todo = "";
    private Calendar calendar = null;
    private Database db;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Log.e("vishnu",calendar.toString());
        db = new Database(MainActivity.this);
        lv = findViewById(R.id.lv);
        loadTask();
/*

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        String testString =
                String.valueOf(calendar.get(Calendar.MINUTE)) +
                        calendar.get(Calendar.HOUR_OF_DAY) +
                        calendar.get(Calendar.DAY_OF_MONTH) +
                        calendar.get(Calendar.MONTH) +
                        (calendar.get(Calendar.YEAR)) % 100;
        Log.e("vishnu", String.valueOf(Long.parseLong(testString)));
        Log.e("vishnu", String.valueOf((int) Long.parseLong(testString)));

*/

        registerForContextMenu(lv);
    }

    @Override
    protected void onPause() {
        super.onPause();
        generateNoteOnSD(MainActivity.this, "TODO List", db.get());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_lv, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_delete_lv: {

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String key = ((TextView) info.targetView).getText().toString();
                delete(key);

                break;
            }
            case R.id.menu_copy_lv: {

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String key = ((TextView) info.targetView).getText().toString();
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);

                ClipData clip = ClipData.newPlainText("Task", key);

                clipboard.setPrimaryClip(clip);

                Toast.makeText(this, "TODO copied to clipboard.", Toast.LENGTH_SHORT).show();

                break;
            }
            case R.id.menu_share_lv: {

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String key = ((TextView) info.targetView).getText().toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "TODO: ");
                intent.putExtra(Intent.EXTRA_TEXT, key);
                startActivity(Intent.createChooser(intent, "Share Using"));

                break;
            }
            case R.id.menu_details_lv: {

                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                String key = ((TextView) info.targetView).getText().toString();


                char[] date = String.valueOf(db.getTarget(key)).toCharArray();
                Calendar c = Calendar.getInstance();
                c.set(c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        Integer.parseInt(
                                String.copyValueOf(date, 4, 2)),
                        Integer.parseInt(
                                String.copyValueOf(date, 2, 2)),
                        Integer.parseInt(
                                String.copyValueOf(date, 0, 2))
                );

                String dateString = c.get(Calendar.HOUR_OF_DAY) + " : " +
                        c.get(Calendar.MINUTE) + ", " +
                        c.get(Calendar.DAY_OF_MONTH) + "/" +
                        c.get(Calendar.MONTH);
/*
                TextView tv1 = new TextView(this);
                tv1.setText("TODO Added on: ");

                TextView tv2 = new TextView(this);
                tv2.setText(db.getDate(key));

                TextView tv3 = new TextView(this);
                tv3.setText("TODO Due on:");

                TextView tv4 = new TextView(this);
                tv4.setText(dateString);

                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(24, 24, 24, 24);
                layout.addView(tv1);
                layout.addView(tv2);
                layout.addView(tv3);
                layout.addView(tv4);
*/
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setCancelable(true)
                        .setTitle("Details")
                        .setMessage("TODO Added on:\n" +
                                db.getDate(key) + "\n" +
                                "TODO Due on:\n" +
                                dateString)
//                        .setView(layout)
                        .setPositiveButton("O.K", null)
                        .create();
                dialog.show();
                break;
            }
            case R.id.menu_edit_lv: {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final String key = ((TextView) info.targetView).getText().toString();

                final AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("Add TODO");

                final View v = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
                alert.setView(v);

                Button OKBtn = v.findViewById(R.id.todoAddOKBtn), cancelBtn = v.findViewById(R.id.todoAddCancelBtn);
                ImageView micIV = v.findViewById(R.id.todoAddMicIV);

                final TimePicker timePicker = v.findViewById(R.id.todoAddTimePicker);
                final DatePicker datePicker = v.findViewById(R.id.todoAddDatePicker);
                final Switch alarmSwitch = v.findViewById(R.id.todoAddSwitch);

                alarmSwitch.setVisibility(View.GONE);
                timePicker.setVisibility(View.GONE);
                datePicker.setVisibility(View.GONE);

                datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

                e1 = v.findViewById(R.id.todoAddTIE);
                e1.setText(key);
                OKBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        todo = e1.getText().toString().trim();
                        int date = 0;

                        if (todo.isEmpty()) {
                            e1.setError("Please Enter A Value");
                        } else {
                            if (!alarmSwitch.isChecked()) {
                                db.update(key, todo.trim());
                                loadTask();
                                alert.dismiss();
                            } else if (alarmSwitch.isChecked()) {
                                calendar = Calendar.getInstance();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                                    calendar.set(Calendar.MINUTE, timePicker.getMinute());
                                }
                                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                                calendar.set(Calendar.MONTH, datePicker.getMonth());
                                calendar.set(Calendar.YEAR, datePicker.getYear());

                                date = Integer.parseInt(String.valueOf(calendar.get(Calendar.MINUTE)) +
                                        calendar.get(Calendar.HOUR_OF_DAY) +
                                        calendar.get(Calendar.DAY_OF_MONTH) +
                                        calendar.get(Calendar.MONTH));

                                db.update(key, todo.trim());
                                loadTask();
//                                myAlarm(calendar, todo);
                                alert.dismiss();
                            }
                        }
                    }
                });
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
                micIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, 10);
                        } else {
                            Toast.makeText(MainActivity.this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });

                alert.setCancelable(true);
                alert.show();

                break;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_about:
                AboutFragment fragment = AboutFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.id_export:
                requestPermission(1);

                break;
            case R.id.id_import:
                requestPermission(2);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermission(int pos) {
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "Please Accept Required Permission", Toast.LENGTH_SHORT).show();
                }
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
            } else {
                if (pos == 1) generateNoteOnSD(MainActivity.this, "TODO List", db.get());
                else if (pos == 2) importTodos();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> arr = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                String[] split = arr.get(0).split(" ");
                StringBuilder sb = new StringBuilder();

                for (String s : split) {

                    sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");

                }
                e1.setText(String.format("%s %s", e1.getText().toString().trim(), sb.toString().trim()));
            }
        }
    }

    public void add_fab_onclick(final View view) {

        Animation shake = AnimationUtils.loadAnimation(this, R.anim.animate);
        view.startAnimation(shake);
//        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setTitle("Loading");
//        progressDialog.show();
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
        alertBuilder.setTitle("Add TODO");

        final View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_layout, null);
        alertBuilder.setView(v);

        Button OKBtn = v.findViewById(R.id.todoAddOKBtn), cancelBtn = v.findViewById(R.id.todoAddCancelBtn);
        ImageView micIV = v.findViewById(R.id.todoAddMicIV);

        final TimePicker timePicker = v.findViewById(R.id.todoAddTimePicker);
        final DatePicker datePicker = v.findViewById(R.id.todoAddDatePicker);
        final Switch alarmSwitch = v.findViewById(R.id.todoAddSwitch);
        e1 = v.findViewById(R.id.todoAddTIE);

        alertBuilder.setCancelable(true);
        final AlertDialog alert = alertBuilder.show();

        datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());

//        if (alert.isShowing())progressDialog.dismiss();

        OKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                todo = e1.getText().toString().trim();
                int date = 0;

                if (todo.isEmpty()) {
                    e1.setError("Please Enter A Value");
                } else {
                    if (!alarmSwitch.isChecked()) {
                        db.insert(todo.trim(), DateFormat.getDateTimeInstance().format(new Date()), date);
                        loadTask();
                        alert.dismiss();
                    } else if (alarmSwitch.isChecked()) {
                        calendar = Calendar.getInstance();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                            calendar.set(Calendar.MINUTE, timePicker.getMinute());
                        }
                        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                        calendar.set(Calendar.MONTH, datePicker.getMonth());
                        calendar.set(Calendar.YEAR, datePicker.getYear());

                        date = Integer.parseInt(String.valueOf(
                                calendar.get(Calendar.MINUTE)) +
                                calendar.get(Calendar.HOUR_OF_DAY) +
                                calendar.get(Calendar.DAY_OF_MONTH) +
                                calendar.get(Calendar.MONTH));

                        db.insert(todo.trim(), DateFormat.getDateTimeInstance().format(new Date()), date);
                        loadTask();
                        myAlarm(calendar, todo);
                        alert.dismiss();
                    }
                }
            }
        });


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

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(MainActivity.this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void myAlarm(Calendar calendar, String todo) {

        if (null != calendar) {
            if (calendar.getTime().compareTo(new Date()) < 0)
                calendar.add(Calendar.DAY_OF_MONTH, 1);

            String timeString =
                    String.valueOf(calendar.get(Calendar.MINUTE)) +
                            calendar.get(Calendar.HOUR_OF_DAY) +
                            calendar.get(Calendar.DAY_OF_MONTH) +
                            calendar.get(Calendar.MONTH);

            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("todo", todo);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(timeString), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    private void loadTask() {
//          FIXME: Changed on 17-12-2019
//          ArrayList<Todo> todoArrayList = new ArrayList<>();
//           ArrayList<String> targetList = db.getTargetList();
//           Collections.sort(targetList);
//           FIXME: Till Here
        db = new Database(MainActivity.this);
        ArrayList<String> taskList = db.get();

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.single_item, taskList);
            lv.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void delete(String string) {

        deleteReminder(string);

        db.delete(String.valueOf(string));
        Toast.makeText(MainActivity.this, "Deleting: " + string, Toast.LENGTH_SHORT).show();
        mAdapter.notifyDataSetChanged();
        loadTask();

    }

    private void deleteReminder(String string) {

        int id = db.getTarget(string);

        Log.e(TAG, id + "");
        if (!(0 == id)) {

            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("todo", string);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    private void importTodos() {
        final File file = new File(Environment.getExternalStoragePublicDirectory("Documents").getAbsolutePath(), "TODOs/TODO List.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            for (int i = 0; i < file.length(); i++) {

                if (line != null) {
                    db.insert(line.trim(), ("This is an Imported TODO. Imported on " + DateFormat.getDateTimeInstance().format(new Date())), 0);
                    loadTask();
                    line = br.readLine();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateNoteOnSD(Context context, String sFileName, ArrayList<String> arrayListBody) {
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory("Documents"), "TODOs");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile + ".txt");

            for (int i = 0; i < arrayListBody.size(); i++) {

                String sBody = arrayListBody.get(i);
                String value = sBody + "\n";
                writer.append(value);

            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
