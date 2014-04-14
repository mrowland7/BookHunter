package com.appspot.brownbookhunter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import org.json.*;


public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private BlockingQueue<Runnable> _workQueue = new LinkedBlockingQueue<Runnable>();
	private ExecutorService _executor = new ThreadPoolExecutor(10, 10, 1000l, TimeUnit.SECONDS, _workQueue);
	private Future<JSONObject> _recFuture = null;
	private Future<JSONObject> _infoFuture = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startWatcherThread();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void scan(View v) {
		Log.d(TAG, "Scanning");
	}
	
	public void getRecs(View v) {
		Log.d(TAG, "Getting recs");
		Callable<JSONObject> getRecTask = new RecTask();
		_recFuture = _executor.submit(getRecTask);
	}
	
	private void startWatcherThread() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				checkFutureResults();
			}
		}).start();
	}

	protected void checkFutureResults() {
		while (true){
			if (_recFuture != null && _recFuture.isDone()){
				JSONObject recInfo;
				try {
					recInfo = _recFuture.get();
					Log.d(TAG, "Rec is done: " + recInfo);
				} catch (InterruptedException e) {
					Log.e(TAG, "rec future interrupted: " + e.getMessage());
				} catch (ExecutionException e) {
					Log.e(TAG, "rec future execution exception: " + e.getMessage());
					e.printStackTrace();
				}
				_recFuture = null;
			}
			
			Thread.yield(); //noop to keep thread alive... #cs32lifelessons
		}
		
	}

}
