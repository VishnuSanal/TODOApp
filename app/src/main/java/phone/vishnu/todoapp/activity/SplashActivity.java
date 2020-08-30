package phone.vishnu.todoapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.FloatRange;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;
import io.github.dreierf.materialintroscreen.animations.IViewTranslation;
import phone.vishnu.todoapp.R;


public class SplashActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        String FIRST_RUN_BOOLEAN = "firstRunPreference";
        if (sharedPreferences.getBoolean(FIRST_RUN_BOOLEAN, true)) {
            showTour();
        } else {
            initTasks();
        }
    }

    private void showTour() {
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.ic_drawing)
                .title("Enhance your productivity with us")
                .description("Would you try?")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.ic_drawing)
                .title("It Works Like........")
                .description("A simple To-Do List App")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.ic_check_box)
                .possiblePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE})
                .title("Accept Permissions")
                .description("To make it clear......\nPermission for External Storage is to store Exported TODO Files")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.ic_import_export)
                .title("Export TODOs to Storage")
                .description("You can Export TODOs to Storage from the overflow menu on the top right")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.ic_import_export)
                .title("Import TODOs from Storage")
                .description("You can Import TODOs to Storage from the overflow menu on the top right")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.ic_notifications)
                .title("Notifications for TODOs")
                .description("You can select the time at which a notification should appear")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.colorAccent)
                .image(R.drawable.ic_delete)
                .title("Swipe to delete")
                .description("You can swipe a TODO to delete")
                .build());

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.colorAccent)
                .title("That's it")
                .image(R.drawable.ic_whatshot)
                .description("Get Started")
                .build());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        String FIRST_RUN_BOOLEAN = "firstRunPreference";
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(FIRST_RUN_BOOLEAN, false).apply();

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
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
/* TODO:Do this

    private void onSharedIntent() {

        if (getIntent().getAction() != null && getIntent().getType() != null) {

            if (getIntent().getAction().equals(Intent.ACTION_SEND)) {

                if (getIntent().getType().startsWith("text/")) {

                    String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                    if (text != null) {
                        Log.d("vishnu", "onSharedIntent() called" + text);
                    }
                }
            }
        }

    }*/