/*
 *<!-- Copyright (C) 2014-2015 NucleaRom
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.nuclear.misc;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;
import java.io.IOException;
import android.preference.SwitchPreference;
import com.android.settings.util.Helpers;
import dalvik.system.VMRuntime;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.android.internal.logging.MetricsLogger;

public class misc extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

	/*    private static final String KEY_SCREN_RECORDER = "screen_recorder";

    private static SparseArray<String> allKeyTitles(Context context) {
        final SparseArray<String> rt = new SparseArray<String>();
        rt.put(R.string.screen_recorder_title, KEY_SCREN_RECORDER);

        return rt;
    }

    private SwitchPreference mSelinux;*/

    private static final String ENABLE_MULTI_WINDOW_KEY = "enable_multi_window";
	private static final String MULTI_WINDOW_SYSTEM_PROPERTY = "persist.sys.debug.multi_window";
	private static final String RESTART_SYSTEMUI = "restart_systemui";
    private static final String COLUM_NUMBER = "colum_number";

    private static final String MEDIA_SCANNER_ON_BOOT = "media_scanner_on_boot";
 	
    private static int COLUM;

	private SwitchPreference mEnableMultiWindow;
	private Preference mRestartSystemUI;
        private SwitchPreference mColumNumber;

        private ListPreference mMSOB;

	private final ArrayList<Preference> mAllPrefs = new ArrayList<Preference>();

  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.nuclear_misc);

        final ContentResolver resolver = getActivity().getContentResolver();
        mEnableMultiWindow = (SwitchPreference) findPreference(ENABLE_MULTI_WINDOW_KEY);
        mRestartSystemUI = findPreference(RESTART_SYSTEMUI);
        mColumNumber = (SwitchPreference) findPreference(COLUM_NUMBER);


        mMSOB = (ListPreference) findPreference(MEDIA_SCANNER_ON_BOOT);
        mAllPrefs.add(mMSOB);
        mMSOB.setOnPreferenceChangeListener(this);
        updateMSOBOptions();

    }

    private static boolean showEnableMultiWindowPreference() {
        return !"user".equals(Build.TYPE);
    }

    private void setEnableMultiWindow(boolean value) {
        SystemProperties.set(MULTI_WINDOW_SYSTEM_PROPERTY, String.valueOf(value));
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.DEVELOPMENT;
    }

    /*private void setColumns(boolean value){

    }*/

    public int getColumns(){
        if (mColumNumber.isChecked()) {
                COLUM=2;
            }else{
                COLUM=1;
            }
        return COLUM;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mEnableMultiWindow) {
            if (mEnableMultiWindow.isChecked()) {
                setEnableMultiWindow(true);
            } else {
                setEnableMultiWindow(false);
            }
        }
     else if (preference == mColumNumber) {
            if (mColumNumber.isChecked()) {
                Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.COLUM_NUMBER, 2);
            }else{
                Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.COLUM_NUMBER, 1);
            }
 	}else if (preference == mRestartSystemUI) {
           Helpers.restartSystemUI();  
	}else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        return false;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
     ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mMSOB) {
            writeMSOBOptions(newValue);
            return true;
	}
        return false;
     } 

           /* // === Indexing ===

            public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {

            @Override
            public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean enabled) {
                final SparseArray<String> keyTitles = allKeyTitles(context);
                final int N = keyTitles.size();
                final List<SearchIndexableRaw> result = new ArrayList<SearchIndexableRaw>(N);
                final Resources res = context.getResources();
                for (int i = 0; i < N; i++) {
                    final SearchIndexableRaw data = new SearchIndexableRaw(context);
                    data.key = keyTitles.valueAt(i);
                    data.title = res.getString(keyTitles.keyAt(i));
                    data.screenTitle = res.getString(R.string.misc_category_title);
                    result.add(data);
                }
                return result;
            }


        public List<String> getNonIndexableKeys(Context context) {
            final ArrayList<String> rt = new ArrayList<String>();
            if (!Utils.isVoiceCapable(context)) {
                rt.add(KEY_SCREN_RECORDER);
            }
            return rt;
        }
    };
*/
   private void resetMSOBOptions() {
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0);
    }

    private void writeMSOBOptions(Object newValue) {
        Settings.System.putInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT,
                Integer.valueOf((String) newValue));
        updateMSOBOptions();
    }

    private void updateMSOBOptions() {
        int value = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0);
        mMSOB.setValue(String.valueOf(value));
        mMSOB.setSummary(mMSOB.getEntry());
     }
}
