package com.example.AndroidContactViewer;

/** Model class for storing a single contact.
 *
 */
public class Contact {
	
	/** Creates a contact and assigns its name.
	 * 
	 * @param name the contact's name
	 */
	public Contact(String name) {
		_name = name;
	}
	
	private String _name;
	
	/** Set the contact's name.
	 */
	public Contact setName(String name) {
		_name = name;
		return this;
	}

	/** Get the contact's name.
	 */
	public String getName() {
		return _name;
	}
	
	private String _phone;

	/**
	 * @return the contact's phone number
	 */
	public String getPhone() {
		return _phone;
	}

	/** Set's the contact's phone number.
	 */
	public Contact setPhone(String phone) {
		_phone = phone;
		return this;
	}
	
	private String _title;

	/**
	 * @return The contact's title
	 */
	public String getTitle() {
		return _title;
	}

	/** Sets the contact's title.
	 */
	public Contact setTitle(String title) {
		_title = title;
		return this;
	}
	
	private String _email;

	/**
	 * @return the contact's e-mail address
	 */
	public String getEmail() {
		return _email;
	}

	/** Sets the contact's e-mail address.
	 */
	public Contact setEmail(String email) {
		_email = email;
		return this;
	}
	
	private String _twitterId;

	/**
	 * @return the contact's Twitter ID
	 */
	public String getTwitterId() {
		return _twitterId;
	}

	/** Sets the contact's Twitter ID.
	 */
	public Contact setTwitterId(String twitterId) {
		_twitterId = twitterId;
		return this;
	}
	
	public String toString() {
		return _name + " (" + _title + ")";
	}
}

