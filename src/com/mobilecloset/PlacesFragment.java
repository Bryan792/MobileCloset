package com.mobilecloset;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

@SuppressWarnings("unused")
public class PlacesFragment extends ParentFragment implements OnClickListener {
	ViewGroup m_vwcontainer;
	LayoutInflater m_vwinflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		m_vwcontainer = container;
		m_vwinflater = inflater;
		View view = inflater
				.inflate(R.layout.fragment_places, container, false);

		Button m_vwClosetButton = (Button) view.findViewById(R.id.closet);
		m_vwClosetButton.setOnClickListener(this);

		Button m_vwOutfitButton = (Button) view.findViewById(R.id.outfits);
		m_vwOutfitButton.setOnClickListener(this);

		// Button m_vwNewButton = (Button) view.findViewById(R.id.newItem);
		// m_vwNewButton.setOnClickListener(this);

		Button m_vwSearchButton = (Button) view.findViewById(R.id.search);
		m_vwSearchButton.setOnClickListener(this);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this.getActivity().getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				/* .enableLogging() */.build();
		ImageLoader.getInstance().init(config);

		return view;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.closet:

			intent.setClass(getActivity(), CategoryActivity.class);
			startActivity(intent);
			break;
		// case R.id.newItem:
		// break;
		case R.id.outfits:
			// setContentView(R.layout.);
			Toast.makeText(getActivity(), "In Development", Toast.LENGTH_SHORT)
					.show();
			//intent.setClass(getActivity(), OutfitsActivity.class);
			//startActivity(intent);

			break;
		case R.id.search:
			Toast.makeText(getActivity(), "Use ActionBar", Toast.LENGTH_SHORT)
			.show();
			break;
		}
	}

}
