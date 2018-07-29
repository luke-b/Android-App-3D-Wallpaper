package com.steepmax.android.wallpapers;

import com.emirac.bonk.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

public class BonkSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
	
	private static final String TAG = "com.emirac.bonk.BonkSettings";
	private static final String SOUND_MIME_TYPE = "audio/*";
	private static final String IMAGE_MIME_TYPE = "image/*";
	private static final String UNKNOWN_TITLE = "(unknown)";

	public static final String	BG_IMAGE_KEY = "bgImagePref";
	public static final String	SOUND_USER_KEY = "soundFilePref";
	public static final String	SOUND_LIST_KEY = "soundStylePref";
	private static final int	CHOOSE_IMAGE_REQUEST = 1;
	private static final int	CHOOSE_SOUND_REQUEST = 2;
	
	private ListPreference		soundList = null;
	private int				soundListOrigSize = 0;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	//	getPreferenceManager().setSharedPreferencesName(BonkWallpaper.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.settings);
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	
		return;
	}

	@Override
	protected void onResume() {
		super.onResume();
		return;
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
		return;
	}

	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		return;
	}


	private void updateSoundList() {
		String vUser = findPreference(SOUND_USER_KEY).getSharedPreferences().getString(SOUND_USER_KEY, null);
		if(vUser != null){
			String title = BonkUtil.uriTitle(getBaseContext(), vUser);
			updateSoundList(title, vUser, false);
		}
		return;
	}
	
	private void updateSoundList(String t, String soundUri, boolean selectFlag) {
		CharSequence[] keys = soundList.getEntries();
		CharSequence[] values = soundList.getEntryValues();
		int i = keys.length;
		String title = (t == null) ? UNKNOWN_TITLE : t;
		if(i > soundListOrigSize){
			keys[i-1] = title;
			values[i-1] = soundUri;
		}
		else {
			CharSequence[] newKeys = new CharSequence[i+1];
			CharSequence[] newValues = new CharSequence[i+1];
			for(int j = 0; j < i; j++){
				newKeys[j] = keys[j];
				newValues[j] = values[j];
			}
			newKeys[i] = title;
			newValues[i] = soundUri;
			keys = newKeys;
			values = newValues;
			i++;
		}
		soundList.setEntries(keys);
		soundList.setEntryValues(values);
		if(selectFlag){
			soundList.setValueIndex(i-1);
		}
		return;
	}

	public boolean onPreferenceClick(Preference pref) {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		int requestCode = 0;
		if(pref.getKey().equals(BG_IMAGE_KEY)){
			i.setType(IMAGE_MIME_TYPE);
			requestCode = CHOOSE_IMAGE_REQUEST;
		}
		else if(pref.getKey().equals(SOUND_USER_KEY)){
			i.setType(SOUND_MIME_TYPE);
			requestCode = CHOOSE_SOUND_REQUEST;			
		}
		else {
			return(false);
		}
		try {
			startActivityForResult(i, requestCode);
		} catch (ActivityNotFoundException e) {
			Log.w(TAG, e.getMessage());
		}
		return(true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if((resultCode == Activity.RESULT_OK) && (requestCode == CHOOSE_IMAGE_REQUEST) && (data != null)) {
			String imagePath = BonkUtil.uriToFilePath(getBaseContext(), data.toUri(0));
			if(imagePath != null){
				findPreference(BG_IMAGE_KEY).getEditor().putString(BG_IMAGE_KEY, imagePath).commit();
			}
		}
		else if((resultCode == Activity.RESULT_OK) && (requestCode == CHOOSE_SOUND_REQUEST) && (data != null)) {
			updateSoundList(BonkUtil.uriTitle(getBaseContext(), data.toUri(0)), data.toUri(0), true);
			findPreference(SOUND_USER_KEY).getEditor().putString(SOUND_USER_KEY, data.toUri(0)).commit();
		}
		return;
	}

}
