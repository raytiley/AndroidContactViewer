package com.example.AndroidContactViewer;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.AndroidContactViewer.datastore.ContactDataSource;

public class ContactListActivity extends ListActivity implements
		OnClickListener {
	private boolean filtered = false;
	private ContactListActivity _activity = null;
	protected ContactAdapter contact_adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_activity = this;

		Resources res = getResources();
		setContentView(R.layout.contact_list);
		ToolbarConfig toolbar = new ToolbarConfig(this,
				res.getString(R.string.contacts));

		// setup the about button
		Button button = toolbar.getToolbarRightButton();
		button.setText(res.getString(R.string.new_contact));
		button.setOnClickListener(this);

		toolbar.hideLeftButton();

		// initialize the list view
		ContactDataSource datasource = new ContactDataSource(this);
		datasource.open();
		contact_adapter = new ContactAdapter(this, R.layout.contact_list_item,
				datasource.all());
		setListAdapter(contact_adapter);
		datasource.close();

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int i, long l) {
				_activity.closeContextMenu();
				return false;
			}
		});

		// setup context menu
		registerForContextMenu(lv);

		// Setup Search
		EditText search_box = (EditText) findViewById(R.id.search_box);
		search_box.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i,
					int i2, int i3) {
				// To change body of implemented methods use File | Settings |
				// File Templates.
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i2,
					int i3) {
				ContactListActivity.this.contact_adapter.getFilter().filter(
						charSequence);
				filtered = true;
			}

			@Override
			public void afterTextChanged(Editable editable) {
				// To change body of implemented methods use File | Settings |
				// File Templates.
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Contact contact = ((ContactAdapter) getListAdapter())
				.getItem(info.position);
		switch (item.getItemId()) {
		case R.id.call:
			if (contact.getDefaultContactPhone() != null &&
			    !contact.getDefaultContactPhone().trim().equals("")) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
	            callIntent.setData(Uri.parse("tel:"+contact.getDefaultContactPhone()));
	            startActivity(callIntent);
			}
            else {
                // TODO : pop up menu of all phone numbers
                Toast.makeText(this, "No default phone number set", 5).show();
            }
			return true;
		case R.id.message:
			if (contact.getDefaultTextPhone() != null &&
		        !contact.getDefaultTextPhone().trim().equals("")) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.setType("vnd.android-dir/mms-sms");
				intent.putExtra("address", contact.getDefaultTextPhone());
				startActivity(intent);
			}
	        else {
                // TODO : pop up menu of all phone numbers
                Toast.makeText(this, "No default phone number set", 5).show();
	        }
			return true;
		case R.id.email:
			if (contact.getDefaultEmail() != null &&
	            !contact.getDefaultEmail().trim().equals("")) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("message/rfc822");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{contact.getDefaultEmail()});
				startActivity(Intent.createChooser(intent, "Send Email"));
			}
	        else {
                // TODO : pop up menu of all emails
                Toast.makeText(this, "No default email set", 5).show();
	        }
			return true;
		case R.id.profile:
			Intent myIntent = new Intent(getBaseContext(),
					ContactViewActivity.class);
			myIntent.putExtra("ContactID", contact.getContactId());
			startActivity(myIntent);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		this.openContextMenu(v);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toolbar_right_button:
			Intent myIntent = new Intent(getBaseContext(),
					ContactEditActivity.class);
			myIntent.putExtra("ContactID", 0);
			startActivity(myIntent);
			break;
		default:
			Toast.makeText(ContactListActivity.this, "Unknown click",
					Toast.LENGTH_SHORT).show();
			break;
		}
	}

	public boolean onSearchRequested() {
		EditText search_box = (EditText) findViewById(R.id.search_box);
		search_box.requestFocus();

		// Return false so that Android doesn't try to run an actual search
		// dialog.
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (filtered) {
			ContactListActivity.this.contact_adapter.getFilter().filter("");
			EditText search_box = (EditText) findViewById(R.id.search_box);
			search_box.setText("");
			filtered = false;
		} else {
			super.onBackPressed();
		}
	}

	/*
	 * We need to provide a custom adapter in order to use a custom list item
	 * view.
	 */
	private class ContactAdapter extends ArrayAdapter<Contact> {

		public ContactAdapter(Context context, int textViewResourceId,
				List<Contact> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View item = inflater.inflate(R.layout.contact_list_item, parent,
					false);

			Contact contact = getItem(position);
			((TextView) item.findViewById(R.id.item_name)).setText(contact
					.getName());
			((TextView) item.findViewById(R.id.item_title)).setText(contact
					.getTitle());
			((TextView) item.findViewById(R.id.item_phone)).setText(contact
					.getDefaultContactPhone());

			return item;
		}
	}
}