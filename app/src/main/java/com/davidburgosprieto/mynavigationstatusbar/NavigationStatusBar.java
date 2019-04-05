package com.davidburgosprieto.mynavigationstatusbar;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class NavigationStatusBar extends LinearLayout {

    public final int MAX_ELEMENTS = 8;
    public final int SHAPE_DRAWABLE_OVAL = 0;
    public final int SHAPE_DRAWABLE_RECTANGLE = 1;
    public final int SHAPE_STYLE_SOLID = 0;
    public final int SHAPE_STYLE_STROKE = 1;

    /* ************************ */
    /* Private member variables */
    /* ************************ */

    private Paint mPaint;
    private Rect mRect;
    private int mBgColor, mTotal, mClickables, mSelected, mSelectedBgColor, mSelectedInnerTxtSize,
            mSelectedOuterTxtSize, mSelectedTxtColor, mSelectedShapeStyle, mSelectedShapeSize,
            mUnselectedBgColor, mUnselectedInnerTxtSize, mUnselectedOuterTxtSize,
            mUnselectedTxtColor, mUnselectedShapeStyle, mUnselectedShapeSize, mUnselectedLinesColor,
            mNotClickableBgColor, mNotClickableInnerTxtSize, mNotClickableOuterTxtSize,
            mNotClickableTxtColor, mNotClickableShapeStyle, mNotClickableShapeSize,
            mNotClickableLinesColor, mShapesDrawable, mLinesWidth;
    private ColorStateList mUnselectedTintColorStateList, mSelectedTintColorStateList,
            mNotClickableTintColorStateList;
    private ArrayList<RelativeLayout> mElements;
    private ArrayList<TextView> mShapes, mOuterNumbers;
    private ArrayList<View> mLeftLines, mRightLines;
    private Resources mRes;
    private OnInteractionListener mListener;

    private int[][] mStates = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
            new int[]{-android.R.attr.state_enabled}, // disabled
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_pressed}  // pressed
    };

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
        // Default values.
        int bgColorDefValue = getResources().getColor(R.color.colorPrimaryDark);
        int disabledBgColorDefValue = getResources().getColor(android.R.color.darker_gray);
        int txtSizeDefValue = 0;
        int lineWidthDefValue = 4;
        int shapeSizeDefValue = 30;

        // Obtain a TypedArray with all elements defined in attrs.xml and initialise member
        // variables with the values input through this TypedArray, also providing default values in
        // case no value for that attribute was input by the user.
        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.NavigationStatusBar);

        mBgColor = ta.getColor(R.styleable.NavigationStatusBar_bg_color, bgColorDefValue);
        mTotal = ta.getInt(R.styleable.NavigationStatusBar_total_elements, MAX_ELEMENTS);
        mTotal = mTotal > MAX_ELEMENTS ? MAX_ELEMENTS : mTotal;
        mClickables = ta.getInt(R.styleable.NavigationStatusBar_clickable_elements, MAX_ELEMENTS);
        mClickables = mClickables > MAX_ELEMENTS ? MAX_ELEMENTS : mClickables;
        mSelected = ta.getInt(R.styleable.NavigationStatusBar_selected_element, 1);
        mSelected = mSelected > mClickables ? mClickables : mSelected;
        mSelectedBgColor = ta.getColor(R.styleable.NavigationStatusBar_selected_bg_color, bgColorDefValue);
        mSelectedTxtColor = ta.getColor(R.styleable.NavigationStatusBar_selected_txt_color, bgColorDefValue);
        mSelectedInnerTxtSize = ta.getInt(R.styleable.NavigationStatusBar_selected_inner_txt_size, txtSizeDefValue);
        mSelectedOuterTxtSize = ta.getInt(R.styleable.NavigationStatusBar_selected_outer_txt_size, txtSizeDefValue);
        mSelectedShapeStyle = ta.getInt(R.styleable.NavigationStatusBar_selected_shape_style, SHAPE_STYLE_SOLID);
        mSelectedShapeSize = ta.getInt(R.styleable.NavigationStatusBar_selected_shape_size, shapeSizeDefValue);
        mUnselectedBgColor = ta.getColor(R.styleable.NavigationStatusBar_unselected_bg_color, bgColorDefValue);
        mUnselectedTxtColor = ta.getColor(R.styleable.NavigationStatusBar_unselected_txt_color, bgColorDefValue);
        mUnselectedInnerTxtSize = ta.getInt(R.styleable.NavigationStatusBar_unselected_inner_txt_size, txtSizeDefValue);
        mUnselectedOuterTxtSize = ta.getInt(R.styleable.NavigationStatusBar_unselected_outer_txt_size, txtSizeDefValue);
        mUnselectedShapeStyle = ta.getInt(R.styleable.NavigationStatusBar_unselected_shape_style, SHAPE_STYLE_SOLID);
        mUnselectedShapeSize = ta.getInt(R.styleable.NavigationStatusBar_unselected_shape_size, shapeSizeDefValue);
        mUnselectedLinesColor = ta.getColor(R.styleable.NavigationStatusBar_unselected_lines_color, bgColorDefValue);
        mNotClickableBgColor = ta.getColor(R.styleable.NavigationStatusBar_not_clickable_bg_color, disabledBgColorDefValue);
        mNotClickableTxtColor = ta.getColor(R.styleable.NavigationStatusBar_not_clickable_txt_color, disabledBgColorDefValue);
        mNotClickableInnerTxtSize = ta.getInt(R.styleable.NavigationStatusBar_not_clickable_inner_txt_size, txtSizeDefValue);
        mNotClickableOuterTxtSize = ta.getInt(R.styleable.NavigationStatusBar_not_clickable_outer_txt_size, txtSizeDefValue);
        mNotClickableShapeStyle = ta.getInt(R.styleable.NavigationStatusBar_not_clickable_shape_style, SHAPE_STYLE_SOLID);
        mNotClickableShapeSize = ta.getInt(R.styleable.NavigationStatusBar_not_clickable_shape_size, shapeSizeDefValue);
        mNotClickableLinesColor = ta.getColor(R.styleable.NavigationStatusBar_not_clickable_lines_color, bgColorDefValue);
        mShapesDrawable = ta.getInt(R.styleable.NavigationStatusBar_shape_drawable, SHAPE_DRAWABLE_OVAL);
        mLinesWidth = ta.getInt(R.styleable.NavigationStatusBar_lines_width, lineWidthDefValue);

        ta.recycle();

        // Set ColorStateList member variables from input colors.
        mUnselectedTintColorStateList = new ColorStateList(mStates,
                new int[]{mUnselectedBgColor, mUnselectedBgColor, mUnselectedBgColor, mUnselectedBgColor});
        mSelectedTintColorStateList = new ColorStateList(mStates,
                new int[]{mSelectedBgColor, mSelectedBgColor, mSelectedBgColor, mSelectedBgColor});
        mNotClickableTintColorStateList = new ColorStateList(mStates,
                new int[]{mNotClickableBgColor, mNotClickableBgColor, mNotClickableBgColor, mNotClickableBgColor});

        // Set background color.
        mPaint.setColor(mBgColor);
        reDraw();
    }

    /**
     * Convert dp (Density-independent Pixels) to pixels.
     *
     * @param dp is the integer dp value to be converted to pixels.
     * @return the float value of pixels corresponding to the given dp value.
     */
    private float dp2px(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) dp, mRes.getDisplayMetrics());
    }

    /**
     * Convert sp (Scale-independent Pixels) to pixels.
     *
     * @param sp is the integer sp value to be converted to pixels.
     * @return the float value of pixels corresponding to the given sp value.
     */
    private float sp2px(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, (float) sp, mRes.getDisplayMetrics());
    }

    private GradientDrawable setShapeStyle(int style, ColorStateList colorStateList) {
        GradientDrawable shape = new GradientDrawable();
        switch (style) {
            case SHAPE_STYLE_STROKE:
                shape.setStroke((int) dp2px(mLinesWidth), mLinesColor);

            case SHAPE_STYLE_SOLID:
            default:
                shape.setColor(colorStateList);
        }
        return shape;
    }

    private void setLayout(Context context) {
        // Inflate navigation status bar layout.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.navigation_status_bar, this, true);

        // Get global resources element.
        mRes = this.getResources();

        mElements = new ArrayList<>();
        mShapes = new ArrayList<>();
        mOuterNumbers = new ArrayList<>();
        mLeftLines = new ArrayList<>();
        mRightLines = new ArrayList<>();

        // Strings for getIdentifier.
        String type = "id";
        String pkg = getContext().getPackageName();

        for (int i = 0; i < MAX_ELEMENTS; i++) {
            // Set all wrappers.
            int elementResId = mRes.getIdentifier("element_" + (i + 1), type, pkg);
            mElements.add(i, (RelativeLayout) findViewById(elementResId));

            // Set visible elements.
            if (i < mTotal) {
                // Set shapes.
                int shapeResId = mRes.getIdentifier("shape_" + (i + 1), type, pkg);
                mShapes.add(i, (TextView) findViewById(shapeResId));
                GradientDrawable shape = new GradientDrawable();
                switch (mShapesDrawable) {
                    case SHAPE_DRAWABLE_RECTANGLE:
                        shape.setShape(GradientDrawable.RECTANGLE);
                        break;

                    case SHAPE_DRAWABLE_OVAL:
                    default:
                        shape.setShape(GradientDrawable.OVAL);
                }
                mShapes.get(i).setBackground(shape);

                // Set outer numbers.
                int numberResId = mRes.getIdentifier("text_" + (i + 1), type, pkg);
                mOuterNumbers.add(i, (TextView) findViewById(numberResId));

                // Set lines.
                int leftLineResId = mRes.getIdentifier("left_line_" + (i + 1), type, pkg);
                int rightLineResId = mRes.getIdentifier("right_line_" + (i + 1), type, pkg);
                mLeftLines.add(i, findViewById(leftLineResId));
                mRightLines.add(i, findViewById(rightLineResId));

                if (i < mClickables) {
                    // Set colors for clickable elements.
                    mShapes.get(i).setBackgroundTintList(mUnselectedTintColorStateList);
                    mLeftLines.get(i).setBackgroundColor(mUnselectedBgColor);
                    if (i == mClickables - 1) {
                        // The line to the right of the last clickable element must be grayed.
                        mRightLines.get(i).setBackgroundColor(mNotClickableBgColor);
                    } else {
                        // The line to the right of any clickable element but the last one must be colored.
                        mRightLines.get(i).setBackgroundColor(mUnselectedBgColor);
                    }

                    // Set listeners on clickable elements.
                    final int index = i;
                    mShapes.get(i).setOnClickListener(new OnClickListener() {
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
                    mShapes.get(i).setBackgroundTintList(mNotClickableTintColorStateList);
                    //mShapes.get(i).setTextColor(mNotClickableInnerTxtColor);
                    mLeftLines.get(i).setBackgroundColor(mNotClickableBgColor);
                    mRightLines.get(i).setBackgroundColor(mNotClickableBgColor);
                }

                // Set active element.
                if (i == mSelected - 1) {
                    setSelected(i);
                } else {
                    setNotSelected(i);
                }

                // Set visibility.
                mElements.get(i).setVisibility(View.VISIBLE);
                if (mSelectedOuterTxtSize > 0 || mUnselectedOuterTxtSize > 0 ||
                        mNotClickableOuterTxtSize > 0) {
                    // Outer TextViews must be
                    mOuterNumbers.get(i).setVisibility(View.VISIBLE);
                }
            } else {
                // Elements beyond mTotal must not be visible.
                mElements.get(i).setVisibility(View.GONE);
                mOuterNumbers.get(i).setVisibility(View.GONE);
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
        TextView shapeTextView = mShapes.get(i);
        TextView numberTextView = mOuterNumbers.get(i);

        // Set background for selected shape.
        shapeTextView.setBackground(mRes.getDrawable(R.drawable.circle_selected));

        // Set text if required.
        String text = Integer.toString(i + 1);
        if (mSelectedInnerTxtSize > 0) {
            shapeTextView.setText(text);
            shapeTextView.setTextSize(sp2px(mSelectedInnerTxtSize));
            shapeTextView.setTextColor(mSelectedTxtColor);
        }
        if (mSelectedOuterTxtSize > 0) {
            numberTextView.setText(text);
            numberTextView.setTextSize(sp2px(mSelectedOuterTxtSize));
            numberTextView.setTextColor(mSelectedTxtColor);
        } else {
            numberTextView.setText("");
        }

        // Set width and height for selected shape.
        final ViewGroup.LayoutParams layoutParams = shapeTextView.getLayoutParams();
        layoutParams.width = mSelectedShapeSize;
        layoutParams.height = mSelectedShapeSize;
        shapeTextView.setLayoutParams(layoutParams);

        // Set background shape.
        shapeTextView.setBackground(setShapeStyle(mSelectedShapeStyle, mSelectedTintColorStateList));
    }

    /**
     * Set style for not selected element.
     *
     * @param i is the index of the element in the global arrays of elements.
     */
    private void setNotSelected(int i) {
        TextView shape = mShapes.get(i);
        TextView numberTextView = mOuterNumbers.get(i);

        // Set background for not selected shape.
        shape.setBackground(mRes.getDrawable(R.drawable.circle_unselected));

        // Set text if required.
        String text = Integer.toString(i + 1);
        if (mUnselectedInnerTxtSize > 0) {
            shape.setText(text);
            shape.setTextSize(sp2px(mUnselectedInnerTxtSize));
            shape.setTextColor(mUnselectedTxtColor);
        }
        if (mUnselectedOuterTxtSize > 0) {
            numberTextView.setText(text);
            numberTextView.setTextSize(sp2px(mUnselectedOuterTxtSize));
            numberTextView.setTextColor(mUnselectedTxtColor);
        } else {
            numberTextView.setText("");
        }

        // Set width and height for not selected shape.
        final ViewGroup.LayoutParams layoutParams = mShapes.get(i).getLayoutParams();
        layoutParams.width = mUnselectedShapeSize;
        layoutParams.height = mUnselectedShapeSize;
        mShapes.get(i).setLayoutParams(layoutParams);

        // Set background shape.
        shape.setBackground(setShapeStyle(mUnselectedShapeStyle, mUnselectedTintColorStateList));
    }
}
