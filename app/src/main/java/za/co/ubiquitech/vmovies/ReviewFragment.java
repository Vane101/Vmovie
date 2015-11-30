package za.co.ubiquitech.vmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import za.co.ubiquitech.vmovies.formObjects.MovieReviews;
import za.co.ubiquitech.vmovies.util.CustomReviewsViewAdapter;

public class ReviewFragment extends Fragment {
    private final String LOG_TAG = ReviewFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie_review, container, false);
        ListView customReviewsListView = (ListView) rootView.findViewById(R.id.reviews_list_view);
        Bundle args = getArguments();
        MovieReviews currentMovieReview = args.getParcelable("review");
        CustomReviewsViewAdapter mReviewsAdapter = new CustomReviewsViewAdapter(getActivity(), currentMovieReview.getReviews());
        customReviewsListView.setAdapter(mReviewsAdapter);
        return rootView;
    }

}
