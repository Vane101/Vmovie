package za.co.ubiquitech.vmovies.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import za.co.ubiquitech.vmovies.MainActivity;

/**
 * @author Vane
 * @since 20/10/2015
 */
public class JsonRequests {
    private static final String LOG_TAG = JsonRequests.class.getSimpleName();

    public List<String> FetchMovieVideoKey(String id) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        try {

            final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String apiKey = "/videos?api_key=" + MainActivity.MOVIE_API_KEY;

            URL url = new URL(MOVIES_BASE_URL + id + apiKey);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder buffer = new StringBuilder();

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
                buffer.append(line).append("\n");
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

    private List<String> getMovieDataFromJSON(String movieStr) throws JSONException {
        String movieKey;

        JSONObject jsonObj = new JSONObject(movieStr);

        //get JSON ARRAY node
        JSONArray movieResults = jsonObj.getJSONArray("results");
        List<String> resultStrs = new ArrayList<>();
        //loop through all movie results
        for (int i = 0; i < movieResults.length(); i++) {
            JSONObject object = movieResults.getJSONObject(i);
            String baseYouTubeUrl = "http://www.youtube.com/watch?v=";
            movieKey = baseYouTubeUrl + object.getString("key");
            resultStrs.add(movieKey);
        }
        return resultStrs;
    }

}




