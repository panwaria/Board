package com.example.board;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FragmentTwo extends Fragment
{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_two, container, false);
	}
	
	private DrawingView mDrawingView = null;
	
	/**
	 * Method to add a new DrawingView to the fragment. 
	 */
	public void createNewBoard()
	{
		// Removing any existing DrawingView
		cleanUpExistingView();

		// Making the drawing layout visible
		Activity parentActivity = getActivity();
		LinearLayout drawingLayout = (LinearLayout) parentActivity.findViewById(R.id.layout_drawing);
		parentActivity.findViewById(R.id.textview_welcome).setVisibility(View.GONE);
		drawingLayout.setVisibility(View.VISIBLE);
		
		// Adding DrawingView to the DrawingLayout
		mDrawingView = new DrawingView(parentActivity);
		mDrawingView.setBackgroundResource(R.drawable.chalkboard);
		drawingLayout.addView(mDrawingView);
	}
	
	/**
	 * Removing any existing DrawingView
	 */
	private void cleanUpExistingView()
	{
		Activity parentActivity = getActivity();
		
		// Resetting the drawing title's text
		((EditText)parentActivity.findViewById(R.id.edittext_drawing)).setText("");
		
		if (mDrawingView != null) 
		{
			// Remove DrawingView from DrawingLayout
			LinearLayout drawingLayout = (LinearLayout) parentActivity.findViewById(R.id.layout_drawing);
			drawingLayout.removeView(mDrawingView);
			
			// Cleaning up the background as well
			Drawable backgroundDrawable = mDrawingView.getBackground();
			if(backgroundDrawable != null)
			{
				backgroundDrawable.setCallback(null);
				mDrawingView.setBackgroundResource(0);
				mDrawingView.destroyDrawingCache();
			}

			mDrawingView = null;
		}
	}
	
	public static final String PREFS = "com.example.board.drawings";
	
	/**
	 * Method to save the drawing in SharedPreferences.
	 */
	public void saveDrawing()
	{
		Activity parentActivity = getActivity();
		
		// Get drawing title from the EditText
		String drawingName =  ((EditText)parentActivity.findViewById(R.id.edittext_drawing)).getText().toString();

		// Prompt user to give a drawing name if he/she has not already entered
		if(drawingName == null || drawingName.isEmpty())
		{
			Toast.makeText(parentActivity, "Please enter a name for your drawing", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mDrawingView == null)
		{
			Toast.makeText(parentActivity, "No drawing found!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		// This is where our drawing is stored
		ArrayList<ArrayList<Float>> overallDrawingList = mDrawingView.mOverallDrawingList;
		String flattenedDrawingListString = "";
		
		// Get number of lines
		int numParts = overallDrawingList.size();

		// Store the drawing as a FLAT STRING in SharedPreferences
		SharedPreferences preferences = parentActivity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();

		// Go through every line of the drawing
		for (int i = 0; i < numParts; i++) 
		{
			ArrayList<Float> partDrawingList = overallDrawingList.get(i);

			int numPoints = partDrawingList.size();
			for (int j = 0; j < numPoints;) 
			{
				flattenedDrawingListString += partDrawingList.get(j++);
				
				if (j < numPoints)
					flattenedDrawingListString += ",";
			}
			
			// Separate strings representing a line by a tab space ('\t')
			flattenedDrawingListString += "\t";
		}
		
		// Store the generated string and commit the changes.
		editor.putString(drawingName, flattenedDrawingListString);
		editor.commit();
		
		Toast.makeText(parentActivity, "'" + drawingName + "' saved successfully.", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Method to open an existing drawing board.
	 * 
	 * @param drawingName	Drawing Title given by user earlier
	 */
	public void openExistingBoard(String drawingName)
	{
		cleanUpExistingView();

		ArrayList<ArrayList<Float>> alreadyStoredDrawingList = new ArrayList<ArrayList<Float>>();
		alreadyStoredDrawingList = readAlreadyStoredDrawingList(drawingName);

		// If there is at least one point in the drawing
		if (alreadyStoredDrawingList != null && !alreadyStoredDrawingList.isEmpty())
		{
			Activity parentActivity = getActivity();
			LinearLayout drawingLayout = (LinearLayout) parentActivity
					.findViewById(R.id.layout_drawing);
			
			if (mDrawingView == null)
			{
				mDrawingView = new DrawingView(parentActivity);
				mDrawingView.setBackgroundResource(R.drawable.chalkboard);
				drawingLayout.addView(mDrawingView);
			}

			if (mDrawingView != null)
			{
				mDrawingView.setOverallDrawingList(alreadyStoredDrawingList);
				mDrawingView.invalidate();
			}

			parentActivity.findViewById(R.id.textview_welcome).setVisibility(View.GONE);
			drawingLayout.setVisibility(View.VISIBLE);
			((EditText) parentActivity.findViewById(R.id.edittext_drawing)).setText(drawingName);
		}
	}

	/**
	 * Helper method to extract already store drawing list from
	 * SharedPreferences in a specific format.
	 */
	private ArrayList<ArrayList<Float>> readAlreadyStoredDrawingList(String drawingName)
	{
		ArrayList<ArrayList<Float>> drawingList = new ArrayList<ArrayList<Float>>();

		Activity parentActivity = getActivity();
		if (parentActivity != null)
		{
			SharedPreferences preferences = parentActivity.getSharedPreferences(FragmentTwo.PREFS,
					Context.MODE_PRIVATE);
			String flattenedDrawingList = preferences.getString(drawingName, null);

			if (flattenedDrawingList != null)
			{
				// Check if the list is empty
				if (flattenedDrawingList.isEmpty())
					return drawingList;

				String drawingLines[] = flattenedDrawingList.split("\t");

				// Unfolding the flat string
				for (int i = 0; i < drawingLines.length; i++)
				{
					String linePoints[] = drawingLines[i].split(",");

					ArrayList<Float> linesList = new ArrayList<Float>();

					for (int j = 0; j < linePoints.length; j++)
						linesList.add(Float.parseFloat(linePoints[j]));

					drawingList.add(linesList);
				}
			}
		}

		return drawingList;
	}

}
