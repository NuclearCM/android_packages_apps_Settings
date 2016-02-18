/*
 * Copyright (C) 2014 The LiquidSmooth Project
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

package com.android.settings.nuclear;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.os.SystemProperties;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.SeekBarPreference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import com.android.settings.search.SearchIndexableRaw;
import com.android.settings.Utils;
import android.util.SparseArray;
import android.content.res.Resources;


import android.provider.SearchIndexableResource;
import android.content.Context;
import android.util.SettingConfirmationHelper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import java.io.File;
import java.io.IOException;
import java.io.DataOutputStream;

public class MainSettings extends SettingsPreferenceFragment implements Indexable {
    private static final String KEY_PERSO = "perso";
    private static final String KEY_REND = "rend";
    private static final String KEY_SECUR = "secur";
    private static final String KEY_BLOQ = "bloq";
    private static final String KEY_MISC = "misc";

	private static final String CATEGORY_PERFORMANCE = "performance_category";

	private static final String KEY_SYNAPSE = "key_synapse";
	private static final String KEY_KERNEL_ADIUTOR = "key_kernel_adiutor";

    /*    private static SparseArray<String> allKeyTitles(Context context) {
        final SparseArray<String> rt = new SparseArray<String>();
        rt.put(R.string.nuclear_perso_title, KEY_PERSO);
        rt.put(R.string.nuclear_cat_title, KEY_REND);
        rt.put(R.string.app_security_title, KEY_SECUR);
        rt.put(R.string.nuclear_bloq_title, KEY_BLOQ);
        rt.put(R.string.misc_category_title, KEY_MISC);

        return rt;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.nuclear_main_settings);

        Resources res = getResources();

        SettingConfirmationHelper.request(
            getActivity(),
            Settings.System.PERFORMANCE_APP,
            res.getString(R.string.performance_app_title),
            res.getString(R.string.performance_app_message),
            res.getString(R.string.kernel_adiutor_title),
            res.getString(R.string.synapse_title),
            null,
            null
        );

    /*    SettingConfirmationHelper.request(
            getActivity(),
            Settings.System.PERFORMANCE_APP,
            res.getString(R.string.performance_app_title),
            res.getString(R.string.performance_app_message),
            res.getString(R.string.kernel_adiutor_title),
            res.getString(R.string.synapse_title),
            null,
            null
        );*/
	Boolean checkPerformance = Settings.System.getInt(getContentResolver(), Settings.System.PERFORMANCE_APP, 0) == 1;

        if(checkPerformance) {
           PreferenceScreen synapse = (PreferenceScreen) findPreference(KEY_SYNAPSE);
           getPreferenceScreen().removePreference(synapse);
        } else {
           PreferenceScreen kernelAdiutor = (PreferenceScreen) findPreference(KEY_KERNEL_ADIUTOR);
           getPreferenceScreen().removePreference(kernelAdiutor);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return 1;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals(KEY_SYNAPSE)) {
            final String appPackageName = "com.af.synapse";
            try {
                getActivity().getPackageManager().getPackageInfo(appPackageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                try {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException ex) {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            }
            return false;
        } else if (preference.getKey().equals(KEY_KERNEL_ADIUTOR)) {
            final String appPackageName = "com.grarak.kerneladiutor";
            try {
                getActivity().getPackageManager().getPackageInfo(appPackageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                try {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (ActivityNotFoundException ex) {
                    getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                return true;
            }
            return false;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
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
                    data.screenTitle = res.getString(R.string.nuclear_settings_title);
                    result.add(data);
                }
                return result;
            }


        public List<String> getNonIndexableKeys(Context context) {
            final ArrayList<String> rt = new ArrayList<String>();
            if (!Utils.isVoiceCapable(context)) {
                rt.add(KEY_REND);
                rt.add(KEY_PERSO);
                rt.add(KEY_BLOQ);
                rt.add(KEY_MISC);
            }
            return rt;
        }
    };*/
}
