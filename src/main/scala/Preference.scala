package iocikun.juj.lojban.dictionary

import _root_.android.os.Bundle
import _root_.android.preference.PreferenceActivity
import _root_.android.content.SharedPreferences
import _root_.android.view.Window

class Preference extends PreferenceActivity
	with SharedPreferences.OnSharedPreferenceChangeListener {

	override def onCreate(savedInstanceState: Bundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		super.onCreate(savedInstanceState)
		addPreferencesFromResource(R.xml.preference)
	}

	override def onResume {
		super.onResume
		getPreferenceScreen.getSharedPreferences.
			registerOnSharedPreferenceChangeListener(this)
	}

	override def onSharedPreferenceChanged(
		sharedPreferences: SharedPreferences, key: String) {
// //		Etc19.handler.post(Etc19.runnable);
	}

}
