package phone.vishnu.todoapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import phone.vishnu.todoapp.R;
import phone.vishnu.todoapp.helper.SharedPreferenceHelper;
import phone.vishnu.todoapp.helper.TourFragmentStateAdapter;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceHelper = new SharedPreferenceHelper(this);

        if (sharedPreferenceHelper.isFirstRun()) {
            showNewTour();
        } else {
            initTasks();
        }
//        TODO: Remove old SS
    }

    private void showNewTour() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tour);

        final ViewPager2 viewPager = findViewById(R.id.splashScreenTourViewPager);

        final TourFragmentStateAdapter adapter = new TourFragmentStateAdapter(this);
        viewPager.setAdapter(adapter);

        final int pageCount = adapter.getItemCount() - 1;

        findViewById(R.id.splashScreenNextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewPager.getCurrentItem() < pageCount)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                else {
                    tourCompleted();
                }
            }
        });
        findViewById(R.id.splashScreenBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });
        findViewById(R.id.splashScreenSkipButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tourCompleted();
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if ((position == pageCount))
                    tourCompleted();
            }
        });
    }

    private void tourCompleted() {
        sharedPreferenceHelper.setFirstRunBoolean(false);
        moveToNext();
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

    private void moveToNext() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }
}
/*

TODO:Import to the App

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

    }
*/