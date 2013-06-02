package com.mobilecloset;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.mobilecloset.ClosetFragment.ImagePagerAdapter;
//import com.nostra13.example.universalimageloader.R;

public class OutfitsFragment extends AbsListViewBaseFragment
{
  ArrayList<Outfit> outfits;
  String[] imageUrls;

  DisplayImageOptions options;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    View view = inflater.inflate(R.layout.ac_image_grid, container, false);
    // Bundle bundle = getIntent().getExtras();
    // imageUrls = bundle.getStringArray(Extra.IMAGES);

    options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.ic_stub)
        .showImageForEmptyUri(R.drawable.ic_empty)
        .showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc()
        .bitmapConfig(Bitmap.Config.RGB_565).build();

    new ClosetURLs().execute("shoes");
    return view;
  }

  private void startImagePagerActivity(int position)
  {
    Intent intent = new Intent(getActivity(), GenericActivity.class).putExtra(
        "fragment", OutfitFragment.class.getName());
    intent.putExtra("outfit", outfits.get(position));
    // intent.putExtra(Extra.IMAGE_POSITION, position);
    startActivity(intent);
  }

  public class ImageAdapter extends BaseAdapter
  {
    @Override
    public int getCount()
    {
      return outfits.size();
    }

    @Override
    public Object getItem(int position)
    {
      return null;
    }

    @Override
    public long getItemId(int position)
    {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
      final ImageView imageView;
      if (convertView == null)
      {
        imageView = (ImageView) getActivity().getLayoutInflater().inflate(
            R.layout.item_grid_image, parent, false);
      }
      else
      {
        imageView = (ImageView) convertView;
      }

      imageLoader.displayImage(outfits.get(position).clothing.get(0).url,
          imageView, options);

      return imageView;
    }
  }

  public class ClosetURLs extends AsyncTask<String, Void, String>
  {
    // changing String to JSONObject
    public String doInBackground(String... path)
    {
      String output = null;
      ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
      nameValuePairs.add(new BasicNameValuePair("tag", path[0]));
      // nameValuePairs.add(new BasicNameValuePair("id",path[1]));

      try
      {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(
            "http://bryanching.net/mcloset/outfit.php");
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        // print response
        output = EntityUtils.toString(entity);
        Log.i("GET RESPONSE—-", output);
        Log.e("log_tag ******", "good connection");
      }
      catch (Exception e)
      {
        Log.e("log_tag ******", "Error in http connection " + e.toString());
      }

      return output;
    }

    protected void onPostExecute(String jo)
    {
      JSONArray ja = null;
      JSONArray out = null;
      JSONObject out2 = null;
      JSONObject jaz = null;
      
      try
      {
        // jaz = new JSONObject(jo);
        // int success = jaz.getInt(ResultFragment.TAG_SUCCESS);
        // if (success == 1)
        // {
        out = new JSONArray(jo);
        // String[] images = new String[ja.length()];
        outfits = new ArrayList<Outfit>();
        for (int k = 0; k < out.length(); k++)
        {
          out2 = out.getJSONObject(k);
          ja = out2.getJSONArray("clothing");
          ArrayList<Clothing> temp = new ArrayList<Clothing>();
          for (int i = 0; i < ja.length(); i++)
          {
            JSONObject jobj = ja.getJSONObject(i);
            JSONArray jtags = jobj.getJSONArray("tag");
            ArrayList<String> tags = new ArrayList<String>(jtags.length());
            for (int j = 0; j < jtags.length(); j++)
            {
              tags.add(jtags.getString(j));
            }
            temp.add(new Clothing(jobj.getInt("id"), jobj.getString("url"),
                tags));
          }
          outfits.add(new Outfit(out2.getString("outfitName"), temp));
        }
        listView = (GridView) getView().findViewById(R.id.gridview);
        ((GridView) listView).setAdapter(new ImageAdapter());
        listView.setOnItemClickListener(new OnItemClickListener()
        {
          @Override
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id)
          {
            startImagePagerActivity(position);
          }
        });

      }
      catch (JSONException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      catch (Exception e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

    }
  }

}