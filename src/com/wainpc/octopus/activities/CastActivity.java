package com.wainpc.octopus.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.media.MediaRouter.RouteInfo;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.cast.MediaInfo;
import com.google.sample.castcompanionlibrary.cast.VideoCastManager;
import com.google.sample.castcompanionlibrary.cast.callbacks.IVideoCastConsumer;
import com.google.sample.castcompanionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.sample.castcompanionlibrary.widgets.MiniController;
import com.wainpc.octopus.R;

public class CastActivity extends ActionBarActivity {
	
	//Cast variables
	public static String tag = "myLogs";
	private MenuItem mediaRouteMenuItem;
	public VideoCastManager mCastManager;
    public IVideoCastConsumer mCastConsumer;
    public MediaInfo mSelectedMedia;
    
    
    private void castInitialize() {
   	 VideoCastManager.checkGooglePlayServices(this);
   	 mCastManager = CastApplication.getCastManager(this);

        
        mCastConsumer = new VideoCastConsumerImpl() {

            @Override
            public void onFailed(int resourceId, int statusCode) {
            }
           

            @Override
            public void onConnectionSuspended(int cause) {
                Log.d(tag, "onConnectionSuspended() was called with cause: " + cause);
            }

            @Override
            public void onConnectivityRecovered() {

            }

            @Override
            public void onCastDeviceDetected(final RouteInfo info) {

                    Log.d(tag, "Route is visible: " + info);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (mediaRouteMenuItem.isVisible()) {
                                Log.d(tag, "Cast Icon is visible: " + info.getName());
                            }
                        }
                    }, 1000);
                }
            };
            mCastManager.reconnectSessionIfPossible(this, false);
    }
    
    
    private void setupActionBar(ActionBar actionBar) {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setIcon(R.drawable.ic_dialog_alert);
        actionBar.setTitle(R.string.app_name);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);

		//add Cast button
        Log.d(tag,"CM:"+mCastManager == null? "null":"not null");
		 mediaRouteMenuItem = mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
		
		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchMenuItem = menu.findItem(R.id.action_search);
		Log.d(tag,"MI:"+searchMenuItem);
		SearchView searchView = (SearchView) searchMenuItem.getActionView();
		Log.d(tag,"SV:"+searchView);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);

		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.action_settings: {
        		// Start new activity
                Intent settingsIntent = new Intent(this.getApplicationContext(), SettingsActivity.class);
                this.startActivity(settingsIntent);
        		break;
        	}
        }
        return true;
 }
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
		castInitialize();
        setupActionBar(actionBar);
	}
	
}
