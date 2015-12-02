package za.co.ubiquitech.vmovies.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import za.co.ubiquitech.vmovies.R;
import za.co.ubiquitech.vmovies.formObjects.MovieDetailsForm;
import za.co.ubiquitech.vmovies.formObjects.MovieReviews;

/**
 * @author Vane
 * @since 17/10/2015
 */
public class CustomTrailerViewAdapter extends BaseAdapter {

    private final Context context;
    private List<String> youTubeKeys = new ArrayList<String>();

    public CustomTrailerViewAdapter(Context context,List<String> videoKey) {
        this.context = context;
        this.youTubeKeys=videoKey;
    }
    @Override
    public int getCount() {
        return youTubeKeys.size();
    }

    @Override
    public String getItem(int i) {
        return youTubeKeys.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String trailer="Trailer ";
        ViewHolder viewHolder;

        if(convertView ==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_trailer, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.trailerTextView = (TextView)  convertView.findViewById(R.id.textview_trailer);
            viewHolder.trailerThumbnail=(ImageView) convertView.findViewById(R.id.trailer_thumbnail);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.trailerTextView.setText(trailer + (position + 1));
        String[] bits = youTubeKeys.get(position).split("=");
        String youTubeKey = bits[bits.length-1];
        String url="http://img.youtube.com/vi/"+youTubeKey+"/sddefault.jpg";
        Log.v("test","URL: "+url);
        Picasso.with(context)
                .load(url)
                .placeholder(R.mipmap.placeholder_youtube)
                .error(R.mipmap.placeholder_error)
                .into(viewHolder.trailerThumbnail);
        return  convertView;
    }

    static class ViewHolder {
        TextView trailerTextView;
        ImageView trailerThumbnail ;
    }
}
