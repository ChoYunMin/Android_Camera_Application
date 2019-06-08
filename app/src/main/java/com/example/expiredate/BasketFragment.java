package com.example.expiredate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class BasketFragment extends Fragment {
    String itemJSON;
    JSONArray items = null;
    ArrayList<HashMap<String, String>> itemList;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_NAME = "name";
    private static final String TAG_BUYDATE = "buydate";
    private static final String TAG_EXPIRYDATE = "expirydate";

    ListView itemListView;

    public BasketFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_basket, container, false);

        //ArrayAdapter Adapter = new ArrayAdapter(getActivity(), android.R.layout.list_item)
        itemList = new ArrayList<HashMap<String, String>>();
        itemListView = (ListView)view.findViewById(R.id.listView_basket);
        getData("http://192.168.35.86/PHP_connection.php");
        return view;
    }

    protected void showList(){
        try{
            JSONObject jsonObject = new JSONObject(itemJSON);
            items = jsonObject.getJSONArray(TAG_RESULTS);

            for(int i = 0; i<items.length(); i++){
                JSONObject c = items.getJSONObject(i);
                String name = c.getString(TAG_NAME);
                String buydate = c.getString(TAG_BUYDATE);
                String expirydate = c.getString(TAG_EXPIRYDATE);

                HashMap<String, String> itemsHashMap = new HashMap<String, String>();

                itemsHashMap.put(TAG_NAME, name);
                itemsHashMap.put(TAG_BUYDATE, buydate + " 구입");
                itemsHashMap.put(TAG_EXPIRYDATE, expirydate + " 까지");

                itemList.add(itemsHashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
              getActivity(), itemList, R.layout.list_item,
              new String[]{TAG_NAME, TAG_BUYDATE , TAG_EXPIRYDATE},
              new int[]{R.id.item_name, R.id.item_buydate, R.id.item_expirydate}
            );

            itemListView.setAdapter(adapter);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params){
                String uri = params[0];

                BufferedReader bufferedReader = null;
                try{
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine()) != null){
                        sb.append(json + '\n');
                    }

                    return sb.toString().trim();
                }catch (Exception e){
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result){
                itemJSON = result;
                showList();
            }
        }

        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}
