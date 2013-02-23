package com.example.AndroidContactViewer;

import java.util.List;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.AndroidContactViewer.datastore.ContactDataSource;

/**
 * Created with IntelliJ IDEA. User: raytiley Date: 2/17/13 Time: 6:12 PM To
 * change this template use File | Settings | File Templates.
 */
public class ContactEditActivity extends Activity implements OnClickListener {

	private Contact _contact;
	private ImageButton _defaultPhoneButton;
	private ImageButton _defaultTextButton;
	private ImageButton _defaultEmailButton;

	public void onCreate(Bundle savedInstanceState) {
		Resources res;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_edit);

		res = getResources();
		ToolbarConfig toolbar = new ToolbarConfig(this,
				res.getString(R.string.edit_contact));

		int contactID = (Integer) getIntent().getExtras().get("ContactID");
		if (contactID == 0) {
			toolbar.getToolbarTitleView().setText(
					res.getString(R.string.new_contact));
			// Lets create a brand spaking new contact.
			_contact = new Contact(0, "");
		} else {
			ContactDataSource datasource = new ContactDataSource(this);
			datasource.open();
			_contact = datasource.get(contactID);
			datasource.close();
		}

		// setup the "Edit" button
		Button button = toolbar.getToolbarRightButton();
		button.setText("Save");
		button.setOnClickListener(this);

		button = toolbar.getToolbarLeftButton();
		button.setText("Back");
		button.setOnClickListener(this);

		((EditText) findViewById(R.id.edit_contact_name)).setText(_contact
				.getName());
		((EditText) findViewById(R.id.edit_contact_title)).setText(_contact
				.getTitle());

		List<String> emailList = _contact.getEmails();
		LinearLayout emails = (LinearLayout) findViewById(R.id.contact_edit_email_list);
		if (emailList.size() != 0) {
			// Can't use a ListView for e-mails or phone lists since we want the
			// entire
			// profile screen to scroll rather than two scroll areas for the
			// e-mails and
			// phone lists.

			for (String email : emailList) {
				LayoutInflater inflater = getLayoutInflater();
				View item = inflater.inflate(R.layout.contact_email_edit_item,
						emails, false);

				((EditText) item
						.findViewById(R.id.contact_edit_email_edit_text))
						.setText(email);

				ImageButton email_btn = (ImageButton) item
						.findViewById(R.id.contact_edit_email_action);
				if (email.equals(_contact.getDefaultEmail())) {
					email_btn.setImageResource(R.drawable.email_selected);
					_defaultEmailButton = email_btn;
				}
				email_btn.setTag(item);
				email_btn.setOnClickListener(this);

				ImageButton remove_btn = (ImageButton) item
						.findViewById(R.id.contact_edit_email_delete);
				remove_btn.setTag(item);
				remove_btn.setOnClickListener(this);

				emails.addView(item);
			}
		}

		// Add phones to view

		List<String> phoneList = _contact.getPhoneNumbers();
		LinearLayout phones = (LinearLayout) findViewById(R.id.contact_edit_phone_list);
		if (phoneList.size() != 0) {

			for (String phone : phoneList) {
				LayoutInflater inflater = getLayoutInflater();
				View item = inflater.inflate(R.layout.contact_phone_edit_item,
						phones, false);

				((EditText) item
						.findViewById(R.id.contact_edit_phone_edit_text))
						.setText(phone);

				ImageButton contact_btn = (ImageButton) item
						.findViewById(R.id.contact_edit_call_action);
				if (phone.equals(_contact.getDefaultContactPhone())) {
					contact_btn.setImageResource(R.drawable.phone_selected);
					_defaultPhoneButton = contact_btn;
				}
				contact_btn.setTag(item);
				contact_btn.setOnClickListener(this);

				ImageButton text_btn = (ImageButton) item
						.findViewById(R.id.contact_edit_txt_action);
				if (phone.equals(_contact.getDefaultTextPhone())) {
					text_btn.setImageResource(R.drawable.texting_selected);
					_defaultTextButton = text_btn;
				}
				text_btn.setTag(item);
				text_btn.setOnClickListener(this);

				ImageButton remove_btn = (ImageButton) item
						.findViewById(R.id.contact_edit_phone_delete);
				remove_btn.setTag(item);
				remove_btn.setOnClickListener(this);

				phones.addView(item);
			}
		}

		// Setup Add Buttons
		ImageButton phone_add = (ImageButton) findViewById(R.id.contact_edit_phone_add);
		phone_add.setOnClickListener(this);

		ImageButton emails_add = (ImageButton) findViewById(R.id.contact_edit_email_add);
		emails_add.setOnClickListener(this);
	}

	public void onClick(View v) {
		View view = (View) v.getTag();
		String text = null;
		switch (v.getId()) {
		case R.id.contact_edit_txt_action:
		case R.id.contact_edit_call_action:
		case R.id.contact_edit_phone_delete:
			text = ((EditText) view
					.findViewById(R.id.contact_edit_phone_edit_text)).getText()
					.toString();
			break;
		case R.id.contact_edit_email_action:
		case R.id.contact_edit_email_delete:
			text = ((EditText) view
					.findViewById(R.id.contact_edit_email_edit_text)).getText()
					.toString();
			break;
		default:
			text = "Unknown";
		}

		switch (v.getId()) {
		case R.id.contact_edit_txt_action:
			// TODO : set default text number on contact
			
			if (_defaultTextButton != null) {
				_defaultTextButton.setImageResource(R.drawable.texting);
			}
			_defaultTextButton = (ImageButton)view.findViewById(R.id.contact_edit_txt_action);
			_defaultTextButton.setImageResource(R.drawable.texting_selected);
			break;
		case R.id.contact_edit_call_action:
			// TODO : set default call number on contact
			
			if (_defaultPhoneButton != null) {
				_defaultPhoneButton.setImageResource(R.drawable.phone);
			}
			_defaultPhoneButton = (ImageButton)view.findViewById(R.id.contact_edit_call_action);
			_defaultPhoneButton.setImageResource(R.drawable.phone_selected);
			break;
		case R.id.contact_edit_email_action:
			// TODO : set default email on contact
			
			if (_defaultEmailButton != null) {
				_defaultEmailButton.setImageResource(R.drawable.email);
			}
			_defaultEmailButton = (ImageButton)view.findViewById(R.id.contact_edit_email_action);
			_defaultEmailButton.setImageResource(R.drawable.email_selected);
			break;
		case R.id.toolbar_left_button:
			finish();
			break;
		case R.id.toolbar_right_button:
			Toast.makeText(ContactEditActivity.this, "Saving Contact",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.contact_edit_phone_delete:
			Toast.makeText(ContactEditActivity.this, "Remove phone: " + text,
					Toast.LENGTH_SHORT).show();
			((LinearLayout) findViewById(R.id.contact_edit_phone_list))
					.removeView(view);
			break;
		case R.id.contact_edit_email_delete:
			Toast.makeText(ContactEditActivity.this, "Remove email: " + text,
					Toast.LENGTH_SHORT).show();
			((LinearLayout) findViewById(R.id.contact_edit_email_list))
					.removeView(view);
			break;
		case R.id.contact_edit_phone_add:
			addViewForPhone();
			break;
		case R.id.contact_edit_email_add:
			addViewForEmail();
			break;
		default:
			Toast.makeText(ContactEditActivity.this,
					"Unknown clickable item ID", Toast.LENGTH_SHORT).show();
			break;

		}
	}

	private void addViewForPhone() {
		LinearLayout phones = (LinearLayout) findViewById(R.id.contact_edit_phone_list);

		LayoutInflater inflater = getLayoutInflater();
		View item = inflater.inflate(R.layout.contact_phone_edit_item, phones,
				false);

		ImageButton contact_btn = (ImageButton) item
				.findViewById(R.id.contact_edit_call_action);
		contact_btn.setTag(item);
		contact_btn.setOnClickListener(this);

		ImageButton text_btn = (ImageButton) item
				.findViewById(R.id.contact_edit_txt_action);
		text_btn.setTag(item);
		text_btn.setOnClickListener(this);

		ImageButton remove_btn = (ImageButton) item
				.findViewById(R.id.contact_edit_phone_delete);
		remove_btn.setTag(item);
		remove_btn.setOnClickListener(this);

		phones.addView(item);
	}

	private void addViewForEmail() {
		LinearLayout emails = (LinearLayout) findViewById(R.id.contact_edit_email_list);

		LayoutInflater inflater = getLayoutInflater();
		View item = inflater.inflate(R.layout.contact_email_edit_item, emails,
				false);

		ImageButton contact_btn = (ImageButton) item
				.findViewById(R.id.contact_edit_email_action);
		contact_btn.setTag(item);
		contact_btn.setOnClickListener(this);

		ImageButton remove_btn = (ImageButton) item
				.findViewById(R.id.contact_edit_email_delete);
		remove_btn.setTag(item);
		remove_btn.setOnClickListener(this);

		emails.addView(item);
	}
}