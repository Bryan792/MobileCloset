package com.mobilecloset;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ClosetFragment extends ParentFragment implements OnClickListener
{
  ArrayList<String> tags;
  LinearLayout m_vwClosetLayout;
  ViewPager pager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_closet, container, false);
//    m_vwClosetLayout = (LinearLayout) view.findViewById(R.id.closetLayout);
    pager = (ViewPager) view.findViewById(R.id.pager);
    new ClosetURLs().execute(getActivity().getIntent().getExtras()
        .getString("tags"));
    // pager.setAdapter(new ImagePagerAdapter(IMAGES));
    // return pager;
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
      intent.setClass(getActivity(), ClosetActivity.class);
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
      // nameValuePairs.add(new BasicNameValuePair("id",path[1]));

      try
      {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(
            "http://bryanching.net/mcloset/get_urls.php");
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        // print response
        output = EntityUtils.toString(entity);
        Log.i("GET RESPONSE—-", output);
        Log.e("log_tag ******", "good connection");
      } catch (Exception e)
      {
        Log.e("log_tag ******", "Error in http connection " + e.toString());
      }

      return output;
    }

    protected void onPostExecute(String jo)
    {
      JSONArray ja = null;
      try
      {
        ja = new JSONObject(jo).getJSONArray("urls");
      } catch (JSONException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      String[] images = new String[ja.length()];
      for (int i = 0; i < ja.length(); i++)
      {
        try
        {
          images[i] = ja.getJSONObject(i).getString("urls")
              .replace("\\/", "\"");
       
          System.out.println(images[0]);
        } catch (JSONException e)
        {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      final String[] IMAGES = new String[] {
          // Heavy images
          "https://lh6.googleusercontent.com/-jZgveEqb6pg/T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s1024/sample_image_01.jpg",
          "https://lh4.googleusercontent.com/-K2FMuOozxU0/T3R4lRAiBTI/AAAAAAAAAE8/a3Eh9JvnnzI/s1024/sample_image_02.jpg",
          "https://lh5.googleusercontent.com/-SCS5C646rxM/T3R4l7QB6xI/AAAAAAAAAFE/xLcuVv3CUyA/s1024/sample_image_03.jpg",
          "https://lh6.googleusercontent.com/-f0NJR6-_Thg/T3R4mNex2wI/AAAAAAAAAFI/45oug4VE8MI/s1024/sample_image_04.jpg",
          "https://lh3.googleusercontent.com/-n-xcJmiI0pg/T3R4mkSchHI/AAAAAAAAAFU/EoiNNb7kk3A/s1024/sample_image_05.jpg",
          "https://lh3.googleusercontent.com/-X43vAudm7f4/T3R4nGSChJI/AAAAAAAAAFk/3bna6D-2EE8/s1024/sample_image_06.jpg",
          "https://lh5.googleusercontent.com/-MpZneqIyjXU/T3R4nuGO1aI/AAAAAAAAAFg/r09OPjLx1ZY/s1024/sample_image_07.jpg",
          "https://lh6.googleusercontent.com/-ql3YNfdClJo/T3XvW9apmFI/AAAAAAAAAL4/_6HFDzbahc4/s1024/sample_image_08.jpg",
          "https://lh5.googleusercontent.com/-Pxa7eqF4cyc/T3R4oasvPEI/AAAAAAAAAF0/-uYDH92h8LA/s1024/sample_image_09.jpg",
          "https://lh4.googleusercontent.com/-Li-rjhFEuaI/T3R4o-VUl4I/AAAAAAAAAF8/5E5XdMnP1oE/s1024/sample_image_10.jpg",
          "https://lh5.googleusercontent.com/-_HU4fImgFhA/T3R4pPVIwWI/AAAAAAAAAGA/0RfK_Vkgth4/s1024/sample_image_11.jpg",
          "https://lh6.googleusercontent.com/-0gnNrVjwa0Y/T3R4peGYJwI/AAAAAAAAAGU/uX_9wvRPM9I/s1024/sample_image_12.jpg",
          "https://lh3.googleusercontent.com/-HBxuzALS_Zs/T3R4qERykaI/AAAAAAAAAGQ/_qQ16FaZ1q0/s1024/sample_image_13.jpg",
          "https://lh4.googleusercontent.com/-cKojDrARNjQ/T3R4qfWSGPI/AAAAAAAAAGY/MR5dnbNaPyY/s1024/sample_image_14.jpg",
          "https://lh3.googleusercontent.com/-WujkdYfcyZ8/T3R4qrIMGUI/AAAAAAAAAGk/277LIdgvnjg/s1024/sample_image_15.jpg",
          "https://lh6.googleusercontent.com/-FMHR7Vy3PgI/T3R4rOXlEKI/AAAAAAAAAGs/VeXrDNDBkaw/s1024/sample_image_16.jpg",
          "https://lh4.googleusercontent.com/-mrR0AJyNTH0/T3R4rZs6CuI/AAAAAAAAAG0/UE1wQqCOqLA/s1024/sample_image_17.jpg",
          "https://lh6.googleusercontent.com/-z77w0eh3cow/T3R4rnLn05I/AAAAAAAAAG4/BaerfWoNucU/s1024/sample_image_18.jpg",
          "https://lh5.googleusercontent.com/-aWVwh1OU5Bk/T3R4sAWw0yI/AAAAAAAAAHE/4_KAvJttFwA/s1024/sample_image_19.jpg",
          "https://lh6.googleusercontent.com/-q-js52DMnWQ/T3R4tZhY2sI/AAAAAAAAAHM/A8kjp2Ivdqg/s1024/sample_image_20.jpg",
          "https://lh5.googleusercontent.com/-_jIzvvzXKn4/T3R4t7xpdVI/AAAAAAAAAHU/7QC6eZ10jgs/s1024/sample_image_21.jpg",
          "https://lh3.googleusercontent.com/-lnGi4IMLpwU/T3R4uCMa7vI/AAAAAAAAAHc/1zgzzz6qTpk/s1024/sample_image_22.jpg",
          "https://lh5.googleusercontent.com/-fFCzKjFPsPc/T3R4u0SZPFI/AAAAAAAAAHk/sbgjzrktOK0/s1024/sample_image_23.jpg",
          "https://lh4.googleusercontent.com/-8TqoW5gBE_Y/T3R4vBS3NPI/AAAAAAAAAHs/EZYvpNsaNXk/s1024/sample_image_24.jpg",
          "https://lh6.googleusercontent.com/-gc4eQ3ySdzs/T3R4vafoA7I/AAAAAAAAAH4/yKii5P6tqDE/s1024/sample_image_25.jpg",
          "https://lh5.googleusercontent.com/--NYOPCylU7Q/T3R4vjAiWkI/AAAAAAAAAH8/IPNx5q3ptRA/s1024/sample_image_26.jpg",
          "https://lh6.googleusercontent.com/-9IJM8so4vCI/T3R4vwJO2yI/AAAAAAAAAIE/ljlr-cwuqZM/s1024/sample_image_27.jpg",
          "https://lh4.googleusercontent.com/-KW6QwOHfhBs/T3R4w0RsQiI/AAAAAAAAAIM/uEFLVgHPFCk/s1024/sample_image_28.jpg",
          "https://lh4.googleusercontent.com/-z2557Ec1ctY/T3R4x3QA2hI/AAAAAAAAAIk/9-GzPL1lTWE/s1024/sample_image_29.jpg",
          "https://lh5.googleusercontent.com/-LaKXAn4Kr1c/T3R4yc5b4lI/AAAAAAAAAIY/fMgcOVQfmD0/s1024/sample_image_30.jpg",
          "https://lh4.googleusercontent.com/-F9LRToJoQdo/T3R4yrLtyQI/AAAAAAAAAIg/ri9uUCWuRmo/s1024/sample_image_31.jpg",
          "https://lh4.googleusercontent.com/-6X-xBwP-QpI/T3R4zGVboII/AAAAAAAAAIs/zYH4PjjngY0/s1024/sample_image_32.jpg",
          "https://lh5.googleusercontent.com/-VdLRjbW4LAs/T3R4zXu3gUI/AAAAAAAAAIw/9aFp9t7mCPg/s1024/sample_image_33.jpg",
          "https://lh6.googleusercontent.com/-gL6R17_fDJU/T3R4zpIXGjI/AAAAAAAAAI8/Q2Vjx-L9X20/s1024/sample_image_34.jpg",
          "https://lh3.googleusercontent.com/-1fGH4YJXEzo/T3R40Y1B7KI/AAAAAAAAAJE/MnTsa77g-nk/s1024/sample_image_35.jpg",
          "https://lh4.googleusercontent.com/-Ql0jHSrea-A/T3R403mUfFI/AAAAAAAAAJM/qzI4SkcH9tY/s1024/sample_image_36.jpg",
          "https://lh5.googleusercontent.com/-BL5FIBR_tzI/T3R41DA0AKI/AAAAAAAAAJk/GZfeeb-SLM0/s1024/sample_image_37.jpg",
          "https://lh4.googleusercontent.com/-wF2Vc9YDutw/T3R41fR2BCI/AAAAAAAAAJc/JdU1sHdMRAk/s1024/sample_image_38.jpg",
          "https://lh6.googleusercontent.com/-ZWHiPehwjTI/T3R41zuaKCI/AAAAAAAAAJg/hR3QJ1v3REg/s1024/sample_image_39.jpg",
          // Light images
          "http://tabletpcssource.com/wp-content/uploads/2011/05/android-logo.png",
          "http://simpozia.com/pages/images/stories/windows-icon.png",
          "https://si0.twimg.com/profile_images/1135218951/gmail_profile_icon3_normal.png",
          "http://www.krify.net/wp-content/uploads/2011/09/Macromedia_Flash_dock_icon.png",
          "http://radiotray.sourceforge.net/radio.png",
          "http://www.bandwidthblog.com/wp-content/uploads/2011/11/twitter-logo.png",
          "http://weloveicons.s3.amazonaws.com/icons/100907_itunes1.png",
          "http://weloveicons.s3.amazonaws.com/icons/100929_applications.png",
          "http://www.idyllicmusic.com/index_files/get_apple-iphone.png",
          "http://www.frenchrevolutionfood.com/wp-content/uploads/2009/04/Twitter-Bird.png",
          "http://3.bp.blogspot.com/-ka5MiRGJ_S4/TdD9OoF6bmI/AAAAAAAAE8k/7ydKtptUtSg/s1600/Google_Sky%2BMaps_Android.png",
          "http://www.desiredsoft.com/images/icon_webhosting.png",
          "http://goodereader.com/apps/wp-content/uploads/downloads/thumbnails/2012/01/hi-256-0-99dda8c730196ab93c67f0659d5b8489abdeb977.png",
          "http://1.bp.blogspot.com/-mlaJ4p_3rBU/TdD9OWxN8II/AAAAAAAAE8U/xyynWwr3_4Q/s1600/antivitus_free.png",
          "http://cdn3.iconfinder.com/data/icons/transformers/computer.png",
          "http://cdn.geekwire.com/wp-content/uploads/2011/04/firefox.png?7794fe",
          "https://ssl.gstatic.com/android/market/com.rovio.angrybirdsseasons/hi-256-9-347dae230614238a639d21508ae492302340b2ba",
          "http://androidblaze.com/wp-content/uploads/2011/12/tablet-pc-256x256.jpg",
          "http://www.theblaze.com/wp-content/uploads/2011/08/Apple.png",
          "http://1.bp.blogspot.com/-y-HQwQ4Kuu0/TdD9_iKIY7I/AAAAAAAAE88/3G4xiclDZD0/s1600/Twitter_Android.png",
          "http://3.bp.blogspot.com/-nAf4IMJGpc8/TdD9OGNUHHI/AAAAAAAAE8E/VM9yU_lIgZ4/s1600/Adobe%2BReader_Android.png",
          "http://cdn.geekwire.com/wp-content/uploads/2011/05/oovoo-android.png?7794fe",
          "http://icons.iconarchive.com/icons/kocco/ndroid/128/android-market-2-icon.png",
          "http://thecustomizewindows.com/wp-content/uploads/2011/11/Nicest-Android-Live-Wallpapers.png",
          "http://c.wrzuta.pl/wm16596/a32f1a47002ab3a949afeb4f",
          "http://macprovid.vo.llnwd.net/o43/hub/media/1090/6882/01_headline_Muse.jpg",
      // // Special cases
      // "file:///sdcard/Universal Image Loader @#&=+-_.,!()~'%20.png", // Image
      // // from
      // // SD
      // // card
      // // with
      // // encoded
      // // symbols
      // "assets://Living Things @#&=+-_.,!()~'%20.jpg", // Image from assets
      // "drawable://" + R.drawable.ic_launcher, // Image from drawables
      // "http://upload.wikimedia.org/wikipedia/ru/b/b6/Как_кот_с_мышами_воевал.png",
      // // Link
      // // with
      // // UTF-8
      // "https://www.eff.org/sites/default/files/chrome150_0.jpg", // Image from
      // // HTTPS
      // "http://bit.ly/soBiXr", // Redirect link
      // "http://img001.us.expono.com/100001/100001-1bc30-2d736f_m.jpg", // EXIF
      // "", // Empty link
      // "http://wrong.site.com/corruptedLink", // Wrong link
      };
      pager.setAdapter(new ImagePagerAdapter(images));
    }
  }
}
