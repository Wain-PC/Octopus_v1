package com.wainpc.octopus.activities;

import java.util.ArrayList;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wainpc.octopus.R;
import com.wainpc.octopus.adapters.HistoryListAdapter;
import com.wainpc.octopus.core.models.HistoryItem;
import com.wainpc.octopus.modules.DatabaseHistory;

public class HistoryActivity extends BaseFragmentActivity {
	
	public SectionsPagerAdapter spAdapter;
	public ViewPager viewPager;
	public static ArrayList<HistoryItem> historyList;
	DatabaseHistory db;
	
	public static HistoryListAdapter listAdapter;
	
	
    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_list;
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(getString(R.string.action_history));
		this.setupMenu(R.menu.history);
		this.resetUI();
		this.switchToLoadingView();
		setupMiniController(findViewById(R.id.mc1));
		db = new DatabaseHistory(this);
		historyList = db.getAllHistory();
		if(historyList.size() == 0) {
			Log.d(tag,"----------Nothing found on the list!");
			this.switchToErrorView(getString(R.string.error_list_history_empty));
			return;
		}
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	
        switch (item.getItemId()) {
        	case R.id.action_purge_history: {
        		// Purge all history
        		Log.d(tag,"FIRED PURGE HISTORY!");
        		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        		builder.setTitle(R.string.dialog_purge_history_header);
        		builder.setMessage(R.string.dialog_purge_history_title);
        		builder.setCancelable(true);
        		builder.setPositiveButton(R.string.dialog_purge_ok, new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		    	db.deleteAllHistory();
        		    	historyList = db.getAllHistory();
        		    	listAdapter.refreshDataset(historyList);
        		        dialog.dismiss(); // ��������� ���������� ����					
        		    }
        		});
        		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
        		    @Override
        		    public void onClick(DialogInterface dialog, int which) {
        		        dialog.dismiss(); // ��������� ���������� ����					
        		    }
        		});
        		AlertDialog dialog = builder.create();
        		dialog.show();
        		break;
        	}       	
        }
        return true;
 }
	
	// Adapter---------------------------------------------------------------------
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return new HistoryFragment();
		}
		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			return getString(R.string.title_latest).toUpperCase(l);
	}
	}
	
	
	// Fragment---------------------------------------------------------------------
		public static class HistoryFragment extends Fragment {
			
			View rootView;
			ListView listView;

			public HistoryFragment() {
			}

			// View---------------------------------------------------------------------
			@Override
			public View onCreateView(LayoutInflater inflater, ViewGroup container,
					Bundle savedInstanceState) {
				
				rootView = inflater.inflate(R.layout.fragment_list, container,
						false);
				
			    
				listView = (ListView) rootView
						.findViewById(R.id.listView_fragment_main);
				listAdapter = new HistoryListAdapter(getActivity(), historyList);
				listView.setAdapter(listAdapter);

				
				listView.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent,
							View view, int position, long id) {
						String seriesId = historyList.get(position)
								.seriesId;
						String seriesTitle = historyList.get(position).title_ru;

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
