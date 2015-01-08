package com.rts;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;

public class SettingsActivity extends SherlockPreferenceActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//ActionBar bar = getSupportActionBar();
		//bar.setDisplayHomeAsUpEnabled(true);
		//bar.setTitle(R.string.settingsactivity_title);

		addPreferencesFromResource(R.xml.preferences);
	}

}