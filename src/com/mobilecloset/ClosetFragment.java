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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ClosetFragment extends ParentFragment implements OnClickListener
{
  

  int selectedPosition;
  Clothing[] clothing;
  // ArrayList<String> tags;
  LinearLayout m_vwClosetLayout;
  ViewPager pager;

  private Callback mActionModeCallback;
  private ActionMode mActionMode;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_closet, container, false);
    // m_vwClosetLayout = (LinearLayout)
    // view.findViewById(R.id.closetLayout);
    pager = (ViewPager) view.findViewById(R.id.pager);
    pager.setOnPageChangeListener(new OnPageChangeListener()
    {

      @Override
      public void onPageSelected(int arg0)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2)
      {
        // TODO Auto-generated method stub
        if (mActionMode != null)
        {
          mActionMode.finish();
        }
        mActionMode = null;
      }

      @Override
      public void onPageScrollStateChanged(int arg0)
      {
        // TODO Auto-generated method stub

      }
    });
    new ClosetURLs().execute(getActivity().getIntent().getExtras()
        .getString("tags"));
    // pager.setAdapter(new ImagePagerAdapter(IMAGES));
    // return pager;

    mActionModeCallback = new Callback()
    {
      public boolean onCreateActionMode(ActionMode mode, Menu menu)
      {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.actionmenu, menu);
        return true;
      }

      @Override
      public boolean onPrepareActionMode(ActionMode mode, Menu menu)
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean onActionItemClicked(ActionMode mode, MenuItem item)
      {
        switch (item.getItemId())
        {
        case R.id.menu_remove:
          clothing[selectedPosition].delete();
          return true;
        case R.id.menu_outfit:
          return true;
        default:
          return false;
        }
      }

      @Override
      public void onDestroyActionMode(ActionMode mode)
      {
        // TODO Auto-generated method stub

      }

    };

    return view;

  }

  @Override
  public void onClick(View v)
  {
    // TODO Auto-generated method stub
    switch (v.getId())
    {
    case R.id.closet:
      Intent intent = new Intent();
      intent.setClass(getActivity(), GenericActivity.class).putExtra(
          "fragment", ClosetFragment.class.getName());
      startActivity(intent);
      break;
    }
  }

  public class UninterceptableViewPager extends ViewPager
  {
    public UninterceptableViewPager(Context context)
    {
      super(context);
    }

    public UninterceptableViewPager(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
      // Tell our parent to stop intercepting our events!
      boolean ret = super.onInterceptTouchEvent(ev);
      if (ret)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
      }
      return ret;
    }
  }

  private class ClothingPagerAdapter extends
      android.support.v4.view.PagerAdapter
  {

    private Clothing[] clothing;
    private LayoutInflater inflater;

    ClothingPagerAdapter(Clothing[] clothing)
    {
      this.clothing = clothing;
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
      return clothing.length;
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position)
    {
      View imageLayout = inflater.inflate(R.layout.item_pager_image, view,
          false);
      TextView tagView = (TextView) imageLayout.findViewById(R.id.tags);
      ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
      final ProgressBar spinner = (ProgressBar) imageLayout
          .findViewById(R.id.loading);
      imageView.setOnLongClickListener(new OnLongClickListener()
      {

        @Override
        public boolean onLongClick(View v)
        {
          int selectedPosition = position;
          if (mActionMode != null)
          {
            return false;
          }
          mActionMode = ((SherlockFragmentActivity) getActivity())
              .startActionMode(mActionModeCallback);
          return true;
        }
      });
      tagView.setText(clothing[position].tag[0]);
      imageLoader.displayImage(clothing[position].url, imageView, options,
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
              Toast.makeText(ClosetFragment.this.getActivity(), message,
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
        HttpPost httppost = new HttpPost(
            "http://bryanching.net/mcloset/geturls2.php");
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
        // jaz = new JSONObject(jo);
        // int success = jaz.getInt(ResultFragment.TAG_SUCCESS);
        // if (success == 1)
        // {
        ja = new JSONArray(jo);
        // String[] images = new String[ja.length()];
        clothing = new Clothing[ja.length()];

        for (int i = 0; i < ja.length(); i++)
        {
          JSONObject jobj = ja.getJSONObject(i);
          JSONArray jtags = jobj.getJSONArray("tag");
          String[] tags = new String[jtags.length()];
          for (int j = 0; j < jtags.length(); j++)
          {
            tags[j] = jtags.getString(j);
          }
          clothing[i] = new Clothing(jobj.getInt("id"), jobj.getString("url"),
              tags);
          // ja.getJSONObject(i).getString("url");
          // }
        }
        pager.setAdapter(new ClothingPagerAdapter(clothing));

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
