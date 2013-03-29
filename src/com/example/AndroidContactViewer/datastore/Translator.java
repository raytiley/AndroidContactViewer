package com.example.AndroidContactViewer.datastore;

import com.example.AndroidContactViewer.Contact;

public class Translator {
	public Contact makeContact(ContactDTO cdto){
		Contact c = new Contact(0, cdto.getName());
		String[] emails = cdto.getEmail().split(",");
		for(String s : emails){
			c.addEmail(s);
		}
		if(emails.length > 0){
			c.setDefaultEmail(emails[0]);
		}
		return c;
	}
}
