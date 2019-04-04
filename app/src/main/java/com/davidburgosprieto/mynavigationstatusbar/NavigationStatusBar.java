package com.davidburgosprieto.mynavigationstatusbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NavigationStatusBar extends LinearLayout {

    public final int MAX_ELEMENTS = 8;
    public final int STYLE_STROKE = 0;
    public final int STYLE_SOLID = 1;

    /* ************************ */
    /* Private member variables */
    /* ************************ */

    private Paint mPaint;
    private Rect mRect;
    private int mBgColor, mSelectedTintColor, mSelectedTxtColor, mUnselectedTintColor,
            mUnselectedTxtColor, mNotClickableTintColor, mNotClickableTxtColor, mStyle, mTotal,
            mClickables, mSelected;
    private ArrayList<RelativeLayout> mLayouts;
    private ArrayList<TextView> mCircles;
    private ArrayList<View> mLeftLines;
    private ArrayList<View> mRightLines;
    private Resources mRes;
    private OnInteractionListener mListener;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRect.left = 0;
        mRect.right = getWidth();
        mRect.top = 0;
        mRect.bottom = getHeight();
        canvas.drawRect(mRect, mPaint);
    }

    /* ************** */
    /* Public methods */
    /* ************** */

    /**
     * This interface must be implemented by activities that contain this fragment to allow an
     * interaction in this fragment to be communicated to the activity and potentially other
     * fragments contained in that activity.
     */
    public interface OnInteractionListener {
        void onInteraction(int buttonIndex);
    }

    public NavigationStatusBar(Context context) {
        super(context);
        init(null, context);
    }

    public NavigationStatusBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }

    public NavigationStatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NavigationStatusBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, context);
    }

    public void setSelectedElement(int index) {
        mSelected = index;
        reDraw();
    }

    public void attachListener(OnInteractionListener listener) {
        mListener = listener;
    }

    /* *************** */
    /* Private methods */
    /* *************** */

    private void reDraw() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    private void init(@Nullable AttributeSet set, Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRect = new Rect();

        // Read attributes array from AttributeSet and set all layout elements using attributes.
        if (set != null) {
            getAttributes(set);
            setLayout(context);
        }
    }

    private void getAttributes(AttributeSet set) {
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.NavigationStatusBar);

        mBgColor = ta.getColor(R.styleable.NavigationStatusBar_bg_color, getResources().getColor(R.color.colorPrimaryDark));
        mTotal = ta.getInt(R.styleable.NavigationStatusBar_total_elements, MAX_ELEMENTS);
        mClickables = ta.getInt(R.styleable.NavigationStatusBar_clickable_elements, MAX_ELEMENTS);
        mSelected = ta.getInt(R.styleable.NavigationStatusBar_selected_element, 1);
        mSelectedTintColor = ta.getColor(R.styleable.NavigationStatusBar_selected_bg_color, getResources().getColor(R.color.colorPrimaryDark));
        mSelectedTxtColor = ta.getColor(R.styleable.NavigationStatusBar_selected_txt_color, getResources().getColor(R.color.colorPrimaryDark));
        mUnselectedTintColor = ta.getColor(R.styleable.NavigationStatusBar_unselected_bg_color, getResources().getColor(R.color.colorPrimaryDark));
        mUnselectedTxtColor = ta.getColor(R.styleable.NavigationStatusBar_unselected_txt_color, getResources().getColor(android.R.color.white));
        mNotClickableTintColor = ta.getColor(R.styleable.NavigationStatusBar_not_clickable_bg_color, getResources().getColor(android.R.color.darker_gray));
        mNotClickableTxtColor = ta.getColor(R.styleable.NavigationStatusBar_not_clickable_txt_color, getResources().getColor(android.R.color.white));
        mStyle = ta.getInt(R.styleable.NavigationStatusBar_style, STYLE_STROKE);

        mPaint.setColor(mBgColor);
        reDraw();
        ta.recycle();
    }

    private void setLayout(Context context) {
        // Inflate navigation status bar layout.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.navigation_status_bar, this, true);

        // Get global resources element.
        mRes = this.getResources();

        mLayouts = new ArrayList<>();
        mCircles = new ArrayList<>();
        mLeftLines = new ArrayList<>();
        mRightLines = new ArrayList<>();

        // Strings for getIdentifier.
        String type = "id";
        String pkg = getContext().getPackageName();

        for (int i = 0; i < MAX_ELEMENTS; i++) {
            // Set all wrappers.
            int layoutResId = mRes.getIdentifier("layout_" + (i + 1), type, pkg);
            mLayouts.add(i, (RelativeLayout) findViewById(layoutResId));

            // Set visible elements.
            if (i < mTotal) {
                // Set circles.
                int circleResId = mRes.getIdentifier("circle_" + (i + 1), type, pkg);
                mCircles.add(i, (TextView) findViewById(circleResId));
                String text = Integer.toString(i + 1);
                mCircles.get(i).setText(text);

                // Set lines.
                int leftLineResId = mRes.getIdentifier("left_line_" + (i + 1), type, pkg);
                int rightLineResId = mRes.getIdentifier("right_line_" + (i + 1), type, pkg);
                mLeftLines.add(i, findViewById(leftLineResId));
                mRightLines.add(i, findViewById(rightLineResId));

                if (i < mClickables) {
                    // Set colors for clickable elements.
                    mCircles.get(i).setBackgroundTintList(mRes.getColorStateList(mUnselectedTintColor));
                    mLeftLines.get(i).setBackgroundColor(mRes.getColor(mUnselectedTintColor));
                    if (i == mClickables - 1) {
                        // The line to the right of the last clickable element must be grayed.
                        mRightLines.get(i).setBackgroundColor(mRes.getColor(mNotClickableTintColor));
                    } else {
                        // The line to the right of any clickable element but the last one must be colored.
                        mRightLines.get(i).setBackgroundColor(mRes.getColor(mUnselectedTintColor));
                    }

                    // Set listeners on clickable elements.
                    final int index = i;
                    mCircles.get(i).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setNotSelected(mSelected - 1);
                            setSelected(index);
                            mSelected = index + 1;
                            if (mListener != null) {
                                mListener.onInteraction(index + 1);
                            }
                        }
                    });
                } else {
                    // Set colors for not clickable elements.
                    mCircles.get(i).setBackgroundTintList(mRes.getColorStateList(mNotClickableTintColor));
                    mCircles.get(i).setTextColor(mRes.getColorStateList(mNotClickableTxtColor));
                    mLeftLines.get(i).setBackgroundColor(mRes.getColor(mNotClickableTintColor));
                    mRightLines.get(i).setBackgroundColor(mRes.getColor(mNotClickableTintColor));
                }

                // Set active element.
                if (i == mSelected - 1) {
                    setSelected(i);
                } else {
                    setNotSelected(i);
                }

                // Set visibility.
                mLayouts.get(i).setVisibility(View.VISIBLE);
            } else {
                // Elements beyond mTotal must not be visible.
                mLayouts.get(i).setVisibility(View.GONE);
            }
        }

        // Hide first left line and last right line.
        mLeftLines.get(0).setVisibility(View.INVISIBLE);
        mRightLines.get(mTotal - 1).setVisibility(View.INVISIBLE);
    }

    /**
     * Set style for selected element.
     *
     * @param i is the index of the element in the global arrays of elements.
     */
    private void setSelected(int i) {
        TextView tv = mCircles.get(i);

        // Set background for selected circle.
        tv.setBackground(mRes.getDrawable(R.drawable.circle_selected));

        // Set text color.
        tv.setTextColor(mRes.getColor(mSelectedTxtColor));

        // Set width and height for selected circle.
        final ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
        layoutParams.width = mRes.getDimensionPixelSize(R.dimen.circle_selected_size);
        layoutParams.height = mRes.getDimensionPixelSize(R.dimen.circle_selected_size);
        tv.setLayoutParams(layoutParams);
    }

    /**
     * Set style for not selected element.
     *
     * @param i is the index of the element in the global arrays of elements.
     */
    private void setNotSelected(int i) {
        TextView tv = mCircles.get(i);

        // Set background for not selected circle.
        tv.setBackground(mRes.getDrawable(R.drawable.circle_unselected));

        // Set text color.
        tv.setTextColor(mRes.getColor(mUnselectedTxtColor));

        // Set width and height for not selected circle.
        final ViewGroup.LayoutParams layoutParams = mCircles.get(i).getLayoutParams();
        layoutParams.width = mRes.getDimensionPixelSize(R.dimen.circle_unselected_size);
        layoutParams.height = mRes.getDimensionPixelSize(R.dimen.circle_unselected_size);
        mCircles.get(i).setLayoutParams(layoutParams);
    }
}
