package com.example.AndroidContactViewer.datastore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import com.example.AndroidContactViewer.Contact;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Data source which updates a web repository with changes as they are made.
 * 
 * This data source assumes a reliable network connection, and as such
 * only retrieves contacts from the web service on startup. All updates are propagated
 * to the web service, but reads are all done locally after the initial update.
 */
public class WebContactDataSource implements ContactRepositoryInterface {
	private static final String URL_BASE = "http://mssecontacts.herokuapp.com/";
	private boolean initialRead = true;

	private Set<Contact> contacts = new HashSet<Contact>();
	private ContactDataSource localContactDataSource;
	private DataUpdateHandler updateHandler;

	private class PutContact extends AsyncTask<Contact, Void, Void>{

		@Override
		protected Void doInBackground(Contact... contact) {

			try{
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android", null);
				HttpPut request = new HttpPut(URL_BASE + "contacts/" + contact[0].getId());
				request.addHeader("Content-Type", "application/json");
				Gson gson = new Gson();
				String contactStr = gson.toJson(contact[0]);
				System.out.println(contactStr);
				request.setEntity(new StringEntity(contactStr));

				HttpResponse response = client.execute(request);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					Log.v("CONTACTS", inputLine);
				}
				client.close();
				return null;
			} catch (Exception e){
				Log.d("CONTACTS", e.getMessage());
				return null;
			}
		}
	}

	private class DeleteContact extends AsyncTask<Contact, Void, Void>{

		@Override
		protected Void doInBackground(Contact... contact) {

			try{
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android", null);
				HttpDelete request = new HttpDelete(URL_BASE + "contacts/" + contact[0].getId());
				request.addHeader("Content-Type", "application/json");

				HttpResponse response = client.execute(request);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					Log.v("CONTACTS", inputLine);
				}
				client.close();

				return null;
			} catch (Exception e){
				Log.d("CONTACTS", e.getMessage());
				return null;
			}
		}
	}

	private class PostContact extends AsyncTask<Contact, Void, Void>{

		@Override
		protected Void doInBackground(Contact... contact) {

			try{
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android", null);
				HttpPost request = new HttpPost(URL_BASE + "contacts/");
				request.addHeader("Content-Type", "application/json");
				Gson gson = new Gson();
				String contactStr = gson.toJson(contact[0]);
				System.out.println(contactStr);
				request.setEntity(new StringEntity(contactStr));

				HttpResponse response = client.execute(request);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					Log.v("CONTACTS", inputLine);
				}
				client.close();
				return null;
			} catch (Exception e){
				Log.d("CONTACTS", e.getMessage());
				return null;
			}
		}
	}

	private class GetContacts extends AsyncTask<String, Void, Void>{
		@Override
		protected void onPostExecute(Void result) {
			WebContactDataSource.this.localContactDataSource.open();
			for(Contact c : contacts){
				if(WebContactDataSource.this.localContactDataSource.get(c.getId()) == null){
					WebContactDataSource.this.localContactDataSource.add(c);
				}
			}
			WebContactDataSource.this.localContactDataSource.close();
			if(updateHandler != null){
				updateHandler.dataUpdated();
			}
		}

		@Override
		protected Void doInBackground(String... arg0) {
			try{
				Log.d("CONTACTS", "Retrieving all contacts");
				AndroidHttpClient client = AndroidHttpClient.newInstance("Android", null);
				HttpUriRequest request = new HttpGet(URL_BASE + "/contacts");
				request.setHeader("Content-Type", "application/json");
				request.setHeader("Accept", "application/json");
				HttpResponse response = client.execute(request);
				Gson gson = new Gson();
				String s;
				String json = "";
				BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				while((s = br.readLine()) != null){
					json += s;
				}
				Log.v("CONTACTS", json);
				List<Contact> cs = gson.fromJson(json, new TypeToken<List<Contact>>(){}.getType());
				client.close();
				for(Contact c : cs){
					contacts.add(c);
				}
				return null;
			} catch (Exception e){
				e.printStackTrace();
				Log.d("CONTACTS", e.getMessage());
				return null;
			} 
		}
	}

	public WebContactDataSource(Context context, DataUpdateHandler updateHandler){
		this.localContactDataSource = new ContactDataSource(context);
		this.updateHandler = updateHandler;
	}

	@Override
	public void open(){
		this.localContactDataSource.open();
		// On the first call to open, get any new contacts from the web service.
		if(initialRead){
			initialRead = false;
			new GetContacts().execute();
		}
	}

	@Override
	public long count() {
		return localContactDataSource.count();
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
		Contact c = localContactDataSource.add(newContact);
		new PostContact().execute(c);
		return c;
	}

	@Override
	public int delete(Contact contact) {
		int i = localContactDataSource.delete(contact);
		new DeleteContact().execute(contact);
		return i;
	}

	@Override
	public Contact update(Contact contact) {
		Contact c =  localContactDataSource.update(contact);
		new PutContact().execute(c);
		return c;
	}

	@Override
	public void close() {
		this.localContactDataSource.close();
	}

}