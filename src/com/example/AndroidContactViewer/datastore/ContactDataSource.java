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

		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
				allContactColumns, null, null, null, null, null);

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

	private Contact cursorToContact(Cursor cursor) {
		Contact contact = new Contact(cursor.getInt(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_ID)),
				cursor.getString(cursor.getColumnIndexOrThrow(MySQLiteHelper.COLUMN_CONTACT_NAME)));
		contact.setTitle(cursor.getString(2));
		// contact.setDefaultTextPhone();
		// contact.setDefaultContactPhone();
		// contact.setDefaultEmail();
		contact.setTwitterId(cursor.getString(6));

		return contact;
	}

	@Override
	public Contact add(Contact contact) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTACT_NAME, contact.getName());
		values.put(MySQLiteHelper.COLUMN_CONTACT_TITLE, contact.getTitle());
		// values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID, );
		// values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID, );
		// values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_EMAIL_ID, );
		values.put(MySQLiteHelper.COLUMN_CONTACT_TWITTER_ID,
				contact.getTwitterId());
		long insertId = database.insert(MySQLiteHelper.TABLE_CONTACTS, null,
				values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
				allContactColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId,
				null, null, null, null);
		cursor.moveToFirst();
		Contact newContact = cursorToContact(cursor);
		cursor.close();
		return newContact;

	}

	@Override
	public int delete(Contact contact) {
		long id = contact.getId();
		return database.delete(MySQLiteHelper.TABLE_CONTACTS,
				MySQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	@Override
	public Contact update(Contact contact) {

		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_CONTACT_NAME, contact.getName());
		values.put(MySQLiteHelper.COLUMN_CONTACT_TITLE, contact.getTitle());
		// values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_TEXT_PHONE_ID, );
		// values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_CONTACT_PHONE_ID, );
		// values.put(MySQLiteHelper.COLUMN_CONTACT_DEFAULT_EMAIL_ID, );
		values.put(MySQLiteHelper.COLUMN_CONTACT_TWITTER_ID,
				contact.getTwitterId());
		database.update(MySQLiteHelper.TABLE_CONTACTS, values, null, null);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
				allContactColumns,
				MySQLiteHelper.COLUMN_ID + " = " + contact.getContactId(),
				null, null, null, null);
		cursor.moveToFirst();
		Contact updatedContact = cursorToContact(cursor);
		cursor.close();
		return updatedContact;

	}

	@Override
	public Contact get(long id) {
		Cursor cursor = database.query(MySQLiteHelper.TABLE_CONTACTS,
				allContactColumns, MySQLiteHelper.COLUMN_ID + " = " + id, null,
				null, null, null);
		cursor.moveToFirst();
		Contact contact = cursorToContact(cursor);
		cursor.close();

		return contact;
	}

}
