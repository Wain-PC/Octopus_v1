package com.wainpc.octopus.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.MyListAdapter;
import com.wainpc.octopus.asynctasks.JsonLoader;
import com.wainpc.octopus.interfaces.AsyncResponse;
import com.wainpc.octopus.modules.JsonParser;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;


//Activity---------------------------------------------------------------------
public class MainActivity extends FragmentActivity implements AsyncResponse {
	

	public SectionsPagerAdapter mSectionsPagerAdapter;
	public ViewPager mViewPager;
	public JSONParser parser = new JSONParser();
	public JsonLoader loader = new JsonLoader();
	public static String tag = "myLogs";
	
	public static ArrayList<HashMap<String, String>> seriesList = new ArrayList<HashMap<String, String>>();
	public HashMap<String, String> map;
	public String rootURL = "http://192.168.1.104:1337/api/latest?json=1";
	
	//success handler on
	public void success(ArrayList<HashMap<String, String>> data) {
		Log.d(tag,"Got data!"+data.size());
		seriesList = data;
		
		// Create the adapter that will return a fragment for each of the three
				// primary sections of the app.
				mSectionsPagerAdapter = new SectionsPagerAdapter(
						getSupportFragmentManager());

				// Set up the ViewPager with the sections adapter.
				mViewPager = (ViewPager) findViewById(R.id.pager);
				mViewPager.setAdapter(mSectionsPagerAdapter);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		
		//make async request
		Log.d(tag,"make async request");
		loader.execute(rootURL);
		loader.delegate = this;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem searchItem = menu.findItem(R.id.action_search);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		return true;
	}

	//Adapter---------------------------------------------------------------------
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new DummySectionFragment();
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	//Fragment---------------------------------------------------------------------
	public static class DummySectionFragment extends Fragment {
		
		
		public MyListAdapter listAdapter;
		View rootView;
		ListView listViewFragmentMain;
		
		public DummySectionFragment() {
		}
		

		//View---------------------------------------------------------------------
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_list,
					container, false);			
			listViewFragmentMain = (ListView) rootView.findViewById(R.id.listView_fragment_main);
			
			listAdapter = new MyListAdapter(getActivity(),seriesList);
			listViewFragmentMain.setAdapter(listAdapter);
			
			return rootView;
		}
		
		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	       
	    }
	}

}
