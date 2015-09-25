package za.co.ubiquitech.vmovies;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import za.co.ubiquitech.vmovies.formObjects.MovieDetailsForm;
import za.co.ubiquitech.vmovies.util.ImageDownloadAdapter;

/**
 * @author vane
 * @since 2015/09/23.
 */
public class MainActivityMoviesViewFragment extends Fragment {
    private final String LOG_TAG = MainActivityMoviesViewFragment.class.getSimpleName();
    JSONArray movieResults = null;
    private static ProgressDialog progressDialog;
    private ImageDownloadAdapter mMovieAdapter;
    private GridView gridView;

    public MainActivityMoviesViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateMovies();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMoveResults();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMoveResults() {

        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity_movies_view, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview_movies);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDetailsForm movieDetails = mMovieAdapter.getItem(position);
                Toast toast = Toast.makeText(getActivity(), movieDetails.getMovieName(), Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(getActivity(), MovieDetailActivity.class).putExtra("selected_movie", movieDetails);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute();
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieDetailsForm[]> {

        @Override
        protected MovieDetailsForm[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            try {

                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
                String sortBy = SP.getString("sort_by", "");

                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/discover/movie?api_key=" + MainActivity.MOVIE_API_KEY +
                        "&" + sortBy;

                URL url = new URL(MOVIES_BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                InputStream inpustStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();

                if (inpustStream == null) {
                    Log.v(LOG_TAG, "Inputstream is null ");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inpustStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                //Stream was emplty. No need to parse
                if (buffer.length() == 0) {
                    Log.v(LOG_TAG, "Buffer is null ");
                    return null;
                }

                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.d(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJSON(moviesJsonStr);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(MovieDetailsForm[] result) {
            progressDialog.dismiss();
            if (result != null) {
                mMovieAdapter = new ImageDownloadAdapter(getActivity(), result);
                gridView.setAdapter(mMovieAdapter);
            }
        }

        private MovieDetailsForm[] getMovieDataFromJSON(String movieStr) throws JSONException {
            String moviePoster;
            String movieName;
            String moviePlot;
            Double movieRating;
            String movieRealseDate;

            JSONObject jsonObj = new JSONObject(movieStr);

            //get JSON ARRAY node
            movieResults = jsonObj.getJSONArray("results");
            MovieDetailsForm[] resultStrs = new MovieDetailsForm[movieResults.length() + 1];
            //loop through all movie results
            for (int i = 0; i < movieResults.length(); i++) {
                JSONObject object = movieResults.getJSONObject(i);
                moviePoster = "http://image.tmdb.org/t/p/w185" + object.getString("poster_path");
                movieName = object.getString("original_title");
                moviePlot = object.getString("overview");
                movieRating = object.getDouble("vote_average");
                movieRealseDate = object.getString("release_date");
                resultStrs[i] = new MovieDetailsForm(moviePoster, movieName, moviePlot, movieRating, movieRealseDate);
            }

            for (MovieDetailsForm s : resultStrs) {
                Log.v(LOG_TAG, "Movie Results: " + s);
            }
            return resultStrs;
        }

    }

}