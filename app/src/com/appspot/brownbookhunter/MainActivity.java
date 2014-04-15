package com.appspot.brownbookhunter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import org.json.*;

import com.google.zxing.IntentIntegrator;
import com.google.zxing.IntentResult;


public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private BlockingQueue<Runnable> _workQueue = new LinkedBlockingQueue<Runnable>();
	private ExecutorService _executor = new ThreadPoolExecutor(10, 10, 1000l, TimeUnit.SECONDS, _workQueue);
	private Future<List<Book>> _recFuture = null;
	private Future<Book> _infoFuture = null;

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
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}
	
	public void getRecs(View v) {
		Log.d(TAG, "Getting recs");
		//Callable<List<Book>> getRecTask = new RecTask("BH39 .S322 1999");
		Callable<List<Book>> getRecTask = new RecTask("31236009770234");
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
			//wait until both rec and info are present to continue to presentation page
			if (_recFuture != null && _recFuture.isDone() && _infoFuture != null && _infoFuture.isDone()){
				Book info;
				ArrayList<Book> recList;
				try {
					info = _infoFuture.get();
					recList = (ArrayList<Book>) _recFuture.get();
					
					Log.d(TAG, "Rec is done: there are " + recList.size() + " recommendations");
					
					//start new book info page 
					Intent i = new Intent(getBaseContext(), BookInfoActivity.class);
					i.putExtra("info", info);
					i.putParcelableArrayListExtra("recs", recList);
					startActivity(i);
					
				} catch (InterruptedException e) {
					Log.e(TAG, "rec future interrupted: " + e.getMessage());
				} catch (ExecutionException e) {
					Log.e(TAG, "rec future execution exception: " + e.getMessage());
				}
				_recFuture = null;
				_infoFuture = null;
				
			}
			
			Thread.yield(); //noop to keep thread alive... #cs32lifelessons
		}
		
	}
	
	//submit scanning result to rec page
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		  if (scanResult != null) {
			  String barcode = scanResult.getContents();
			  Log.d(TAG, "Scan result was: " + barcode);
			  Callable<List<Book>> getRecTask = new RecTask(barcode);
			  _recFuture = _executor.submit(getRecTask);
			  Callable<Book> getInfoTask = new InfoTask(barcode);
			  _infoFuture = _executor.submit(getInfoTask);
		  }
		}

}
