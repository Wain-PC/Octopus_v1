package com.wainpc.octopus.modules;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import android.util.Log;

import com.wainpc.octopus.modules.JsonParser;

public class HttpLoader {
	
	public static String sendQuery(String... inputURL) {

		try {
			String tag = "myLogs";
			URL url = new URL(inputURL[0]);
			Log.d(tag,"Sending request to "+inputURL[0]);
			
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();

			// Log the server response code
			int responseCode = connection.getResponseCode();
			 //Log.d(tag, "Server responded with: " + responseCode);

			// And if the code was HTTP_OK then parse the contents
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// Convert request content to string
				InputStream is = connection.getInputStream();
				String content = convertInputStream(is, "UTF-8");
				is.close();
				 //Log.d(tag,"L_send_QuERY_RESPOND  "+content);
				return content;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static String convertInputStream(InputStream is, String encoding) {
		Scanner scanner = new Scanner(is, encoding).useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : "";
	}
}
