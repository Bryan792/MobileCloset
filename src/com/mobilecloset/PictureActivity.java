package com.mobilecloset;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;

public class PictureActivity extends ParentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final ActionBar bar = getSupportActionBar();
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		PictureFragment mPictureFragment = new PictureFragment();
		getSupportFragmentManager().beginTransaction()
				.add(android.R.id.content, mPictureFragment).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.mainmenu, menu);
		/*
		 * // set up a listener for the refresh item final MenuItem refresh =
		 * (MenuItem) menu.findItem(R.id.menu_refresh);
		 * refresh.setOnMenuItemClickListener(new OnMenuItemClickListener() { //
		 * on selecting show progress spinner for 1s public boolean
		 * onMenuItemClick(MenuItem item) { //
		 * item.setActionView(R.layout.progress_action); handler.postDelayed(new
		 * Runnable() { public void run() { refresh.setActionView(null); } },
		 * 1000); return false; } });
		 */
		return super.onCreateOptionsMenu(menu);
	}

}
