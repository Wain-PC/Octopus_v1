package com.wainpc.octopus.activities;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.BookmarkListAdapter;
import com.wainpc.octopus.core.models.Bookmark;
import com.wainpc.octopus.modules.DatabaseBookmarks;

public class BookmarkActivity extends BaseFragmentActivity {
	
	public SectionsPagerAdapter spAdapter;
	public ViewPager viewPager;
	public static ArrayList<Bookmark> bookmarkList;
	
	
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		this.resetUI();
		this.switchToLoadingView();
		setupMiniController(findViewById(R.id.mc1));
		DatabaseBookmarks db = new DatabaseBookmarks(this);
		bookmarkList = db.getAllBookmarks();
		spAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		//disable loading mode
		this.switchToListView();
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(spAdapter);
	}
	
	@Override
    protected void onResume() {
		Log.d(tag, "onResume() was called");
        super.onResume();
    }
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();  
    }
	
	// Adapter---------------------------------------------------------------------
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;
			switch (position) {
			case 0: {
				f = new BookmarksFragment();
				break;
			}
			case 1: {
				f = new BookmarksFragment();
				break;
			}
			}
			return f;
		}
		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_latest).toUpperCase(l);
			case 1:
				return getString(R.string.title_genres).toUpperCase(l);
		
			}
			return null;
		}
	}
	
	
	// Fragment---------------------------------------------------------------------
		public static class BookmarksFragment extends Fragment {

			
			public BookmarkListAdapter listAdapter;
			View rootView;
			ListView listView;

			public BookmarksFragment() {
			}

			// View---------------------------------------------------------------------
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				
				rootView = inflater.inflate(R.layout.fragment_list, container,
						false);
				
			    
				listView = (ListView) rootView
						.findViewById(R.id.listView_fragment_main);
				listAdapter = new BookmarkListAdapter(getActivity(), bookmarkList);
				listView.setAdapter(listAdapter);

				
				listView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						String seriesId = bookmarkList.get(position)
								.seriesId;
						String seriesTitle = bookmarkList.get(position).title_ru;

						// open series page
						Intent seriesPage = new Intent(getActivity()
								.getApplicationContext(),
								SeriesActivity.class);
						seriesPage.putExtra("id", seriesId);
						seriesPage.putExtra("title", seriesTitle);
						startActivity(seriesPage);
					}
				});
				
				return rootView;
			}
		}
		//----END FRAGMENT-------------------------------------------

}
