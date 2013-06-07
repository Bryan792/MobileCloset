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

public class Outfit implements Parcelable
{
  int oid;
  String name;
  ArrayList<Clothing> clothing;

  public Outfit(int oid, String name, ArrayList<Clothing> clothing)
  {
    super();
    this.oid = oid;
    this.name = name;
    this.clothing = clothing;
  }

  public void delete()
  {
    new deleteOutfit().execute("" + this.oid);
  }

  @Override
  public int describeContents()
  {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeInt(oid);
    dest.writeString(name);
    dest.writeList(clothing);
  }

  public static final Parcelable.Creator<Outfit> CREATOR = new Parcelable.Creator<Outfit>()
  {
    public Outfit createFromParcel(Parcel in)
    {
      return new Outfit(in);
    }

    public Outfit[] newArray(int size)
    {
      return new Outfit[size];
    }
  };

  public Outfit(Parcel in)
  {
    super();
    this.oid = in.readInt();
    this.name = in.readString();
    this.clothing = in.readArrayList(Clothing.class.getClassLoader());
  }

  public void addClothing(Clothing input)
  {
    new addToOutfit().execute("" + oid, "" + input.id);
  }

  public class addToOutfit extends AsyncTask<String, Void, String>
  {
    // changing String to JSONObject
    public String doInBackground(String... path)
    {
      String output = null;
      ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
      nameValuePairs.add(new BasicNameValuePair("oid", path[0]));
      nameValuePairs.add(new BasicNameValuePair("id", path[1]));

      // nameValuePairs.add(new BasicNameValuePair("id",path[1]));

      try
      {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(
            "http://bryanching.net/mcloset/insert_into_outfit.php");
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

  public class deleteOutfit extends AsyncTask<String, Void, String>
  {
    // changing String to JSONObject
    public String doInBackground(String... path)
    {
      String output = null;
      ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
      nameValuePairs.add(new BasicNameValuePair("oid", path[0]));

      // nameValuePairs.add(new BasicNameValuePair("id",path[1]));

      try
      {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(
            "http://bryanching.net/mcloset/delete_outfit.php");
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

}
