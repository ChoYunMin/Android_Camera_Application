package com.example.expiredate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

public class ListViewItem {
    private String itemUrl;
    private String itemName;
    private String itemBuydate;
    private String itemExpirydate;
    private Bitmap itemImage;

    public void setItemUrl(String url){
        itemUrl = url;
        //new DownloadImageTask().execute(itemUrl);
    }

    public void setItemName(String name){
        itemName = name;
    }

    public void setItemBuydate(String buydate){
        itemBuydate = buydate;
    }

    public void setItemExpirydate(String expirydate){
        itemExpirydate = expirydate;
    }

    public Bitmap getItemImage(){
        return this.itemImage;
    }

    public String getItemName(){
        return this.itemName;
    }

    public String getItemBuydate(){
        return this.itemBuydate;
    }

    public String getItemExpirydate(){
        return this.itemExpirydate;
    }

    public String getItemUrl(){
        return this.itemUrl;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{
        public DownloadImageTask(){}

        protected Bitmap doInBackground(String... urls){
            String urldisplay = urls[0];
            Bitmap bitmap = null;

            try{
                InputStream in = new URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            }catch(Exception e){
                Log.e("No Image: ", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result){
            itemImage = result;
        }
    }
}
