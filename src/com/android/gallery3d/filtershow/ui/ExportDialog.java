/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.android.gallery3d.filtershow.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.imageshow.MasterImage;
import com.android.gallery3d.filtershow.pipeline.ImagePreset;
import com.android.gallery3d.filtershow.pipeline.ProcessingService;
import com.android.gallery3d.filtershow.tools.SaveImage;
import com.android.gallery3d.ui.BaseDialogFragment;

import org.codeaurora.gallery.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ExportDialog extends BaseDialogFragment implements SeekBar.OnSeekBarChangeListener {
    SeekBar mSeekBar;
    TextView mSeekVal;
    EditText mWidthText;
    EditText mHeightText;
    TextView mEstimatedSize;
    int mQuality = 95;
    int mExportWidth = 0;
    int mExportHeight = 0;
    Rect mOriginalBounds;
    int mCompressedSize;
    Rect mCompressedBounds;
    float mExportCompressionMargin = 1.1f;
    float mRatio;
    String mSliderLabel;
    boolean mEditing = false;
    Handler mHandler;
    int mUpdateDelay = 1000;
    Runnable mUpdateRunnable = () -> {
        updateCompressionFactor();
        updateSize();
    };

    private class Watcher implements TextWatcher {
        private final EditText mEditText;

        Watcher(EditText text) {
            mEditText = text;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            textChanged(mEditText);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mHandler = new Handler(getActivity().getMainLooper());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = initCustomLayout();
        builder.setView(view);
        builder.setTitle(R.string.export_flattened);
        builder.setPositiveButton(R.string.done, (dialog, id) -> {
            FilterShowActivity activity = (FilterShowActivity) getActivity();
            Uri sourceUri = MasterImage.getImage().getUri();
            File dest = SaveImage.getNewFile(activity, activity.getSelectedImageUri());
            float scaleFactor = mExportWidth / (mOriginalBounds == null ? 1f :
                    (float) mOriginalBounds.width());
            if (!activity.isWaterMarked()) {
                Intent processIntent = ProcessingService.getSaveIntent(activity,
                        MasterImage.getImage().getPreset(), dest,
                        activity.getSelectedImageUri(), sourceUri, true,
                        mSeekBar.getProgress(), scaleFactor, false, -1);
                activity.startService(processIntent);
            } else {
                activity.getSaveWaterMark().saveImage(activity,
                        MasterImage.getImage().getHighresImage(),
                        activity.getSelectedImageUri(), null, mSeekBar.getProgress(),
                        scaleFactor, true);
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        updateCompressionFactor();
        updateSize();
        return builder.create();
    }

    private View initCustomLayout() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filtershow_export_dialog, null);
        mSeekBar = view.findViewById(R.id.qualitySeekBar);
        mSeekVal = view.findViewById(R.id.qualityTextView);
        mSliderLabel = getString(R.string.quality) + ": ";
        mSeekBar.setProgress(mQuality);
        mSeekVal.setText(mSliderLabel + mSeekBar.getProgress());
        mSeekBar.setOnSeekBarChangeListener(this);
        mWidthText = view.findViewById(R.id.editableWidth);
        mHeightText = view.findViewById(R.id.editableHeight);
        mEstimatedSize = view.findViewById(R.id.estimadedSize);

        mOriginalBounds = MasterImage.getImage().getOriginalBounds();
        ImagePreset preset = MasterImage.getImage().getPreset();
        if (mOriginalBounds == null || preset == null) return null;
        mOriginalBounds = preset.finalGeometryRect(mOriginalBounds.width(),
                mOriginalBounds.height());
        if (preset != null) {
            mOriginalBounds = preset.finalGeometryRect(mOriginalBounds.width(),
                    mOriginalBounds.height());
        }
        if (mOriginalBounds == null) {
            return null;
        }
        mRatio = mOriginalBounds.width() / (float) mOriginalBounds.height();
        mWidthText.setText(String.valueOf(mOriginalBounds.width()));
        mHeightText.setText(String.valueOf(mOriginalBounds.height()));
        mExportWidth = mOriginalBounds.width();
        mExportHeight = mOriginalBounds.height();
        mWidthText.addTextChangedListener(new Watcher(mWidthText));
        mHeightText.addTextChangedListener(new Watcher(mHeightText));
        return view;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Do nothing
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSeekVal.setText(mSliderLabel + progress);
        mQuality = mSeekBar.getProgress();
        scheduleUpdateCompressionFactor();
    }

    private void scheduleUpdateCompressionFactor() {
        mHandler.removeCallbacks(mUpdateRunnable);
        mHandler.postDelayed(mUpdateRunnable, mUpdateDelay);
    }

    public void updateCompressionFactor() {
        Bitmap bitmap = MasterImage.getImage().getFilteredImage();
        if (bitmap == null) {
            return;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, mQuality, out);
        mCompressedSize = out.size();
        mCompressedBounds = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public void updateSize() {
        if (mCompressedBounds == null) {
            return;
        }
        // This is a rough estimate of the final save size. There's some loose correlation
        // between a compressed jpeg and a larger version of it in function of the image
        // area. Not a perfect estimate by far.
        float originalArea = mCompressedBounds.width() * mCompressedBounds.height();
        float newArea = mExportWidth * mExportHeight;
        float factor = originalArea / (float) mCompressedSize;
        float compressedSize = newArea / factor;
        compressedSize *= mExportCompressionMargin;
        float size = compressedSize / 1024.f / 1024.f;
        size = ((int) (size * 100)) / 100f;
        String estimatedSize = size + " MB";
        mEstimatedSize.setText(estimatedSize);
    }

    private void textChanged(EditText text) {
        if (mEditing) {
            return;
        }
        mEditing = true;
        int width = 1;
        int height = 1;
        if (text.getId() == R.id.editableWidth) {
            if (mWidthText.getText() != null) {
                String value = String.valueOf(mWidthText.getText());
                if (value.length() > 0) {
                    try {
                        width = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        width = Integer.MAX_VALUE;
                    }
                    if (width > mOriginalBounds.width()) {
                        width = mOriginalBounds.width();
                    }
                    if (width <= 0) {
                        width = (int) Math.ceil(mRatio);
                    }
                    height = (int) (width / mRatio);
                }
                mWidthText.setText(String.valueOf(width));
                mHeightText.setText(String.valueOf(height));
            }
        } else if (text.getId() == R.id.editableHeight) {
            if (mHeightText.getText() != null) {
                String value = String.valueOf(mHeightText.getText());
                if (value.length() > 0) {
                    try {
                        height = Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        height = Integer.MAX_VALUE;
                    }
                    if (height > mOriginalBounds.height()) {
                        height = mOriginalBounds.height();
                    }
                    if (height <= 0) {
                        height = 1;
                    }
                    width = (int) (height * mRatio);
                }
                mHeightText.setText(String.valueOf(height));
                mWidthText.setText(String.valueOf(width));
            }
        }
        mExportWidth = width;
        mExportHeight = height;
        updateSize();
        mEditing = false;
    }

}
