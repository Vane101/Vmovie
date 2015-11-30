package za.co.ubiquitech.vmovies.formObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Vane
 * @since 22/10/2015
 */
public class Review implements Parcelable {

    private String author;
    private String content;
    private String movieId;

    public Review(String author, String content, String movieId) {
        this.author = author;
        this.content = content;
        this.movieId = movieId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    protected Review(Parcel in) {
        author = in.readString();
        content = in.readString();
        movieId = in.readString();

    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(movieId);
    }
}
