package za.co.ubiquitech.vmovies;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import za.co.ubiquitech.vmovies.database.MoviesContract;
import za.co.ubiquitech.vmovies.formObjects.MovieDetailsForm;
import za.co.ubiquitech.vmovies.util.ImageDownloadAdapter;

/**
 * @author vane
 * @since 2015/09/23.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MovieFragment.class.getSimpleName();
    JSONArray movieResults = null;
    private ImageDownloadAdapter mMovieAdapter;
    private GridView gridView;

    private static final int CURSOR_LOADER = 0;


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(MovieDetailsForm selectedMovie);
    }

    public MovieFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.most_popular) {
            updateMovies("popular");
        }

        if (id == R.id.top_rated) {
            updateMovies("top_rated");
        }

        if (id == R.id.favourite) {
            updateMovies("favourite");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_activity_movies_view, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview_movies);
        getActivity().setTitle("Favourite");

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDetailsForm movieDetails = mMovieAdapter.getItem(position);
                Toast toast = Toast.makeText(getActivity(), movieDetails.getMovieName(), Toast.LENGTH_SHORT);
                toast.show();
                ((Callback) getActivity()).onItemSelected(movieDetails);
            }
        });

        //Long press gives user option to delete a request
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                if (getActivity().getTitle().equals("Favourite")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setMessage("Delete Movie from list");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MovieDetailsForm movieDetails = mMovieAdapter.getItem(position);

                            List<File> deleteFile = new ArrayList<>();

                            deleteFile.add(new File(getRealPathFromURI(movieDetails.getMovieBackdrop())));
                            deleteFile.add(new File(getRealPathFromURI(movieDetails.getMoviePoster())));
                            Uri mUri = ContentUris.withAppendedId(MoviesContract.MovieEntry.CONTENT_URI,
                                    position + 1);
                            getActivity().getContentResolver().delete(mUri, null, null);
                            mUri = ContentUris.withAppendedId(MoviesContract.ReviewEntry.CONTENT_URI,
                                    Long.parseLong(movieDetails.getMovieId()));
                            getActivity().getContentResolver().delete(mUri, null, null);
                            deletePictures((deleteFile));
                            getLoaderManager().restartLoader(CURSOR_LOADER, null, MovieFragment.this);
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });

        return rootView;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String path = cursor.getString(index);
            cursor.close();
            return path;
        }
    }

    //Delete downloaded pictures from phone storage
    private void deletePictures(List<File> fileList) {
        File file;
        for (int i = 0; i < fileList.size(); i++) {
            file = fileList.get(i);
            file.delete();
        }
    }

    private void updateMovies(String sortBy) {

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
        if (sortBy.equals("popular")) {
            fetchMoviesTask.execute(sortBy, "Most Popular");
        } else if (sortBy.equals("top_rated")) {
            fetchMoviesTask.execute(sortBy, "Top Rated");
        } else {
            getActivity().setTitle("Favourite");
            getLoaderManager().restartLoader(CURSOR_LOADER, null, MovieFragment.this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER, null, MovieFragment.this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int count = 0;
        MovieDetailsForm[] movies = new MovieDetailsForm[data.getCount()];
        if (data.getCount() > 0) {//check if cursor not empty
            data.moveToFirst();
            do {
                movies[count] = new MovieDetailsForm(
                        data.getString(1),
                        data.getString(2),
                        data.getString(3),
                        data.getString(4),
                        data.getString(5),
                        data.getString(6),
                        data.getString(7));
                count++;
                data.moveToNext();
            } while (!data.isAfterLast());
        }
        mMovieAdapter = new ImageDownloadAdapter(getActivity(), movies);
        gridView.setAdapter(mMovieAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, MovieDetailsForm[]> {

        private final Context mContext;
        private String title;
        private ProgressDialog progressDialog;

        FetchMoviesTask(Activity activity) {
            this.mContext = activity;
        }

        @Override
        protected MovieDetailsForm[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            title = params[1];

            try {
                String sortBy = params[0];
                final String MOVIES_BASE_URL = "http://api.themoviedb.org/3/movie/" + sortBy + "?api_key=" + MainActivity.MOVIE_API_KEY +
                        "&" + sortBy;

                URL url = new URL(MOVIES_BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(10 * 1000);
                urlConnection.connect();


                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    Log.v(LOG_TAG, "Inputstream is null ");
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

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
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(MovieDetailsForm[] result) {
            progressDialog.dismiss();
            if (result != null) {
                getActivity().setTitle(title);
                mMovieAdapter = new ImageDownloadAdapter(getActivity(), result);
                gridView.setAdapter(mMovieAdapter);
            } else {
                showError("Error retrieving movie, please check internet connection");
            }
        }

        private MovieDetailsForm[] getMovieDataFromJSON(String movieStr) throws JSONException {
            String moviePoster;
            String movieName;
            String moviePlot;
            String movieRating;
            String movieReleaseDate;
            String movieId;
            String movieBackdrop;

            JSONObject jsonObj = new JSONObject(movieStr);

            //get JSON ARRAY node
            movieResults = jsonObj.getJSONArray("results");
            MovieDetailsForm[] resultStrs = new MovieDetailsForm[movieResults.length() + 1];
            //loop through all movie results
            for (int i = 0; i < movieResults.length(); i++) {
                JSONObject object = movieResults.getJSONObject(i);
                movieBackdrop = "http://image.tmdb.org/t/p/w500" + object.getString("backdrop_path");
                moviePoster = "http://image.tmdb.org/t/p/w342" + object.getString("poster_path");
                movieName = object.getString("original_title");
                moviePlot = object.getString("overview");
                movieRating = object.getString("vote_average");
                movieReleaseDate = object.getString("release_date");
                movieId = object.getString("id");
                resultStrs[i] = new MovieDetailsForm(movieId, movieName, moviePoster, movieBackdrop, moviePlot, movieReleaseDate, movieRating);
            }
            return resultStrs;
        }

    }

    private void showError(String message) {
        //Display error
        final Snackbar snackBar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
        snackBar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

}