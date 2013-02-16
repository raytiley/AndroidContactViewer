package com.example.AndroidContactViewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContactRepository {
	
	/**
	 * Instance of the repository
	 */
	private static ContactRepository _repository = null;
	
	/**
	 * The list of contacts in the system
	 */
	private ArrayList<Contact> _contacts;
	
	/**
	 * Creates a repository to contain contacts.
	 */
	private ContactRepository() {
		// TODO: Obtain from Android device
		_contacts = new ArrayList<Contact>();
		_contacts.add(new Contact(0, "Malcom Reynolds")
				.setDefaultEmail("mal@serenity.com")
				.setTitle("Captain")
				.setDefaultContactPhone("612-555-1234")
				.setDefaultTextPhone("612-555-1234")
				.setTwitterId("malcomreynolds"));
		_contacts.add(new Contact(1, "Zoe Washburne")
				.setDefaultEmail("zoe@serenity.com")
				.setTitle("First Mate")
				.setDefaultContactPhone("612-555-5678")
				.setDefaultTextPhone("612-555-5678")
				.setTwitterId("zoewashburne"));
		_contacts.add(new Contact(2, "Hoban Washburne")
				.setDefaultEmail("wash@serenity.com")
				.setTitle("Pilot")
				.setDefaultContactPhone("612-555-9012")
				.setDefaultTextPhone("612-555-9012")
				.setTwitterId("wash"));
		_contacts.add(new Contact(3, "Jayne Cobb")
				.setDefaultEmail("jayne@serenity.com")
				.setTitle("Muscle")
				.setDefaultContactPhone("612-555-3456")
				.setDefaultTextPhone("612-555-3456")
				.setTwitterId("heroofcanton"));
		_contacts.add(new Contact(4, "Kaylee Frye")
				.setDefaultEmail("kaylee@serenity.com")
				.setTitle("Engineer")
				.setDefaultContactPhone("612-555-7890")
				.setDefaultTextPhone("612-555-7890")
				.setTwitterId("kaylee"));
		_contacts.add(new Contact(5, "Simon Tam")
				.setDefaultEmail("simon@serenity.com")
				.setTitle("Doctor")
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
	}
	
	public static ContactRepository getRepository() {
		if (_repository == null) {
			_repository = new ContactRepository();
		}
		return _repository;
	}
	
	public List<Contact> getContacts() {
		return _contacts;
	}
	
	public Contact getContact(int contactId) {
		Iterator<Contact> it = _contacts.iterator();
		Contact contact;
		
		while (it.hasNext()) {
			contact = it.next();
			if (contact.getContactId() == contactId) {
				return contact;
			}
		}
		
		return null;
	}

}
