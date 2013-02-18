package com.example.AndroidContactViewer;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/** Use this class to configure the toolbar for an activity.
 *
 */
public class ToolbarConfig {

	private Activity _activity;
	
	public ToolbarConfig(Activity activity, String title) {
		_activity = activity;
        if (hasToolbar()) {
        	getToolbarTitleView().setText(title);
        } else {
        	throw new RuntimeException("Trying to initialize the toolbar in a layout that doesn't have one!");
        }
	}
    
    /** Returns true if this activity's content view has a toolabr.
     */
    public boolean hasToolbar() {
    	return _activity.findViewById(R.id.toolbar) != null;
    }
	
    /** Gets a reference to the right button on the toolbar.
     */
    public Button getToolbarRightButton() {
    	return (Button)_activity.findViewById(R.id.toolbar_right_button);
    }
	
    /** Gets a reference to the left button on the toolbar.
     */
    public Button getToolbarLeftButton() {
    	return (Button)_activity.findViewById(R.id.toolbar_left_button);
    }
	
    /** Gets a reference to the title TextView on the toolbar.
     */
    public TextView getToolbarTitleView() {
    	return (TextView)_activity.findViewById(R.id.toolbar_title);
    }
    
    /**
     * Hides the left toolbar button
     */
    public void hideLeftButton() {
    	getToolbarLeftButton().setVisibility(View.GONE);
    }
    
    /**
     * Hides the right toolbar button
     */
    public void hideRightButton() {
    	getToolbarRightButton().setVisibility(View.GONE);
    }
	
}
