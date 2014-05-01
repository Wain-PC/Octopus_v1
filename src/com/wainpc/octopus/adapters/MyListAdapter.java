package com.wainpc.octopus.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wainpc.octopus.R;
import com.wainpc.octopus.core.models.Series;


public class MyListAdapter extends BaseAdapter  {

	    private Activity activity;
	    private ArrayList<HashMap<String, String>> data;
	    private LayoutInflater inflater=null;
		private static final String tag = "myLogs";
		private static ImageLoader him;
		private Context context;
	

	    public MyListAdapter(Activity a, ImageLoader im, ArrayList<HashMap<String, String>> d) {
	        activity = a;
	        data=d;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        context = activity.getApplicationContext();
	        him = im;
	    }
	    
	    public MyListAdapter(Activity a, ImageLoader im, Series s) {
	        activity = a;
	        //get list of episodes from series
	        data = s.getListArrayOfEpisodes();
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        context = activity.getApplicationContext();
    		him = im;     
	    }

	    public int getCount() {
	        return data.size();
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		  View vi=convertView;
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.adapter_list, null);

	        TextView title = (TextView)vi.findViewById(R.id.title); // title	      
	        ImageView image=(ImageView)vi.findViewById(R.id.image); // thumb image
	        
	        HashMap<String, String> map = new HashMap<String, String>();
	        map = data.get(position);
	      
	        title.setText(map.get("title"));
	        him.displayImage(map.get("posterURL"), image);
	      
	        
	         
	        return vi;
	}
	
	
	
}


