package com.mobilecloset;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CategoryFragment extends ParentFragment {
	ArrayList<JSONObject> tags;
	ListView m_vwTagsList;
	TagAdapter m_TagAdapter;

	// LinearLayout m_vwClosetLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar abs = getSherlockActivity().getSupportActionBar();
		abs.setTitle("My Categories");
		abs.setDisplayHomeAsUpEnabled(true);
		setHasOptionsMenu(true);

		View view = inflater.inflate(R.layout.fragment_category, container,
				false);
		m_vwTagsList = (ListView) view.findViewById(R.id.tagList);
		tags = new ArrayList<JSONObject>();
		m_TagAdapter = new TagAdapter(getActivity(), R.layout.item_tag_list,
				tags);
		m_vwTagsList.setAdapter(m_TagAdapter);
		m_vwTagsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				JSONObject jo = m_TagAdapter.getItem(position);
				try {
					Toast.makeText(CategoryFragment.this.getActivity(),
							jo.getString("tags"), Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent intent = null;
				try {
					intent = new Intent().putExtra("tags", jo.getString("tags"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				intent.setClass(getActivity(), GenericActivity.class).putExtra(
						"fragment", ClosetFragment.class.getName());
				startActivity(intent);
			}
		});
		(new TagDLer()).execute();
		return view;
	}

	@SuppressWarnings("unused")
	private void getTags() throws JSONException, InterruptedException,
			ExecutionException {
		new JSONArray(new TagDLer()
				.execute("select tags from mobilecloset group by tags").get()
				.toString());
		// for each tag, add new viewpager to view
	}

	@SuppressWarnings("unused")
	private void getPics(String tag) throws JSONException,
			InterruptedException, ExecutionException {
		new JSONArray(new TagDLer()
				.execute("select urls from mobilecloset where tags='" + "tag'")
				.get().toString());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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

	public class TagDLer extends AsyncTask<String, Void, String> {
		// changing String to JSONObject
		public String doInBackground(String... path) {
			String output = null;
			// ArrayList<NameValuePair> nameValuePairs = new
			// ArrayList<NameValuePair>(1);
			// nameValuePairs.add(new BasicNameValuePair("stringQuery",
			// path[0]));
			// nameValuePairs.add(new BasicNameValuePair("id",path[1]));

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"http://bryanching.net/mcloset/get_all.php");
				// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				// print response
				output = EntityUtils.toString(entity);
				Log.i("GET RESPONSE—-", output);
				Log.e("log_tag ******", "good connection");
			} catch (Exception e) {
				Log.e("log_tag ******",
						"Error in http connection " + e.toString());
			}

			return output;
		}

		protected void onPostExecute(String jo) {
			if (!isCancelled()) {
				tags.clear();
				JSONArray ja = null;
				try {
					ja = new JSONObject(jo).getJSONArray("tags");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				for (int i = 0; i < ja.length(); i++) {
					try {
						tags.add(ja.getJSONObject(i));
						m_TagAdapter.notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class TagAdapter extends ArrayAdapter<JSONObject> {

		int resource;
		String response;
		Context context;

		// Initialize adapter
		public TagAdapter(Context context, int resource, List<JSONObject> items) {
			super(context, resource, items);
			this.resource = resource;
			this.context = context;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout tagView = (LinearLayout) convertView;
			JSONObject tagEntry = getItem(position);

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				tagView = (LinearLayout) inflater.inflate(
						R.layout.item_tag_list, null);
			} else {
				tagView = (LinearLayout) convertView;
			}

			TextView tv = (TextView) tagView.findViewById(R.id.tagTextView);
			try {
				tv.setText(tagEntry.getString("tags"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return tagView;
		}
	}
}
