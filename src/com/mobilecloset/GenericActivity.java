package com.mobilecloset;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;

public class GenericActivity extends ParentActivity
{
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    final ActionBar bar;
   
    Object fragment = null;
    try
    {
    	bar= getSupportActionBar();
    	bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    	fragment = Class.forName(getIntent().getStringExtra("fragment"))
          .newInstance();
      // Toast.makeText(this, "Worked", Toast.LENGTH_SHORT).show();
    }
    catch (InstantiationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (IllegalAccessException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (ClassNotFoundException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
        .beginTransaction();
    fragmentTransaction.add(android.R.id.content, (Fragment) fragment);
    fragmentTransaction.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    getSupportMenuInflater().inflate(R.menu.mainmenu, menu);
    return super.onCreateOptionsMenu(menu);
  }

}