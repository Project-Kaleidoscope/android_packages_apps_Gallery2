/*
 * Copyright (c) 2016, The Linux Foundation. All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *     * Neither the name of The Linux Foundation nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.android.gallery3d.filtershow.category;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.editors.EditorCrop;

import org.codeaurora.gallery.R;

public class EditorCropPanel extends BasicGeometryPanel {
    private final int[] mCropAspectIds = {
            R.id.crop_menu_none,
            R.id.crop_menu_original,
            R.id.crop_menu_1to1
    };
    private final int[] mCropDrawableIds = {
            R.drawable.crop_free_background,
            R.drawable.crop_original_background,
            R.drawable.crop_one_background
    };
    private final int[] mCropTextIds = {
            R.string.aspectNone_effect,
            R.string.aspectOriginal_effect,
            R.string.aspect1to1_effect
    };
    private EditorCrop mEditorCrop;
    private int mSelectPosition = 0;
    private final View.OnClickListener mOnClickListener = view -> {
        int index = (int) view.getTag();
        changeSelection(index);
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FilterShowActivity filterShowActivity = (FilterShowActivity) context;
        if (filterShowActivity.isReloadByConfigurationChanged()) {
            mSelectPosition = filterShowActivity.getEditorCropButtonSelect();
        }
        mEditorCrop = (EditorCrop) filterShowActivity.getEditor(EditorCrop.ID);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isLandscape()) {
            mEditorName.setText(R.string.crop);
            mBottomPanel.setVisibility(View.VISIBLE);
        }
        mMainView.setBackgroundColor(requireContext().getColor(R.color.edit_actionbar_background));

        mBottomPanel.setVisibility(View.VISIBLE);

        if (mEditorCrop != null) {
            mEditorCrop.reflectCurrentFilter();
        }

        highlightIndex(mSelectPosition);

        final FilterShowActivity activity = (FilterShowActivity) requireActivity();
        mApplyButton.setOnClickListener(view1 -> {
            if (mEditorCrop != null) {
                mEditorCrop.finalApplyCalled();
            }
            activity.backToMain();
            activity.setActionBar();
        });
        mExitButton.setOnClickListener(view1 -> {
            activity.cancelCurrentFilter();
            activity.backToMain();
            activity.setActionBar();
        });
    }

    @Override
    protected void initButtons() {
        super.initButtons();
        int size = mButtons.length;
        for (int i = 0; i < size; i++) {
            ImageButton view = mButtons[i];
            view.setImageDrawable(ResourcesCompat.getDrawable(getResources(), mCropDrawableIds[i], null));
            // ues tag to store index.
            view.setTag(i);
            view.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    protected void initTexts() {
        super.initTexts();
        int size = mTextViews.length;
        for (int i = 0; i < size; i++) {
            TextView view = mTextViews[i];
            view.setText(mCropTextIds[i]);
        }
    }

    private void changeSelection(int index) {
        if (index >= 0 && index < mButtons.length) {
            mSelectPosition = index;
            if (mEditorCrop != null) {
                mEditorCrop.changeCropAspect(mCropAspectIds[index]);
            }
            highlightIndex(index);
        }
    }

    private void highlightIndex(int index) {
        int size = mButtons.length;
        for (int i = 0; i < size; i++) {
            View view = mButtons[i];
            view.setSelected(index == i);
        }
        size = mTextViews.length;
        for (int i = 0; i < size; i++) {
            TextView view = mTextViews[i];
            view.setTextColor(index == i ?
                    getResources().getColor(R.color.crop_text_color, null) :
                    Color.WHITE);
        }
    }

    @Override
    public void onDetach() {
        if (mEditorCrop != null) {
            mEditorCrop.detach();
        }
        super.onDetach();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FilterShowActivity activity = (FilterShowActivity) requireActivity();
        activity.saveEditorCropState(mSelectPosition);
    }
}
