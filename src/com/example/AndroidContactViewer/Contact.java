package com.example.AndroidContactViewer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Model class for storing a single contact.
 * 
 */
@SuppressLint("DefaultLocale")
public class Contact {

	/**
	 * The contact ID
	 */
	private String id;

	/**
	 * @param _contactId the _contactId to set
	 */
	public void set_contactId(String _contactId) {
		this.id = _contactId;
	}

	/**
	 * The contact name.
	 */
	private String name;

	/**
	 * The contact's title.
	 */
	private String title;

	/**
	 * The default phone number to use for texting.
	 */
	private String default_txt_phone;

	/**
	 * The default phone number to use when making a call.
	 */
	private String default_call_phone;

	/**
	 * The list of all phone numbers associated with this contact.
	 */
	private SortedSet<String> phones;

	/**
	 * The default e-mail to use to send an e-mail.
	 */
	private String default_email;

	/**
	 * The list of all e-mails associated with this contact.
	 */
	private SortedSet<String> emails;

	/**
	 * The contact's Twitter ID.
	 */
	private String _twitterId;
	
	private String groupId;
	
	/**
	 * Creates a contact and assigns its name.
	 * 
	 * @param name
	 *            the contact's name
	 */
	public Contact(String contactId, String name) {
		id = contactId;
		this.name = name;
		title = "";
		default_call_phone = "";
		default_txt_phone = "";
		phones = new TreeSet<String>();
		default_email = "";
		emails = new TreeSet<String>();
		_twitterId = "";
	}
	
	public Contact(String name){
		this(UUID.randomUUID().toString().replace('-', 'x'), name);
	}
	

	/**
	 * Set the contact's name.
	 */
	public Contact setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Get the contact's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the contact's default texting phone number
	 */
	public String getDefaultTextPhone() {
		return default_txt_phone;
	}

	/**
	 * Set's the contact's default texting phone number.
	 */
	public Contact setDefaultTextPhone(String defaultTextPhone) {
		default_txt_phone = defaultTextPhone;
		this.addPhoneNumber(default_txt_phone);
		return this;
	}

	/**
	 * @return the contact's default contact phone number
	 */
	public String getDefaultContactPhone() {
		return default_call_phone;
	}

	/**
	 * Set's the contact's default contact phone number.
	 */
	public Contact setDefaultContactPhone(String defaultContactPhone) {
		default_call_phone = defaultContactPhone;
		this.addPhoneNumber(defaultContactPhone);
		return this;
	}

	/**
	 * Gets the list of phone numbers associated with this contact.
	 * 
	 * @return The contact's phone numbers.
	 */
	public List<String> getPhoneNumbers() {
		return new ArrayList<String>(phones);
	}

	/**
	 * Adds the given phone number to the list of phone numbers associated with
	 * this account.
	 * 
	 * @param phoneNumber
	 *            The phone number to add.
	 */
	public Contact addPhoneNumber(String phoneNumber) {
        if(phoneNumber.length() > 1) {
		    phones.add(phoneNumber);
        }
        return this;
	}

	/**
	 * @return The contact's id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return The contact's title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the contact's title.
	 */
	public Contact setTitle(String title) {
		this.title = title;
		return this;
	}

	/**
	 * @return the contact's default e-mail address
	 */
	public String getDefaultEmail() {
		return default_email;
	}

	/**
	 * Sets the contact's default e-mail address.
	 */
	public Contact setDefaultEmail(String defaultEmail) {
		default_email = defaultEmail;
		this.addEmail(defaultEmail);
		return this;
	}

	/**
	 * Gets the list of e-mails associated with this contact.
	 * 
	 * @return The contact's e-mails.
	 */
	public List<String> getEmails() {
		return new ArrayList<String>(emails);
	}

	/**
	 * Adds the given email to the list of emails associated with this account.
	 * 
	 * @param phoneNumber
	 *            The phone number to add.
	 */
	public Contact addEmail(String email) {
        if(email.length() > 1) {
		    emails.add(email);
        }
        return this;
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
		if (title != null) {
			return name + " (" + title + ")";
		} else {
			return name;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if(id != null){
			return id.hashCode();
		}
		return super.hashCode();
	}

	/**
	 * Compares if two contacts are identical. Contacts are identical if their
	 * Contact ID is the same.
	 */
	public boolean equals(Object other) {
		if (!(other instanceof Contact)) {
			return false;
		}
		if (((Contact) other).id == id) {
			return true;
		}
		if(((Contact) other).id.equals(id)){
			return true;
		}
		return false;
	}

    public void clearEmailsAndPhones() {
        this.default_call_phone = "";
        this.default_txt_phone = "";
        this.default_email = "";
        this.emails.clear();
        this.phones.clear();
    }

    public void downloadGravatar(Context context) {

        // Try to get gravatar
        if(this.getId() != null) {
            try {
                String email = this.getDefaultEmail() == null ? this.getName() : this.getDefaultEmail().toLowerCase();
                String gravatar = MD5Util.md5Hex(email);
                URL url = new URL("http://www.gravatar.com/avatar/" + gravatar + "?d=monsterid");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                String filename = this.getLocalGravatarPath();
                FileOutputStream output = context.openFileOutput(filename, Context.MODE_PRIVATE);

                int read;
                byte[] data = new byte[1024];
                while((read = is.read(data)) != -1)
                    output.write(data, 0, read);
            }
            catch (Exception e)
            {
                Log.e("gravatar", e.getMessage());
            }
        }
    }

    public String getLocalGravatarPath() {
        return this.getId() + "-gravatar.jpg";
    }

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
