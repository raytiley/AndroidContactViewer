package com.example.AndroidContactViewer.datastore;

import android.content.Context;

public class ContactRepositoryFactory {
	private static final boolean useWeb = false;

	private static ContactRepositoryFactory INSTANCE;
	private ContactRepositoryFactory(){
		
	}
	
	public static ContactRepositoryFactory getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ContactRepositoryFactory();
		}
		return INSTANCE;
	}

	public ContactRepositoryInterface getContactRepository(Context context){
		if(useWeb){
			return new WebContactDataSource(context);
		}
		return new ContactDataSource(context);
		
	}
}
