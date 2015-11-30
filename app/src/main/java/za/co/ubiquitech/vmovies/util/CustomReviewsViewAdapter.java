package za.co.ubiquitech.vmovies.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import za.co.ubiquitech.vmovies.R;
import za.co.ubiquitech.vmovies.formObjects.MovieReviews;
import za.co.ubiquitech.vmovies.formObjects.Review;

/**
 * @author Vane
 * @since 18/10/2015
 */
public class CustomReviewsViewAdapter extends BaseAdapter {
    private final Context context;
    private List<Review> movieReviews = new ArrayList<Review>();

    public CustomReviewsViewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        movieReviews = reviews;
    }

    @Override
    public int getCount() {
        return movieReviews.size();
    }

    @Override
    public Review getItem(int i) {

        return movieReviews.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_reviews, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.authorTextView = (TextView) convertView.findViewById(R.id.list_item_reviews_author_textview);
            viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.list_item_reviews_content_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.authorTextView.setText(getItem(position).getAuthor());
        viewHolder.contentTextView.setText(getItem(position).getContent());

        return convertView;
    }

    static class ViewHolder {
        TextView authorTextView;
        TextView contentTextView;
    }

}
