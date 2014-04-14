package com.appspot.brownbookhunter;

import java.util.List;

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_book_info);
		Bundle data = getIntent().getExtras();
		book = data.getParcelable("info");
		books = data.getParcelableArrayList("recs");
		Log.d(TAG, "book is " + book);
		Log.d(TAG, "received recs: " + books.size());
		
		populateTextFields();
		displayRecs();
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
		TextView info = (TextView) findViewById(R.id.info);
		
		title.setText(book.getTitle());
		author.setText(book.getAuthor());
		info.setText("ASDFASDFASDF");
	}
	
	private void displayRecs() {
		
	}
	
	public void email(View v){
		Log.d(TAG, "emailing citation info");
		String citation = book.getAuthor() + ". " + book.getTitle() + ". asdfasdf";
		
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"michael_rowland+bookhunter@brown.edu"});
		i.putExtra(Intent.EXTRA_SUBJECT, "Your citation");
		i.putExtra(Intent.EXTRA_TEXT   , citation);
		try {
		    startActivity(Intent.createChooser(i, "Send citation..."));
		} catch (android.content.ActivityNotFoundException ex) {
		    Toast.makeText(BookInfoActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
}
