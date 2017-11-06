package com.example.sh_polak.hiyda.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.backendless.Backendless;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.utils.ImageLoadTask;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by hackeru on 17/09/2017.
 */

public class PartyListAdapter extends BaseAdapter {

    private final List<Map> results;
    private final Context context;
    private final int res;
    public static int countReturnedViews = 0;

    public PartyListAdapter(Context c, int res, List<Map> results){
        this.results = results;
        this.context = c;
        this.res=res;

    }
    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Map getItem(int i) {
        return results.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        final Map item = getItem(i);
        final PartyHolder holder;
        if(convertView == null){
            convertView  = LayoutInflater.from(context).inflate(this.res, null);
            holder  = prepareHolder(convertView);
        }
        else holder = (PartyHolder)convertView.getTag();
        holder.mText.setText(item.get("name").toString());
        holder.mDate.setText(item.get("DateTime").toString());
        holder.mIMageView.setImageBitmap(null);//*/
        final ImageView img = ((ImageView)convertView.findViewById(R.id.circularImage));
        new ImageLoadTask(){
             protected void onPostExecute(Bitmap bmp) {
                 if(bmp != null) img.setImageBitmap(bmp);
             }
        }.execute(item.get("PartyImage").toString());
        return convertView;
    }
    private PartyHolder prepareHolder(View convertView){
        PartyHolder holder;
        holder = new PartyHolder();
        holder.mText = (TextView) convertView.findViewById(R.id.name);
        holder.mDate = (TextView) convertView.findViewById(R.id.date);
        holder.mIMageView = (ImageView) convertView.findViewById(R.id.circularImage);
        convertView.setTag(holder);
        return holder;
    }

    private class PartyHolder{
        ImageView mIMageView;
        TextView  mDate, mText;

    }

}
