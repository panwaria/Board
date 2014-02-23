package com.example.board;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DrawingActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawing);
		
		/** [Hint]: Get the intent from MainActivity and see if the 
		    user wants to create a new board or open an existing board. **/
		
		FragmentTwo fragmentTwo = (FragmentTwo) getFragmentManager().findFragmentById(R.id.fragment_two);
		
		Intent i = getIntent();
		String drawingName = i.getStringExtra("drawing_name");
		if(drawingName == null || drawingName.equals(""))
			fragmentTwo.createNewBoard();
		else
			fragmentTwo.openExistingBoard(drawingName);
			
	}

	/** [Hint]: You need to save drawings as well. So, implement click 
	    callback for 'Save' button. **/
	
	public void onButtonClick(View v)
	{
		switch(v.getId())
		{
		case R.id.btn_two_save:
			
			FragmentTwo fragmentTwo = (FragmentTwo) getFragmentManager().findFragmentById(R.id.fragment_two);
			fragmentTwo.saveDrawing();
			
			break;
			
		default:
		}
	}
}
