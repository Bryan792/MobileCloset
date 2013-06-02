package com.mobilecloset;

import java.security.MessageDigest;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class PlacesFragment extends ParentFragment implements OnClickListener
{
  ViewGroup m_vwcontainer;
  LayoutInflater m_vwinflater;
  public static String id;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    // try {
    // PackageInfo info = getActivity().getPackageManager().getPackageInfo(
    // "com.mobilecloset", PackageManager.GET_SIGNATURES);
    // for (Signature signature : info.signatures)
    // {
    // MessageDigest md = MessageDigest.getInstance("SHA");
    // md.update(signature.toByteArray());
    // System.out.println(Base64.encodeToString(md.digest(), Base64.DEFAULT));
    // }
    // }
    // catch(Exception e)
    // {
    // e.printStackTrace();
    // }
    //
    m_vwcontainer = container;
    m_vwinflater = inflater;
    View view = inflater.inflate(R.layout.fragment_places, container, false);

    Button m_vwClosetButton = (Button) view.findViewById(R.id.closet);
    m_vwClosetButton.setOnClickListener(this);

    Button m_vwOutfitButton = (Button) view.findViewById(R.id.outfits);
    m_vwOutfitButton.setOnClickListener(this);

    // Button m_vwNewButton = (Button) view.findViewById(R.id.newItem);
    // m_vwNewButton.setOnClickListener(this);

    Button m_vwSearchButton = (Button) view.findViewById(R.id.search);
    m_vwSearchButton.setOnClickListener(this);

    ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this
        .getActivity().getApplicationContext())
        .threadPriority(Thread.NORM_PRIORITY - 2)
        .denyCacheImageMultipleSizesInMemory().enableLogging().build();
    ImageLoader.getInstance().init(config);

    // Facebook
    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
    authButton.setFragment(this);

    return view;
  }

  @Override
  public void onClick(View v)
  {
    Intent intent = new Intent();
    // TODO Auto-generated method stub
    switch (v.getId())
    {
    case R.id.closet:

      intent.setClass(getActivity(), GenericActivity.class).putExtra(
          "fragment", CategoryFragment.class.getName());
      startActivity(intent);
      break;
    // case R.id.newItem:
    // break;
    case R.id.outfits:
      // setContentView(R.layout.);
      Toast.makeText(getActivity(), "In Development", Toast.LENGTH_SHORT)
          .show();
      intent.setClass(getActivity(), OutfitsActivity.class);
      startActivity(intent);

      break;
    case R.id.search:
      Toast.makeText(getActivity(), "Use ActionBar", Toast.LENGTH_SHORT).show();
      break;
    }
  }

  /******************************************/
  // Facebook stuff
  private static final String TAG = "MainFragment";
  private Session.StatusCallback callback = new Session.StatusCallback()
  {
    @Override
    public void call(Session session, SessionState state, Exception exception)
    {
      onSessionStateChange(session, state, exception);
    }
  };

  private void onSessionStateChange(Session session, SessionState state,
      Exception exception)
  {
    if (state.isOpened())
    {
      Log.i(TAG, "Logged in...");
      Request.executeMeRequestAsync(session, new Request.GraphUserCallback()
      {
        public void onCompleted(GraphUser user, Response response)
        {
          if (response != null)
          {
            Log.d(TAG, user.getId());
            id = user.getId();
          }

        }
      }

      );
    }

    else if (state.isClosed())
    {
      Log.i(TAG, "Logged out...");
    }
  }

  private UiLifecycleHelper uiHelper;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    uiHelper = new UiLifecycleHelper(getActivity(), callback);
    uiHelper.onCreate(savedInstanceState);
  }

  @Override
  public void onResume()
  {
    super.onResume();
    Session session = Session.getActiveSession();
    if (session != null && (session.isOpened() || session.isClosed()))
    {
      onSessionStateChange(session, session.getState(), null);
    }

    uiHelper.onResume();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);
    uiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onPause()
  {
    super.onPause();
    uiHelper.onPause();
  }

  @Override
  public void onDestroy()
  {
    super.onDestroy();
    uiHelper.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }

}
