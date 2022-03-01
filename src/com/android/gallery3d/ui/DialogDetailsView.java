/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.gallery3d.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.data.MediaDetails;
import com.android.gallery3d.exif.ExifInterface;
import com.android.gallery3d.ui.DetailsAddressResolver.AddressResolvingListener;
import com.android.gallery3d.ui.DetailsHelper.CloseListener;
import com.android.gallery3d.ui.DetailsHelper.DetailsSource;
import com.android.gallery3d.ui.DetailsHelper.DetailsViewContainer;
import com.android.gallery3d.ui.DetailsHelper.ResolutionResolvingListener;

import org.codeaurora.gallery.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

public class DialogDetailsView implements DetailsViewContainer {
    @SuppressWarnings("unused")
    private static final String TAG = "DialogDetailsView";

    private final AbstractGalleryActivity mActivity;
    private DetailsAdapter mAdapter;
    private MediaDetails mDetails;
    private final DetailsSource mSource;
    private int mIndex;
    private AlertDialog mDialog;
    private CloseListener mListener;

    public DialogDetailsView(AbstractGalleryActivity activity, DetailsSource source) {
        mActivity = activity;
        mSource = source;
    }

    @Override
    public void show() {
        reloadDetails();
        mDialog.show();
    }

    @Override
    public void hide() {
        mDialog.hide();
    }

    @Override
    public void reloadDetails() {
        int index = mSource.setIndex();
        if (index == -1) return;
        MediaDetails details = mSource.getDetails();
        if (details != null) {
            if (mIndex == index && mDetails == details) return;
            mIndex = index;
            mDetails = details;
            setDetails(details);
        }
    }

    private void setDetails(MediaDetails details) {
        mAdapter = new DetailsAdapter(details);
        ListView detailsList = (ListView) LayoutInflater.from(mActivity.getAndroidContext()).inflate(
                R.layout.details_list, null, false);
        detailsList.setAdapter(mAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(R.string.details);
        builder.setView(detailsList);
        builder.setPositiveButton(R.string.close, (dialog, whichButton) -> mDialog.dismiss());

        mDialog = builder.create();
        mDialog.setOnDismissListener(dialog -> {
            if (mListener != null) {
                mListener.onClose();
            }
        });
        mDialog.show();

        builder.setView(detailsList);
        builder.setPositiveButton(R.string.close,
                (DialogInterface.OnClickListener) (dialog, whichButton) -> mDialog.dismiss());
    }

    public static class Details {

        private final String title;
        private final String details;

        public Details(String title, String details) {
            this.title = title;
            this.details = details;
        }

        public String getTitle() {
            return title;
        }

        public String getDetails() {
            return details;
        }
    }

    private class DetailsAdapter extends BaseAdapter
            implements AddressResolvingListener, ResolutionResolvingListener {
        private final ArrayList<Details> mItems;
        private int mLocationIndex;
        private final Locale mDefaultLocale = Locale.getDefault();
        private final DecimalFormat mDecimalFormat = new DecimalFormat(".####");
        private int mWidthIndex = -1;
        private int mHeightIndex = -1;

        public DetailsAdapter(MediaDetails details) {
            Context context = mActivity.getAndroidContext();
            mItems = new ArrayList<>(details.size());
            mLocationIndex = -1;
            setDetails(context, details);
        }

        private String exifDateToFormatedDate(String exifDt) {
            try {
                Date date = ExifInterface.DATETIME_FORMAT.parse(exifDt);
                return DateFormat.getDateTimeInstance().format(date);
            } catch (ParseException e) {
                return exifDt;
            }
        }

        private void setDetails(Context context, MediaDetails details) {
            boolean resolutionIsValid = true;
            String path = null;
            for (Entry<Integer, Object> detail : details) {
                String value;
                switch (detail.getKey()) {
                    case MediaDetails.INDEX_LOCATION: {
                        double[] latlng = (double[]) detail.getValue();
                        mLocationIndex = mItems.size();
                        value = DetailsHelper.resolveAddress(mActivity, latlng, this);
                        break;
                    }
                    case MediaDetails.INDEX_SIZE: {
                        value = Formatter.formatFileSize(
                                context, (Long) detail.getValue());
                        break;
                    }
                    case MediaDetails.INDEX_WHITE_BALANCE: {
                        value = "1".equals(detail.getValue())
                                ? context.getString(R.string.manual)
                                : context.getString(R.string.auto);
                        break;
                    }
                    case MediaDetails.INDEX_FLASH: {
                        MediaDetails.FlashState flash =
                                (MediaDetails.FlashState) detail.getValue();
                        // TODO: camera doesn't fill in the complete values, show more information
                        // when it is fixed.
                        if (flash.isFlashFired()) {
                            value = context.getString(R.string.flash_on);
                        } else {
                            value = context.getString(R.string.flash_off);
                        }
                        break;
                    }
                    case MediaDetails.INDEX_EXPOSURE_TIME: {
                        value = (String) detail.getValue();
                        double time = Double.parseDouble(value);
                        if (time < 1.0f) {
                            value = String.format(mDefaultLocale, "%d/%d", 1,
                                    (int) (0.5f + 1 / time));
                        } else {
                            int integer = (int) time;
                            time -= integer;
                            value = integer + "''";
                            if (time > 0.0001) {
                                value += String.format(mDefaultLocale, " %d/%d", 1,
                                        (int) (0.5f + 1 / time));
                            }
                        }
                        break;
                    }
                    case MediaDetails.INDEX_WIDTH:
                        mWidthIndex = mItems.size();
                        if (detail.getValue().toString().equalsIgnoreCase("0")) {
                            value = context.getString(R.string.unknown);
                            resolutionIsValid = false;
                        } else {
                            value = toLocalInteger(detail.getValue());
                        }
                        break;
                    case MediaDetails.INDEX_HEIGHT: {
                        mHeightIndex = mItems.size();
                        if (detail.getValue().toString().equalsIgnoreCase("0")) {
                            value = context.getString(R.string.unknown);
                            resolutionIsValid = false;
                        } else {
                            value = toLocalInteger(detail.getValue());
                        }
                        break;
                    }
                    case MediaDetails.INDEX_PATH:
                        // Prepend the new-line as a) paths are usually long, so
                        // the formatting is better and b) an RTL UI will see it
                        // as a separate section and interpret it for what it
                        // is, rather than trying to make it RTL (which messes
                        // up the path).
                        value = detail.getValue().toString();
                        path = value;
                        break;
                    case MediaDetails.INDEX_ISO:
                        value = toLocalNumber(Integer.parseInt((String) detail.getValue()));
                        break;
                    case MediaDetails.INDEX_FOCAL_LENGTH:
                        double focalLength = Double.parseDouble(detail.getValue().toString());
                        value = toLocalNumber(focalLength);
                        break;
                    case MediaDetails.INDEX_ORIENTATION:
                        value = toLocalInteger(detail.getValue());
                        break;
                    case MediaDetails.INDEX_DATETIME_ORIGINAL:
                        value = exifDateToFormatedDate(detail.getValue().toString());
                        break;
                    default: {
                        Object valueObj = detail.getValue();
                        // This shouldn't happen, log its key to help us diagnose the problem.
                        if (valueObj == null) {
//                            Utils.fail("%s's value is Null",
//                                    DetailsHelper.getDetailsName(context, detail.getKey()));
//                            DetailsHelper.getDetailsName(context, detail.getKey()) is null
                            value = "Null";
                        } else {
                            value = valueObj.toString();
                        }
                    }
                }
                int key = detail.getKey();
                if (details.hasUnit(key)) {
                    value = String.format("%s %s", value, context.getString(details.getUnit(key)));
                }
                mItems.add(new Details(DetailsHelper.getDetailsName(context, key), value));
                if (!resolutionIsValid) {
                    DetailsHelper.resolveResolution(path, this);
                }
            }
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mDetails.getDetail(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView titleTextView;
            TextView detailsTextView;
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(mActivity.getAndroidContext()).inflate(
                        R.layout.details, parent, false);
            } else {
                view = convertView;
            }
            titleTextView = view.findViewById(R.id.details_title);
            detailsTextView = view.findViewById(R.id.details_content);
            Details details = mItems.get(position);
            titleTextView.setText(details.getTitle());
            detailsTextView.setText(details.getDetails());
            return view;
        }

        @Override
        public void onAddressAvailable(Details addressDetails) {
            mItems.set(mLocationIndex, addressDetails);
            notifyDataSetChanged();
        }

        @Override
        public void onResolutionAvailable(int width, int height) {
            if (width == 0 || height == 0) return;
            // Update the resolution with the new width and height
            Context context = mActivity.getAndroidContext();
            mItems.set(mWidthIndex, new Details(
                    DetailsHelper.getDetailsName(context, MediaDetails.INDEX_WIDTH), String.valueOf(width)));
            mItems.set(mHeightIndex, new Details(
                    DetailsHelper.getDetailsName(context, MediaDetails.INDEX_HEIGHT), String.valueOf(height)));
            notifyDataSetChanged();
        }

        /**
         * Converts the given integer (given as String or Integer object) to a
         * localized String version.
         */
        private String toLocalInteger(Object valueObj) {
            if (valueObj instanceof Integer) {
                return toLocalNumber((Integer) valueObj);
            } else {
                String value = valueObj.toString();
                try {
                    value = toLocalNumber(Integer.parseInt(value));
                } catch (NumberFormatException ex) {
                    // Just keep the current "value" if we cannot
                    // parse it as a fallback.
                }
                return value;
            }
        }

        /**
         * Converts the given integer to a localized String version.
         */
        private String toLocalNumber(int n) {
            return String.format(mDefaultLocale, "%d", n);
        }

        /**
         * Converts the given double to a localized String version.
         */
        private String toLocalNumber(double n) {
            return mDecimalFormat.format(n);
        }
    }

    @Override
    public void setCloseListener(CloseListener listener) {
        mListener = listener;
    }
}
