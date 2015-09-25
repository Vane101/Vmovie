package za.co.ubiquitech.vmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import za.co.ubiquitech.vmovies.formObjects.MovieDetailsForm;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("selected_movie")) {
            MovieDetailsForm movieDetailsForm = (MovieDetailsForm) intent.getSerializableExtra("selected_movie");
            ((TextView) rootView.findViewById(R.id.detail_movie_name)).setText(movieDetailsForm.getMovieName());
            ((TextView) rootView.findViewById(R.id.detail_movie_plot)).setText(movieDetailsForm.getMoviePlot());
            ((TextView) rootView.findViewById(R.id.release_date_view)).setText(movieDetailsForm.getMovieRealseDate());
            ((TextView) rootView.findViewById(R.id.rating_view)).setText(Double.toString(movieDetailsForm.getMovieRating()) + "/10");

            ImageView moviePoster = (ImageView) rootView.findViewById(R.id.detail_poster_image);
            Picasso.with(getActivity())
                    .load(movieDetailsForm.getMoviePoster())
                    .into(moviePoster);
        }
        return rootView;
    }
}
