package za.co.ubiquitech.vmovies.formObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vane
 * @since 8/21/2015.
 */
public class MovieDetailsForm implements Parcelable {
    private String moviePoster;
    private String movieName;
    private String moviePlot;
    private String movieRating;
    private String movieId;
    private String movieReleaseDate;
    private String movieBackdrop;


    public MovieDetailsForm(String movieId, String movieName, String moviePoster, String movieBackdrop,
                            String moviePlot, String movieReleaseDate, String movieRating) {
        this.movieBackdrop = movieBackdrop;
        this.moviePoster = moviePoster;
        this.movieName = movieName;
        this.moviePlot = moviePlot;
        this.movieRating = movieRating;
        this.movieReleaseDate = movieReleaseDate;
        this.movieId = movieId;

    }

    public static final Creator<MovieDetailsForm> CREATOR = new Creator<MovieDetailsForm>() {
        @Override
        public MovieDetailsForm createFromParcel(Parcel in) {
            return new MovieDetailsForm(in);
        }

        @Override
        public MovieDetailsForm[] newArray(int size) {
            return new MovieDetailsForm[size];
        }
    };

    public String getMoviePoster() {
        return moviePoster;
    }

    public String getMovieName() {
        return movieName;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getMovieBackdrop() {
        return movieBackdrop;
    }

    public String getMovieReleaseDate() {
        return movieReleaseDate;
    }

    public void setMovieBackdrop(String movieBackdrop) {
        this.movieBackdrop = movieBackdrop;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected MovieDetailsForm(Parcel in) {
        this.moviePoster = in.readString();
        this.movieName = in.readString();
        this.moviePlot = in.readString();
        this.movieReleaseDate = in.readString();
        this.movieId = in.readString();
        this.movieRating = in.readString();
        this.movieBackdrop = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(moviePoster);
        parcel.writeString(movieName);
        parcel.writeString(moviePlot);
        parcel.writeString(movieReleaseDate);
        parcel.writeString(movieId);
        parcel.writeString(movieRating);
        parcel.writeString(movieBackdrop);
    }
}
