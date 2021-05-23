package phone.vishnu.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import phone.vishnu.todoapp.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clearObsoleteData();

        initTasks();

    }

    private void clearObsoleteData() {
        try {

            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit().clear().apply();

            File file = new File(
                    new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TODOApp").toString() +
                            File.separator +
                            ".Screenshot" + ".jpg"
            );

            if (file != null && file.exists())
                file.delete();

            File file2 = new File(
                    new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "TODOs").toString() +
                            File.separator +
                            "TODOs" + ".txt"
            );

            if (file2 != null && file2.exists())
                file2.delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTasks() {
        //TODO: Implement Startup Receiver

        setContentView(R.layout.activity_splash);

        int SPLASH_TIMEOUT = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, SPLASH_TIMEOUT * 1000);
    }
}