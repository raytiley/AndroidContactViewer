package com.example.AndroidContactViewer.datastore;

import com.example.AndroidContactViewer.Contact;

public class Translator {
	public Contact makeContact(ContactDTO cdto){
		Contact c = new Contact(cdto.get_id(), cdto.getName());
		
		if(cdto.getEmail() != null){
			String[] emails = cdto.getEmail().split(",");
			for(String s : emails){
				c.addEmail(s);
			}
			if(emails.length > 0){
				c.setDefaultEmail(emails[0]);
			}
		}
		
		if(cdto.getPhone() != null){
			String[] phones = cdto.getPhone().split(",");
			for(String s : phones){
				c.addPhoneNumber(s);
			}
			// TODO improve to allow setting these independently
			if(phones.length > 0){
				c.setDefaultContactPhone(phones[0]);
			}
			if(phones.length > 0){
				c.setDefaultTextPhone(phones[0]);
			}
		}
		
		c.setTitle(cdto.getTitle());
		c.setTwitterId(cdto.getTwitterId());
		
		return c;
	}
}
