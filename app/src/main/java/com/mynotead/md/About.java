package com.mynotead.md;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.Preference;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;

public class About extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about);
		findPreference("abo").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

				@Override
				public boolean onPreferenceClick(Preference p1) {
					new AlertDialog.Builder(About.this).setMessage(R.string.message).create().show();
					return true;
				}
				
			
		});
		findPreference("aut").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

				private String url;

				@Override
				public boolean onPreferenceClick(Preference p1) {
					url = "mqqwpa://im/chat?chat_type=wpa&uin=1831553190";
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					return true;
				
				}
			
		});
	}
	
}

