/*
 * Copyright (C) 2021 Codepunk, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Adapted from the Android Open Source Project
 * Repository: android/superproject
 * Project: packages/apps/Settings/
 * File path: src/com/android/settings/widget/MasterSwitchPreference.java
 *
 * Changes to original:
 * Class is public
 * Extends TwoTargetPreference instead of RestrictedPreference
 * Removes the setDisabledByAdmin method
 * Adds final isDisabledByAdmin method as a stub
 */

package com.codepunk.doofenschmirtz.android.settings.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Switch;
import com.codepunk.doofenschmirtz.android.settingslib.TwoTargetPreference;
import com.codepunk.doofenschmirtz.settings.R;

import androidx.preference.PreferenceViewHolder;

public class MasterSwitchPreference extends TwoTargetPreference {

    private Switch mSwitch;
    private boolean mChecked;
    private boolean mCheckedSet;
    private boolean mEnableSwitch = true;

    public MasterSwitchPreference(Context context, AttributeSet attrs,
                                  int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MasterSwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MasterSwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MasterSwitchPreference(Context context) {
        super(context);
    }

    @Override
    protected int getSecondTargetResId() {
        return R.layout.android_preference_widget_master_switch;
    }

    @SuppressLint({ "CutPasteId", "ClickableViewAccessibility" })
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final View switchWidget = holder.findViewById(R.id.switchWidget);
        if (switchWidget != null) {
            switchWidget.setVisibility(isDisabledByAdmin() ? View.GONE : View.VISIBLE);
            switchWidget.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSwitch != null && !mSwitch.isEnabled()) {
                        return;
                    }
                    setChecked(!mChecked);
                    if (!callChangeListener(mChecked)) {
                        setChecked(!mChecked);
                    } else {
                        persistBoolean(mChecked);
                    }
                }
            });

            // Consumes move events to ignore drag actions.
            switchWidget.setOnTouchListener((v, event) -> {
                return event.getActionMasked() == MotionEvent.ACTION_MOVE;
            });
        }

        mSwitch = (Switch) holder.findViewById(R.id.switchWidget);
        if (mSwitch != null) {
            mSwitch.setContentDescription(getTitle());
            mSwitch.setChecked(mChecked);
            mSwitch.setEnabled(mEnableSwitch);
        }
    }

    public boolean isChecked() {
        return mSwitch != null && mChecked;
    }

    public void setChecked(boolean checked) {
        // Always set checked the first time; don't assume the field's default of false.
        final boolean changed = mChecked != checked;
        if (changed || !mCheckedSet) {
            mChecked = checked;
            mCheckedSet = true;
            if (mSwitch != null) {
                mSwitch.setChecked(checked);
            }
        }
    }

    public void setSwitchEnabled(boolean enabled) {
        mEnableSwitch = enabled;
        if (mSwitch != null) {
            mSwitch.setEnabled(enabled);
        }
    }

    final public boolean isDisabledByAdmin() {
        return false;
    }

    public Switch getSwitch() {
        return mSwitch;
    }

    @Override
    protected boolean shouldHideSecondTarget() {
        return getSecondTargetResId() == 0;
    }
}
