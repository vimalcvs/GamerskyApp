package com.news.gamersky;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.bumptech.glide.Glide;
import com.github.piasy.biv.BigImageViewer;
import com.google.android.material.snackbar.Snackbar;
import com.news.gamersky.util.NightModeUtil;
import com.news.gamersky.util.ReadingProgressUtil;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment(), "setting")
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.setting, rootKey);

            final Preference preference = findPreference("manual_clear_cache");
            final Preference preference1 = findPreference("manual_clear_save");
            final SwitchPreference switchPreference = findPreference("swpie_back");
            final SeekBarPreference seekBarPreference = findPreference("swipe_back_distance");
            final SeekBarPreference seekBarPreference1 = findPreference("swipe_sides_sensitivity");
            final SwitchPreference switchPreference1 = findPreference("no_bottombar");
            final SwitchPreference switchPreference2 = findPreference("float_bottombar");
            final ListPreference listPreference = findPreference("night_mode");
            final ListPreference listPreference1 = findPreference("bottom_mode");
            final ListPreference listPreference2 = findPreference("article_text_size");

            listPreference.setValueIndex(Integer.parseInt(listPreference.getValue()));
            listPreference1.setValueIndex(Integer.parseInt(listPreference1.getValue()));
            listPreference2.setValueIndex(Integer.parseInt(listPreference2.getValue()));

            if (switchPreference.isChecked()) {
                seekBarPreference.setVisible(true);
                seekBarPreference1.setVisible(true);
            } else {
                seekBarPreference.setVisible(false);
                seekBarPreference1.setVisible(false);
            }

            switchPreference2.setVisible(!switchPreference1.isChecked());


            switchPreference.setOnPreferenceChangeListener((preference2, newValue) -> {
                if ((boolean) newValue) {
                    seekBarPreference.setVisible(true);
                    seekBarPreference1.setVisible(true);
                } else {
                    seekBarPreference.setVisible(false);
                    seekBarPreference1.setVisible(false);
                }
                return true;
            });
            switchPreference1.setOnPreferenceChangeListener((preference22, newValue) -> {
                if ((boolean) newValue) {
                    switchPreference2.setVisible(false);
                    switchPreference2.setChecked(false);
                } else {
                    switchPreference2.setVisible(true);
                    switchPreference2.setChecked(true);
                }
                return true;
            });

            preference.setOnPreferenceClickListener(preference23 -> {
                clearGlideDiskCache();
                return true;
            });

            preference1.setOnPreferenceClickListener(preference24 -> {
                ReadingProgressUtil.clearNewsClickList(getContext());
                ReadingProgressUtil.clearReadingProgress(getContext());
                ReadingProgressUtil.clearSearchClickList(getContext());
                Snackbar.make(SettingsFragment.this.getView(), "记录已清除", Snackbar.LENGTH_LONG).show();
                return true;
            });

            listPreference.setOnPreferenceChangeListener((preference25, newValue) -> {
                //Log.i("TAG", "onPreferenceChange: "+newValue);
                NightModeUtil.changeNightMode(Integer.parseInt((String) newValue));
                return true;
            });
        }


        public void clearGlideDiskCache() {

            new Thread(() -> {
                Glide.get(SettingsFragment.this.getContext()).clearDiskCache();
                BigImageViewer.imageLoader().cancelAll();
                SettingsFragment.this.getActivity().runOnUiThread(() -> Snackbar.make(SettingsFragment.this.getView(), "缓存已清除", Snackbar.LENGTH_LONG).show());
            }).start();

        }
    }

}