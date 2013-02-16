package com.example.AndroidContactViewer;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactProfile extends Activity {
	private Contact contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_profile);
		
		int contactID = (Integer)getIntent().getExtras().get("ContactID");
		
		ToolbarConfig toolbar = new ToolbarConfig(this, "Profile");
		
		contact = ContactRepository.getRepository().getContact(contactID);

		// setup the "Edit" button
		Button button = toolbar.getToolbarRightButton();
		button.setText("Edit");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(
						ContactProfile.this,
						"Edit Profile Clicked.",
						Toast.LENGTH_LONG).show();
			}
		});
		
		button = toolbar.getToolbarLeftButton();
		button.setText("Back");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				finish();
			}
		});
		
		((TextView)findViewById(R.id.profile_name)).setText(contact.getName());
		((TextView)findViewById(R.id.profile_title)).setText(contact.getTitle());
		((ListView)findViewById(R.id.profile_email_list)).setAdapter(new EmailListAdapter(this, R.layout.profile_email_item, contact.getEmails()));
		((ListView)findViewById(R.id.profile_phone_list)).setAdapter(new PhoneListAdapter(this, R.layout.profile_phone_item, contact.getPhoneNumbers()));
	}

	/*
	 * We need to provide a custom adapter in order to use a custom list item
	 * view.
	 */
	private class EmailListAdapter extends ArrayAdapter<String> {

		public EmailListAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View item = inflater.inflate(R.layout.profile_email_item, parent, false);

			String email = getItem(position);
			((TextView) item.findViewById(R.id.profile_email_item_text)).setText(email);

			return item;
		}
	}

	/*
	 * We need to provide a custom adapter in order to use a custom list item
	 * view.
	 */
	private class PhoneListAdapter extends ArrayAdapter<String> {

		public PhoneListAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View item = inflater.inflate(R.layout.profile_phone_item, parent, false);

			String phone = getItem(position);
			((TextView) item.findViewById(R.id.profile_phone_item_text)).setText(phone);

			return item;
		}
	}


}
