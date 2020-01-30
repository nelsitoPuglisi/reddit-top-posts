package com.nelsito.reddittopposts;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

public class LandscapeImageView extends AppCompatImageView
{
    public LandscapeImageView(Context context)
    {
        super(context);
    }

    public LandscapeImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LandscapeImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() * 9 / 16); //Snap to width
    }
}