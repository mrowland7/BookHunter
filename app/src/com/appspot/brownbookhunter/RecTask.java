package com.appspot.brownbookhunter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RecTask implements Callable<List<Book>> {

	private static final String TAG = "RecTask";
	private String searchTerm;
	public RecTask(String searchTerm) {
		this.searchTerm = searchTerm;
		
	}
	@Override
	public List<Book> call() throws Exception {
		JSONObject recInfo = null;
		List<Book> bookRecs = new ArrayList<Book>();
		String recUrl = "http://brownbookhunter.appspot.com/recs";
		HttpClient hc = new DefaultHttpClient();
		try{
			HttpPost request = new HttpPost(recUrl);
			
			List<NameValuePair> params = new ArrayList<NameValuePair>(1);
			params.add(new BasicNameValuePair("call_no", searchTerm));
			request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse response = hc.execute(request);
			
			recInfo = readResponse(response);
			JSONArray actualResult = (JSONArray) recInfo.get("thing");
			for (int i = 0; i < actualResult.length(); i++){
				bookRecs.add(new Book((JSONObject) actualResult.get(i)));
			}
			
			Collections.sort(bookRecs);
			
			Log.d(TAG, "search term was " + searchTerm);
			
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
		return bookRecs;
	}

	private JSONObject readResponse(HttpResponse response) throws IllegalStateException, IOException, JSONException {
		InputStream is = response.getEntity().getContent();
		StringBuilder result = new StringBuilder();
		int c;
		result.append("{ \"thing\": ");
		while ((c = is.read()) != -1) {
			result.append((char) c);
		}
		result.append("}");
		return new JSONObject(result.toString());
	}
}
