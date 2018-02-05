package com.example.sh_polak.hiyda.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.backendless.persistence.DataQueryBuilder;
import com.example.sh_polak.hiyda.Activities.SpecificPartyActivity;
import com.example.sh_polak.hiyda.R;
import com.example.sh_polak.hiyda.utils.ImageLoadTask;
import com.example.sh_polak.hiyda.utils.MySqlLite;

import java.util.List;
import java.util.Map;

/**
 * Created by hackeru on 9/26/2017.
 */

public class RecycleAdapterList extends RecyclerView.Adapter<RecycleAdapterList.RecyclerHolder> {
    Context context;
    List<Map> result;
    SQLiteDatabase db;

    public RecycleAdapterList(Context context, List<Map> result) {
        this.context = context;
        this.result = result;
      try{
          db = new MySqlLite(context).getReadableDatabase();

      }catch (NullPointerException e){
          System.out.println(e);
      }


    }

    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {//build view
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        //View v =new View(context);
        final RecyclerHolder holder = new RecyclerHolder(view);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = holder.getIndex();

                if (!result.get(index).get("PartyImage").toString().equals(null))
                    goToSpecificActivity(result.get(index).get("objectId").toString());
            }
        });
        return holder;
    }


    //Change the specific view
    @Override
    public void onBindViewHolder(final RecyclerHolder holder, final int position) {

        Cursor c = db.rawQuery("SELECT * FROM favoritess ",null );
            final String partyName = result.get(position).get("name").toString();
            final String partyDate = result.get(position).get("DateTime").toString();
            final String partyImage = result.get(position).get("PartyImage").toString();
         holder.textView.setText(result.get(position).get("name").toString());
        ImageLoadTask task = new ImageLoadTask() {//Configure the imageView.
            @Override
            protected void onPreExecute() {
                holder.imageView.setImageBitmap(null);
                holder.progressBar.setVisibility(View.VISIBLE);
            }
           protected void onPostExecute(Bitmap bmp) {
                if (bmp != null) {
                    holder.imageView.setImageBitmap(bmp);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                } else {
                    holder.imageView.setImageResource(R.drawable.no_image);
                }
            }
        };
        holder.loadTask(task, partyImage);
        holder.textView2.setText(partyDate);
        holder.setIndex(position);
        ifExistInTable(holder,partyName);
        addToFavoritesList(holder,partyName,partyImage,partyDate);
        }


    private void ifExistInTable(RecyclerHolder holder, String partyName) {//checking if value exist in sql table favoritess if set checked else unchecked

       if (db != null) {
            Cursor c = db.rawQuery("SELECT name FROM favoritess WHERE name =?", new String[]{partyName}); // check if the value exists
            if (c.moveToFirst()) { //check if found the value
                holder.favorite.setChecked(true);
                boolean b = c.getString(0).equals(partyName);
                if (c.getString(0).equals(partyName)) {// if the value match set checked current row
                    holder.favorite.setChecked(true);
                } else
                    holder.favorite.setChecked(false);
            } else if (holder.favorite.isChecked()) {
                holder.favorite.setChecked(false);
            }
        }
    }

    private void addToFavoritesList(RecyclerHolder holder,String partyname,String partyImage ,String partyDate) { //when checked add to sql  table favoritess when is not checked remove..
        holder.favorite.setOnCheckedChangeListener((buttonView, isChecked) -> {
           String insert;
           if(buttonView.isPressed()){
            if (isChecked) {
                insert = " INSERT INTO favoritess (name,PartyImage,DateTime,isFavorite) VALUES ((?),(?),(?),'1') ";
                db.execSQL(insert, new String[]{partyname, partyImage, partyDate});
            } else {
                insert = " DELETE FROM favoritess WHERE name =(?)";
                db.execSQL(insert, new String[]{partyname});
            }
        }});
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
        public CheckBox favorite; // add to list MyFavorites list.
        private int index;
        private ImageLoadTask currentTask;

        public RecyclerHolder(View view) {
            super(view);
            this.textView = (TextView) view.findViewById(R.id.name);
            this.textView2 = (TextView) view.findViewById(R.id.date);
            this.imageView = (ImageView) view.findViewById(R.id.circularImage);
            this.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            this.layout = (RelativeLayout) view.findViewById(R.id.layout);
            this.favorite = (CheckBox) view.findViewById(R.id.favoriteParties);
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void loadTask(ImageLoadTask newTask, String image) {//Avoid bug error of reloding images that not placed right.
            if (this.currentTask != null) //if there is old task already, cancel it
                this.currentTask.cancel(false);
            this.currentTask = newTask; //set new reference
            //run new task
            newTask.execute(image);
        }
    }

    private void goToSpecificActivity(final String object) {//if clicked go to SpecificPartyActivity and send specifc row objects from backendless
        final Intent i = new Intent(context, SpecificPartyActivity.class);
        DataQueryBuilder builder = DataQueryBuilder.create();
        builder.setWhereClause("ObjectId = '" + object + "'");
        i.putExtra("objectId","ObjectId = '" + object + "'");
        context.startActivity(i);
    }

}
