package com.appspot.brownbookhunter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BookInfoActivity extends Activity {
	private static final String TAG = "BookInfoActivity"; 
	private List<Book> books;
	private Book book;
	private BlockingQueue<Runnable> _workQueue = new LinkedBlockingQueue<Runnable>();
	private ExecutorService _executor = new ThreadPoolExecutor(10, 10, 1000l, TimeUnit.SECONDS, _workQueue);
	private Future<List<Book>> _recFuture = null;
	private Book _nextBook = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_book_info);
		Bundle data = getIntent().getExtras();
		book = data.getParcelable("info");
		books = data.getParcelableArrayList("recs");
		Log.d(TAG, "book is " + book.getTitle() + " by " + book.getAuthor());
		Log.d(TAG, "received recs: " + books.size());
		
		populateTextFields();
		displayRecs();
		startWatcherThread();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void populateTextFields() {
		TextView title = (TextView) findViewById(R.id.title);
		TextView author = (TextView) findViewById(R.id.author);
		TextView info = (TextView) findViewById(R.id.cite_info);
		TextView call = (TextView) findViewById(R.id.call_no);
		
		title.setText(book.getTitle());
		author.setText(book.getAuthor());
		info.setText(book.getPublisherPlace() + " " + book.getPublisher() + " " + book.getYear()) ;
		call.setText(book.getCallNumber());
	}
	
	private void displayRecs() {
		TextView first = (TextView) findViewById(R.id.rec1);
		TextView second = (TextView) findViewById(R.id.rec2);
		TextView third = (TextView) findViewById(R.id.rec3);
		
		int len = books.size();
		//should factor this and DRY but...
		if (len >= 1) {
			first.setText(books.get(0).getTitle());
			//button
		}
		else findViewById(R.id.rec1layout).setVisibility(View.INVISIBLE);
		if (len >= 2) {
			second.setText(books.get(1).getTitle());
		}
		else findViewById(R.id.rec2layout).setVisibility(View.INVISIBLE);
		if (len >= 3) {
			third.setText(books.get(2).getTitle());
		}
		else findViewById(R.id.rec3layout).setVisibility(View.INVISIBLE);
		
	}
	
	public void email(View v){
		Log.d(TAG, "emailing citation info");
		String citation = book.getAuthor() + ". " + book.getTitle() + ". " + book.getPublisherPlace() + " " + book.getPublisher() + " " + book.getYear();
		
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"michael_rowland+bookhunter@brown.edu"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Citation for " + book.getTitle());
		i.putExtra(Intent.EXTRA_TEXT   , citation);
		try {
		    startActivity(Intent.createChooser(i, "Send citation..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(BookInfoActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void newInfo1(View v) {
		_nextBook = books.get(0);
		_recFuture = _executor.submit(new RecTask(_nextBook.getBarcode()));
		
	}
	
	public void newInfo2(View v) {
		_nextBook = books.get(1);
		_recFuture = _executor.submit(new RecTask(_nextBook.getBarcode()));
		
	}
	
	public void newInfo3(View v) {
		_nextBook = books.get(2);
		_recFuture = _executor.submit(new RecTask(_nextBook.getBarcode()));		
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
			if (_recFuture != null && _recFuture.isDone() && _nextBook != null){
				ArrayList<Book> recList;
				try {
					recList = (ArrayList<Book>) _recFuture.get();
					
					Log.d(TAG, "Rec is done: there are " + recList.size() + " recommendations for " + _nextBook.getTitle());
					List<Book> toDelete = new ArrayList<Book>();
					for (Book b: recList) {
						Log.d(TAG, "title is "+ b.getTitle());
						if (b.getTitle().equals(_nextBook.getTitle())){
							 Log.d(TAG, "trying to remove " + _nextBook.getTitle());
							 toDelete.add(b);
						 }
					}
					for (Book b: toDelete) {
						recList.remove(b);
					}
					 Log.d(TAG, "after removal, there are now " + recList.size() + " recommendations");
					
					//start new book info page 
					Intent i = new Intent(getBaseContext(), BookInfoActivity.class);
					i.putExtra("info", _nextBook);
					i.putParcelableArrayListExtra("recs", recList);
					startActivity(i);
					
				} catch (InterruptedException e) {
					Log.e(TAG, "rec future interrupted: " + e.getMessage());
				} catch (ExecutionException e) {
					Log.e(TAG, "rec future execution exception: " + e.getMessage());
				}
				_recFuture = null;
				_nextBook = null;
				
			}
			Thread.yield(); //noop to keep thread alive... #cs32lifelessons
		}
		
	}
	
	public void scanAgain(View v) {
		Intent i = new Intent(getBaseContext(), MainActivity.class);
		startActivity(i);
	}
	
}
