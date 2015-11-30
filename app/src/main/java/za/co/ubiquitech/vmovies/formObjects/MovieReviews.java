package za.co.ubiquitech.vmovies.formObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vane
 * @since 18/10/2015
 */
public class MovieReviews implements Parcelable {
    private List<Review> reviews = new ArrayList<Review>();
    ;
    private List<String> movieYouTubeURL = new ArrayList<String>();

    public MovieReviews(List<String> youTubeURL, List<Review> reviews) {
        this.movieYouTubeURL = youTubeURL;
        this.reviews = reviews;
    }

    protected MovieReviews(Parcel in) {
        this.movieYouTubeURL = new ArrayList<String>();
        in.readList(movieYouTubeURL, null);
        this.reviews = new ArrayList<Review>();
        in.readList(reviews, MovieReviews.class.getClassLoader());

    }

    public MovieReviews() {
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<String> getMovieYouTubeURL() {
        return movieYouTubeURL;
    }

    public void setMovieYouTubeURL(List<String> movieYouTubeURL) {
        this.movieYouTubeURL = movieYouTubeURL;
    }

    public static final Creator<MovieReviews> CREATOR = new Creator<MovieReviews>() {
        @Override
        public MovieReviews createFromParcel(Parcel in) {
            return new MovieReviews(in);
        }

        @Override
        public MovieReviews[] newArray(int size) {
            return new MovieReviews[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(movieYouTubeURL);
        parcel.writeList(reviews);
    }
}
