package com.shiweinan.keyboard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static class SettingFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .add(R.id.preference, new SettingFragment())
                //.replace(android.R.id.content, new SettingFragment())
                .commit();

        Settings.setContext(this);

        /*BottomNavigationView navigation = findViewById(R.id.navigation);
        final Intent intent = new Intent(this, SettingsActivity.class);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setContentView(R.layout.activity_main);
                        return true;
                    case R.id.navigation_dashboard:
                        return true;
                    case R.id.navigation_settings:
                        getFragmentManager().beginTransaction()
                                .replace(android.R.id.content, new SettingFragment())
                                .commit();
                        return true;
                }
                return false;
            }
        });*/
    }
}
