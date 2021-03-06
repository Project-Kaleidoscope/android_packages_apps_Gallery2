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

package com.android.gallery3d.filtershow.filters;

import android.content.res.Resources;
import android.util.JsonReader;
import android.util.JsonWriter;

import androidx.annotation.NonNull;

import com.android.gallery3d.filtershow.editors.BasicEditor;

import java.io.IOException;
import java.util.ArrayList;

public class FilterRepresentation {
    public static final byte TYPE_BORDER = 1;
    public static final byte TYPE_FX = 2;
    public static final byte TYPE_WBALANCE = 3;
    public static final byte TYPE_VIGNETTE = 4;
    public static final byte TYPE_NORMAL = 5;
    public static final byte TYPE_TINYPLANET = 6;
    public static final byte TYPE_GEOMETRY = 7;
    public static final byte TYPE_MAKEUP = 8;
    public static final byte TYPE_DUALCAM = 9;
    public static final byte TYPE_TRUEPORTRAIT = 10;
    public static final byte TYPE_PRESETFILTER = 11;
    public static final byte TYPE_WATERMARK_CATEGORY = 12;
    public static final byte TYPE_WATERMARK = 13;
    protected static final String NAME_TAG = "Name";
    private static final String TAG = "FilterRepresentation";
    private static final boolean DEBUG = false;
    private String mName;
    private int mPriority = TYPE_NORMAL;
    private Class<?> mFilterClass;
    private boolean mSupportsPartialRendering = false;
    private int mTextId = 0;
    private int mEditorId = BasicEditor.ID;
    private final int mButtonId = 0;
    private int mOverlayId = 0;
    private int mColorId = 0;
    private boolean mOverlayOnly = false;
    private boolean mShowParameterValue = true;
    private boolean mIsBooleanFilter = false;
    private boolean isSvgOverlay = false;
    private Resources.Theme currentTheme;
    private String mSerializationName;

    public FilterRepresentation(String name) {
        mName = name;
        mSerializationName = name.toUpperCase();
    }

    public FilterRepresentation copy() {
        FilterRepresentation representation = new FilterRepresentation(mName);
        representation.useParametersFrom(this);
        if (getFilterType() == TYPE_WATERMARK) {
            representation.setSerializationName(getSerializationName());
            representation.setFilterType(TYPE_WATERMARK);
        }
        return representation;
    }

    protected void copyAllParameters(FilterRepresentation representation) {
        representation.setName(getName());
        representation.setFilterClass(getFilterClass());
        representation.setFilterType(getFilterType());
        representation.setSupportsPartialRendering(supportsPartialRendering());
        representation.setTextId(getTextId());
        representation.setEditorId(getEditorId());
        representation.setOverlayId(getOverlayId());
        representation.setOverlayOnly(getOverlayOnly());
        representation.setShowParameterValue(showParameterValue());
        representation.mSerializationName = mSerializationName;
        representation.setIsBooleanFilter(isBooleanFilter());
    }

    public boolean equals(FilterRepresentation representation) {
        if (representation == null) {
            return false;
        }
        return representation.mFilterClass == mFilterClass
                && representation.mName.equalsIgnoreCase(mName)
                && representation.mPriority == mPriority
                // TODO: After we enable partial rendering, we can switch back
                // to use member variable here.
                && representation.supportsPartialRendering() == supportsPartialRendering()
                && representation.mTextId == mTextId
                && representation.mEditorId == mEditorId
                && representation.mButtonId == mButtonId
                && representation.mOverlayId == mOverlayId
                && representation.mOverlayOnly == mOverlayOnly
                && representation.mShowParameterValue == mShowParameterValue
                && representation.mIsBooleanFilter == mIsBooleanFilter
                && representation.mColorId == mColorId;
    }

    public int getColorId() {
        return mColorId;
    }

    public void setColorId(int colorId) {
        mColorId = colorId;
    }

    public boolean isBooleanFilter() {
        return mIsBooleanFilter;
    }

    public void setIsBooleanFilter(boolean value) {
        mIsBooleanFilter = value;
    }

    @NonNull
    @Override
    public String toString() {
        return mName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSerializationName() {
        return mSerializationName;
    }

    public void setSerializationName(String sname) {
        mSerializationName = sname;
    }

    public int getFilterType() {
        return mPriority;
    }

    public void setFilterType(int priority) {
        mPriority = priority;
    }

    public boolean isNil() {
        return false;
    }

    public boolean supportsPartialRendering() {
        return mSupportsPartialRendering;
    }

    public void setSupportsPartialRendering(boolean value) {
        mSupportsPartialRendering = value;
    }

    public void useParametersFrom(FilterRepresentation a) {
    }

    public boolean allowsSingleInstanceOnly() {
        return false;
    }

    public Class<?> getFilterClass() {
        return mFilterClass;
    }

    public void setFilterClass(Class<?> filterClass) {
        mFilterClass = filterClass;
    }

    // This same() function is different from equals(), basically it checks
    // whether 2 FilterRepresentations are the same type. It doesn't care about
    // the values.
    public boolean same(FilterRepresentation b) {
        if (b == null) {
            return false;
        }
        return getFilterClass() == b.getFilterClass();
    }

    public int getTextId() {
        return mTextId;
    }

    public void setTextId(int textId) {
        mTextId = textId;
    }

    public int getOverlayId() {
        return mOverlayId;
    }

    public void setOverlayId(int overlayId) {
        mOverlayId = overlayId;
    }

    public void setOverlayId(int overlayId, Resources.Theme theme) {
        mOverlayId = overlayId;
        currentTheme = theme;
    }

    public Resources.Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(Resources.Theme theme) {
        currentTheme = theme;
    }

    public boolean getOverlayOnly() {
        return mOverlayOnly;
    }

    public void setOverlayOnly(boolean value) {
        mOverlayOnly = value;
    }

    public boolean isSvgOverlay() {
        return isSvgOverlay;
    }

    public void setSvgOverlay(boolean svgOverlay) {
        isSvgOverlay = svgOverlay;
    }

    final public int getEditorId() {
        return mEditorId;
    }

    public void setEditorId(int editorId) {
        mEditorId = editorId;
    }

    public int[] getEditorIds() {
        return new int[]{mEditorId};
    }

    public boolean showParameterValue() {
        return mShowParameterValue;
    }

    public void setShowParameterValue(boolean showParameterValue) {
        mShowParameterValue = showParameterValue;
    }

    public String getStateRepresentation() {
        return "";
    }

    /**
     * Method must "beginObject()" add its info and "endObject()"
     *
     * @param writer
     * @throws IOException
     */
    public void serializeRepresentation(JsonWriter writer) throws IOException {
        writer.beginObject();
        {
            String[][] rep = serializeRepresentation();
            for (String[] strings : rep) {
                writer.name(strings[0]);
                writer.value(strings[1]);
            }
        }
        writer.endObject();
    }

    // this is the old way of doing this and will be removed soon
    public String[][] serializeRepresentation() {
        return new String[][]{{NAME_TAG, getName()}};
    }

    public void deSerializeRepresentation(JsonReader reader) throws IOException {
        ArrayList<String[]> al = new ArrayList<>();
        reader.beginObject();
        while (reader.hasNext()) {
            String[] kv = {reader.nextName(), reader.nextString()};
            al.add(kv);

        }
        reader.endObject();
        String[][] oldFormat = al.toArray(new String[al.size()][]);

        deSerializeRepresentation(oldFormat);
    }

    // this is the old way of doing this and will be removed soon
    public void deSerializeRepresentation(String[][] rep) {
        for (String[] strings : rep) {
            if (NAME_TAG.equals(strings[0])) {
                mName = strings[1];
                break;
            }
        }
    }

    // Override this in subclasses
    public int getStyle() {
        return -1;
    }

    public boolean canMergeWith(FilterRepresentation representation) {
        return getFilterType() == FilterRepresentation.TYPE_GEOMETRY
                && representation.getFilterType() == FilterRepresentation.TYPE_GEOMETRY;
    }
}
