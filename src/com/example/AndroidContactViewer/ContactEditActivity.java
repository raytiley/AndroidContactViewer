package com.example.AndroidContactViewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
    	Resources res;
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_edit);
        
        res = getResources();
        ToolbarConfig toolbar = new ToolbarConfig(this, res.getString(R.string.edit_contact));

        int contactID = (Integer)getIntent().getExtras().get("ContactID");
        if(contactID == 0) {
            toolbar.getToolbarTitleView().setText(res.getString(R.string.new_contact));
            //Lets create a brand spaking new contact.
            _contact = new Contact(0, "");
        } else {
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
        }

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
        LinearLayout emails = (LinearLayout)findViewById(R.id.contact_edit_email_list);
        if (emailList.size() != 0) {
            // Can't use a ListView for e-mails or phone lists since we want the entire
            // profile screen to scroll rather than two scroll areas for the e-mails and
            // phone lists.


            for (String email : emailList) {
                LayoutInflater inflater = getLayoutInflater();
                View item = inflater.inflate(R.layout.contact_email_edit_item, emails, false);

                ((EditText) item.findViewById(R.id.contact_edit_email_edit_text)).setText(email);

                ImageButton email_btn = (ImageButton)item.findViewById(R.id.contact_edit_email_action);
                email_btn.setTag(item);
                email_btn.setOnClickListener(this);

                ImageButton remove_btn = (ImageButton)item.findViewById(R.id.contact_edit_email_delete);
                remove_btn.setTag(item);
                remove_btn.setOnClickListener(this);

                emails.addView(item);
            }
        }

        // Add phones to view

        List<String> phoneList = _contact.getPhoneNumbers();
        LinearLayout phones = (LinearLayout)findViewById(R.id.contact_edit_phone_list);
        if (phoneList.size() != 0) {

            for (String phone : phoneList) {
                LayoutInflater inflater = getLayoutInflater();
                View item = inflater.inflate(R.layout.contact_phone_edit_item, phones, false);

                ((EditText) item.findViewById(R.id.contact_edit_phone_edit_text)).setText(phone);

                ImageButton contact_btn = (ImageButton)item.findViewById(R.id.contact_edit_call_action);
                contact_btn.setTag(item);
                contact_btn.setOnClickListener(this);

                ImageButton text_btn = (ImageButton)item.findViewById(R.id.contact_edit_txt_action);
                text_btn.setTag(item);
                text_btn.setOnClickListener(this);

                ImageButton remove_btn = (ImageButton)item.findViewById(R.id.contact_edit_phone_delete);
                remove_btn.setTag(item);
                remove_btn.setOnClickListener(this);

                phones.addView(item);
            }
        }

        // Setup Add Buttons
        ImageButton phone_add = (ImageButton)findViewById(R.id.contact_edit_phone_add);
        phone_add.setOnClickListener(this);

        ImageButton emails_add = (ImageButton)findViewById(R.id.contact_edit_email_add);
        emails_add.setOnClickListener(this);
    }

    public void onClick(View v) {
        View view = (View)v.getTag();
        String text = null;
        switch(v.getId()) {
            case R.id.contact_edit_txt_action:
            case R.id.contact_edit_call_action:
            case R.id.contact_edit_phone_delete:
                text = ((EditText)view.findViewById(R.id.contact_edit_phone_edit_text)).getText().toString();
                break;
            case R.id.contact_edit_email_action:
            case R.id.contact_edit_email_delete:
                text = ((EditText)view.findViewById(R.id.contact_edit_email_edit_text)).getText().toString();
                break;
            default:
                text = "Unknown";
        }


        switch(v.getId()) {
            case R.id.contact_edit_txt_action:
                Toast.makeText(
                        ContactEditActivity.this,
                        "Set default for txt messages to: " + text,
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact_edit_call_action:
                Toast.makeText(
                        ContactEditActivity.this,
                        "Set default for calling to: " + text,
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.contact_edit_email_action:
                Toast.makeText(
                        ContactEditActivity.this,
                        "Set default for emailing to: " + text,
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
            case R.id.contact_edit_phone_delete:
                Toast.makeText(
                        ContactEditActivity.this,
                        "Remove phone: " + text,
                        Toast.LENGTH_SHORT).show();
                ((LinearLayout)findViewById(R.id.contact_edit_phone_list)).removeView(view);
                break;
            case R.id.contact_edit_email_delete:
                Toast.makeText(
                        ContactEditActivity.this,
                        "Remove email: " + text,
                        Toast.LENGTH_SHORT).show();
                ((LinearLayout)findViewById(R.id.contact_edit_email_list)).removeView(view);
                break;
            case R.id.contact_edit_phone_add:
                addViewForPhone();
                break;
            case R.id.contact_edit_email_add:
                addViewForEmail();
                break;
            default:
                Toast.makeText(
                        ContactEditActivity.this,
                        "Unknown clickable item ID",
                        Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void addViewForPhone() {
        LinearLayout phones = (LinearLayout)findViewById(R.id.contact_edit_phone_list);

        LayoutInflater inflater = getLayoutInflater();
        View item = inflater.inflate(R.layout.contact_phone_edit_item, phones, false);

        ImageButton contact_btn = (ImageButton)item.findViewById(R.id.contact_edit_call_action);
        contact_btn.setTag(item);
        contact_btn.setOnClickListener(this);

        ImageButton text_btn = (ImageButton)item.findViewById(R.id.contact_edit_txt_action);
        text_btn.setTag(item);
        text_btn.setOnClickListener(this);

        ImageButton remove_btn = (ImageButton)item.findViewById(R.id.contact_edit_phone_delete);
        remove_btn.setTag(item);
        remove_btn.setOnClickListener(this);

        phones.addView(item);
    }

    private void addViewForEmail() {
        LinearLayout emails = (LinearLayout)findViewById(R.id.contact_edit_email_list);

        LayoutInflater inflater = getLayoutInflater();
        View item = inflater.inflate(R.layout.contact_email_edit_item, emails, false);

        ImageButton contact_btn = (ImageButton)item.findViewById(R.id.contact_edit_email_action);
        contact_btn.setTag(item);
        contact_btn.setOnClickListener(this);

        ImageButton remove_btn = (ImageButton)item.findViewById(R.id.contact_edit_email_delete);
        remove_btn.setTag(item);
        remove_btn.setOnClickListener(this);

        emails.addView(item);
    }
}