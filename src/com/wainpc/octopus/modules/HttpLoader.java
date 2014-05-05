package com.wainpc.octopus.modules;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class HttpLoader {
	
	public static String sendQuery(String... inputURL) {

		try {
			String tag = "myLogs";
			String url = inputURL[0];
			String responseBody = "";
			Log.d(tag,"Sending request to "+inputURL[0]);
			
			HttpRequest request = HttpRequest.get(url);

			// Log the server response code
			int responseCode = request.code();
			 Log.d(tag, "Server responded with code: " + responseCode);

			// And if the code was HTTP_OK then parse the contents
			if (request.ok()) {
				// Convert request content to string
				responseBody = request.body("utf-8");
				return responseBody;
			}
			else {
				return "ERR";
				//return responseBody;
			}

		} catch (HttpRequestException e) {
			e.getCause();
			return "ERR";
		}
	}

	
	
	public static String encodeURIComponent(String component)   {     
		String result = null;      
		
		try {       
			result = URLEncoder.encode(component, "UTF-8")   
				   .replaceAll("\\%28", "(")                          
				   .replaceAll("\\%29", ")")   		
				   .replaceAll("\\+", "%20")                          
				   .replaceAll("\\%27", "'")   			   
				   .replaceAll("\\%21", "!")
				   .replaceAll("\\%7E", "~"); 
		}
		catch (UnsupportedEncodingException e) {       
			result = component;     
		}      
		
		return result;   
	} 
	
}
