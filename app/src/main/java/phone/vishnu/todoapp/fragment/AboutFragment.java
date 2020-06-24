package phone.vishnu.todoapp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import phone.vishnu.todoapp.R;

import static android.content.Context.MODE_PRIVATE;

public class AboutFragment extends Fragment {

    private TextView sourceCodeTV, feedbackTV, resetTV;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_about, container, false);
        sourceCodeTV = inflate.findViewById(R.id.aboutPageViewSourceCodeTextView);
        resetTV = inflate.findViewById(R.id.aboutResetSettingsButton);
        feedbackTV = inflate.findViewById(R.id.aboutPageFeedbackTextView);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sourceCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://github.com/VishnuSanal/TODOApp");
                startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
            }
        });
        feedbackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                composeEmail(new String[]{getActivity().getString(R.string.email_address_of_developer)}, "Feedback of " + getActivity().getString(R.string.app_name));
            }
        });
        resetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FIRST_RUN_BOOLEAN = "firstRunPreference";

                SharedPreferences.Editor editor = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE).edit();
                editor.putBoolean(FIRST_RUN_BOOLEAN, true);
                editor.apply();

                Toast.makeText(getActivity(), "Settings Reset.....\nRestart App for changes to take effect.....", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}