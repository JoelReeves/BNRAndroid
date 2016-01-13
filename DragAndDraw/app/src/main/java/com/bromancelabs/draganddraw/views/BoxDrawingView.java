package com.bromancelabs.draganddraw.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.bromancelabs.draganddraw.models.Box;

import java.util.ArrayList;
import java.util.List;

public class BoxDrawingView extends View {
    private static final String TAG = BoxDrawingView.class.getSimpleName();
    private static final String PARCELABLE_KEY = "parcelable_key";
    private static final String BOX_KEY = "box_key_";

    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;
    private float[] mBoxPointsArray;

    // Used when creating the view in code
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    // Used when inflating the view from XML
    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Paint the boxes a nice semitransparent red (ARGB)
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        // Paint the background off-white
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARCELABLE_KEY, super.onSaveInstanceState());

        int boxCount = 1;

        for(Box box : mBoxen) {
            mBoxPointsArray = new float[]{box.getOrigin().x, box.getOrigin().y, box.getCurrent().x, box.getCurrent().y};
            bundle.putFloatArray(BOX_KEY + boxCount, mBoxPointsArray);
            boxCount ++;
        }

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(PARCELABLE_KEY));

            // Keeping a count of the boxes drawn. Used as part of the bundle key
            int boxCount = 1;

            // Staying in the loop if the bundle contains the key we're looking for
            while (bundle.containsKey(BOX_KEY + boxCount)) {
                // Getting the x and y values from the bundle.
                mBoxPointsArray = bundle.getFloatArray(BOX_KEY + boxCount);

                // Creating the boxes from the saved array and drawing them back onto the screen
                PointF origin;
                PointF current;

                if (mBoxPointsArray != null) {
                    origin = new PointF(mBoxPointsArray[0], mBoxPointsArray[1]);
                    current = new PointF(mBoxPointsArray[2], mBoxPointsArray[3]);

                    Box box = new Box(origin);
                    box.setCurrent(current);

                    mBoxen.add(box);
                    boxCount++;
                }
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        String action = "";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                action = "ACTION_DOWN";
                // Reset drawing state
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "ACTION_MOVE";
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "ACTION_UP";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "ACTION_CANCEL";
                mCurrentBox = null;
                break;
        }

        Log.d(TAG, action + " at x=" + current.x + ", y=" + current.y);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Fill the background
        canvas.drawPaint(mBackgroundPaint);

        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
    }
}
