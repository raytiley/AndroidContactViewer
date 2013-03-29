package com.example.AndroidContactViewer;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import com.example.AndroidContactViewer.datastore.ContactRepositoryFactory;
import com.example.AndroidContactViewer.datastore.ContactRepositoryInterface;

public class ContactListActivity extends ListActivity implements
		OnClickListener {
	private boolean filtered = false;
	protected ContactAdapter contact_adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		ContactRepositoryInterface datasource = ContactRepositoryFactory.getInstance().getContactRepository(this);
		datasource.open();

        // Check if the user wants to prepopulate some awesomeness
        if(datasource.count() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("You have no contacts, want us to make some for you?")
                    .setTitle("No Contacts")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Make some contacts.
                            createNewContacts();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            //Do nothing
                        }
                    })
                    .create()
                    .show();
        }

		contact_adapter = new ContactAdapter(this, R.layout.contact_list_item,
				datasource.all());
		setListAdapter(contact_adapter);
		datasource.close();

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);

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
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    protected void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
	}

	public void onRestoreInstanceState(Bundle icicle) {
		super.onRestoreInstanceState(icicle);
		refreshList();
	}

	private void refreshList() {
		this.contact_adapter.clear();
		ContactRepositoryInterface datasource = ContactRepositoryFactory.getInstance().getContactRepository(this);
		datasource.open();
		for(Contact c : datasource.all()) {
			this.contact_adapter.add(c);
		}
		datasource.close();
		this.contact_adapter.notifyDataSetChanged();

		EditText search_box = (EditText) findViewById(R.id.search_box);
		contact_adapter.getFilter().filter(search_box.getText().toString());
	}

    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contact_long_hold_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		Contact contact = ((ContactAdapter) getListAdapter())
				.getItem(info.position);
		switch (item.getItemId()) {
            case R.id.profile:
                Intent profileIntent = new Intent(getBaseContext(),
                        ContactViewActivity.class);
                profileIntent.putExtra("ContactID", contact.getId());
                startActivity(profileIntent);
                return true;
            case R.id.edit:
                Intent editIntent = new Intent(getBaseContext(),
                        ContactEditActivity.class);
                editIntent.putExtra("ContactID", contact.getId());
                startActivity(editIntent);
                return true;
            case R.id.delete:
                //TODO Maybe a confermation???
                ContactRepositoryInterface datasource = ContactRepositoryFactory.getInstance().getContactRepository(this);
                this.contact_adapter.remove(contact);
                datasource.open();
                datasource.delete(contact);
                datasource.close();

                refreshList();
                return true;
            default:
                return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
        final int pos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Action")
                .setItems(new String[]{"Call", "Text", "Email"}, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Contact contact = ((ContactAdapter) getListAdapter()).getItem(pos);
                switch(which) {
                    case 0:
                        if (contact.getDefaultContactPhone() != null &&
                                !contact.getDefaultContactPhone().trim().equals("")) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+contact.getDefaultContactPhone()));
                            startActivity(callIntent);
                        }
                        else {
                            // TODO : pop up menu of all phone numbers
                            Toast.makeText(ContactListActivity.this, "No default phone number set", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 1:
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
                            Toast.makeText(ContactListActivity.this, "No default phone number set", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 2:
                        if (contact.getDefaultEmail() != null &&
                                !contact.getDefaultEmail().trim().equals("")) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("message/rfc822");
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{contact.getDefaultEmail()});
                            startActivity(Intent.createChooser(intent, "Send Email"));
                        }
                        else {
                            // TODO : pop up menu of all emails
                            Toast.makeText(ContactListActivity.this, "No default email set", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });

        builder.create().show();
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

    private void createNewContacts() {
        ContactRepositoryInterface datasource = ContactRepositoryFactory.getInstance().getContactRepository(this);
        datasource.open();

        Contact ray = new Contact(0, "Ray Tiley")
                .setTitle("Tightrope Media Systems")
                .addEmail("raytiley@gmail.com")
                .setDefaultEmail("raytiley@gmail.com")
                .addPhoneNumber("207-518-8612")
                .addPhoneNumber("866-866-4118")
                .setDefaultTextPhone("207-518-8612")
                .setDefaultContactPhone("866-866-4118");

        Contact tyler = new Contact(0, "Tyler Smith")
                .setTitle("General Dynamics")
                .addEmail("tylerhesthedude@gmail.com")
                .setDefaultEmail("tylerhesthedude@gmail.com")
                .addPhoneNumber("555-555-1000")
                .setDefaultContactPhone("555-555-1000")
                .setDefaultTextPhone("555-555-1000");


        Contact steveA = new Contact(0, "Steve Atterbury")
                .setTitle("Lockheed")
                .addEmail("unimatrix01@gmail.com")
                .setDefaultEmail("unimatrix01@gmail.com")
                .addPhoneNumber("555-555-2000")
                .setDefaultTextPhone("555-555-2000")
                .setDefaultContactPhone("555-555-2000");

        Contact steveM = new Contact(0, "Steve McAdams")
                .setTitle("Lockheed")
                .addEmail("smcadams86@gmail.com")
                .addPhoneNumber("555-555-3000");

        ray = datasource.add(ray);
        steveA = datasource.add(steveA);
        tyler = datasource.add(tyler);
        steveM = datasource.add(steveM);

        ray.downloadGravatar(this);
        steveA.downloadGravatar(this);
        tyler.downloadGravatar(this);
        steveM.downloadGravatar(this);

        datasource.close();
        
        refreshList();

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

            //Check if we have gravatar on disk
            String filename = Integer.toString(contact.getId()) + "-gravatar.jpg";
            try
            {
                File imgFile = getFileStreamPath(filename);
                if(imgFile.exists())
                {
                    ImageView iv = (ImageView) item.findViewById(R.id.item_profile_image);
                    Bitmap gravatar = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    iv.setImageBitmap(gravatar);
                }
            }
            catch(Exception e)
            {
                Log.e("gravatar", e.getMessage());
            }

			return item;
		}
	}
}