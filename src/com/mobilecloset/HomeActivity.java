package com.mobilecloset;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;

public class HomeActivity extends ParentActivity
{
	public static double height;
	public static double width;
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    final ActionBar bar = getSupportActionBar();
    bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction
        .add(android.R.id.content, (Fragment) new HomeFragment());
    fragmentTransaction.commit();
    setDims();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getSupportMenuInflater().inflate(R.menu.mainmenu, menu);
    return super.onCreateOptionsMenu(menu);
  }

  
  public void setDims()
  {

	    DisplayMetrics metrics = new DisplayMetrics();
	    ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
	  
	    //= getWindowManager().getDefaultDisplay();
	    int h = metrics.heightPixels; 
	    //display.getSize(size);
	    int w = metrics.widthPixels;
	    
	    height = h *.75;
	    width = w *.85;
	    
  }
}