package com.example.board;

import android.app.Activity;
import android.os.Bundle;

public class DrawingActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		
		/** [Hint]: Get the intent from MainActivity and see if the 
		    user wants to create a new board or open an existing board. **/
	}

	/** [Hint]: You need to save drawings as well. So, implement click 
	    callback for 'Save' button. **/
}
