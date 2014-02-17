package com.example.board;

import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		View openExistingButtonView = findViewById(R.id.btn_one_open_existing);
		registerForContextMenu(openExistingButtonView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onButtonClick(View v)
	{
		FragmentTwo fragmentTwo = (FragmentTwo) getFragmentManager().findFragmentById(R.id.fragment_two);
		
		switch(v.getId())
		{
		case R.id.btn_one_create_new:
			
			if (fragmentTwo == null)	// PHONE!
			{
				// Need to launch another activity
				Intent i = new Intent(this, DrawingActivity.class);
				/** [Hint]: PAss an extra parameter to tell DrawingActivity
				    that you want to create a new board this time. **/
				startActivity(i);
			}
			else	// TABLET!
			{
				// Let's create a new board.
				fragmentTwo.createNewBoard();
				Toast.makeText(this, "New board created.", Toast.LENGTH_SHORT).show();
			}
			
			break;
			
		case R.id.btn_one_open_existing:
			
			openContextMenu(findViewById(R.id.btn_one_open_existing));
			
			break;
			
		case R.id.btn_two_save:
			
			fragmentTwo.saveDrawing();
			
			break;
			
		default:
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		// Setting title for ContextMenu
		menu.setHeaderTitle(getResources().getString(R.string.one_select_board));
		
		// Get all the drawing names.
		SharedPreferences preferences = getSharedPreferences(FragmentTwo.PREFS, Context.MODE_PRIVATE);
		Map<String, ?> prefs = preferences.getAll();

		// Iterating through all the items in SharedPreferences
		for(Map.Entry<String, ?> entry : prefs.entrySet()) 
		{
			// Extracting the key, which is actually a drawing name.
		    String key = entry.getKey().toString();
		    
		    // Add them to menus.
		    menu.add(key);
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		// Get the drawing name
		String drawingName = item.getTitle().toString();
		 
		// Check again, if this is a PHONE or a TABLET.
		FragmentTwo fragmentTwo = (FragmentTwo) getFragmentManager().findFragmentById(R.id.fragment_two);
		if (fragmentTwo == null)	// PHONE!
		{
			/** [Hint] Need to launch DrawingActivity and pass on extra parameters
			    to tell DrawingActivity to execute openExistingBoard() method. **/
			Toast.makeText(this, "Will implement later.", Toast.LENGTH_SHORT).show();
		}
		else						// TABLET!
		{
			// FragmentTwo is in the same Activity. Update its UI to show the corresponding drawing.
			fragmentTwo.openExistingBoard(drawingName);
		}
		
        return super.onContextItemSelected(item);
	}

}
