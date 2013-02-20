package com.example.AndroidContactViewer;

import java.util.List;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.*;
import com.example.AndroidContactViewer.datastore.ContactDataSource;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class ContactViewActivity extends Activity implements OnClickListener {
	private Contact _contact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_view);

		int contactID = (Integer) getIntent().getExtras().get("ContactID");

		Resources res = getResources();

		ToolbarConfig toolbar = new ToolbarConfig(this,
				res.getString(R.string.profile));
		ContactDataSource datasource = new ContactDataSource(this);
		datasource.open();
		_contact = datasource.get(contactID);
		datasource.close();

		// setup the "Edit" button
		Button button = toolbar.getToolbarRightButton();
		button.setText(res.getString(R.string.edit));
		button.setOnClickListener(this);

		button = toolbar.getToolbarLeftButton();
		button.setText(res.getString(R.string.back));
		button.setOnClickListener(this);

		((TextView) findViewById(R.id.profile_name))
				.setText(_contact.getName());
		((TextView) findViewById(R.id.profile_title)).setText(_contact
				.getTitle());

		// Add e-mails to view

		List<String> emailList = _contact.getEmails();

		if (emailList.size() != 0) {
			// Can't use a ListView for e-mails or phone lists since we want the
			// entire
			// profile screen to scroll rather than two scroll areas for the
			// e-mails and
			// phone lists.

			LinearLayout emails = (LinearLayout) findViewById(R.id.profile_email_list);
			for (String email : emailList) {
				LayoutInflater inflater = getLayoutInflater();
				View item = inflater.inflate(R.layout.contact_email_view_item,
						emails, false);

				((TextView) item.findViewById(R.id.profile_email_item_text))
						.setText(email);

				ImageButton email_btn = (ImageButton) item
						.findViewById(R.id.profile_email_item_image);
				email_btn.setTag(email);
				email_btn.setOnClickListener(this);

				emails.addView(item);
			}
		} else {
			// If there are no e-mails associated with this contact, don't show
			// the label
			((TextView) findViewById(R.id.profile_emails_label))
					.setVisibility(View.GONE);
		}

		// Add phones to view

		List<String> phoneList = _contact.getPhoneNumbers();

		if (phoneList.size() != 0) {
			LinearLayout phones = (LinearLayout) findViewById(R.id.profile_phone_list);
			for (String phone : phoneList) {
				LayoutInflater inflater = getLayoutInflater();
				View item = inflater.inflate(R.layout.contact_phone_view_item,
						phones, false);

				((TextView) item.findViewById(R.id.profile_phone_item_text))
						.setText(phone);

				ImageButton contact_btn = (ImageButton) item
						.findViewById(R.id.profile_phone_item_contact);
				contact_btn.setTag(phone);
				contact_btn.setOnClickListener(this);

				ImageButton text_btn = (ImageButton) item
						.findViewById(R.id.profile_phone_item_texting);
				text_btn.setTag(phone);
				text_btn.setOnClickListener(this);

				phones.addView(item);
			}
		} else {
			// If there are no phone numbers associated with this contact, don't
			// show the label
			((TextView) findViewById(R.id.profile_phones_label))
					.setVisibility(View.GONE);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.profile_email_item_image:
			String email = (String) v.getTag();
			Toast.makeText(ContactViewActivity.this,
					"E-mailing address " + email, Toast.LENGTH_SHORT).show();
			break;
		case R.id.profile_phone_item_contact:
			String phone = (String) v.getTag();
			Toast.makeText(ContactViewActivity.this, "Calling phone " + phone,
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.profile_phone_item_texting:
			String txt = (String) v.getTag();
			Toast.makeText(ContactViewActivity.this, "Texting phone " + txt,
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.toolbar_left_button:
			finish();
			break;
		case R.id.toolbar_right_button:
			Intent myIntent = new Intent(getBaseContext(),
					ContactEditActivity.class);
			myIntent.putExtra("ContactID", _contact.getContactId());
			startActivity(myIntent);
			break;
		default:
			Toast.makeText(ContactViewActivity.this,
					"Unknown clickable item ID", Toast.LENGTH_SHORT).show();
			break;

		}
	}
}