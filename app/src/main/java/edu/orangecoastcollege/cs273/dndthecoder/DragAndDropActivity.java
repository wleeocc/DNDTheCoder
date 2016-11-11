package edu.orangecoastcollege.cs273.dndthecoder;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DragAndDropActivity extends Activity {

    private Context context;
    private GridLayout dragLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);
        context = this;

        dragLayout = (GridLayout) findViewById(R.id.activity_drag_and_drop);


        //TODO:  Add the CoderDragListener to every LinearLayout in activity_drag_and_drop
        // Loop through all 25 Linear Layouts which are children of the GridLayout
        int lLayoutChildCount = dragLayout.getChildCount();
        for (int i = 0; i < lLayoutChildCount; i++)
        {
            View childView = dragLayout.getChildAt(i);
            if (childView instanceof LinearLayout)
            {
                LinearLayout childLinearLayout = (LinearLayout) childView;
                childLinearLayout.setOnDragListener(new CoderDragListener());
                int ivChildCount = childLinearLayout.getChildCount();
                for (int j = 0; j < ivChildCount; j++)
                {
                    View grandchildView = childLinearLayout.getChildAt(j);
                    if (grandchildView instanceof ImageView)
                    {
                        ImageView childImageView = (ImageView) grandchildView;
                        //TODO:  Add the CoderTouchListener to every ImageView within each LinearLayout
                        childImageView.setOnTouchListener(new CoderTouchListener());
                    }
                }
            }
        }
    }


    /**
     * CoderTouchListener implements an OnTouchListener, specifically for ImageViews.
     * It employs a DragShadowBuilder, such that whenever an ImageView is touched,
     * it will be elevated, shadowed and have a visual effect that
     * indicates it's ready to be dragged (and dropped).
     */
    class CoderTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                // As of Android Nougat (7), startDrag is deprecated, use startDragAndDrop
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    view.startDragAndDrop(data, shadowBuilder, view, 0);
                else
                    view.startDrag(data, shadowBuilder, view, 0);

                view.setVisibility(View.INVISIBLE);
                return true;
            }
            return false;
        }
    }

    /**
     * CoderDragListener implements an OnDragListener, specifically for LinearLayouts.
     * It alternates layouts between a normal shape (gradient square with white background)
     * to a target shape (gradient square with red background).
     *
     * The GridLayout must remove the view before it can be added to another view,
     * therefore removeView *must* be called before addView.
     */
    class CoderDragListener implements View.OnDragListener {
        Drawable targetShape = ContextCompat.getDrawable(context, R.drawable.target_shape);
        Drawable normalShape = ContextCompat.getDrawable(context, R.drawable.normal_shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing, handled by OnTouchListener (ShadowBuilder)
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(targetShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackground(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to somewhere else in the ViewGroup
                    View targetView = (View) event.getLocalState();
                    ViewGroup gridLayout = (ViewGroup) targetView.getParent();
                    LinearLayout linearLayout = (LinearLayout) v;
                    gridLayout.removeView(targetView);
                    linearLayout.addView(targetView);
                    targetView.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackground(normalShape);
                default:
                    break;
            }
            return true;
        }
    }


}

