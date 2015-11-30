package za.co.ubiquitech.vmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Vane
 * @since 21/10/2015
 */
public class MoviesDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = MoviesDBHelper.class.getSimpleName();

    //name and version
    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1; /// TODO: 21/10/2015

    MoviesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Create database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MoviesContract.MovieEntry.TABLE_MOVIES + "(" + MoviesContract.MovieEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.COLUMN_ID + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.COLUMN_POSTER + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_BACKDROP + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_PLOT + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_RELEASEDATE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_RATING + " TEXT);";


        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
                MoviesContract.ReviewEntry.TABLE_REVIEWS + "(" +
                MoviesContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +

                "FOREIGN KEY (" + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_MOVIES + " (" + MoviesContract.MovieEntry._ID + ") );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    // Upgrade database when version is changed.
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " +
                newVersion + ". OLD DATA WILL BE DESTROYED");
        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_MOVIES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                MoviesContract.MovieEntry.TABLE_MOVIES + "'");

        // Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewEntry.TABLE_REVIEWS);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                MoviesContract.ReviewEntry.TABLE_REVIEWS + "'");

        // re-create database
        onCreate(sqLiteDatabase);
    }
}
