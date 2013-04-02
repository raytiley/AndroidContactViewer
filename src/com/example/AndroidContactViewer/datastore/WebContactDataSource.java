package com.example.AndroidContactViewer.datastore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.example.AndroidContactViewer.Contact;
import com.google.gson.Gson;

public class WebContactDataSource implements ContactRepositoryInterface {
	private static final String URL_BASE = "http://contacts.tinyapollo.com/";
	private static final String API_KEY = "somethingwitty";

	private List<Contact> contacts;
	private ContactDataSource localContactDataSource;

	private class PutContacts extends AsyncTask<List<Contact>, Void, Void>{

		@Override
		protected Void doInBackground(List<Contact>... arg0) {
			synchronized(contacts){
				try{
					for(Contact c : contacts){
						try{
							AndroidHttpClient client = AndroidHttpClient.newInstance("Android", null);
							HttpPut request = new HttpPut(URL_BASE + "contacts/" + c.getId() + "?key=" + API_KEY);
							Gson gson = new Gson();
							String contactStr = gson.toJson(c);
							System.out.println(contactStr);
							request.setEntity(new StringEntity("name:tyler"));
							
							HttpResponse response = client.execute(request);
							BufferedReader in = new BufferedReader(new InputStreamReader(
							        response.getEntity().getContent()));
							    String inputLine;
							    while ((inputLine = in.readLine()) != null) {
							      System.out.println(inputLine);
							    }
							
							// Apache IOUtils makes this pretty easy :)
//							Gson gson = new Gson();
//							ServiceResult result = gson.fromJson(
//									new InputStreamReader(response.getEntity().getContent()), 
//									ServiceResult.class);
							client.close();
//							for(ContactDTO cdto : result.getContacts()){
//								contacts.add(new Translator().makeContact(cdto));
//							}
							return null;
						} catch (Exception e){
							Log.d("CONTACTS", e.getMessage());
							return null;
						}
					}
				} finally {
					contacts.notify();
				}
				return null;
			}
		}

	}

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
					e.printStackTrace();
					Log.d("CONTACTS", e.getMessage());
					return null;
				} finally {
					contacts.notify();
				}
			}
		}

	}

	public WebContactDataSource(Context context){
		this.localContactDataSource = new ContactDataSource(context);
	}

	@Override
	public void open(){
		this.localContactDataSource.open();
		contacts = new ArrayList<Contact>();
		new GetContacts().execute();
		try {
			synchronized(contacts){
				contacts.wait();				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// TODO update local contact store
		for(Contact c : contacts){
			if(this.localContactDataSource.get(c.getId()) == null){
				this.localContactDataSource.add(c);
			}
		}
	}

	@Override
	public long count() {
		return Long.valueOf(contacts.size());
	}

	@Override
	public Contact get(String id) {
		return localContactDataSource.get(id);
	}

	@Override
	public List<Contact> all() {
		return localContactDataSource.all();
	}

	@Override
	public Contact add(Contact newContact) {
		return localContactDataSource.add(newContact);
	}

	@Override
	public int delete(Contact contact) {
		return localContactDataSource.delete(contact);
	}

	@Override
	public Contact update(Contact contact) {
		return localContactDataSource.update(contact);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void close() {
//		synchronized(contacts){
//			contacts.clear();
//			contacts.addAll(localContactDataSource.all());
//			new PutContacts().execute(contacts);
//			try {
//				contacts.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		// TODO Sync up with the web page
		this.localContactDataSource.close();
	}

}
