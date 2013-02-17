package com.example.AndroidContactViewer;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Model class for storing a single contact.
 * 
 */
public class Contact {

	/**
	 * The contact ID
	 */
	private int _contactId;

	/**
	 * The contact name.
	 */
	private String _name;

	/**
	 * The contact's title.
	 */
	private String _title;

	/**
	 * The default phone number to use for texting.
	 */
	private String _defaultTextPhone;

	/**
	 * The default phone number to use when making a call.
	 */
	private String _defaultContactPhone;

	/**
	 * The list of all phone numbers associated with this contact.
	 */
	private SortedSet<String> _phones;

	/**
	 * The default e-mail to use to send an e-mail.
	 */
	private String _defaultEmail;

	/**
	 * The list of all e-mails associated with this contact.
	 */
	private SortedSet<String> _emails;

	/**
	 * The contact's Twitter ID.
	 */
	private String _twitterId;

	/**
	 * Creates a contact and assigns its name.
	 * 
	 * @param name
	 *            the contact's name
	 */
	public Contact(int contactId, String name) {
		_contactId = contactId;
		_name = name;
		_title = null;
		_defaultContactPhone = null;
		_defaultTextPhone = null;
		_phones = new TreeSet<String>();
		_defaultEmail = null;
		_emails = new TreeSet<String>();
		_twitterId = null;
	}

	/**
	 * Gets the contact's ID.
	 */
	public int getContactId() {
		return _contactId;
	}

	/**
	 * Set the contact's name.
	 */
	public Contact setName(String name) {
		_name = name;
		return this;
	}

	/**
	 * Get the contact's name.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @return the contact's default texting phone number
	 */
	public String getDefaultTextPhone() {
		return _defaultTextPhone;
	}

	/**
	 * Set's the contact's default texting phone number.
	 */
	public Contact setDefaultTextPhone(String defaultTextPhone) {
		_defaultTextPhone = defaultTextPhone;
		_phones.add(_defaultTextPhone);
		return this;
	}

	/**
	 * @return the contact's default contact phone number
	 */
	public String getDefaultContactPhone() {
		return _defaultContactPhone;
	}

	/**
	 * Set's the contact's default contact phone number.
	 */
	public Contact setDefaultContactPhone(String defaultContactPhone) {
		_defaultContactPhone = defaultContactPhone;
		_phones.add(_defaultContactPhone);
		return this;
	}

	/**
	 * Gets the list of phone numbers associated with this contact.
	 * 
	 * @return The contact's phone numbers.
	 */
	public List<String> getPhoneNumbers() {
		return new ArrayList<String>(_phones);
	}

	/**
	 * Adds the given phone number to the list of phone numbers associated with
	 * this account.
	 * 
	 * @param phoneNumber
	 *            The phone number to add.
	 */
	public void addPhoneNumber(String phoneNumber) {
		_phones.add(phoneNumber);
	}

	/**
	 * Removes the given phone number from the list of phone numbers associated
	 * with this account.
	 * 
	 * @param phoneNumber
	 *            The phone number to remove from the account.
	 */
	public void removePhoneNumber(String phoneNumber) {
		_phones.remove(phoneNumber);
		if (phoneNumber == _defaultContactPhone) {
			_defaultContactPhone = null;
		}
		if (phoneNumber == _defaultTextPhone) {
			_defaultTextPhone = null;
		}
	}

	/**
	 * @return The contact's id
	 */
	public int getId() {
		return _contactId;
	}

	/**
	 * @return The contact's title
	 */
	public String getTitle() {
		return _title;
	}

	/**
	 * Sets the contact's title.
	 */
	public Contact setTitle(String title) {
		_title = title;
		return this;
	}

	/**
	 * @return the contact's default e-mail address
	 */
	public String getDefaultEmail() {
		return _defaultEmail;
	}

	/**
	 * Sets the contact's default e-mail address.
	 */
	public Contact setDefaultEmail(String defaultEmail) {
		_defaultEmail = defaultEmail;
		_emails.add(_defaultEmail);
		return this;
	}

	/**
	 * Gets the list of e-mails associated with this contact.
	 * 
	 * @return The contact's e-mails.
	 */
	public List<String> getEmails() {
		return new ArrayList<String>(_emails);
	}

	/**
	 * Adds the given email to the list of emails associated with this account.
	 * 
	 * @param phoneNumber
	 *            The phone number to add.
	 */
	public void addEmail(String email) {
		_emails.add(email);
	}

	/**
	 * Removes the given e-mail from the list of e-mails associated with this
	 * account.
	 * 
	 * @param email
	 *            The e-mail to remove.
	 */
	public void removeEmail(String email) {
		_emails.remove(email);
		if (email == _defaultEmail) {
			_defaultEmail = null;
		}
	}

	/**
	 * @return the contact's Twitter ID
	 */
	public String getTwitterId() {
		return _twitterId;
	}

	/**
	 * Sets the contact's Twitter ID.
	 */
	public Contact setTwitterId(String twitterId) {
		_twitterId = twitterId;
		return this;
	}

	/**
	 * Returns a string representation of this class.
	 */
	public String toString() {
		if (_title != null) {
			return _name + " (" + _title + ")";
		} else {
			return _name;
		}
	}

	/**
	 * Compares if two contacts are identical. Contacts are identical if their
	 * Contact ID is the same.
	 */
	public boolean equals(Object other) {
		if (!(other instanceof Contact)) {
			return false;
		}
		if (((Contact) other)._contactId == _contactId) {
			return true;
		}
		return false;
	}
}
