package za.co.ubiquitech.vmovies.formObjects;

import java.io.Serializable;

/**
 * @author vane
 * @since 8/21/2015.
 */
public class MovieDetailsForm implements Serializable {
    String moviePoster;
    String movieName;
    String moviePlot;
    Double movieRating;
    String movieRealseDate;

    public MovieDetailsForm(String moviePoster, String movieName, String moviePlot, Double movieRating, String movieRealseDate) {
        this.moviePoster = moviePoster;
        this.movieName = movieName;
        this.moviePlot = moviePlot;
        this.movieRating = movieRating;
        this.movieRealseDate = movieRealseDate;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public void setMoviePlot(String moviePlot) {
        this.moviePlot = moviePlot;
    }

    public Double getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(Double movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieRealseDate() {
        return movieRealseDate;
    }

    public void setMovieRealseDate(String movieRealseDate) {
        this.movieRealseDate = movieRealseDate;
    }

}
