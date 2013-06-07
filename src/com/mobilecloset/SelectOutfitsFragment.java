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
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.mobilecloset.OutfitsFragment.CreateOutfit;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
//import com.mobilecloset.ClosetFragment.ImagePagerAdapter;
//import com.nostra13.example.universalimageloader.R;

public class SelectOutfitsFragment extends AbsListViewBaseFragment implements
    OnClickListener
{
  ArrayList<Outfit> outfits;
  String[] imageUrls;
  EditText outfitName;
  Clothing clothing;
  Button saveOutfit;
  DisplayImageOptions options;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    ActionBar abs = getSherlockActivity().getSupportActionBar();
    abs.setTitle("Select Outfit");
    abs.setDisplayHomeAsUpEnabled(true);
    setHasOptionsMenu(true);

    View view = inflater.inflate(R.layout.fragment_outfits, container, false);
    // Bundle bundle = getIntent().getExtras();
    // imageUrls = bundle.getStringArray(Extra.IMAGES);
    saveOutfit = (Button) view.findViewById(R.id.saveButton);
    saveOutfit.setOnClickListener(this);
    outfitName = (EditText) view.findViewById(R.id.newJokeEditText);
    options = new DisplayImageOptions.Builder()
        .showStubImage(R.drawable.ic_stub)
        .showImageForEmptyUri(R.drawable.ic_empty)
        .showImageOnFail(R.drawable.ic_error).cacheInMemory().cacheOnDisc()
        .bitmapConfig(Bitmap.Config.RGB_565).build();
    clothing = getActivity().getIntent().getParcelableExtra("clothing");
    new ClosetURLs().execute();
    return view;
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu)
  {
    super.onPrepareOptionsMenu(menu);

    // This does work
    // MenuItem someMenuItem = menu.findItem(R.id.menu_upload);
    // someMenuItem.setVisible(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
    case android.R.id.home:
      // This ID represents the Home or Up button. In the case of this
      // activity, the Up button is shown. Use NavUtils to allow users
      // to navigate up one level in the application structure. For
      // more details, see the Navigation pattern on Android Design:
      //
      // http://developer.android.com/design/patterns/navigation.html#up-vs-back
      //
      NavUtils.navigateUpTo(getActivity(), new Intent(getActivity(),
          GenericActivity.class).putExtra("fragment",
          HomeFragment.class.getName()));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void startImagePagerActivity(int position)
  {
//    Intent intent = new Intent(getActivity(), GenericActivity.class).putExtra(
//        "fragment", SelectOutfitFragment.class.getName());
//    intent.putExtra("outfit", outfits.get(position));
//    intent.putExtra("clothing", clothing);
//    // intent.putExtra(Extra.IMAGE_POSITION, position);
//    startActivity(intent);
    outfits.get(position).addClothing(clothing);
    getActivity().finish();
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

      if (outfits.get(position).clothing.isEmpty())
      {
        imageLoader
            .displayImage(
                "http://image.shutterstock.com/display_pic_with_logo/1252747/115370923/stock-photo-men-s-outfit-115370923.jpg",
                imageView, options);
      }
      else
      {
        imageLoader.displayImage(outfits.get(position).clothing.get(0).url,
            imageView, options);
      }
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
      nameValuePairs.add(new BasicNameValuePair("name", HomeFragment.id));
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
            if (!jobj.isNull("id"))
              temp.add(new Clothing(jobj.getInt("id"), jobj.getString("url"),
                  tags));
          }
          outfits.add(new Outfit(out2.getInt("oid"), out2
              .getString("outfitName"), temp));
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

  public class CreateOutfit extends AsyncTask<String, Void, String>
  {
    // changing String to JSONObject
    public String doInBackground(String... path)
    {
      String output = null;
      ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
      nameValuePairs.add(new BasicNameValuePair("name", HomeFragment.id));
      nameValuePairs.add(new BasicNameValuePair("outfitName", path[0]));
      nameValuePairs.add(new BasicNameValuePair("id", path[1]));
      // nameValuePairs.add(new BasicNameValuePair("id",path[1]));

      try
      {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(
            "http://bryanching.net/mcloset/insert_outfit.php");
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
  }

  @Override
  public void onClick(View v)
  {
    switch (v.getId())
    {
    case R.id.saveButton:
      if (!outfitName.getText().toString().isEmpty())
      {
        System.out.println("clicked");
        new CreateOutfit().execute(outfitName.getText().toString(), ""
            + clothing.id);
      }
      InputMethodManager imm = (InputMethodManager) getActivity()
          .getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(outfitName.getWindowToken(), 0);
      getActivity().finish();
      break;
    }
  }

}