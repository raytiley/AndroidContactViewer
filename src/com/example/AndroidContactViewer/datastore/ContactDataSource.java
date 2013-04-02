package com.example.AndroidContactViewer.datastore;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.AndroidContactViewer.Contact;

public class ContactDataSource implements ContactRepositoryInterface {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	
	private String[] allContactColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_CONTACT_NAME,
			MySQLiteHelper.COLUMN_CONTACT_TITLE,
			MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID,
			MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID,
			MySQLiteHelper.COLUMN_CONTACT_DEFAULT_EMAIL_ID,
			MySQLiteHelper.COLUMN_CONTACT_TWITTER_ID };
	private String[] allPhoneColumns = { MySQLiteHelper.COLUMN_PHONE_ID,
			MySQLiteHelper.COLUMN_PHONE_NUMBER,
			MySQLiteHelper.COLUMN_PHONE_PARENT_ID };
	private String[] allEmailColumns = { MySQLiteHelper.COLUMN_EMAIL_ID,
			MySQLiteHelper.COLUMN_EMAIL_PARENT_ID,
			MySQLiteHelper.COLUMN_EMAIL_ADDRESS };

	public ContactDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	@Override
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	@Override
	public void close() {
		dbHelper.close();
	}

	@Override
	public long count() {
		return DatabaseUtils.queryNumEntries(database,
				MySQLiteHelper.TABLE_CONTACTS);
	}

	@Override
	public List<Contact> all() {
		List<Contact> allContacts = new LinkedList<Contact>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS, allContactColumns, null, null, null, null, null); 
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Contact contact = cursorToContact(cursor);
			allContacts.add(contact);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return allContacts;

	}
	
	@Override
	public Contact add(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ID, contact.getId());
		values.put(MySQLiteHelper.COLUMN_CONTACT_NAME, contact.getName());
		values.put(MySQLiteHelper.COLUMN_CONTACT_TITLE, contact.getTitle());
		values.put(MySQLiteHelper.COLUMN_CONTACT_TWITTER_ID,
				contact.getTwitterId());
		database.insert(MySQLiteHelper.TABLE_CONTACTS, null,
				values);
		
		List<String> emails = contact.getEmails();
		emails.add(contact.getDefaultEmail());
		addAllEmailsForContact(contact.getId(), emails);
		
		List<String> phones = contact.getPhoneNumbers();
		phones.add(contact.getDefaultContactPhone());
		phones.add(contact.getDefaultTextPhone());
		addAllPhonesForContact(contact.getId(), phones);
		
		setDefaults(contact.getId(), contact);
		
		return get(contact.getId());
	}

	private void setDefaults(String insertId, Contact contact) {
		if (contact.getDefaultContactPhone() == "" && contact.getPhoneNumbers().size() > 0) {
			setDefaultPhone(insertId, contact.getPhoneNumbers().get(0));
		}
		else {
			setDefaultPhone(insertId, contact.getDefaultContactPhone());
		}

		if (contact.getDefaultEmail() == "" && contact.getEmails().size() > 0) {
			setDefaultEmail(insertId, contact.getEmails().get(0));
		}
		else {
			setDefaultEmail(insertId, contact.getDefaultEmail());
		}

		if (contact.getDefaultTextPhone() == "" && contact.getPhoneNumbers().size() > 0) {
			setDefaultText(insertId, contact.getPhoneNumbers().get(0));
		}
		else {
			setDefaultText(insertId, contact.getDefaultTextPhone());
		}
	}

	@Override
	public int delete(Contact contact) {
		String id = contact.getId();
		dropAllEmailsForContact(id);
		dropAllPhonesForContact(id);
		return database.delete(MySQLiteHelper.TABLE_CONTACTS,
				MySQLiteHelper.COLUMN_ID + " = '" + id + "'", null);
	}

	@Override
	public Contact update(Contact contact) {
		dropAllEmailsForContact(contact.getId());
		dropAllPhonesForContact(contact.getId());
		
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTACT_NAME, contact.getName());
		values.put(MySQLiteHelper.COLUMN_CONTACT_TITLE, contact.getTitle());
		values.put(MySQLiteHelper.COLUMN_CONTACT_TWITTER_ID,
				contact.getTwitterId());
		database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = '" + contact.getId() + "'", null);
		
		addAllEmailsForContact(contact.getId(), contact.getEmails());
		addAllPhonesForContact(contact.getId(), contact.getPhoneNumbers());
		
		setDefaults(contact.getId(), contact);
		return get(contact.getId());
	}

	@Override
	public Contact get(String contactId) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
				allContactColumns, MySQLiteHelper.COLUMN_ID + " = '" + contactId + "'", null,
				null, null, null);
		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			return null;
		}
		Contact contact = cursorToContact(cursor);
		cursor.close();

		return contact;
	}

	private Contact cursorToContact(Cursor cursor) {
		Contact contact = new Contact(
				cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CONTACT_NAME)));
		contact.setTitle(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CONTACT_TITLE)));
		String defaultPhone = getDefaultPhone(contact.getId());
		if (defaultPhone != null && !"".equals(defaultPhone.trim())) {
			contact.setDefaultContactPhone(defaultPhone);
		}
		String defaultEmail = getDefaultEmail(contact.getId());
		if (defaultEmail != null && !"".equals(defaultEmail.trim())) {
			contact.setDefaultEmail(defaultEmail);
		}
		String defaultText = getDefaultText(contact.getId());
		if (defaultText != null && !"".equals(defaultText.trim())) {
			contact.setDefaultTextPhone(defaultText);
		}
		contact.setTwitterId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CONTACT_TWITTER_ID)));
		
		for (String email : getContactEmails(contact.getId())) {
			contact.addEmail(email);
		}
		for (String phone : getContactPhones(contact.getId())) {
			contact.addPhoneNumber(phone);
		}

		return contact;
	}
	
	private List<String> getContactPhones(String contactId) {
		List<String> phones = new LinkedList<String>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PHONE, allPhoneColumns, MySQLiteHelper.COLUMN_PHONE_PARENT_ID + " = '" + contactId + "'", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String phone = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_PHONE_NUMBER));
			phones.add(phone);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return phones;
	}
	
	private List<String> getContactEmails(String contactId) {
		List<String> emails = new LinkedList<String>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EMAIL, allEmailColumns, MySQLiteHelper.COLUMN_EMAIL_PARENT_ID + " = '" + contactId + "'", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String email = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EMAIL_ADDRESS));
			emails.add(email);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return emails;
				  
	}
	
	private void dropAllEmailsForContact(String contactId) {
		database.delete(MySQLiteHelper.TABLE_EMAIL,
				MySQLiteHelper.COLUMN_EMAIL_PARENT_ID + " = '" + contactId + "'", null);
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_EMAIL_ID, "");
		database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = '" + contactId + "'", null);
	}
	
	private void dropAllPhonesForContact(String contactId) {
		database.delete(MySQLiteHelper.TABLE_PHONE,
				MySQLiteHelper.COLUMN_PHONE_PARENT_ID + " = '" + contactId + "'", null);
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID, "");
		values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID, "");
		database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = '" + contactId + "'", null);
	}
	
	private void addAllPhonesForContact(String contactId, List<String> phones) {
		for (String phone : phones) {
			if (phone != null && !"".equals(phone.trim()) && !phoneNumberExists(contactId, phone)) {
				ContentValues values = new ContentValues();
				values.put(MySQLiteHelper.COLUMN_PHONE_NUMBER, phone);
				values.put(MySQLiteHelper.COLUMN_PHONE_PARENT_ID, contactId);
				database.insert(MySQLiteHelper.TABLE_PHONE, null, values);
			}
		}
	}
	
	private void addAllEmailsForContact(String contactId, List<String> emails) {
		for (String email : emails) {
			if (email != null && !"".equals(email.trim()) && !emailExists(contactId, email)) {
				Log.i("smcad", "Adding : [" + email + "] to Contact [" + contactId + "]");
				ContentValues values = new ContentValues();
				values.put(MySQLiteHelper.COLUMN_EMAIL_ADDRESS, email);
				values.put(MySQLiteHelper.COLUMN_EMAIL_PARENT_ID, contactId);
				database.insert(MySQLiteHelper.TABLE_EMAIL, null, values);
			}
		}
	}
	
	private void setDefaultEmail(String contactId, String email) {
		Log.i("smcad", "setDefaultEmail : ["+email+"] for contact [" + contactId + "]");
		if (email != null && !"".equals(email.trim()) && emailExists(contactId, email)) {
			Log.i("smcad", "searching for email [" + email + "]");
			String where = MySQLiteHelper.COLUMN_EMAIL_ADDRESS + " = '" + email + "' AND " + MySQLiteHelper.COLUMN_EMAIL_PARENT_ID + " = '" + contactId + "'";
			Cursor cursor = database.query(MySQLiteHelper.TABLE_EMAIL, allEmailColumns, where, null, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int email_id = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EMAIL_ID));
				ContentValues values = new ContentValues();
				values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_EMAIL_ID, email_id);
				
				Log.i("smcad", "setting default email table id [" + email_id + "] for contact [" + contactId + "]");
				database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = '" + contactId + "'", null);	
			}
			cursor.close();
		}
	}
	
	private void setDefaultPhone(String contactId, String phone) {
		setDefaultPhoneHelper(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID, contactId, phone);
	}

	private void setDefaultText(String contactId, String phone) {
		setDefaultPhoneHelper(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID, contactId, phone);
	}
	
	private void setDefaultPhoneHelper(String column, String contactId, String phone) {
		Log.i("smcad", "setDefaultPhoneHelper(" + column + ", " + contactId + ", " + phone + ")");
		if (phone != null && !"".equals(phone.trim()) && phoneNumberExists(contactId, phone)) {
			String where = MySQLiteHelper.COLUMN_PHONE_NUMBER + " = '" + phone + "' AND " + MySQLiteHelper.COLUMN_PHONE_PARENT_ID + " = '" + contactId + "'";
			Cursor cursor = database.query(MySQLiteHelper.TABLE_PHONE, allPhoneColumns, where, null, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int phone_id = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_PHONE_ID));
				ContentValues values = new ContentValues();
				values.put(column, phone_id);
				database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = '" + contactId + "'", null);
			}
			cursor.close();
		}
	}

	private String getDefaultEmail(String contactId) {
		int email_id = -1;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS, allContactColumns, MySQLiteHelper.COLUMN_ID + " = '" + contactId + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			email_id = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_EMAIL_ID));
			cursor.close();
		}
		if (email_id != -1) {
			cursor = database.query(MySQLiteHelper.TABLE_EMAIL, allEmailColumns, MySQLiteHelper.COLUMN_EMAIL_ID + " = " + email_id, null, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				String email = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EMAIL_ADDRESS));
				cursor.close();
				return email;
			}
		}
		cursor.close();
		return "";
	}
	
	private String getDefaultPhone(String contactId) {
		return getPhoneHelper(contactId, MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID);
	}
	
	
	
	private String getDefaultText(String contactId) {
		return getPhoneHelper(contactId, MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID);
	}
	
	private String getPhoneHelper(String contactId, String column) {
		int phone_id = -1;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS, allContactColumns, MySQLiteHelper.COLUMN_ID + " = '" + contactId + "'", null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			phone_id = cursor.getInt(cursor.getColumnIndex(column));
			cursor.close();
		}
		if (phone_id != -1) {
			cursor = database.query(MySQLiteHelper.TABLE_PHONE, allPhoneColumns, MySQLiteHelper.COLUMN_PHONE_ID + " = " + phone_id, null, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				String phone = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_PHONE_NUMBER));
				cursor.close();
				return phone;
			}
		}
		cursor.close();
		return "";
	}
	
	private boolean phoneNumberExists(String contactId, String phone) {
		String where = MySQLiteHelper.COLUMN_PHONE_NUMBER + " = '" + phone + "' AND " + MySQLiteHelper.COLUMN_PHONE_PARENT_ID + " = '" + contactId + "'";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PHONE, allPhoneColumns, where, null, null, null, null);
		cursor.moveToFirst();
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}
	
	private boolean emailExists(String contactId, String email) {
		String where = MySQLiteHelper.COLUMN_EMAIL_ADDRESS + " = '" + email + "' AND " + MySQLiteHelper.COLUMN_EMAIL_PARENT_ID + " = '" + contactId + "'";
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EMAIL, allEmailColumns, where, null, null, null, null);
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}

}
