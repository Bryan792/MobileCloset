package com.mobilecloset;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class OutfitFragment extends ParentFragment implements OnClickListener
{

  int selectedPosition;
  ArrayList<Clothing> clothing;
  // ArrayList<String> tags;
  LinearLayout m_vwClosetLayout;
  ViewPager pager;

  private Callback mActionModeCallback;
  private ActionMode mActionMode;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    ActionBar abs = getSherlockActivity().getSupportActionBar();
    abs.setTitle("Outfit");
    abs.setDisplayHomeAsUpEnabled(true);
    setHasOptionsMenu(true);

    View view = inflater.inflate(R.layout.fragment_closet, container, false);
    // m_vwClosetLayout = (LinearLayout)
    // view.findViewById(R.id.closetLayout);
    TextView outfitNameTextView = (TextView) view
        .findViewById(R.id.outfitNameText);
    pager = (ViewPager) view.findViewById(R.id.pager);
    pager.setOnPageChangeListener(new OnPageChangeListener()
    {

      @Override
      public void onPageSelected(int arg0)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void onPageScrolled(int arg0, float arg1, int arg2)
      {
        // TODO Auto-generated method stub
        if (mActionMode != null)
        {
          mActionMode.finish();
        }
        mActionMode = null;
      }

      @Override
      public void onPageScrollStateChanged(int arg0)
      {
        // TODO Auto-generated method stub

      }
    });

    // pager.setAdapter(new ImagePagerAdapter(IMAGES));
    // return pager;
    final Outfit outfit = getActivity().getIntent()
        .getParcelableExtra("outfit");

    mActionModeCallback = new Callback()
    {
      public boolean onCreateActionMode(ActionMode mode, Menu menu)
      {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.actionmenu, menu);
        menu.findItem(R.id.menu_outfit).setVisible(false);
        return true;
      }

      @Override
      public boolean onPrepareActionMode(ActionMode mode, Menu menu)
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean onActionItemClicked(ActionMode mode, MenuItem item)
      {
        switch (item.getItemId())
        {
        case R.id.menu_remove:
          outfit.deleteClothing(clothing.get(selectedPosition));
          return true;
        case R.id.menu_outfit:
          Intent intent = new Intent();
          intent.setClass(getActivity(), GenericActivity.class)
              .putExtra("fragment", SelectOutfitsFragment.class.getName())
              .putExtra("clothing", clothing.get(selectedPosition));
          startActivity(intent);
          return true;
        default:
          return false;
        }
      }

      @Override
      public void onDestroyActionMode(ActionMode mode)
      {
        // TODO Auto-generated method stub

      }

    };

    clothing = outfit.clothing;
    outfitNameTextView.setText(outfit.name);
    if (clothing.isEmpty())
    {
      getActivity().setContentView(R.layout.fragment_empty);
    }
    else
    {
      pager.setAdapter(new ClothingPagerAdapter(clothing));
    }
    return view;

  }

  @Override
  public void onPrepareOptionsMenu(Menu menu)
  {
    super.onPrepareOptionsMenu(menu);

    // This does work
    // MenuItem someMenuItem = menu.findItem(R.id.menu_upload);
    // someMenuItem.setVisible(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId())
    {
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
          OutfitsFragment.class.getName()));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(View v)
  {
    // TODO Auto-generated method stub
    switch (v.getId())
    {
    case R.id.closet:
      Intent intent = new Intent();
      intent.setClass(getActivity(), GenericActivity.class).putExtra(
          "fragment", OutfitFragment.class.getName());
      startActivity(intent);
      break;
    }
  }

  public class UninterceptableViewPager extends ViewPager
  {
    public UninterceptableViewPager(Context context)
    {
      super(context);
    }

    public UninterceptableViewPager(Context context, AttributeSet attrs)
    {
      super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
      // Tell our parent to stop intercepting our events!
      boolean ret = super.onInterceptTouchEvent(ev);
      if (ret)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
      }
      return ret;
    }
  }

  private class ClothingPagerAdapter extends
      android.support.v4.view.PagerAdapter
  {

    private ArrayList<Clothing> clothing;
    private LayoutInflater inflater;

    ClothingPagerAdapter(ArrayList<Clothing> clothing)
    {
      this.clothing = clothing;
      inflater = getActivity().getLayoutInflater();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
      ((ViewPager) container).removeView((View) object);
    }

    @Override
    public void finishUpdate(View container)
    {
    }

    @Override
    public int getCount()
    {
      return clothing.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position)
    {
      View imageLayout = inflater.inflate(R.layout.item_pager_image, view,
          false);
      TextView tagView = (TextView) imageLayout.findViewById(R.id.tags);
      ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
      imageView.setMaxHeight((int) HomeActivity.height);
      imageView.setMaxWidth((int) HomeActivity.width);

      final ProgressBar spinner = (ProgressBar) imageLayout
          .findViewById(R.id.loading);
      imageView.setOnLongClickListener(new OnLongClickListener()
      {

        @Override
        public boolean onLongClick(View v)
        {
          if (mActionMode != null)
          {
            return false;
          }
          mActionMode = ((SherlockFragmentActivity) getActivity())
              .startActionMode(mActionModeCallback);
          return true;
        }
      });
      tagView.setText("");
      for (String tag : clothing.get(position).tag)
      {
        tagView.append(tag + " ");
      }
      imageLoader.displayImage(clothing.get(position).url, imageView, options,
          new SimpleImageLoadingListener()
          {
            @Override
            public void onLoadingStarted(String imageUri, View view)
            {
              spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                FailReason failReason)
            {
              String message = null;
              switch (failReason.getType())
              {
              case IO_ERROR:
                message = "Input/Output error";
                break;
              case DECODING_ERROR:
                message = "Image can't be decoded";
                break;
              case NETWORK_DENIED:
                message = "Downloads are denied";
                break;
              case OUT_OF_MEMORY:
                message = "Out Of Memory error";
                break;
              case UNKNOWN:
                message = "Unknown error";
                break;
              }
              Toast.makeText(OutfitFragment.this.getActivity(), message,
                  Toast.LENGTH_SHORT).show();

              spinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                Bitmap loadedImage)
            {
              spinner.setVisibility(View.GONE);
            }
          });

      ((ViewPager) view).addView(imageLayout, 0);
      return imageLayout;
    }

    public boolean isViewFromObject(View view, Object object)
    {
      return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader)
    {
    }

    @Override
    public Parcelable saveState()
    {
      return null;
    }

    @Override
    public void startUpdate(View container)
    {
    }
  }

}
