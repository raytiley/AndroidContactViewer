package com.example.AndroidContactViewer.datastore;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.example.AndroidContactViewer.Contact;
import com.google.gson.Gson;

public class WebContactDataSource implements ContactRepositoryInterface {
	private static final String URL_BASE = "http://contacts.tinyapollo.com/";
	private static final String API_KEY = "demo";

	private List<Contact> contacts;

	private class GetContacts extends AsyncTask<String, Void, Void>{

		@Override
		protected Void doInBackground(String... arg0) {
			synchronized(contacts){
				try{
					AndroidHttpClient client = AndroidHttpClient.newInstance("Android", null);
					HttpUriRequest request = new HttpGet(URL_BASE + "contacts/?key=" + API_KEY);
					HttpResponse response = client.execute(request);
					Gson gson = new Gson();
					ServiceResult result = gson.fromJson(
							new InputStreamReader(response.getEntity().getContent()), 
							ServiceResult.class);
					client.close();
					for(ContactDTO cdto : result.getContacts()){
						contacts.add(new Translator().makeContact(cdto));
					}
					return null;
				} catch (Exception e){
					Log.d("CONTACTS", e.getMessage());
					return null;
				} finally {
					contacts.notify();
				}
			}
		}

	}

	@Override
	public void open(){
		contacts = new ArrayList<Contact>();
		new GetContacts().execute();
		try {
			synchronized(contacts){
				contacts.wait();				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long count() {
		return Long.valueOf(contacts.size());
	}

	@Override
	public Contact get(long id) {
		return contacts.get(0);
	}

	@Override
	public List<Contact> all() {
		return contacts;
	}

	@Override
	public Contact add(Contact newContact) {
		// Translate to contact dto
		// post it
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(Contact contact) {
		// translate to contact dto
		// post it
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Contact update(Contact contact) {
		// translate to contact dto
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

}
