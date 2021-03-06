/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.gallery3d.filtershow.filters;

import android.renderscript.Element;
import android.renderscript.ScriptIntrinsicConvolve3x3;

import com.android.gallery3d.filtershow.editors.BasicEditor;

import org.codeaurora.gallery.R;

public class ImageFilterSharpen extends ImageFilterRS {
    private static final String SERIALIZATION_NAME = "SHARPEN";
    private static final String TAG = "ImageFilterSharpen";
    private ScriptIntrinsicConvolve3x3 mScript;

    private FilterBasicRepresentation mParameters;

    public ImageFilterSharpen() {
        mName = "Sharpen";
    }

    public FilterRepresentation getDefaultRepresentation() {
        FilterRepresentation representation = new FilterBasicRepresentation("Sharpen", -100, 0, 100);
        representation.setSerializationName(SERIALIZATION_NAME);
        representation.setShowParameterValue(true);
        representation.setFilterClass(ImageFilterSharpen.class);
        representation.setTextId(R.string.sharpness);
        representation.setEditorId(BasicEditor.ID);
        representation.setSupportsPartialRendering(true);
        return representation;
    }

    public void useRepresentation(FilterRepresentation representation) {
        mParameters = (FilterBasicRepresentation) representation;
    }

    @Override
    protected void resetAllocations() {
        // nothing to do
    }

    @Override
    public void resetScripts() {
        if (mScript != null) {
            mScript.destroy();
            mScript = null;
        }
    }

    @Override
    protected void createFilter(android.content.res.Resources res, float scaleFactor,
                                int quality) {
        if (mScript == null) {
            mScript = ScriptIntrinsicConvolve3x3.create(getRenderScriptContext(),
                    Element.RGBA_8888(getRenderScriptContext()));
        }
    }

    private void computeKernel() {
        float p1 = mParameters.getValue();
        float value = p1 / 100.0f;
        float[] f = new float[9];
        f[0] = -value;
        f[1] = -value;
        f[2] = -value;
        f[3] = -value;
        f[4] = 8 * value + 1;
        f[5] = -value;
        f[6] = -value;
        f[7] = -value;
        f[8] = -value;
        mScript.setCoefficients(f);
    }

    @Override
    protected void bindScriptValues() {
    }

    @Override
    protected void runFilter() {
        if (mParameters == null) {
            return;
        }
        computeKernel();
        mScript.setInput(getInPixelsAllocation());
        mScript.forEach(getOutPixelsAllocation());
    }

}
