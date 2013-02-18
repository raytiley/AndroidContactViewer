package com.example.AndroidContactViewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.AndroidContactViewer.datastore.ContactDataSource;

/**
 * Created with IntelliJ IDEA.
 * User: raytiley
 * Date: 2/17/13
 * Time: 6:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class ContactEditActivity extends Activity {

    private Contact _contact;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_edit);

        int contactID = (Integer)getIntent().getExtras().get("ContactID");

        ToolbarConfig toolbar = new ToolbarConfig(this, "Edit Contact");
        ContactDataSource datasource = new ContactDataSource(this);
        datasource.open();
        _contact = datasource.get(contactID);
        datasource.close();

        // setup the "Edit" button
        Button button = toolbar.getToolbarLeftButton();
        button.setText("Back");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        // setup the save button
        button = toolbar.getToolbarRightButton();
        button.setText("Save");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //TODO Actually save the contact
                Toast.makeText(getBaseContext(), "Contact Saved", 5);
                finish();
            }
        });

        ((EditText)findViewById(R.id.edit_contact_name)).setText(_contact.getName());
        ((EditText)findViewById(R.id.edit_contact_title)).setText(_contact.getTitle());
    }
}