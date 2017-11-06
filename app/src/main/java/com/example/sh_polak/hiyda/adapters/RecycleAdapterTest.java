package com.example.sh_polak.hiyda.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.example.sh_polak.hiyda.Activities.SpecificPartyActivity;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.utils.ImageLoadTask;

import java.util.List;
import java.util.Map;

/**
 * Created by hackeru on 9/26/2017.
 */

public class RecycleAdapterTest extends RecyclerView.Adapter<RecycleAdapterTest.RecyclerHolder> {
    Context context;
    List<Map> result;
    boolean isCliked;


    public RecycleAdapterTest(Context context, List<Map> result) {
        this.context = context;
        this.result = result;
    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {//build view
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerHolder holder, final int position) {//change the specific view
//        Map map = result.get(position);//// TODO add image and textView import the image from result.get("ImageParty")
        ((RecyclerHolder) holder).textView.setText(result.get(position).get("name").toString());

        new ImageLoadTask() {
            protected void onPostExecute(Bitmap bmp) {
                holder.imageView.setImageResource(R.drawable.no_image);
                if (bmp != null) holder.imageView.setImageBitmap(bmp);
                holder.progressBar.setVisibility(View.INVISIBLE);

            }
        }.execute(result.get(position).get("PartyImage").toString());
        holder.textView2.setText(result.get(position).get("DateTime").toString());
        if(!result.get(position).get("PartyImage").toString().equals(null))
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSpecificActivity(result.get(position).get("objectId").toString());
            }
        });
        holder.favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("!!!!!!!!!!!!!!!!!!!!!");
                if (isChecked) {
                    System.out.println("True");
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return result.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {
        public TextView textView, textView2;
        public ImageView imageView;
        public ProgressBar progressBar;
        public RelativeLayout layout;
        public boolean iscliked;
        public CheckBox favorite;

        public RecyclerHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.name);
            this.textView2 = (TextView) view.findViewById(R.id.date);
            this.imageView = (ImageView) view.findViewById(R.id.circularImage);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            this.layout = (RelativeLayout) view.findViewById(R.id.layout);
            this.favorite=(CheckBox) view.findViewById(R.id.favoriteParties);
        }

    }

    private void goToSpecificActivity(final String object) {//if cliked go to SpecificPartyActivity and send specifc row objects from backendless
        final Intent i = new Intent(context, SpecificPartyActivity.class);
        DataQueryBuilder builder = DataQueryBuilder.create();
        builder.setWhereClause("ObjectId = '"+object+"'");
        Backendless.Data.of("A_publicist_user").find(builder,new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> repnonse) {
                Map result = repnonse.get(0);
                i.putExtra("name", result.get("name").toString()).putExtra("time", result.get("DateTime").toString()).putExtra("capacity", Integer.parseInt(result.get("Capacity").toString())).putExtra("location", result.get("Location").toString()).putExtra("Image", result.get("PartyImage").toString()).putExtra("ObjectId", result.get("objectId").toString());//show current details of party  in specificParty
                context.startActivity(i);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("searchItemError", fault.getDetail());

            }
        });
    }

}
