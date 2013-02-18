package com.example.AndroidContactViewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.AndroidContactViewer.datastore.ContactDataSource;
import android.view.View.OnClickListener;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: raytiley
 * Date: 2/17/13
 * Time: 6:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContactEditActivity extends Activity implements OnClickListener {

    private Contact _contact;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.contact_edit);

        int contactID = (Integer)getIntent().getExtras().get("ContactID");

        ToolbarConfig toolbar = new ToolbarConfig(this, "Edit Contact");
        ContactDataSource datasource = new ContactDataSource(this);
        datasource.open();
        _contact = datasource.get(contactID);

        //Just for shits and grins add a bunch of emails and phones since the DB doesn't seem to be holding them yet
        _contact.addPhoneNumber("732-531-7175");
        _contact.addPhoneNumber("207-775-2900");
        _contact.addPhoneNumber("207-518-8612");
        _contact.addPhoneNumber("732-531-1234");
        _contact.addPhoneNumber("207-775-1234");
        _contact.addPhoneNumber("207-518-1234");

        _contact.addEmail("raytiley@gmail.com");
        _contact.addEmail("ray@ctn5.org");
        _contact.addEmail("ray.tiley@trms.com");
        _contact.addEmail("raytiley@example.com");
        _contact.addEmail("ray@example.com");
        _contact.addEmail("ray.tiley@example.com");

        datasource.close();

        // setup the "Edit" button
        Button button = toolbar.getToolbarRightButton();
        button.setText("Save");
        button.setOnClickListener(this);

        button = toolbar.getToolbarLeftButton();
        button.setText("Back");
        button.setOnClickListener(this);

        ((EditText)findViewById(R.id.edit_contact_name)).setText(_contact.getName());
        ((EditText)findViewById(R.id.edit_contact_title)).setText(_contact.getTitle());

        List<String> emailList = _contact.getEmails();

        if (emailList.size() != 0) {
            // Can't use a ListView for e-mails or phone lists since we want the entire
            // profile screen to scroll rather than two scroll areas for the e-mails and
            // phone lists.

            LinearLayout emails = (LinearLayout)findViewById(R.id.contact_edit_email_list);
            for (String email : emailList) {
                LayoutInflater inflater = getLayoutInflater();
                View item = inflater.inflate(R.layout.contact_email_edit_item, emails, false);

                ((EditText) item.findViewById(R.id.contact_edit_email_edit_text)).setText(email);

                ImageButton email_btn = (ImageButton)item.findViewById(R.id.contact_edit_email_action);
                email_btn.setTag(email);
                email_btn.setOnClickListener(this);

                emails.addView(item);
            }
        } else {
            // If there are no e-mails associated with this contact, don't show the label
            ((TextView)findViewById(R.id.profile_emails_label)).setVisibility(View.GONE);
        }

        // Add phones to view

        List<String> phoneList = _contact.getPhoneNumbers();

        if (phoneList.size() != 0) {
            LinearLayout phones = (LinearLayout)findViewById(R.id.contact_edit_phone_list);
            for (String phone : phoneList) {
                LayoutInflater inflater = getLayoutInflater();
                View item = inflater.inflate(R.layout.contact_phone_edit_item, phones, false);

                ((EditText) item.findViewById(R.id.contact_edit_phone_edit_text)).setText(phone);

                ImageButton contact_btn = (ImageButton)item.findViewById(R.id.contact_edit_call_action);
                contact_btn.setTag(phone);
                contact_btn.setOnClickListener(this);

                ImageButton text_btn = (ImageButton)item.findViewById(R.id.contact_edit_txt_action);
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
            case R.id.contact_edit_txt_action:
                String txtPhone = (String)v.getTag();
                Toast.makeText(
                        ContactEditActivity.this,
                        "Set default for txt messages to: " + txtPhone,
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact_edit_call_action:
                String callPhone = (String)v.getTag();
                Toast.makeText(
                        ContactEditActivity.this,
                        "Set default for calling to: " + callPhone,
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact_edit_email_action:
                String email = (String)v.getTag();
                Toast.makeText(
                        ContactEditActivity.this,
                        "Set default for emailing to: " + email,
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.toolbar_left_button:
                finish();
                break;
            case R.id.toolbar_right_button:
                Toast.makeText(
                        ContactEditActivity.this,
                        "Saving Contact",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(
                        ContactEditActivity.this,
                        "Unknown clickable item ID",
                        Toast.LENGTH_SHORT).show();
                break;

        }
    }
}