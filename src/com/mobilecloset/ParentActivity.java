package com.mobilecloset;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ParentActivity extends SherlockFragmentActivity implements
    SearchView.OnQueryTextListener, SearchView.OnSuggestionListener
{

  protected static final String STATE_PAUSE_ON_SCROLL = "STATE_PAUSE_ON_SCROLL";
  protected static final String STATE_PAUSE_ON_FLING = "STATE_PAUSE_ON_FLING";
  protected ImageLoader imageLoader = ImageLoader.getInstance();

  protected ParentActivity listView;

  protected boolean pauseOnScroll = false;
  protected boolean pauseOnFling = true;

  /** Request codes for starting new Activities. */
  // private static final int PICTURE_DOWNLOAD = 1;
  public static final int PICTURE_UPLOAD = 2;
  public static final int SELECT_PICTURE = 3;

  InputStream is;
  public static final boolean cFlag = true;

  private Bitmap bm;

  private static File mediaFile = null;
  private String selectedImagePath;

  private static final String[] COLUMNS = { BaseColumns._ID,
      SearchManager.SUGGEST_COLUMN_TEXT_1, };

  private static SuggestionsAdapter mSuggestionsAdapter;

  public boolean onOptionsItemSelected(MenuItem item)
  {
    Intent intent;
    switch (item.getItemId())
    {

    // case R.id.menu_download:
    // if (haveInternet(this)) {
    // downloadPictures();
    // //intent = new
    // Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    // //startActivityForResult(intent,PICTURE_DOWNLOAD);
    // } else {
    // displayToast("No internet connectivity");
    // }
    // return true;
    case R.id.menu_upload:
      if (haveInternet(this))
      {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri = getOutputMediaFileUri(PICTURE_UPLOAD);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PICTURE_UPLOAD);
      }
      else
      {
        displayToast("No internet connectivity");
      }
      return true;
    case R.id.menu_select:
      if (haveInternet(this))
      {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
            SELECT_PICTURE);
      }
      else
      {
        displayToast("No internet connectivity");
      }
      return true;
      // some stuff from grid view that we have yet to implement
      /*
       * case R.id.item_pause_on_scroll: pauseOnScroll = !pauseOnScroll;
       * item.setChecked(pauseOnScroll); applyScrollListener(); return true;
       * case R.id.item_pause_on_fling: pauseOnFling = !pauseOnFling;
       * item.setChecked(pauseOnFling); applyScrollListener(); return true;
       */
    default:
      return super.onOptionsItemSelected(item);
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK)
    {
      if (requestCode == SELECT_PICTURE)
      {
        Uri selectedImageUri = data.getData();
        getPath(selectedImageUri);
        if (haveInternet(this))
        {
          upload(selectedImagePath);

        }
        else
        {
          displayToast("No Internect Connectivity");
        }

      }
      else if (requestCode == PICTURE_UPLOAD)
      {
        // data.getDataString();
        Log.d("path", "PATH TO picture IS: " + mediaFile.getAbsolutePath());
        if (haveInternet(this))
        {
          upload(mediaFile.getAbsolutePath());

        }
        else
        {
          displayToast("No Internect Connectivity");
        }

      }
    }
  }

  public void upload(String path)
  {
    Intent intent = new Intent().putExtra("path", path);
    intent.setClass(this, GenericActivity.class).putExtra("fragment",
        PictureFragment.class.getName());
    startActivity(intent);
  }

  public void getPath(Uri uri)
  {
    String[] filePathColumn = { MediaStore.Images.Media.DATA };

    Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null,
        null);
    cursor.moveToFirst();

    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
    selectedImagePath = cursor.getString(columnIndex);
    cursor.close();

    Log.d("IMAGE PATH", "filePath: " + selectedImagePath);
  }

  public void displayToast(String message)
  {
    String toastText = message;

    Toast toast = Toast.makeText(this, toastText, Toast.LENGTH_SHORT);
    toast.show();
  }

  /**
   * private Bitmap decodeFile(File f) throws IOException { Bitmap b = null;
   * 
   * // Decode image size // final int IMAGE_MAX_SIZE = 1000;
   * BitmapFactory.Options o = new BitmapFactory.Options(); o.inJustDecodeBounds
   * = true;
   * 
   * FileInputStream fis = new FileInputStream(f);
   * BitmapFactory.decodeStream(fis, null, o); fis.close();
   * 
   * int scale = 1; /* if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth >
   * IMAGE_MAX_SIZE) { scale = (int) Math.pow( 2, (int)
   * Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(o.outHeight,
   * o.outWidth)) / Math.log(0.5))); }
   * 
   * 
   * // Decode with inSampleSize BitmapFactory.Options o2 = new
   * BitmapFactory.Options(); o2.inSampleSize = scale; fis = new
   * FileInputStream(f); b = BitmapFactory.decodeStream(fis, null, o2);
   * fis.close();
   * 
   * return b; }
   */

  /**
   * Check if we have internet access
   * 
   * @param ctx
   * @return
   */
  public static boolean haveInternet(Context ctx)
  {

    NetworkInfo info = (NetworkInfo) ((ConnectivityManager) ctx
        .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

    if (info == null)
    {
      return false;
    }
    else if (!info.isConnected())
    {
      return false;
    }
    if (info.isRoaming())
    {
      return false;
    }
    return true;
  }

  public void startUploading()
  {

    new AddTask().execute();

  }

  class AddTask extends AsyncTask<Void, Void, Void>
  {

    private ProgressDialog dialog;

    protected void onPreExecute()
    {

      dialog = new ProgressDialog(ParentActivity.this);
      dialog.setMessage("Retrieving data ...");
      dialog.setIndeterminate(true);
      dialog.setCancelable(false);
      dialog.show();

    }

    protected Void doInBackground(Void... unused)
    {

      ByteArrayOutputStream bao = new ByteArrayOutputStream();
      // takes up a lot of memory..I think
      bm.compress(Bitmap.CompressFormat.JPEG, 75, bao);
      // bm.compress(Bitmap.CompressFormat.PNG, 100, bao);
      byte[] ba = bao.toByteArray();
      // only works on api level 8+
      String imageString = Base64.encodeToString(ba, Base64.DEFAULT);
      ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
      nameValuePairs.add(new BasicNameValuePair("image", imageString));
      nameValuePairs.add(new BasicNameValuePair("name", "randomThink"));
      try
      {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(
            "http://sunjaydhama.com/projects/mobileCloset/image_upload.php");
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response = httpclient.execute(httppost);
        // responsez = convertResponseToString(response);
        HttpEntity entity = response.getEntity();
        is = entity.getContent();
      }
      catch (Exception e)
      {
        Log.e("log_tag", "Error in http connection " + e.toString());
      }
      return (null);
    }

    protected void onProgressUpdate(Void... unused)
    {
      // grid_main.setAdapter(imgadp);
    }

    protected void onPostExecute(Void unused)
    {
      Log.d("post execute", "**********Success!!!!********");
      dialog.dismiss();

    }
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

        String tags = new String("|hats");
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
                  + path + tags + "\"" + lineEnd);
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
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return null;
    }

  }

  /*
   * public String convertResponseToString(HttpResponse response) throws
   * IllegalStateException, IOException {
   * 
   * String res = ""; StringBuffer buffer = new StringBuffer(); int
   * contentLength = (int) response.getEntity().getContentLength(); // getting
   * // content // length�.. // Toast.makeText(UploadImage.this,
   * "contentLength : " + // contentLength, Toast.LENGTH_LONG).show(); if
   * (contentLength < 0) { } else { byte[] data = new byte[512]; int len = 0;
   * try { while (-1 != (len = is.read(data))) { buffer.append(new String(data,
   * 0, len)); // converting // to string // and // appending // to //
   * stringbuffer�.. } } catch (IOException e) { e.printStackTrace(); } try {
   * is.close(); // closing the stream�.. } catch (IOException e) {
   * e.printStackTrace(); } res = buffer.toString(); // converting stringbuffer
   * to string�..
   * 
   * // Toast.makeText(UploadImage.this, "Result : " + res, //
   * Toast.LENGTH_LONG).show(); // System.out.println("Response => " + //
   * EntityUtils.toString(response.getEntity())); } return res;
   * 
   * }
   * 
   * }
   */

  private static File getOutputMediaFile(int type)
  {

    File mediaStorageDir;
    mediaStorageDir = new File(
        Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "MobileCloset");

    if (!mediaStorageDir.exists())
    {
      /*
       * If there isn't one, we have to create it. Call mediaStorageDir's
       * mkdirs() method Check to see if it did NOT create the directory
       * appropriately.
       */
      if (!mediaStorageDir.mkdirs())
      {
        /*
         * 
         * If both of these checks fail, send a debug Log message to Logcat from
         * WalkAbout stating that the directory creation process was a fail,
         * then return null.
         */
        Log.d("File", "Directory creation process was a fail");
        return null;
      }
    }
    String timestam = null;
    timestam = DateFormat.getDateTimeInstance().format(new Date());
    timestam = timestam.replaceAll("\\s", "_");
    timestam = timestam.replaceAll(":", "-");

    mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"
        + timestam + ".jpg");

    return mediaFile;
  }

  public static Uri getOutputMediaFileUri(int fileType)
  {

    return Uri.fromFile(getOutputMediaFile(fileType));
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {

    // Create the search view
    SearchView searchView = new SearchView(getSupportActionBar()
        .getThemedContext());
    searchView.setQueryHint("Search by tag");
    searchView.setOnQueryTextListener(this);
    searchView.setOnSuggestionListener(this);

    if (mSuggestionsAdapter == null)
    {
      MatrixCursor cursor = new MatrixCursor(COLUMNS);
//      cursor.addRow(new String[] { "1", "cream" });
//      cursor.addRow(new String[] { "2", "collar" });
//      cursor.addRow(new String[] { "3", "fancy" });
//      cursor.addRow(new String[] { "4", "shirt" });
//      cursor.addRow(new String[] { "5", "dress" });
//      cursor.addRow(new String[] { "6", "skirt" });
//      cursor.addRow(new String[] { "7", "pants" });
//      cursor.addRow(new String[] { "8", "shorts" });
      mSuggestionsAdapter = new SuggestionsAdapter(getSupportActionBar()
          .getThemedContext(), cursor);
    }

    searchView.setSuggestionsAdapter(mSuggestionsAdapter);

    menu.add("Search")
        .setIcon(R.drawable.ic_action_ic_action_search)
        .setActionView(searchView)
        .setShowAsAction(
            MenuItem.SHOW_AS_ACTION_ALWAYS
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

    // for grid view
    /*
     * MenuItem pauseOnScrollItem = menu.findItem(R.id.item_pause_on_scroll);
     * pauseOnScrollItem.setVisible(true);
     * pauseOnScrollItem.setChecked(pauseOnScroll);
     * 
     * MenuItem pauseOnFlingItem = menu.findItem(R.id.item_pause_on_fling);
     * pauseOnFlingItem.setVisible(true);
     * pauseOnFlingItem.setChecked(pauseOnFling);
     */

    return true;
  }

  @Override
  public boolean onQueryTextSubmit(String query)
  {
    Intent intent = new Intent().putExtra("query", query);
    intent.setClass(this, GenericActivity.class).putExtra("fragment",
        ResultFragment.class.getName());
    startActivity(intent);
    // Toast.makeText(this, "You searched for: " + query,
    // Toast.LENGTH_LONG).show();
    return true;
  }

  @Override
  public boolean onQueryTextChange(String newText)
  {
    return false;
  }

  @Override
  public boolean onSuggestionSelect(int position)
  {
    return false;
  }

  @Override
  public boolean onSuggestionClick(int position)
  {
    Cursor c = (Cursor) mSuggestionsAdapter.getItem(position);
    String query = c.getString(c
        .getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
    Toast.makeText(this, "Suggestion clicked: " + query, Toast.LENGTH_LONG)
        .show();
    return true;
  }

  private class SuggestionsAdapter extends CursorAdapter
  {

    public SuggestionsAdapter(Context context, Cursor c)
    {
      super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
      LayoutInflater inflater = LayoutInflater.from(context);
      View v = inflater.inflate(android.R.layout.simple_list_item_1, parent,
          false);
      return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
      TextView tv = (TextView) view;
      final int textIndex = cursor
          .getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
      tv.setText(cursor.getString(textIndex));
    }
  }

  public void onRestoreInstanceState(Bundle savedInstanceState)
  {
    pauseOnScroll = savedInstanceState.getBoolean(STATE_PAUSE_ON_SCROLL, false);
    pauseOnFling = savedInstanceState.getBoolean(STATE_PAUSE_ON_FLING, true);
  }

  @Override
  public void onResume()
  {
    super.onResume();
    applyScrollListener();
  }

  private void applyScrollListener()
  {
    // listView.setOnScrollListener(new
    // PauseOnScrollListener(imageLoader,pauseOnScroll, pauseOnFling));
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    outState.putBoolean(STATE_PAUSE_ON_SCROLL, pauseOnScroll);
    outState.putBoolean(STATE_PAUSE_ON_FLING, pauseOnFling);
  }

}
