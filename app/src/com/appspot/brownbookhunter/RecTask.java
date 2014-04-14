package com.appspot.brownbookhunter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class RecTask implements Callable<List<Book>> {

	private static final String TAG = "RecTask";

	@Override
	public List<Book> call(String callNumber) throws Exception {
		JSONObject recInfo = null;
		List<Book> bookRecs = new ArrayList<Book>();
		String recUrl = "http://brownbookhunter.appspot.com/recs";
		HttpClient hc = new DefaultHttpClient();
		try{
			HttpPost request = new HttpPost(recUrl);
			JSONObject params = new JSONObject();
			params.put("call_no", "BH39 .S322 1999");
			StringEntity se = new StringEntity(params.toString());
			request.addHeader("content-type", "application/x-www-form-urlencoded");
			request.setEntity(se);
			HttpResponse response = hc.execute(request);
			
			
			recInfo = readResponse(response);
			JSONArray actualResult = (JSONArray) recInfo.get("thing");
			for (int i = 0; i < actualResult.length(); i++){
				bookRecs.add(new Book((JSONObject) actualResult.get(i)));
			}
			JSONObject firstThing = (JSONObject) actualResult.get(0);
			
			Log.d(TAG, "Hopefully this is something?" + firstThing);
			
			
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
		Log.d(TAG, "Result length is " + result.toString().length());
//		Log.d(TAG, "Result string is:  "+ result.toString());
//		String testString = result.toString().substring(0, 200);
//		Log.d(TAG, "test string is " + testString);
//		return new JSONObject(testString);
		return new JSONObject(result.toString());
	}
}
