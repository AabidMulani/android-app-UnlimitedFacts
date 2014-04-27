package com.probytemedia.amazingfacts.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.probytemedia.amazingfacts.activity.MainActivity;


public class ColoredTextView extends TextView {

	public ColoredTextView(Context context) {
		super(context);
		this.setTextColor(MainActivity.mainTextColor);
	}

	public ColoredTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
        this.setTextColor(MainActivity.mainTextColor);
	}

	public ColoredTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        this.setTextColor(MainActivity.mainTextColor);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

}