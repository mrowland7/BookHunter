package com.appspot.brownbookhunter;

import org.json.JSONException;
import org.json.JSONObject;

public class Book implements Comparable<Book>{
	private int allTimeCheckouts;
	private int recentCheckouts;
	private String isbn;
	private String callNumber;
	private String author;
	private String publisher;
	private String publisherPlace;
	private String title;
	
	public Book(JSONObject j) throws JSONException {
		allTimeCheckouts = Integer.parseInt((String) j.get("all_time_checkouts"));
		recentCheckouts  = Integer.parseInt((String) j.get("recent_checkouts"));
		isbn = (String) j.get("isbn_dirty");
		callNumber = (String) j.get("call_no");
		author = (String) j.get("author");
		publisher = (String) j.get("publisher");
		publisherPlace = (String) j.get("pub_place");
		title = (String) j.get("title");
	}

	//negative if less than
	@Override
	public int compareTo(Book arg0) {
		return (arg0.allTimeCheckouts + arg0.recentCheckouts * 2) - (allTimeCheckouts + 2 * recentCheckouts);
	}

	public int getAllTimeCheckouts() {
		return allTimeCheckouts;
	}

	public int getRecentCheckouts() {
		return recentCheckouts;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getCallNumber() {
		return callNumber;
	}

	public String getAuthor() {
		return author;
	}

	public String getPublisher() {
		return publisher;
	}

	public String getPublisherPlace() {
		return publisherPlace;
	}
	
	public String getTitle() {
		return title;
	}
}
