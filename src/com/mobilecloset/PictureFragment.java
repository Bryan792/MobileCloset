package com.mobilecloset;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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

public class PictureFragment extends ParentFragment implements OnClickListener
{
  ImageView formPic;
  TextView formCategory;
  TextView formTag;
  String path;
  Button upload;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    View view = inflater.inflate(R.layout.fragment_picture, container, false);
    formPic = (ImageView) view.findViewById(R.id.formPic);
    formCategory = (TextView) view.findViewById(R.id.formCategory);
    formTag = (TextView) view.findViewById(R.id.formTag);
    upload = (Button) view.findViewById(R.id.upload);
    upload.setOnClickListener(this);
    path = getActivity().getIntent().getStringExtra("path");
    imageLoader.displayImage("file://" + path, formPic, options);

    // new Upload().execute(path);
    return view;
  }

  class Upload extends AsyncTask<String, Void, Void>
  {

    protected Void doInBackground(String... unused)
    {
      try
      {
        String path = unused[0];
        File picture = new File(path);
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        // DataInputStream inputStream = null;

        String urlServer = "http://bryanching.net/mcloset/handle_upload.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        // EditText tagsField = (EditText) findViewById(R.id.editText1);
        // String tags;
        // tags = tagsField.getText().toString();
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        FileInputStream fileInputStream = new FileInputStream(picture);

        String categories = new String("|" + formCategory.getText());
        String tags = new String();
        for (String string : formTag.getText().toString().split(" "))
        {
          tags = tags.concat("|" + string);
        }
        URL url = new URL(urlServer);
        connection = (HttpURLConnection) url.openConnection();
        if (connection != null)
        { // Allow Inputs &Outputs
          connection.setDoInput(true);
          connection.setDoOutput(true);
          connection.setUseCaches(false);

          // Enable POST method connection.setRequestMethod("POST");

          connection.setRequestProperty("Connection", "Keep-Alive");
          connection.setRequestProperty("Content-Type",
              "multipart/form-data;boundary=" + boundary);

          outputStream = new DataOutputStream(connection.getOutputStream());
          outputStream.writeBytes(twoHyphens + boundary + lineEnd);
          Log.d("tags", tags);
          outputStream
              .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                  + path + categories + tags + "\"" + lineEnd);
          outputStream.writeBytes(lineEnd);

          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          buffer = new byte[bufferSize];

          // Read file
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);

          while (bytesRead > 0)
          {
            outputStream.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
          }

          outputStream.writeBytes(lineEnd);
          outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

          // Responses from the server (code and message)
          int serverResponseCode = connection.getResponseCode();
          String serverResponseMessage = connection.getResponseMessage();

          Log.d(serverResponseMessage, "Response Code: " + serverResponseCode);
          Log.d(serverResponseMessage, "Response message: "
              + serverResponseMessage);
          fileInputStream.close();
          outputStream.flush();
          outputStream.close();

        }
      } catch (Exception e)
      {
        e.printStackTrace();
      }
      return null;
    }

    protected void onPostExecute(Void unused)
    {
      getActivity().finish();
    }

  }

  @Override
  public void onClick(View v)
  {
    // TODO Auto-generated method stub
    switch (v.getId())
    {
    case R.id.upload:
      new Upload().execute(path);
      break;
    }
  }

}