package com.saepulohzone.rambupintar;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by SaepulohZone on 25/05/2016.
 */
public class GridViewAdapter extends BaseAdapter {
    private ArrayList<String> list;
    private Activity activity;
    private AssetManager assetManager;
    private InputStream is;
    private LayoutInflater inflater;

    public GridViewAdapter(Activity activity, ArrayList<String> list){
        super();
        this.activity=activity;
        this.list=list;
    }
    @Override
    public int getCount(){
        return list.size();
    }
    @Override
    public String getItem(int position){
        return list.get(position);
    }
    @Override
    public long getItemId(int position){
        return 0;
    }
    public static class ViewHolder{
        public ImageView mImageView;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder view;
        inflater=activity.getLayoutInflater();
        if(convertView==null){
            view=new ViewHolder();
            convertView=inflater.inflate(
                    R.layout.layout_item_grid,null);
            view.mImageView=(ImageView)convertView
                    .findViewById(R.id.image);
            convertView.setTag(view);
        }else{
            view=(ViewHolder)convertView.getTag();
        }
        Log.d("Data Dalam Grid", list.get(position));
        assetManager=activity.getAssets();
        try {
            Log.d("Memasukan", list.get(position)+".png");
            is=assetManager.open(list.get(position)+".png");
            Bitmap bitmap= BitmapFactory.decodeStream(is);
            view.mImageView.setImageBitmap(bitmap);
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return convertView;
    }
}
