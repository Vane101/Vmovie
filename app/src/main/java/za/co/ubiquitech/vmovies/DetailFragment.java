package za.co.ubiquitech.vmovies;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DialerFilter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import za.co.ubiquitech.vmovies.database.MoviesContract;
import za.co.ubiquitech.vmovies.formObjects.MovieDetailsForm;
import za.co.ubiquitech.vmovies.formObjects.MovieReviews;
import za.co.ubiquitech.vmovies.formObjects.Review;
import za.co.ubiquitech.vmovies.util.CustomReviewsViewAdapter;
import za.co.ubiquitech.vmovies.util.CustomTrailerViewAdapter;
import za.co.ubiquitech.vmovies.util.JsonRequests;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private CustomTrailerViewAdapter mTrailerAdapter;
    private MovieDetailsForm selectedMovie;
    private MovieReviews currentMovieReview;
    private TwoWayView customTrailerListView;
    private int mPosition;
    private Uri mUri;
    private static final int CURSOR_LOADER = 1;
    static final String DETAIL_MOVIE = "SELECTED_MOVIE";
    static final String REVIEW_KEY = "REVIEW_KEY";

    private ShareActionProvider mShareActionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (savedInstanceState != null) {
            currentMovieReview = savedInstanceState.getParcelable(REVIEW_KEY);
            selectedMovie = savedInstanceState.getParcelable(DETAIL_MOVIE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_detail, menu);

        MenuItem item = menu.findItem(R.id.share_trailer);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        ;

        /** Setting a share intent */
        mShareActionProvider.setShareIntent(getDefaultShareIntent());
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Returns a share intent
     */
    private Intent getDefaultShareIntent() {
        String trailerMessage;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        trailerMessage = "Vmovies: No movie Trailer";
        sendIntent.putExtra(Intent.EXTRA_TEXT, trailerMessage);
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        CoordinatorLayout detailLayout = (CoordinatorLayout) rootView.findViewById(R.id.detail_layout);

        Bundle arguments = getArguments();
        if (arguments != null) {
            selectedMovie = arguments.getParcelable(DetailFragment.DETAIL_MOVIE);
        }

        if (selectedMovie != null) {
            detailLayout.setVisibility(View.VISIBLE);
            customTrailerListView = (TwoWayView) rootView.findViewById(R.id.trailer_list_view);
            ((TextView) rootView.findViewById(R.id.detail_movie_name)).setText(selectedMovie.getMovieName());
            ((TextView) rootView.findViewById(R.id.detail_movie_plot)).setText(selectedMovie.getMoviePlot());
            ((TextView) rootView.findViewById(R.id.release_date_view)).setText(selectedMovie.getMovieReleaseDate());
            ((TextView) rootView.findViewById(R.id.rating_view)).setText(selectedMovie.getMovieRating() + "/10");

            ImageView moviePoster = (ImageView) rootView.findViewById(R.id.detail_poster_image);
            Picasso.with(getActivity())
                    .load(selectedMovie.getMovieBackdrop())
                    .placeholder(R.mipmap.placeholder_film)
                    .error(R.mipmap.placeholder_error)
                    .into(moviePoster);

            this.mPosition = Integer.parseInt(selectedMovie.getMovieId());
            this.mUri = ContentUris.withAppendedId(MoviesContract.ReviewEntry.CONTENT_URI,
                    mPosition);

            if (currentMovieReview != null) {
                mTrailerAdapter = new CustomTrailerViewAdapter(getActivity(), currentMovieReview.getMovieYouTubeURL());
                customTrailerListView.setAdapter(mTrailerAdapter);
            } else {
                getLoaderManager().restartLoader(CURSOR_LOADER, null, DetailFragment.this);
            }

            Button btn_review = (Button) rootView.findViewById(R.id.btn_reviews);
            btn_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentMovieReview.getReviews() != null && currentMovieReview.getReviews().size() > 0) {
                        //Custom Dialog
                        final Dialog dialog = new Dialog(getContext(), R.style.AlertDialogStyle);
                        dialog.setContentView(R.layout.custom_dialog_review);
                        dialog.setTitle("Movie Reviews");
                        //Place data in dialog box
                        ListView customReviewsListView = (ListView) dialog.findViewById(R.id.reviews_list_view);
                        CustomReviewsViewAdapter mReviewsAdapter = new CustomReviewsViewAdapter(getActivity(), currentMovieReview.getReviews());
                        customReviewsListView.setAdapter(mReviewsAdapter);
                        dialog.show();

                    } else {
                        showError("No reviews found for this movie");
                    }
                }
            });

            customTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //http://intransitione.com/blog/play-a-video-on-youtube-using-an-intent/
                    String movieKey = mTrailerAdapter.getItem(position);
                    Uri uri = Uri.parse(movieKey);
                    Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(youTubeIntent);
                }
            });

            FloatingActionButton favButton = (FloatingActionButton) rootView.findViewById(R.id.btn_fav_submit);
            favButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 safeImages();
                                             }
                                         }
            );

        } else {
            detailLayout.setVisibility(View.GONE);
        }

        return rootView;
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                mUri,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    // Set the cursor in our CursorAdapter once the Cursor is loaded
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int count = 0;
        int authorIndex = data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR);
        int contentIndex = data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT);
        int movieId = data.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID);

        Review[] reviews = new Review[data.getCount()];
        if (data.getCount() > 0) {//check if cursor not empty
            data.moveToFirst();
            do {
                reviews[count] = new Review(data.getString(authorIndex),
                        data.getString(contentIndex),
                        data.getString(movieId));
                count++;
                data.moveToNext();
            } while (!data.isAfterLast());
        }

        currentMovieReview = new MovieReviews();
        if (data.getCount() > 0) {
            currentMovieReview.setReviews(Arrays.asList(reviews));
        } else {
            updateReviews();
        }
    }

    // reset CursorAdapter on Loader Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void safeImages() {
        Picasso.with(getActivity()).load(selectedMovie.getMoviePoster()).into(target);
        Picasso.with(getActivity()).load(selectedMovie.getMovieBackdrop()).into(target2);

        ///Movie poster file name
        String filename = "poster_" + selectedMovie.getMovieId() + ".jpg";
        File fileDir = new File(getAppStorageDir());
        String mFilePath = fileDir.toString();
        File file = new File(mFilePath, filename);
        selectedMovie.setMoviePoster("file://" + file.toString());

        ///Movie backdrop file name
        filename = "backdrop_" + selectedMovie.getMovieId() + ".jpg";
        fileDir = new File(getAppStorageDir());
        mFilePath = fileDir.toString();
        file = new File(mFilePath, filename);
        selectedMovie.setMovieBackdrop("file://" + file.toString());
        insertData(selectedMovie);
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = null;
                    try {
                        String filename = "poster_" + selectedMovie.getMovieId() + ".jpg";

                        File fileDir = new File(getAppStorageDir());
                        String mFilePath = fileDir.toString();

                        File dir = new File(mFilePath);
                        if (!dir.mkdirs()) {
                            Log.e(LOG_TAG, "Directory not created");
                        }
                        file = new File(mFilePath, filename);
                        if (file.exists())
                            file.delete();
                        file.createNewFile();

                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }
    };
    private Target target2 = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File file = null;

                    try {
                        String filename = "backdrop_" + selectedMovie.getMovieId() + ".jpg";

                        File fileDir = new File(getAppStorageDir());
                        String mFilePath = fileDir.toString();

                        File dir = new File(mFilePath);
                        if (!dir.mkdirs()) {
                            Log.e(LOG_TAG, "Directory not created");
                        }

                        file = new File(mFilePath, filename);
                        if (file.exists())
                            file.delete();
                        file.createNewFile();

                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }
    };

    public String getAppStorageDir() {
        // Get the directory for the user's public pictures directory.
        String appName = getActivity().getResources().getString(R.string.app_name);
        File file = new File(Environment.getExternalStoragePublicDirectory(appName), "Images");
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file.toString();
    }

    // insert data into database
    public void insertData(MovieDetailsForm movie) {

        ContentValues movieValuesArr = new ContentValues();
        movieValuesArr.put(MoviesContract.MovieEntry.COLUMN_ID, movie.getMovieId());
        movieValuesArr.put(MoviesContract.MovieEntry.COLUMN_NAME, movie.getMovieName());
        movieValuesArr.put(MoviesContract.MovieEntry.COLUMN_POSTER, movie.getMoviePoster());
        movieValuesArr.put(MoviesContract.MovieEntry.COLUMN_PLOT, movie.getMoviePlot());
        movieValuesArr.put(MoviesContract.MovieEntry.COLUMN_BACKDROP, movie.getMovieBackdrop());
        movieValuesArr.put(MoviesContract.MovieEntry.COLUMN_RELEASEDATE, movie.getMovieReleaseDate());
        movieValuesArr.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.getMovieRating());

        // bulkInsert ContentValues array
        Uri uri = getActivity().getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI,
                movieValuesArr);
        if (uri == null) {
            showError(selectedMovie.getMovieName() + " is already in your favourite list");

        } else {
            writeReviewsToDatabase(currentMovieReview.getReviews());
            showError(selectedMovie.getMovieName() + " has been added to your favourite list");
        }
    }

    private void writeReviewsToDatabase(List<Review> reviews) {
        Vector<ContentValues> cVVector = new Vector<ContentValues>(reviews.size());

        for (int i = 0; i < reviews.size(); i++) {
            ContentValues reviewArr = new ContentValues();

            Review movieReviews = reviews.get(i);

            reviewArr.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, movieReviews.getMovieId());
            reviewArr.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, movieReviews.getAuthor());
            reviewArr.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, movieReviews.getContent());

            cVVector.add(reviewArr);
        }
        // bulkInsert reviews
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getActivity().getContentResolver().bulkInsert(MoviesContract.ReviewEntry.CONTENT_URI,
                    cvArray);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(REVIEW_KEY, currentMovieReview);
        outState.putParcelable(DETAIL_MOVIE, selectedMovie);
    }

    private void updateReviews() {
        FetchReviewTask fetchReviewTask = new FetchReviewTask();
        fetchReviewTask.execute();
    }

    public class FetchReviewTask extends AsyncTask<Void, Void, MovieReviews> {
        private ProgressDialog progressDialog;

        @Override
        protected MovieReviews doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            try {
                final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
                final String apiKey = "/reviews?api_key=" + MainActivity.MOVIE_API_KEY;

                URL url = new URL(MOVIES_BASE_URL + selectedMovie.getMovieId() + apiKey);
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
                return getReviewDataFromJSON(moviesJsonStr);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private MovieReviews getReviewDataFromJSON(String movieStr) throws JSONException {
            String author;
            String content;
            List<String> movieYouTubeUrl;

            JSONObject jsonObj = new JSONObject(movieStr);

            //get JSON ARRAY node
            JSONArray movieResults = jsonObj.getJSONArray("results");
            List<Review> reviewList = new ArrayList<>();
            //loop through all movie results
            for (int i = 0; i < movieResults.length(); i++) {
                JSONObject object = movieResults.getJSONObject(i);
                author = object.getString("author");
                content = object.getString("content");
                reviewList.add(new Review(author, content, selectedMovie.getMovieId()));
            }
            movieYouTubeUrl = new JsonRequests().FetchMovieVideoKey(selectedMovie.getMovieId());
            return new MovieReviews(movieYouTubeUrl, reviewList);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Loading Reviews...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(MovieReviews result) {
            progressDialog.dismiss();
            if (result != null) {
                currentMovieReview = result;
                String trailerMessage;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");
                if (!currentMovieReview.getMovieYouTubeURL().isEmpty()) {
                    trailerMessage = "Vmovies: " + selectedMovie.getMovieName() + ", Watch Trailer: " + currentMovieReview.getMovieYouTubeURL().get(0);

                } else {
                    trailerMessage = "Vmovies: " + selectedMovie.getMovieName() + " NO youTube trailer available";
                }
                sendIntent.putExtra(Intent.EXTRA_TEXT, trailerMessage);
                mShareActionProvider.setShareIntent(sendIntent);

                mTrailerAdapter = new CustomTrailerViewAdapter(getActivity(), result.getMovieYouTubeURL());
                customTrailerListView.setAdapter(mTrailerAdapter);
            }
        }

    }

}
