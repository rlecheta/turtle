package br.android.tarta;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Tela de preferências
 * 
 * @author ricardo
 *
 */
public class PreferencesFromXml extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
