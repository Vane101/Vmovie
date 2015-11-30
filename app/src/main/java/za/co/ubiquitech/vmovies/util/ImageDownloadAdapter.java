package za.co.ubiquitech.vmovies.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import za.co.ubiquitech.vmovies.R;
import za.co.ubiquitech.vmovies.formObjects.MovieDetailsForm;


/**
 * @author vane
 * @since 8/20/2015.
 */
public class ImageDownloadAdapter extends BaseAdapter {
    private final String LOG_TAG = ImageDownloadAdapter.class.getSimpleName();
    private final Context context;
    private final List<MovieDetailsForm> movies = new ArrayList<MovieDetailsForm>();


    public ImageDownloadAdapter(Context context, MovieDetailsForm[] movieDetailForms) {
        this.context = context;
        Collections.addAll(movies, movieDetailForms);
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public MovieDetailsForm getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);
            holder = new ViewHolder();
            holder.movieImageView = (ImageView) convertView.findViewById(R.id.poster_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (getItem(position) != null) {
            String url = getItem(position).getMoviePoster();
            Picasso.with(context)
                    .load(url)
                    .into(holder.movieImageView);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView movieImageView;
    }

}
