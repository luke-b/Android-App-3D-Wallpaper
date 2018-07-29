package com.steepmax.android.wallpapers;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.emirac.bonk.R;

public class SoundManager {

	private static final String TAG = "com.emirac.bonk.SoundManager";

	// sound resource arrays from arrays.xml
	// the array's index in here corresponds to its index in 
	//  the sound style ListPreference.
	private static final int[]	arrayIDs = {
		R.array.bonks,
		R.array.dogs,
		R.array.banjo,
		R.array.menagerie
	};
	// number of concurrent sounds
	private static final int N_STREAMS = 10;
	private static final int MAX_SOUND_LENGTH = 32000;

	private Context				mContext;
	private int					mSoundArrayRID;
	private int[]				mSoundIDs;
	private int					mNumSounds;
	private SoundPool			mSoundPool;
	private	String				mUserSoundName = null;
	private	long				mUserSoundLength = 0;
	private boolean				mUserSound = false;

	SoundManager(Context c){
		mContext = c;
		mSoundIDs = null;
		mSoundPool = null;
		mNumSounds = 0;
		mSoundArrayRID = arrayIDs[0];
	}

	public void setScheme(String idx){
		try{
			mUserSound = false;
			mUserSoundName = null;
			int resIndex = Integer.parseInt(idx);
			if((resIndex >= 0) && (resIndex < arrayIDs.length)){
				mSoundArrayRID = arrayIDs[resIndex];
				if(mSoundPool != null){
					loadSounds();
				}
			}
		}
		catch(NumberFormatException e) {
			// might need a user sound.  if the idx didn't decode it must be
			//  a Uri of some sound specified by the user
			if(idx != null){
				mUserSoundName = idx;
				mUserSoundLength = BonkUtil.uriFileSize(mContext, idx);
				Log.v(TAG, "soundUriFileSize(" + idx + ") = " + mUserSoundLength + "bytes");
				if(mUserSoundLength > MAX_SOUND_LENGTH) {
					mUserSoundLength = MAX_SOUND_LENGTH;
				}
				// unload the old ones
				if(mSoundPool != null){
					for(int i = 0; i < mNumSounds; i++){
						mSoundPool.unload(mSoundIDs[i]);
					}
				}
				mUserSound = true;
				mNumSounds = 0;
			}
			Log.v(TAG, e.getMessage());
			return;
		}
		return;
	}
	
	public void on(){
		if(mSoundPool == null){
			mSoundPool = new SoundPool(N_STREAMS, AudioManager.STREAM_MUSIC, 0);
		}
		if(!mUserSound){
			loadSounds();
		}
		return;
	}
	
	public void off(){
		if(mSoundPool != null){
			mSoundPool.release();
			mSoundPool = null;
			mNumSounds = 0;
		}
		return;
	}
	
	public void play(){
	/*	if(mSoundPool != null){
			if(mUserSound && (mNumSounds == 0)){
				loadUserSound();
			}
			if(mNumSounds == 1){
				mSoundPool.play(mSoundIDs[0], 1, 1, 0, 0, radiusToRate(b1, b2, maxR, minR));
			}
			else if (mNumSounds > 0){
				mSoundPool.play(mSoundIDs[radiusToIndex(b1, b2, maxR, minR)], 1, 1, 0, 0, 1);				
			}
		} */
		return;
	}
	
	private void loadSounds() {
		// unload the old ones
		for(int i = 0; i < mNumSounds; i++){
			mSoundPool.unload(mSoundIDs[i]);
		}
		// load new sounds
		mNumSounds = 0;
		TypedArray soundRIDs = mContext.getResources().obtainTypedArray(mSoundArrayRID);
		if(soundRIDs != null){
			mNumSounds = soundRIDs.length();
			if(mNumSounds > 0){
				mSoundIDs = new int[mNumSounds];
				for(int i = 0; i < mNumSounds; i++){
					mSoundIDs[i] = mSoundPool.load(mContext, soundRIDs.getResourceId(i,R.raw.bonksound), 1);
				}
			}
			soundRIDs.recycle();
		}
		if(mNumSounds == 0){
			mNumSounds = 1;
			mSoundIDs = new int[1];
			mSoundIDs[0] = mSoundPool.load(mContext, R.raw.bonksound, 1);
		}
		return;
	}
	
	private void loadUserSound(){
		if(mUserSound && (mNumSounds == 0)){
			FileInputStream fi;
			FileDescriptor fd;
			try {
				String fpath = BonkUtil.uriToFilePath(mContext, mUserSoundName);
				if(fpath != null){
					int id = 0;
					fi = new FileInputStream(fpath);
					fd = fi.getFD();
					if(mUserSoundLength > 0){
						id = mSoundPool.load(fd, 0, mUserSoundLength, 1);
					}
					else{
						id = mSoundPool.load(fd, 0, MAX_SOUND_LENGTH, 1);					
					}
					if(id >= 0){
						mNumSounds = 1;
						mSoundIDs = new int[1];
						mSoundIDs[0] = id;
					}
					fi.close();
				}
			} catch (FileNotFoundException e) {
				Log.w(TAG, e.getMessage());
			} catch (IOException e) {
				Log.w(TAG, e.getMessage());
			}
		}
		return;
	}

	private float radiusToRate() {
		// return a float between 0.5 and 2.0 depending
		//  upon the sum of the radii of the two input
		//  circles.
	/*	float rate = 1;
		float r2 = (float) (b1.getR() + b2.getR()); 
		double m = (1.5 / ((minR<<1) - (maxR<<1)));
		rate = (float) (m * (r2 - (minR<<1))) + 2;
		return(rate); */
		return 0;
	}

	private int radiusToIndex() {
		// return an int between 0 and mNumSounds-1 depending
		//  upon the sum of the radii of the two input
		//  circles.
	/*	int i = (int) ((((b1.getR() + b2.getR())- minR) / ((maxR - minR)<<1)) * mNumSounds) ;
		if(i < 0){
			i = 0;
		}
		else if(i >= mNumSounds){
			i = mNumSounds -1; 
		}
		return(i); */
		return 0;
	}

}
