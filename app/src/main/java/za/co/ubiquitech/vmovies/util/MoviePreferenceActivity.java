package za.co.ubiquitech.vmovies.util;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import za.co.ubiquitech.vmovies.R;

/**
 * @author vane
 * @since 2015/09/23.
 */
public class MoviePreferenceActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace
                (android.R.id.content, new MoviePreferenceFragment()).commit();
    }

    public static class MoviePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }
}
