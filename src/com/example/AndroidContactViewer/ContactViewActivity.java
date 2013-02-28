package com.example.AndroidContactViewer;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

import com.example.AndroidContactViewer.datastore.ContactDataSource;

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

		toolbar.hideLeftButton();



        setupEmailsAndPhones();


	}

    @Override
    protected void onResume() {
        super.onResume();

        ContactDataSource datasource = new ContactDataSource(this);
        datasource.open();
        _contact = datasource.get(_contact.getId());
        datasource.close();

        setupEmailsAndPhones();
    }

    public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.profile_email_item_image:
			String email = (String) v.getTag();
			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("message/rfc822");
			intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
			startActivity(Intent.createChooser(intent, "Send Email"));
			break;
		case R.id.profile_phone_item_contact:
			String phone = (String) v.getTag();
			Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+phone));
            startActivity(callIntent);
			break;
		case R.id.profile_phone_item_texting:
			String txt = (String) v.getTag();
			intent = new Intent(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setType("vnd.android-dir/mms-sms");
			intent.putExtra("address", txt);
			startActivity(intent);
			break;
		case R.id.toolbar_left_button:
			finish();
			break;
		case R.id.toolbar_right_button:
			Intent myIntent = new Intent(getBaseContext(),
					ContactEditActivity.class);
			myIntent.putExtra("ContactID", _contact.getId());
			startActivity(myIntent);
			break;
		default:
			Toast.makeText(ContactViewActivity.this,
					"Unknown clickable item ID", Toast.LENGTH_SHORT).show();
			break;

		}
	}

    private void setupEmailsAndPhones() {

        //Check if we have gravatar on disk
        String filename = _contact.getLocalGravatarPath();
        try
        {
            File imgFile = getFileStreamPath(filename);
            if(imgFile.exists())
            {
                 ImageView iv = (ImageView) findViewById(R.id.profile_image);
                Bitmap gravatar = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                iv.setImageBitmap(gravatar);
            }
        }
        catch(Exception e)
        {
            Log.e("gravatar", e.getMessage());
        }


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
            emails.removeAllViews();

            for (String email : emailList) {
                LayoutInflater inflater = getLayoutInflater();
                View item = inflater.inflate(R.layout.contact_email_view_item,
                        emails, false);

                ((TextView) item.findViewById(R.id.profile_email_item_text))
                        .setText(email);

                ImageButton email_btn = (ImageButton) item
                        .findViewById(R.id.profile_email_item_image);
                if (email.equals(_contact.getDefaultEmail())) {
                    email_btn.setImageResource(R.drawable.email_selected_transparent);
                }
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
            phones.removeAllViews();

            for (String phone : phoneList) {
                LayoutInflater inflater = getLayoutInflater();
                View item = inflater.inflate(R.layout.contact_phone_view_item,
                        phones, false);

                ((TextView) item.findViewById(R.id.profile_phone_item_text))
                        .setText(phone);

                ImageButton contact_btn = (ImageButton) item
                        .findViewById(R.id.profile_phone_item_contact);
                if (phone.equals(_contact.getDefaultContactPhone())) {
                    contact_btn.setImageResource(R.drawable.phone_selected_transparent);
                }
                contact_btn.setTag(phone);
                contact_btn.setOnClickListener(this);

                ImageButton text_btn = (ImageButton) item
                        .findViewById(R.id.profile_phone_item_texting);
                if (phone.equals(_contact.getDefaultTextPhone())) {
                    text_btn.setImageResource(R.drawable.texting_selected_transparent);
                }
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
}