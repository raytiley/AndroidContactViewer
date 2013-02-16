package com.example.AndroidContactViewer;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactListActivity extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		ToolbarConfig toolbar = new ToolbarConfig(this, "Contacts");

		// setup the about button
		Button button = toolbar.getToolbarRightButton();
		button.setText("About");
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Toast.makeText(
						ContactListActivity.this,
						"This is a sample application made for SENG 5199-1 in the MSSE program.",
						Toast.LENGTH_LONG).show();
			}
		});

		// make some contacts

		// initialize the list view
		setListAdapter(new ContactAdapter(this, R.layout.list_item, ContactRepository.getRepository().getContacts()));
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

		// handle the item click events
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/*
				// When clicked, show a toast with the TextView text
				Toast.makeText(
						getApplicationContext(),
						"Clicked: "
								+ ((ContactAdapter) getListAdapter()).getItem(
										position).getName(), Toast.LENGTH_SHORT)
						.show();
				*/
				Intent myIntent = new Intent(getBaseContext(), ContactProfile.class);
				myIntent.putExtra("ContactID", ((ContactAdapter)getListAdapter()).getItem(position).getContactId());
				startActivity(myIntent);
			}
		});

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
			View item = inflater.inflate(R.layout.list_item, parent, false);

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
