package phone.vishnu.todoapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.adapter.FavoritesDataAdapter;
import phone.vishnu.todoapp.adapter.RecyclerViewAdapter;
import phone.vishnu.todoapp.fragment.AboutFragment;
import phone.vishnu.todoapp.model.Shelve;
import phone.vishnu.todoapp.receiver.NotificationReceiver;
import phone.vishnu.todoapp.viewmodel.ShelveViewModel;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_REQUEST_CODE = 1;
    private static final int EDIT_REQUEST_CODE = 2;
    private static final int PERMISSION_REQ_CODE = 2222;
    private ShelveViewModel shelveViewModel;
    private RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.id_export_as_image) {
            if (isPermissionGranted()) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        shareScreenshot(MainActivity.this);
                    }
                });
            } else {
                isPermissionGranted();
            }
        } else if (itemId == R.id.id_about) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                getSupportFragmentManager().beginTransaction().add(R.id.container, AboutFragment.newInstance(), "About").addToBackStack(null).commit();
                setVisibility(false);
            }
        } else if (itemId == R.id.id_export) {
            if (isPermissionGranted()) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        exportNotes(shelveViewModel.getAllShelves().getValue());
                    }
                });
            } else {
                isPermissionGranted();
            }
        } else if (itemId == R.id.id_import) {
            if (isPermissionGranted()) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        importNotes();

                    }
                });
            } else {
                isPermissionGranted();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setVisibility(boolean makeVisible) {
        if (makeVisible) {
            Objects.requireNonNull(getSupportActionBar()).show();
            findViewById(R.id.addMainFAB).setVisibility(View.VISIBLE);
        } else {
            Objects.requireNonNull(getSupportActionBar()).hide();
            findViewById(R.id.addMainFAB).setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                if (Objects.equals(fragment.getTag(), "About"))
                    setVisibility(true);
            }
        }
        super.onBackPressed();
    }

    private void shareScreenshot(Context context) {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams") View shareView = inflater.inflate(R.layout.share_layout, null);

        FavoritesDataAdapter adapter = new FavoritesDataAdapter(context.getApplicationContext(), shelveViewModel.getAllShelves().getValue());
        ((ListView) shareView.findViewById(R.id.shareListView)).setAdapter(adapter);
        adapter.notifyDataSetChanged();

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        shareView.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.EXACTLY));

        shareView.setDrawingCacheEnabled(true);

        Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        shareView.layout(0, 0, metrics.widthPixels, metrics.heightPixels);
        shareView.draw(c);

        shareView.buildDrawingCache(true);

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TODOApp");
        if (!root.exists()) root.mkdirs();
        String imagePath = root.toString() + File.separator + ".Screenshot" + ".jpg";
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(imagePath));
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == ADD_REQUEST_CODE) {

                String title = Objects.requireNonNull(data.getExtras()).getString(AddEditActivity.TITLE_EXTRA, "");
                String description = data.getExtras().getString(AddEditActivity.DESCRIPTION_EXTRA, "");
                String dueDate = data.getExtras().getString(AddEditActivity.DUE_DATE_EXTRA, "");

                Shelve shelve = new Shelve(title, description, (dueDate));
                shelveViewModel.insert(shelve);
                myAlarm(dueDate, title, description);
//                recyclerView.scrollToPosition(0);
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            } else if (requestCode == EDIT_REQUEST_CODE) {

                int id = Objects.requireNonNull(data.getExtras()).getInt(AddEditActivity.ID_EXTRA, -1);

                if (-1 == id) {
                    Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
                } else {
                    String title = data.getExtras().getString(AddEditActivity.TITLE_EXTRA, "");
                    String description = data.getExtras().getString(AddEditActivity.DESCRIPTION_EXTRA, "");
                    String dueDate = data.getExtras().getString(AddEditActivity.DUE_DATE_EXTRA, "");

                    Shelve shelve = new Shelve(title, description, (dueDate));
                    shelve.setId(id);
                    shelveViewModel.update(shelve);
                    /*TODO:
                    if (!Objects.equals(dueDate, ""))myAlarm(dueDate, title, description);
                    else deleteReminder(shelve);*/
                    myAlarm(dueDate, title, description);
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Changes Not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exportNotes(shelveViewModel.getAllShelves().getValue());
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        shelveViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ShelveViewModel.class);
        shelveViewModel.getAllShelves().observe(this, new Observer<List<Shelve>>() {
            @Override
            public void onChanged(List<Shelve> shelves) {
                adapter.submitList(shelves);
            }
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

                /*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete TODO");
                builder.setIcon(R.drawable.ic_delete);
                builder.setMessage("This TODO will be permanently deleted");
                builder.setNegativeButton("Cancel",null);
                builder.setPositiveButton("O.K", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();*/
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Shelve shelve, int id) {
                if (id == R.id.todoEditIV) {
                    Intent i = new Intent(MainActivity.this, AddEditActivity.class);
                    i.putExtra(AddEditActivity.ID_EXTRA, shelve.getId());
                    i.putExtra(AddEditActivity.TITLE_EXTRA, shelve.getTitle());
                    i.putExtra(AddEditActivity.DESCRIPTION_EXTRA, shelve.getDescription());
                    i.putExtra(AddEditActivity.DUE_DATE_EXTRA, shelve.getDateDue());

                    startActivityForResult(i, EDIT_REQUEST_CODE);
                } else if (id == R.id.todoCopyIV) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("TODO", shelve.getTitle() + "\n" + shelve.getDescription());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(MainActivity.this, "TODO copied to clipboard.", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.todoDetailsIV) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setCancelable(true)
                            .setTitle("Details")
                            .setMessage(getDueDate(shelve.getDateDue()))
                            .setPositiveButton("O.K", null)
                            .create();
                    dialog.show();
                }

            }
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

    private void myAlarm(String timeInMillis, String title, String description) {

        if (!Objects.equals(timeInMillis, "")) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(timeInMillis));

            if (calendar.getTime().compareTo(new Date()) < 0)
                calendar.add(Calendar.DAY_OF_MONTH, 1);

            String timeString =
                    String.valueOf(calendar.get(Calendar.MINUTE)) +
                            calendar.get(Calendar.HOUR_OF_DAY) +
                            calendar.get(Calendar.DAY_OF_MONTH) +
                            calendar.get(Calendar.MONTH);

            Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("description", description);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(timeString), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    public void addFABClicked(View view) {
        startActivityForResult(new Intent(MainActivity.this, AddEditActivity.class), ADD_REQUEST_CODE);
    }

    public void exportNotes(List<Shelve> shelves) {
        String fileName = "TODOs";
        try {
            File root = new File(Environment.getExternalStoragePublicDirectory("Documents"), "TODOs");
            if (!root.exists()) {
                root.mkdirs();
            }
            File file = new File(root, fileName);
            FileWriter writer = new FileWriter(file + ".txt");

            Gson gson = new Gson();

            String json = gson.toJson(shelves);

            writer.append(json);

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void importNotes() {
        final File file = new File(Environment.getExternalStoragePublicDirectory("Documents").getAbsolutePath(), "TODOs/TODOs.txt");
        String line = "";

        try {
            InputStream inputStream = new FileInputStream(file);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                line = stringBuilder.toString();

                if (line != null) {

                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Shelve>>() {
                    }.getType();
                    List<Shelve> shelves = gson.fromJson(line, type);

                    for (Shelve shelve : shelves) {
                        Shelve s = new Shelve(shelve.getTitle(), shelve.getDescription(), shelve.getDateDue());
                        shelveViewModel.insert(s);
                        myAlarm(shelve.getDateDue(), shelve.getTitle(), shelve.getDescription());
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDueDate(String dueDate) {
        if (!dueDate.equals("")) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(dueDate.trim()));
            dueDate = calendar.get(Calendar.HOUR_OF_DAY) + " : " +
                    calendar.get(Calendar.MINUTE) + " - " +
                    calendar.get(Calendar.DAY_OF_MONTH) + "/" +
                    (calendar.get(Calendar.MONTH) + 1);
        }
        return "TODO Due On: " + dueDate;
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPermissionDeniedDialog();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void showPermissionDeniedDialog() {

        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Permission to Capture Screenshot of the Screen");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
}