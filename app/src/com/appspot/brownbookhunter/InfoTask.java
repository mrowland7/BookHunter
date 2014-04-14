package com.appspot.brownbookhunter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class InfoTask implements Callable<Book> {

	private static final String TAG = "RecTask";
	private String searchTerm;
	public InfoTask(String searchTerm) {
		this.searchTerm = searchTerm;
		
	}
	@Override
	public Book call() throws Exception {
		Book book = null;
		String recUrl = "http://brownbookhunter.appspot.com/info";
		HttpClient hc = new DefaultHttpClient();
		try{
			HttpPost request = new HttpPost(recUrl);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			params.add(new BasicNameValuePair("call_no", searchTerm));
			request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse response = hc.execute(request);
			
			JSONObject info = readResponse(response);
			book = new Book(info);

			Log.d(TAG, "Hopefully this is something?" + info);
			Log.d(TAG, "Requested book is " + book);
			
			
		} catch (JSONException e) {
			Log.e(TAG, "Json exception: " + e.getMessage());
			Log.e(TAG, "Json exception: " + e.toString());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Unsupported encoding: " + e.getMessage());
		} catch (ClientProtocolException e) {
			Log.e(TAG, "client protocol: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "io exception: " + e.getMessage());
		} finally {
			hc.getConnectionManager().shutdown();
		}
		return book;
	}

	private JSONObject readResponse(HttpResponse response) throws IllegalStateException, IOException, JSONException {
		InputStream is = response.getEntity().getContent();
		StringBuilder result = new StringBuilder();
		int c;
		while ((c = is.read()) != -1) {
			result.append((char) c);
		}
		return new JSONObject(result.toString());
	}

}
