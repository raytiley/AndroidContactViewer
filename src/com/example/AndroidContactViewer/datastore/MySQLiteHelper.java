package com.example.AndroidContactViewer.datastore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_CONTACTS = "contacts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTACT_NAME = "_name";
	public static final String COLUMN_CONTACT_TITLE = "_title";
	public static final String COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID = "_defaultTextPhone_id";
	public static final String COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID = "_defaultContactPhone_id";
	public static final String COLUMN_CONTACT_DEFAULT_EMAIL_ID = "_defaultEmail_id";
	public static final String COLUMN_CONTACT_TWITTER_ID = "_twitterId";
	
	public static final String TABLE_PHONE = "phone";
	public static final String COLUMN_PHONE_ID = "_id";
	public static final String COLUMN_PHONE_NUMBER = "_number";
	public static final String COLUMN_PHONE_PARENT_ID = "_contact_id";
	
	public static final String TABLE_EMAIL = "email";
	public static final String COLUMN_EMAIL_ID = "_id";
	public static final String COLUMN_EMAIL_PARENT_ID = "_contact_id";
	public static final String COLUMN_EMAIL_ADDRESS = "_email";
	
	private static final String DATABASE_NAME = "contacts.db";
	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String DATABASE_CREATE_CONTACTS = "create table "
			+ TABLE_CONTACTS + "(" + COLUMN_ID
			+ " text primary key, "
			+ COLUMN_CONTACT_NAME + " text not null, "
			+ COLUMN_CONTACT_TITLE + " text, "
			+ COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID + " integer, "
			+ COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID + " integer, "
			+ COLUMN_CONTACT_DEFAULT_EMAIL_ID + " integer, "
			+ COLUMN_CONTACT_TWITTER_ID + " text);";
	private static final String DATABASE_CREATE_PHONES = "create table "
			+ TABLE_PHONE + "(" + COLUMN_PHONE_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_PHONE_PARENT_ID + " text not null, "
			+ COLUMN_PHONE_NUMBER + " text not null);";
	private static final String DATABASE_CREATE_EMAILS = "create table "
			+ TABLE_EMAIL + "(" + COLUMN_EMAIL_ID
			+ " integer primary key autoincrement, "
			+ COLUMN_EMAIL_PARENT_ID + " text not null, "
			+ COLUMN_EMAIL_ADDRESS + " text not null);";
	

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_CONTACTS);
		database.execSQL(DATABASE_CREATE_PHONES);
		database.execSQL(DATABASE_CREATE_EMAILS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EMAIL);
		onCreate(db);
	}

}