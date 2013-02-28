package com.example.AndroidContactViewer.datastore;

import java.util.ArrayList;
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

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();

		// populate database with dummy contacts
		if (count() == 0) {
			List<Contact> _contacts = new ArrayList<Contact>();
			_contacts.add(new Contact(0, "Malcom Reynolds")
					.setDefaultEmail("mal@serenity.com").setTitle("Captain")
					.setDefaultContactPhone("612-555-1234")
					.setDefaultTextPhone("612-555-1234")
					.setTwitterId("malcomreynolds"));
			_contacts.add(new Contact(1, "Zoe Washburne")
					.setDefaultEmail("zoe@serenity.com").setTitle("First Mate")
					.setDefaultContactPhone("612-555-5678")
					.setDefaultTextPhone("612-555-5678")
					.setTwitterId("zoewashburne"));
			Contact steve = new Contact(9, "Steven McAdams")
					.setDefaultEmail("steven.mcadams@lmco.com")
					.setTitle("Steve")
					.setDefaultContactPhone("123-456-9874")
					.setDefaultTextPhone("321-654-6541");
			steve.addEmail("smcadams86@gmail.com");
			steve.addPhoneNumber("645-612-6548");
			_contacts.add(steve);

			_contacts.add(new Contact(2, "Hoban Washburne")
					.setDefaultEmail("wash@serenity.com").setTitle("Pilot")
					.setDefaultContactPhone("612-555-9012")
					.setDefaultTextPhone("612-555-9012").setTwitterId("wash"));
			_contacts.add(new Contact(3, "Jayne Cobb")
					.setDefaultEmail("jayne@serenity.com").setTitle("Muscle")
					.setDefaultContactPhone("612-555-3456")
					.setDefaultTextPhone("612-555-3456")
					.setTwitterId("heroofcanton"));
			_contacts
					.add(new Contact(4, "Kaylee Frye")
							.setDefaultEmail("kaylee@serenity.com")
							.setTitle("Engineer")
							.setDefaultContactPhone("612-555-7890")
							.setDefaultTextPhone("612-555-7890")
							.setTwitterId("kaylee"));
			_contacts.add(new Contact(5, "Simon Tam")
					.setDefaultEmail("simon@serenity.com").setTitle("Doctor")
					.setDefaultContactPhone("612-555-4321")
					.setDefaultTextPhone("612-555-4321")
					.setTwitterId("simontam"));
			_contacts.add(new Contact(6, "River Tam")
					.setDefaultEmail("river@serenity.com")
					.setTitle("Doctor's Sister")
					.setDefaultContactPhone("612-555-8765")
					.setDefaultTextPhone("612-555-8765")
					.setTwitterId("miranda"));
			_contacts.add(new Contact(7, "Shepherd Book")
					.setDefaultEmail("shepherd@serenity.com")
					.setTitle("Shepherd")
					.setDefaultContactPhone("612-555-2109")
					.setDefaultTextPhone("612-555-2109")
					.setTwitterId("shepherdbook"));
			_contacts.add(new Contact(8, "Basic Contact"));

			for (Contact c : _contacts) {
				add(c);
			}
		}
	}

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
		values.put(MySQLiteHelper.COLUMN_CONTACT_NAME, contact.getName());
		values.put(MySQLiteHelper.COLUMN_CONTACT_TITLE, contact.getTitle());
		values.put(MySQLiteHelper.COLUMN_CONTACT_TWITTER_ID,
				contact.getTwitterId());
		long insertId = database.insert(MySQLiteHelper.TABLE_CONTACTS, null,
				values);
		
		List<String> emails = contact.getEmails();
		emails.add(contact.getDefaultEmail());
		addAllEmailsForContact(insertId, emails);
		
		List<String> phones = contact.getPhoneNumbers();
		phones.add(contact.getDefaultContactPhone());
		phones.add(contact.getDefaultTextPhone());
		addAllPhonesForContact(insertId, phones);
		
		setDefaultPhone(insertId, contact.getDefaultContactPhone());
		setDefaultEmail(insertId, contact.getDefaultEmail());
		setDefaultText(insertId, contact.getDefaultTextPhone());
		
		return get(insertId);
	}

	@Override
	public int delete(Contact contact) {
		long id = contact.getId();
		dropAllEmailsForContact(id);
		dropAllPhonesForContact(id);
		return database.delete(MySQLiteHelper.TABLE_CONTACTS,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
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
		database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = " + contact.getId(), null);
		
		addAllEmailsForContact(contact.getId(), contact.getEmails());
		addAllPhonesForContact(contact.getId(), contact.getPhoneNumbers());
		setDefaultPhone(contact.getId(), contact.getDefaultContactPhone());
		setDefaultEmail(contact.getId(), contact.getDefaultEmail());
		setDefaultText(contact.getId(), contact.getDefaultTextPhone());
		
		return get(contact.getId());
	}

	@Override
	public Contact get(long contactId) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
				allContactColumns, MySQLiteHelper.COLUMN_ID + " = " + contactId, null,
				null, null, null);
		cursor.moveToFirst();
		Contact contact = cursorToContact(cursor);
		cursor.close();

		return contact;
	}

	private Contact cursorToContact(Cursor cursor) {
		Contact contact = new Contact(
				(int) cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)),
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
	
	private List<String> getContactPhones(long contactId) {
		List<String> phones = new LinkedList<String>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PHONE, allPhoneColumns, MySQLiteHelper.COLUMN_PHONE_PARENT_ID + " = " + contactId, null, null, null, null);
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
	
	private List<String> getContactEmails(long contactId) {
		List<String> emails = new LinkedList<String>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EMAIL, allEmailColumns, MySQLiteHelper.COLUMN_EMAIL_PARENT_ID + " = " + contactId, null, null, null, null);
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
	
	private void dropAllEmailsForContact(long contactId) {
		database.delete(MySQLiteHelper.TABLE_EMAIL,
				MySQLiteHelper.COLUMN_EMAIL_PARENT_ID + " = " + contactId, null);
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_EMAIL_ID, "");
		database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = " + contactId, null);
	}
	
	private void dropAllPhonesForContact(long contactId) {
		database.delete(MySQLiteHelper.TABLE_PHONE,
				MySQLiteHelper.COLUMN_PHONE_PARENT_ID + " = " + contactId, null);
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID, "");
		values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID, "");
		database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = " + contactId, null);
	}
	
	private void addAllPhonesForContact(long contactId, List<String> phones) {
		for (String phone : phones) {
			if (phone != null && !"".equals(phone.trim()) && !phoneNumberExists(contactId, phone)) {
				ContentValues values = new ContentValues();
				values.put(MySQLiteHelper.COLUMN_PHONE_NUMBER, phone);
				values.put(MySQLiteHelper.COLUMN_PHONE_PARENT_ID, contactId);
				database.insert(MySQLiteHelper.TABLE_PHONE, null, values);
			}
		}
	}
	
	private void addAllEmailsForContact(long contactId, List<String> emails) {
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
	
	private void setDefaultEmail(long contactId, String email) {
		Log.i("smcad", "setDefaultEmail : ["+email+"] for contact [" + contactId + "]");
		if (email != null && !"".equals(email.trim()) && emailExists(contactId, email)) {
			Log.i("smcad", "searching for email [" + email + "]");
			String where = MySQLiteHelper.COLUMN_EMAIL_ADDRESS + " = '" + email + "' AND " + MySQLiteHelper.COLUMN_EMAIL_PARENT_ID + " = " + contactId;
			Cursor cursor = database.query(MySQLiteHelper.TABLE_EMAIL, allEmailColumns, where, null, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int email_id = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_EMAIL_ID));
				ContentValues values = new ContentValues();
				values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_EMAIL_ID, email_id);
				
				Log.i("smcad", "setting default email table id [" + email_id + "] for contact [" + contactId + "]");
				database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = " + contactId, null);	
			}
			cursor.close();
		}
	}
	
	private void setDefaultPhone(long contactId, String phone) {
		setDefaultPhoneHelper(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID, contactId, phone);
	}

	private void setDefaultText(long contactId, String phone) {
		setDefaultPhoneHelper(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID, contactId, phone);
	}
	
	private void setDefaultPhoneHelper(String column, long contactId, String phone) {
		Log.i("smcad", "setDefaultPhoneHelper(" + column + ", " + contactId + ", " + phone + ")");
		if (phone != null && !"".equals(phone.trim()) && phoneNumberExists(contactId, phone)) {
			String where = MySQLiteHelper.COLUMN_PHONE_NUMBER + " = '" + phone + "' AND " + MySQLiteHelper.COLUMN_PHONE_PARENT_ID + " = " + contactId;
			Cursor cursor = database.query(MySQLiteHelper.TABLE_PHONE, allPhoneColumns, where, null, null, null, null);
			cursor.moveToFirst();
			if (cursor.getCount() > 0) {
				int phone_id = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_PHONE_ID));
				ContentValues values = new ContentValues();
				values.put(column, phone_id);
				database.update(MySQLiteHelper.TABLE_CONTACTS, values, MySQLiteHelper.COLUMN_ID + " = " + contactId, null);
			}
			cursor.close();
		}
	}

	private String getDefaultEmail(long contactId) {
		int email_id = -1;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS, allContactColumns, MySQLiteHelper.COLUMN_ID + " = " + contactId, null, null, null, null);
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
	
	private String getDefaultPhone(long contactId) {
		return getPhoneHelper(contactId, MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID);
	}
	
	
	
	private String getDefaultText(long contactId) {
		return getPhoneHelper(contactId, MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID);
	}
	
	private String getPhoneHelper(long contactId, String column) {
		int phone_id = -1;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS, allContactColumns, MySQLiteHelper.COLUMN_ID + " = " + contactId, null, null, null, null);
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
	
	private boolean phoneNumberExists(long contactId, String phone) {
		String where = MySQLiteHelper.COLUMN_PHONE_NUMBER + " = '" + phone + "' AND " + MySQLiteHelper.COLUMN_PHONE_PARENT_ID + " = " + contactId;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_PHONE, allPhoneColumns, where, null, null, null, null);
		cursor.moveToFirst();
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}
	
	private boolean emailExists(long contactId, String email) {
		String where = MySQLiteHelper.COLUMN_EMAIL_ADDRESS + " = '" + email + "' AND " + MySQLiteHelper.COLUMN_EMAIL_PARENT_ID + " = " + contactId;
		Cursor cursor = database.query(MySQLiteHelper.TABLE_EMAIL, allEmailColumns, where, null, null, null, null);
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}

}
