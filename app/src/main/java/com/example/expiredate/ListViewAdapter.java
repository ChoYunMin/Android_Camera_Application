package com.example.expiredate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private Context context = null;
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();
    //private Bitmap bitmapSumnail;
    //ImageView itemImageView;

    public ListViewAdapter(Context mContext){
        super();
        context = mContext;
    }

    // Adapter에 사용되는 데이터 개수를 리턴
    @Override
    public int getCount(){
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        // listview_item layout을 inflate 하여 convertview 참조 획득
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        // 화면에 표시될 View(Layout이 inflate된)로부터 위젯에 대한 참조 획득
        ImageView itemImageView = (ImageView)convertView.findViewById(R.id.item_sumnail);
        TextView itemNameView = (TextView)convertView.findViewById(R.id.item_name);
        TextView itemExpirydateView = (TextView)convertView.findViewById(R.id.item_expirydate);
        TextView itemBuydateView = (TextView)convertView.findViewById(R.id.item_buydate);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        //ListViewItem listViewItem = listViewItemList.get(position);
        ListViewItem listViewItem = getItem(position);

        Bitmap bitmapSumnail = null;
        try{
            bitmapSumnail = new DownloadImageTask().execute(listViewItem.getItemUrl()).get();
        }catch (Exception e){
            e.printStackTrace();
        }

        // 아이템 내 각 위젯에 데이터 반영
        if(bitmapSumnail == null){
            Drawable drawable = context.getResources().getDrawable(R.drawable.camera_color);
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            itemImageView.setImageBitmap(bitmap);
        }else{
            itemImageView.setImageBitmap(bitmapSumnail);
        }

        itemNameView.setText(listViewItem.getItemName());
        itemExpirydateView.setText(listViewItem.getItemExpirydate());
        itemBuydateView.setText(listViewItem.getItemBuydate());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position){
        //return position;
        return 0;
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public ListViewItem getItem(int position){
        return listViewItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수, 원하는 대로 작성 가능
    public void addItem(String url, String name, String buydate, String expirydate){
        ListViewItem item = new ListViewItem();

        item.setItemUrl(url);
        item.setItemName(name);
        item.setItemBuydate(buydate);
        item.setItemExpirydate(expirydate);

        listViewItemList.add(item);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        public DownloadImageTask(){}

        protected Bitmap doInBackground(String... urls){
            String urldisplay = urls[0];
            Bitmap bitmap = null;

            try{
                InputStream in = new URL(urldisplay).openStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                bitmap = BitmapFactory.decodeStream(in, null, options);
            }catch(Exception e){
                Log.e("No Image: ", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result){
            //itemImage = result;
            /*
            if(result == null){
                Drawable drawable = context.getResources().getDrawable(R.drawable.camera_color);
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                itemImageView.setImageBitmap(bitmap);
            }else{
                itemImageView.setImageBitmap(result);
            }
            */
        }
    }
}
