package com.example.board;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

/**
 * Class representing a View on which one can draw like a paint board.
 */
public class DrawingView extends View 
{
	private Bitmap mBitmap;	// To hold the pixels
	private Canvas mCanvas;	// To host 'draw' calls
	private Path mPath;		// A drawing primitive used in this case
	private Paint mPaint; 	// To define colors and styles of drawing
	private ArrayList<Float> mPartsDrawingList;				// To store individual lines
	public ArrayList<ArrayList<Float>> mOverallDrawingList;	// To store overall drawing 
	private float mX, mY;	// Current location
	private static final float TOUCH_TOLERANCE = 0;

	public DrawingView(Context c) 
	{
		super(c);
		
		// Set Drawing Paint Attributes
		setPaint();
		
		mBitmap = Bitmap.createBitmap(400, 580, Bitmap.Config.ARGB_8888); // 'ARGB_8888' => Each pixel is stored on 4 bytes.
		mCanvas = new Canvas(mBitmap);
		mPath = new Path();

		mOverallDrawingList = new ArrayList<ArrayList<Float>>();		
	}
	
	/**
	 * Method to set Paint attributes
	 */
	private void setPaint() 
	{
		mPaint = new Paint();
		
		// Setting different properties of Paint object. Feel free to play with these.
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFFFFFF); // White
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(8);
	}
	
	/**
	 * Method to set overall drawing list to a given list.
	 * @param drawingList	Given drawing list
	 */
	public void setOverallDrawingList(ArrayList<ArrayList<Float>> drawingList)
	{
		mOverallDrawingList = drawingList;
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		canvas.drawBitmap(mBitmap, 0, 0, null);
		canvas.drawPath(mPath, mPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) 
		{
		case MotionEvent.ACTION_DOWN:

			touch_start(x, y);
			invalidate();
			break;
			
		case MotionEvent.ACTION_MOVE:
			
			touch_move(x, y);
			invalidate();
			break;
			
		case MotionEvent.ACTION_UP:
		
			touch_up();
			invalidate();
			break;
		}
		return true;
	}
	
	/**
	 * Callback for the case when pressed gesture has started. 
	 * 
	 * @param x		Inital Starting loc X
	 * @param y		Inital Starting loc Y
	 */
	private void touch_start(float x, float y)
	{		
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;

		mPartsDrawingList = new ArrayList<Float>();
		mPartsDrawingList.add(mX);
		mPartsDrawingList.add(mY);
	}

	/**
	 * Callback for the case when a change happens during a press gesture
	 * 
	 * @param x		Most recent point X
	 * @param y		Most recent point Y
	 */
	private void touch_move(float x, float y) 
	{
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}

		mPartsDrawingList.add(mX);
		mPartsDrawingList.add(mY);
	}

	/**
	 * Callback for the case when pressed gesture is finished.
	 */
	private void touch_up() 
	{
		mPath.lineTo(mX, mY);
		
		// Commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		
		// Reset path so we don't double draw
		mPath.reset();

		mOverallDrawingList.add(mPartsDrawingList);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		redrawPath(mCanvas);
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility)
	{
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == View.VISIBLE)
		{
			if (mBitmap != null && mCanvas != null)
			{
				redrawPath(mCanvas);
				invalidate();
			}
		}
	}

	/*
	 * Helper method to redraw the entire path again on Canvas.
	 */
	private void redrawPath(Canvas canvas)
	{
		int numLines = mOverallDrawingList.size();
		for (int i = 0; i < numLines; i++)
		{
			// Un-flatten the drawing points
			ArrayList<Float> partDrawingList = mOverallDrawingList.get(i);
			touch_start(partDrawingList.get(0), partDrawingList.get(1));

			int partDrawingListSize = partDrawingList.size();

			for (int j = 2; j < partDrawingListSize; j += 2)
			{
				// Simulate the move gestures
				touch_move(partDrawingList.get(j), partDrawingList.get(j + 1));
			}

			mPath.lineTo(mX, mY);

			// Commit the path to our offscreen
			canvas.drawPath(mPath, mPaint);

			// Reset path so we don't double draw
			mPath.reset();
		}
	}

}

