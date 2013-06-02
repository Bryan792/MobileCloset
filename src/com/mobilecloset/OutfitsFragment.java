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
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class OutfitsFragment extends ParentFragment
{
  // JSON Node names
  public static final String TAG_SUCCESS = "success";
  private static String url_outfits = "http://bryanching.net/mcloset/outfits.php";
  ArrayList<String> tags;
  LinearLayout m_vwClosetLayout;
  ViewPager pager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    View view = inflater.inflate(R.layout.gridview, container, false);
    pager = (ViewPager) view.findViewById(R.id.pager);
    GridView gridView;
    gridView = (GridView) view.findViewById(R.id.gridview);
    // gridView.setAdapter(new ImageAdapter(view.getContext()));
    return gridView;
    // return view;
  }

  private class ImagePagerAdapter extends android.support.v4.view.PagerAdapter
  {

    private String[] images;
    private LayoutInflater inflater;

    ImagePagerAdapter(String[] images)
    {
      this.images = images;
      inflater = getActivity().getLayoutInflater();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
      ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void finishUpdate(View container)
    {
    }

    @Override
    public int getCount()
    {
      return images.length;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position)
    {
      View imageLayout = inflater.inflate(R.layout.item_pager_image, view,
          false);
      ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
      final ProgressBar spinner = (ProgressBar) imageLayout
          .findViewById(R.id.loading);

      imageLoader.displayImage(images[position], imageView, options,
          new SimpleImageLoadingListener()
          {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
              spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                FailReason failReason)
            {
              String message = null;
              switch (failReason.getType())
              {
              case IO_ERROR:
                message = "Input/Output error";
                break;
              case DECODING_ERROR:
                message = "Image can't be decoded";
                break;
              case NETWORK_DENIED:
                message = "Downloads are denied";
                break;
              case OUT_OF_MEMORY:
                message = "Out Of Memory error";
                break;
              case UNKNOWN:
                message = "Unknown error";
                break;
              }
              Toast.makeText(OutfitsFragment.this.getActivity(), message,
                  Toast.LENGTH_SHORT).show();

              spinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                Bitmap loadedImage)
            {
              spinner.setVisibility(View.GONE);
            }
          });

      ((ViewPager) view).addView(imageLayout, 0);
      return imageLayout;
    }

    public boolean isViewFromObject(View view, Object object)
    {
      return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader)
    {
    }

    @Override
    public Parcelable saveState()
    {
      return null;
    }

    @Override
    public void startUpdate(View container)
    {
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
      nameValuePairs.add(new BasicNameValuePair("name", PlacesFragment.id));

      // nameValuePairs.add(new BasicNameValuePair("id",path[1]));

      try
      {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url_outfits);
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
      JSONObject jaz = null;
      try
      {
        jaz = new JSONObject(jo);
        int success = jaz.getInt("success");
        if (success == 1)
        {
          ja = new JSONObject(jo).getJSONArray("urls");
          String[] images = new String[ja.length()];
          for (int i = 0; i < ja.length(); i++)
          {
            images[i] = ja.getJSONObject(i).getString("urls")
                .replace("\\/", "\"");
          }
          pager.setAdapter(new ImagePagerAdapter(images));
        }
        else
        {

          Toast toast = Toast.makeText(getActivity(), "Not Found",
              Toast.LENGTH_SHORT);
          toast.show();
          Intent intent = new Intent();
          // intent.setClass(getActivity(),
          // GenericActivity.class).putExtra("fragment",
          // PlacesActivity.class.getName());
          startActivity(intent);
        }

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
