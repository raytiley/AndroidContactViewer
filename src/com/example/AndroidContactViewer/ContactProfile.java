package com.example.AndroidContactViewer;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AndroidContactViewer.datastore.ContactDataSource;

public class ContactProfile extends Activity implements OnClickListener {
	private Contact contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_profile);
		
		int contactID = (Integer)getIntent().getExtras().get("ContactID");
		
		ToolbarConfig toolbar = new ToolbarConfig(this, "Profile");
		ContactDataSource datasource = new ContactDataSource(this);
		datasource.open();
		contact = datasource.get(contactID);
		datasource.close();

		// setup the "Edit" button
		Button button = toolbar.getToolbarRightButton();
		button.setText("Edit");
		button.setOnClickListener(this);
		
		button = toolbar.getToolbarLeftButton();
		button.setText("Back");
		button.setOnClickListener(this);
		
		((TextView)findViewById(R.id.profile_name)).setText(contact.getName());
		((TextView)findViewById(R.id.profile_title)).setText(contact.getTitle());
		
		// Add e-mails to view
		
		List<String> emailList = contact.getEmails();
		
		if (emailList.size() != 0) {
			// Can't use a ListView for e-mails or phone lists since we want the entire
			// profile screen to scroll rather than two scroll areas for the e-mails and
			// phone lists.
			
			LinearLayout emails = (LinearLayout)findViewById(R.id.profile_email_list);
			for (String email : emailList) {
				LayoutInflater inflater = getLayoutInflater();
				View item = inflater.inflate(R.layout.profile_email_item, emails, false);
				
				((TextView) item.findViewById(R.id.profile_email_item_text)).setText(email);
				
				ImageButton email_btn = (ImageButton)item.findViewById(R.id.profile_email_item_image);
				email_btn.setTag(email);
				email_btn.setOnClickListener(this);
				
				emails.addView(item);
			}
		} else {
			// If there are no e-mails associated with this contact, don't show the label
			((TextView)findViewById(R.id.profile_emails_label)).setVisibility(View.GONE);
		}
		
		// Add phones to view
		
		List<String> phoneList = contact.getPhoneNumbers();
		
		if (phoneList.size() != 0) {
			LinearLayout phones = (LinearLayout)findViewById(R.id.profile_phone_list);
			for (String phone : phoneList) {
				LayoutInflater inflater = getLayoutInflater();
				View item = inflater.inflate(R.layout.profile_phone_item, phones, false);
				
				((TextView) item.findViewById(R.id.profile_phone_item_text)).setText(phone);
				
				ImageButton contact_btn = (ImageButton)item.findViewById(R.id.profile_phone_item_contact);
				contact_btn.setTag(phone);
				contact_btn.setOnClickListener(this);
				
				ImageButton text_btn = (ImageButton)item.findViewById(R.id.profile_phone_item_texting);
				text_btn.setTag(phone);
				text_btn.setOnClickListener(this);
				
				phones.addView(item);
			}
		} else {
			// If there are no phone numbers associated with this contact, don't show the label
			((TextView)findViewById(R.id.profile_phones_label)).setVisibility(View.GONE);
		}
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.profile_email_item_image:
				String email = (String)v.getTag();
				Toast.makeText(
					ContactProfile.this,
					"E-mailing address " + email,
					Toast.LENGTH_SHORT).show();
				break;
			case R.id.profile_phone_item_contact:
				String phone = (String)v.getTag();
				Toast.makeText(
					ContactProfile.this,
					"Calling phone " + phone,
					Toast.LENGTH_SHORT).show();
				break;
			case R.id.profile_phone_item_texting:
				String txt = (String)v.getTag();
				Toast.makeText(
					ContactProfile.this,
					"Texting phone " + txt,
					Toast.LENGTH_SHORT).show();
				break;
			case R.id.toolbar_left_button:
				finish();
				break;
			case R.id.toolbar_right_button:
				Toast.makeText(
						ContactProfile.this,
						"Edit Profile Clicked.",
						Toast.LENGTH_LONG).show();
				break;
			default:
				Toast.makeText(
					ContactProfile.this,
					"Unknown clickable item ID",
					Toast.LENGTH_SHORT).show();
				break;
				
		}
	}
}
