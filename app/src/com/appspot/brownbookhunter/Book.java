package com.appspot.brownbookhunter;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {
	private int allTimeCheckouts;
	private int recentCheckouts;
	private String isbn;
	private String callNumber;
	private String author;
	private String publisher;
	private String publisherPlace;
	
	public Book(JSONObject j) throws JSONException {
		allTimeCheckouts = Integer.parseInt((String) j.get("all_time_checkouts"));
		recentCheckouts  = Integer.parseInt((String) j.get("recent_checkouts"));
		isbn = (String) j.get("isbn_dirty");
		callNumber = (String) j.get("call_no");
		author = (String) j.get("author");
		publisher = (String) j.get("publisher");
		publisherPlace = (String) j.get("pub_place");
	}
}
