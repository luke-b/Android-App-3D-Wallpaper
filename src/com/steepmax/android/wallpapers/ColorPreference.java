
package com.steepmax.android.wallpapers;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.LinearLayout;
import android.graphics.PorterDuff;

public class ColorPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener
{
	private static final String	androidns="http://schemas.android.com/apk/res/android";
	
	private SeekBar		mSeekBarR;
	private SeekBar		mSeekBarG;
	private SeekBar		mSeekBarB;
	private Context		mContext;
	private int			mDefault;
	private int			mMax = 255;
	private int			mValue = 0;
	private int			mRed = 0; 
	private int			mGreen = 0;
	private int			mBlue = 0;
	private ImageView	mColorView;

	public ColorPreference(Context context, AttributeSet attrs) { 
		super(context,attrs); 
		mContext = context;
		mDefault = attrs.getAttributeIntValue(androidns,"defaultValue", 0);
		mMax = attrs.getAttributeIntValue(androidns,"max", 255);
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// check if the "OK" button was pressed
		if (which == DialogInterface.BUTTON_POSITIVE) {
			if(shouldPersist()){
				persistInt(mValue);
			}
		}
		return;
	}

	@Override 
	protected View onCreateDialogView() {
		LinearLayout.LayoutParams params;
		LinearLayout layout = new LinearLayout(mContext);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(6,6,6,6);
		
		mColorView = new ImageView(mContext);
		mColorView.setMinimumHeight(30);
		mColorView.setBackgroundColor(0xffff0000);
		params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.bottomMargin = 25;
		layout.addView(mColorView, params);		
	
		mSeekBarR = new SeekBar(mContext);
		mSeekBarR.setOnSeekBarChangeListener(this);
		mSeekBarR.getProgressDrawable().setColorFilter(0xbbff0000, PorterDuff.Mode.SRC_OVER);
		layout.addView(mSeekBarR, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		mSeekBarG = new SeekBar(mContext);
		mSeekBarG.setOnSeekBarChangeListener(this);
		mSeekBarG.getProgressDrawable().setColorFilter(0xbb00ff00, PorterDuff.Mode.SRC_OVER);
		layout.addView(mSeekBarG, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		mSeekBarB = new SeekBar(mContext);
		mSeekBarB.setOnSeekBarChangeListener(this);
		mSeekBarB.getProgressDrawable().setColorFilter(0xbb0000ff, PorterDuff.Mode.SRC_OVER);
		layout.addView(mSeekBarB, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		if (shouldPersist()){
			mValue = getPersistedInt(mDefault);
			mRed = (mValue & 0x00ff0000) >> 16;
			mGreen = (mValue & 0x0000ff00) >> 8;
			mBlue = mValue & 0x000000ff;
		}

		mSeekBarR.setMax(mMax);
		mSeekBarR.setProgress(mRed);
		mSeekBarG.setMax(mMax);
		mSeekBarG.setProgress(mGreen);
		mSeekBarB.setMax(mMax);
		mSeekBarB.setProgress(mBlue);
		mColorView.setBackgroundColor(mValue | 0xff000000);
		return(layout);
	}
	
	@Override 
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		mSeekBarR.setMax(mMax);
		mSeekBarR.setProgress(mRed);
		mSeekBarG.setMax(mMax);
		mSeekBarG.setProgress(mGreen);
		mSeekBarB.setMax(mMax);
		mSeekBarB.setProgress(mBlue);
		return;
	}
	
	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if(restore){
			mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
			mRed = (mValue & 0x00ff0000) >> 16;
			mGreen = (mValue & 0x0000ff00) >> 8;
			mBlue = mValue & 0x000000ff;
		}
		else {
			mValue = (Integer)defaultValue;
			mRed = (mValue & 0x00ff0000) >> 16;
			mGreen = (mValue & 0x0000ff00) >> 8;
			mBlue = mValue & 0x000000ff;
		}
		return;
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch){
		int v = value;
		if(seek == mSeekBarR){
			v = (mValue & 0x00ffff) | (value << 16);
		}
		else if(seek == mSeekBarG){
			v = (mValue & 0xff00ff) | (value << 8);
		}
		else if(seek == mSeekBarB){
			v = (mValue & 0xffff00) | (value);
		}
		mValue = v;
		mColorView.setBackgroundColor(mValue | 0xff000000);	
		callChangeListener(new Integer(value));
		return;
	}
	
	public void onStartTrackingTouch(SeekBar seek){
		return;
	}
	
	public void onStopTrackingTouch(SeekBar seek){
		return;
	}

	public void setMax(int max){
		mMax = max; 
		return;
	}

	public int getMax(){
		return(mMax);
	}

}
