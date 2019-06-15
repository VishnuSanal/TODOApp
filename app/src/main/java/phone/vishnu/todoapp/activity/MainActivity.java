package phone.vishnu.todoapp.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.database.Database;
import phone.vishnu.todoapp.fragments.AboutFragment;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText e1;
    private ArrayList<String> taskList = new ArrayList<>();
    private ListView lv;
    private String todo;
    private Database db;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new Database(MainActivity.this);
        lv = findViewById(R.id.lv);
        loadTask();
        registerForContextMenu(lv);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_lv, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_delete_lv) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String key = ((TextView) info.targetView).getText().toString();
            delete(key);

        } else if (item.getItemId() == R.id.menu_copy_lv) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String key = ((TextView) info.targetView).getText().toString();
            ClipboardManager clipboard = (ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData clip = ClipData.newPlainText("Task", key);

            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "TODO copied to clipboard.", Toast.LENGTH_SHORT).show();

        } else if (item.getItemId() == R.id.menu_share_lv) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String key = ((TextView) info.targetView).getText().toString();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "TODO: ");
            intent.putExtra(Intent.EXTRA_TEXT, key);
            startActivity(Intent.createChooser(intent, "Share Using"));

        } else if (item.getItemId() == R.id.menu_details_lv) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            String key = ((TextView) info.targetView).getText().toString();


            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setCancelable(true)
                    .setTitle("TODO Added on:")
                    .setMessage(db.getdate(key))
                    .setPositiveButton("O.K", null)
                    .create();
            dialog.show();


        } else if (item.getItemId() == R.id.menu_edit_lv) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final String key = ((TextView) info.targetView).getText().toString();


            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            TextInputLayout t1 = new TextInputLayout(this);
            t1.setHintAnimationEnabled(true);
            t1.setPadding(24, 12, 24, 4);
            t1.isHintEnabled();

            e1 = new TextInputEditText(this);

            e1.setHint("TODO");
            e1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            ImageButton button = new ImageButton(this);
            button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            button.setImageResource(R.drawable.ic_mic);
            button.setOnClickListener(new View.OnClickListener() {
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

            t1.addView(e1);
            t1.addView(button);


            e1.setText(key);
            e1.selectAll();

            alert.setTitle("Edit TODO");
            alert.setView(t1);
            alert.setPositiveButton("O.K", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String task = e1.getText().toString();

                    if (task.isEmpty()) {
                        e1.setError("Please Enter A Value");
                    } else {
                        db.update(key, task);
                        loadTask();
                        Toast.makeText(MainActivity.this, "Edited TODO: " + key + " to " + task, Toast.LENGTH_SHORT).show();
                    }

                    dialogInterface.dismiss();
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.show();

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
        if (item.getItemId() == R.id.id_about) {
            AboutFragment fragment = AboutFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.constraintLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        if (item.getItemId() == R.id.id_export) {
            generateNoteOnSD(MainActivity.this, "TODO List", db.get());
        }
        if (item.getItemId() == R.id.id_import) {
            importTodos();
        }
        return super.onOptionsItemSelected(item);
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

        view.animate().xBy(10).yBy(10);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        TextInputLayout t1 = new TextInputLayout(MainActivity.this);
        t1.setHintAnimationEnabled(true);
        t1.setPadding(24, 12, 24, 4);
        t1.isHintEnabled();

        e1 = new TextInputEditText(MainActivity.this);
        e1.setHint("TODO");
        e1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);


        ImageButton button = new ImageButton(MainActivity.this);
        button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        button.setImageResource(R.drawable.ic_mic);
        button.setOnClickListener(new View.OnClickListener() {
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

        t1.addView(e1);
        t1.addView(button);

        alert.setTitle("Add TODO");
        alert.setView(t1);
        alert.setPositiveButton("O.K", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                todo = e1.getText().toString().trim();

                if (todo.isEmpty()) {
                    e1.setError("Please Enter A Value");
                } else {
                    db.insert(todo.trim(), DateFormat.getDateTimeInstance().format(new Date()));
                    loadTask();
                    dialogInterface.dismiss();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

//      TODO: Keyboard Auto Popping Up

        alert.setCancelable(true);


        alert.show();
    }

    private void loadTask() {

        db = new Database(MainActivity.this);

        taskList = db.get();

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

        db.delete(String.valueOf(string));

        Toast.makeText(MainActivity.this, "Deleting History: " + string, Toast.LENGTH_SHORT).show();

        mAdapter.notifyDataSetChanged();

        loadTask();

    }

    private void importTodos() {
        final File file = new File(Environment.getExternalStoragePublicDirectory("Documents").getAbsolutePath(), "TODOs/TODO List.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            for (int i = 0; i < file.length(); i++) {

                if (line != null) {
                    db.insert(line.trim(), ("This is an Imported TODO. Imported on " + DateFormat.getDateTimeInstance().format(new Date())));
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
            Toast.makeText(context, "Saved TODO List to Documents Directory", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
