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

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Clothing implements Parcelable
{

  int id;
  String url;
  ArrayList<String> tag;

  public Clothing(int id, String url, ArrayList<String> tag)
  {
    super();
    this.id = id;
    this.url = url;
    this.tag = tag;
  }

  public void delete()
  {
    new deleteClothing().execute("" + this.id);
  }

  public class deleteClothing extends AsyncTask<String, Void, String>
  {
    // changing String to JSONObject
    public String doInBackground(String... path)
    {
      String output = null;
      ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
      nameValuePairs.add(new BasicNameValuePair("id", path[0]));
      // nameValuePairs.add(new BasicNameValuePair("id",path[1]));

      try
      {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(
            "http://bryanching.net/mcloset/delete.php");
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        // print response
        output = EntityUtils.toString(entity);
        Log.i("GET RESPONSE—-", "output: " + output);
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

    }
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(id);
    dest.writeString(url);
    dest.writeList(tag);
  }

  public static final Parcelable.Creator<Clothing> CREATOR = new Parcelable.Creator<Clothing>()
  {
    public Clothing createFromParcel(Parcel in)
    {
      return new Clothing(in);
    }

    public Clothing[] newArray(int size)
    {
      return new Clothing[size];
    }
  };

  public Clothing(Parcel in)
  {
    super();
    this.id = in.readInt();
    this.url = in.readString();
    this.tag = in.readArrayList(String.class.getClassLoader());
  }

}