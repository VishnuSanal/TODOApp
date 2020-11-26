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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
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

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

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

    private ShelveViewModel shelveViewModel;
    private RecyclerViewAdapter adapter;

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
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    shareScreenshot(MainActivity.this);
                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                            showPermissionDeniedDialog();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                            permissionToken.continuePermissionRequest();
                        }
                    })
                    .check();
        } else if (itemId == R.id.id_about) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                getSupportFragmentManager().beginTransaction().add(R.id.container, AboutFragment.newInstance(), "About").addToBackStack(null).commit();
                setVisibility(false);
            }
        } else if (itemId == R.id.id_export) {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    exportNotes(shelveViewModel.getAllShelves().getValue());
                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                            showPermissionDeniedDialog();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                            permissionToken.continuePermissionRequest();
                        }
                    })
                    .check();
        } else if (itemId == R.id.id_import) {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    importNotes();
                                }
                            });
                        }

                        @Override
                        public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                            showPermissionDeniedDialog();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                            permissionToken.continuePermissionRequest();
                        }
                    })
                    .check();
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
                myAlarm(shelve);
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

                    myAlarm(shelve);
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
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
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
                if (shelves.size() == 0) {
                    findViewById(R.id.recyclerViewEmptyHintIV).setVisibility(View.VISIBLE);
                    findViewById(R.id.recyclerViewEmptyHintTV).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.recyclerViewEmptyHintIV).setVisibility(View.GONE);
                    findViewById(R.id.recyclerViewEmptyHintTV).setVisibility(View.GONE);
                }
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

                showUndoSnackBar(adapter.getShelve(viewHolder.getAdapterPosition()));

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
            }
        }
    }

    private void showUndoSnackBar(final Shelve shelveDeleted) {
        View view = findViewById(R.id.container);
        Snackbar snackbar = Snackbar.make(view, "TODO Deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete(shelveDeleted);
            }
        });
        snackbar.show();
    }

    private void undoDelete(Shelve shelveDeleted) {
        myAlarm(shelveDeleted);
        shelveViewModel.insert(shelveDeleted);
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
                        myAlarm(shelve);
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

    private void showPermissionDeniedDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Necessary Permissions");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                startActivity(
                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.fromParts("package", getPackageName(), null))
                );
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                Toast.makeText(MainActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();

    }
}