package com.appspot.brownbookhunter;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Comparable<Book>, Parcelable{
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

	public Book(Parcel in) {
		String[] data = new String[8];
		in.readStringArray(data);
		allTimeCheckouts = Integer.parseInt(data[0]);
		recentCheckouts  = Integer.parseInt(data[1]);
		isbn = data[2];
		callNumber = data[3];
		author = data[4];
		publisher = data[5];
		publisherPlace = data[6];
		title = data[7];
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

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[]{
				""+allTimeCheckouts,
				""+recentCheckouts,
				isbn,
				callNumber,
				author,
				publisher,
				publisherPlace,
				title
		});
	}

	public static final Parcelable.Creator<Book> CREATOR= new Parcelable.Creator<Book>() {

		@Override
		public Book createFromParcel(Parcel source) {
			return new Book(source);  //using parcelable constructor
		}

		@Override
		public Book[] newArray(int size) {
			return new Book[size];
		}
	};

}
