package za.co.ubiquitech.vmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

import za.co.ubiquitech.vmovies.formObjects.MovieDetailsForm;

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {
    public static final String MOVIE_API_KEY = "b3d57ac00feabdc38238f51d1255f024";
    private boolean mTwoPane = false;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private Bundle arguments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //http://facebook.github.io/stetho/
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            DetailFragment detailFragment = new DetailFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.movie_detail_container, detailFragment, DETAILFRAGMENT_TAG).commit();

            if (savedInstanceState != null) {
                arguments=savedInstanceState.getBundle(DetailFragment.DETAIL_MOVIE);

                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(DetailFragment.DETAIL_MOVIE, arguments);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(MovieDetailsForm selectedMovie) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_MOVIE, selectedMovie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailFragment.DETAIL_MOVIE, selectedMovie);
            startActivity(intent);
        }
    }

}
