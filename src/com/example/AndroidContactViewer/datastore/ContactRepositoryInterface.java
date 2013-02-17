package com.example.AndroidContactViewer.datastore;

import java.util.List;

import com.example.AndroidContactViewer.Contact;

public interface ContactRepositoryInterface {

	public long count();
	public Contact get(long id);
	public List<Contact> all();
	public Contact add(Contact newContact);
	public int delete(Contact contact);
	public Contact update(Contact contact);
	
}
