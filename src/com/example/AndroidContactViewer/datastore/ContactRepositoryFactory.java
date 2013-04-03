package com.example.AndroidContactViewer.datastore;

import android.content.Context;

public class ContactRepositoryFactory {
	private static final boolean useWeb = true;

	private static ContactRepositoryFactory INSTANCE;
	private ContactRepositoryFactory(){
		
	}
	
	public static ContactRepositoryFactory getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ContactRepositoryFactory();
		}
		return INSTANCE;
	}

	public ContactRepositoryInterface getContactRepository(Context context, DataUpdateHandler updateHandler){
		if(useWeb){
			return new WebContactDataSource(context, updateHandler);
		}
		return new ContactDataSource(context);
		
	}
}
