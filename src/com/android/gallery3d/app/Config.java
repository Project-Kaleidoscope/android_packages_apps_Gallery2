/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.gallery3d.app;

import android.content.Context;
import android.content.res.Resources;

import com.android.gallery3d.ui.AlbumSetSlotRenderer;
import com.android.gallery3d.ui.AlbumSlotRenderer;
import com.android.gallery3d.ui.SlotView;
import com.android.gallery3d.ui.TimeLineSlotRenderer;
import com.android.gallery3d.ui.TimeLineSlotView;

import org.codeaurora.gallery.R;

final class Config {
    public static class AlbumSetPage {
        private static AlbumSetPage sInstance;

        public SlotView.Spec slotViewSpec;
        public AlbumSetSlotRenderer.LabelSpec labelSpec;
        public int paddingTop;
        public int paddingBottom;
        public int placeholderColor;
        public int paddingLeft;
        public int paddingRight;
        public int paddingTopLand;
        public int paddingBottomLand;
        public int paddingLeftLand;
        public int paddingRightLand;

        private AlbumSetPage(Context context) {
            Resources resources = context.getResources();

            placeholderColor = context.getColor(R.color.albumset_placeholder);

            slotViewSpec = new SlotView.Spec();
            //slotViewSpec.rowsLand = resources.getInteger(R.integer.albumset_rows_land);
            //slotViewSpec.rowsPort = resources.getInteger(R.integer.albumset_rows_port);
            slotViewSpec.colsLand = resources.getInteger(R.integer.albumset_cols_land);
            slotViewSpec.colsPort = resources.getInteger(R.integer.albumset_cols_port);
            slotViewSpec.slotGap = resources.getDimensionPixelSize(R.dimen.albumset_slot_gap);
            slotViewSpec.slotGapLand = resources.getDimensionPixelSize(R.dimen.albumset_slot_gap_land);
            slotViewSpec.slotHeightAdditional = 0;
            slotViewSpec.slotWidth = resources.getDimensionPixelSize(R.dimen.slot_width);
            slotViewSpec.slotHeight = resources.getDimensionPixelSize(R.dimen.slot_height);

            paddingTop = resources.getDimensionPixelSize(R.dimen.albumset_padding_top);
            paddingBottom = resources.getDimensionPixelSize(R.dimen.albumset_padding_bottom);
            paddingLeft = resources.getDimensionPixelSize(R.dimen.albumset_padding_left);
            paddingRight = resources.getDimensionPixelSize(R.dimen.albumset_padding_right);

            paddingTopLand = resources.getDimensionPixelSize(R.dimen.albumset_padding_top_land);
            paddingBottomLand = resources.getDimensionPixelSize(R.dimen.albumset_padding_bottom_land);

            paddingLeftLand = resources.getDimensionPixelSize(R.dimen.albumset_padding_left_land);
            paddingRightLand = resources.getDimensionPixelSize(R.dimen.albumset_padding_right_land);

            labelSpec = new AlbumSetSlotRenderer.LabelSpec();
            labelSpec.labelBackgroundHeight = resources.getDimensionPixelSize(R.dimen.albumset_label_background_height);
            /*labelSpec.titleOffset = resources.getDimensionPixelSize(R.dimen.albumset_title_offset);
            labelSpec.countOffset = resources.getDimensionPixelSize(R.dimen.albumset_count_offset);*/
            labelSpec.titleFontSize = resources.getDimensionPixelSize(R.dimen.albumset_title_font_size);
            labelSpec.countFontSize = resources.getDimensionPixelSize(R.dimen.albumset_count_font_size);
            labelSpec.leftMargin = resources.getDimensionPixelSize(R.dimen.albumset_left_margin);
            labelSpec.titleRightMargin = resources.getDimensionPixelSize(R.dimen.albumset_title_right_margin);
            labelSpec.titleLeftMargin = resources.getDimensionPixelSize(R.dimen.albumset_title_left_margin);
            labelSpec.countRightMargin = resources.getDimensionPixelSize(R.dimen.albumset_count_right_margin);
            /*labelSpec.iconSize = resources.getDimensionPixelSize(R.dimen.albumset_icon_size);*/
            labelSpec.backgroundColor = context.getColor(R.color.albumset_label_background);
            labelSpec.titleColor = context.getColor(R.color.albumset_label_title);
            labelSpec.countColor = context.getColor(R.color.albumset_label_count);
        }

        public static synchronized AlbumSetPage get(Context context) {
            if (sInstance == null) {
                sInstance = new AlbumSetPage(context);
            }
            return sInstance;
        }
    }

    public static class AlbumPage {
        private static AlbumPage sInstance;
        public AlbumSlotRenderer.LabelSpec labelSpec;
        public SlotView.Spec slotViewSpec;
        public int placeholderColor;
        public int paddingTop;
        public int paddingBottom;
        public int paddingLeft;
        public int paddingRight;
        public int paddingTopLand;
        public int paddingBottomLand;
        public int paddingLeftLand;
        public int paddingRightLand;

        private AlbumPage(Context context) {
            Resources resources = context.getResources();

            placeholderColor = context.getColor(R.color.album_placeholder);

            slotViewSpec = new SlotView.Spec();
            //slotViewSpec.rowsLand = resources.getInteger(R.integer.album_rows_land);
            //slotViewSpec.rowsPort = resources.getInteger(R.integer.album_rows_port);
            slotViewSpec.colsLand = resources.getInteger(R.integer.album_cols_land);
            slotViewSpec.colsPort = resources.getInteger(R.integer.album_cols_port);
            slotViewSpec.slotWidth = resources.getDimensionPixelSize(R.dimen.slot_width_album);
            slotViewSpec.slotHeight = resources.getDimensionPixelSize(R.dimen.slot_height_album);
            slotViewSpec.slotGap = resources.getDimensionPixelSize(R.dimen.album_slot_gap);
            slotViewSpec.slotGapLand = resources.getDimensionPixelSize(R.dimen.album_slot_gap_land);

            paddingTop = resources.getDimensionPixelSize(R.dimen.album_padding_top);
            paddingBottom = resources.getDimensionPixelSize(R.dimen.album_padding_bottom);

            paddingLeft = resources.getDimensionPixelSize(R.dimen.album_padding_left);
            paddingRight = resources.getDimensionPixelSize(R.dimen.album_padding_right);

            paddingTopLand = resources.getDimensionPixelSize(R.dimen.album_padding_top_land);
            paddingBottomLand = resources.getDimensionPixelSize(R.dimen.album_padding_bottom_land);

            paddingLeftLand = resources.getDimensionPixelSize(R.dimen.album_padding_left_land);
            paddingRightLand = resources.getDimensionPixelSize(R.dimen.album_padding_right_land);
        }

        public static synchronized AlbumPage get(Context context) {
            if (sInstance == null) {
                sInstance = new AlbumPage(context);
            }
            return sInstance;
        }
    }

    public static class ManageCachePage extends AlbumSetPage {
        private static ManageCachePage sInstance;

        public final int cachePinSize;
        public final int cachePinMargin;

        public ManageCachePage(Context context) {
            super(context);
            Resources resources = context.getResources();
            cachePinSize = resources.getDimensionPixelSize(R.dimen.cache_pin_size);
            cachePinMargin = resources.getDimensionPixelSize(R.dimen.cache_pin_margin);
        }

        public static synchronized ManageCachePage get(Context context) {
            if (sInstance == null) {
                sInstance = new ManageCachePage(context);
            }
            return sInstance;
        }
    }

    public static class AlbumPageList {
        private static AlbumPageList sInstance;

        public SlotView.Spec slotViewSpec;
        public AlbumSlotRenderer.LabelSpec labelSpec;
        public int paddingTop;
        public int paddingBottom;
        public int paddingLeft;
        public int paddingRight;
        public int placeholderColor;

        private AlbumPageList(Context context) {
            Resources resources = context.getResources();

            placeholderColor = context.getColor(R.color.album_placeholder);

            slotViewSpec = new SlotView.Spec();
            slotViewSpec.slotHeight = resources.getDimensionPixelSize(R.dimen.slot_height_albumlist);
            slotViewSpec.slotGap = resources.getDimensionPixelSize(R.dimen.albumlist_slot_gap);
            slotViewSpec.slotGapLand = resources.getDimensionPixelSize(R.dimen.albumlist_slot_gap);
            paddingTop = resources.getDimensionPixelSize(R.dimen.albumlist_padding_top);
            paddingBottom = resources.getDimensionPixelSize(R.dimen.album_padding_bottom);

            paddingLeft = resources.getDimensionPixelSize(R.dimen.albumlist_left_margin);
            paddingRight = resources.getDimensionPixelSize(R.dimen.album_padding_right);
            labelSpec = new AlbumSlotRenderer.LabelSpec();
            labelSpec.labelBackgroundHeight = resources.getDimensionPixelSize(R.dimen.albumlist_label_background_height);
            labelSpec.titleFontSize = resources.getDimensionPixelSize(R.dimen.albumset_title_font_size);
            labelSpec.leftMargin = resources.getDimensionPixelSize(R.dimen.albumlist_left_margin);
            labelSpec.titleLeftMargin = resources.getDimensionPixelSize(R.dimen.albumlist_title_margin);
            labelSpec.iconSize = resources.getDimensionPixelSize(R.dimen.albumlist_thumb_size);
            labelSpec.backgroundColor = context.getColor(R.color.albumset_label_background);
            labelSpec.titleColor = context.getColor(R.color.albumlist_label_title);
        }

        public static synchronized AlbumPageList get(Context context) {
            if (sInstance == null) {
                sInstance = new AlbumPageList(context);
            }
            return sInstance;
        }
    }

    public static class TimeLinePage {
        private static TimeLinePage sInstance;

        public TimeLineSlotView.Spec slotViewSpec;
        public TimeLineSlotRenderer.LabelSpec labelSpec;
        public int placeholderColor;

        private TimeLinePage(Context context) {
            Resources resources = context.getResources();

            placeholderColor = context.getColor(R.color.album_placeholder);

            slotViewSpec = new TimeLineSlotView.Spec();
            slotViewSpec.colsLand = resources.getInteger(R.integer.album_cols_land);
            slotViewSpec.colsPort = resources.getInteger(R.integer.album_cols_port);
            slotViewSpec.slotGapPort = resources.getDimensionPixelSize(R.dimen.timeline_port_slot_gap);
            slotViewSpec.slotGapLand = resources.getDimensionPixelSize(R.dimen.timeline_land_slot_gap);
            slotViewSpec.titleHeight = resources.getDimensionPixelSize(R.dimen.timeline_title_height);

            labelSpec = new TimeLineSlotRenderer.LabelSpec();

            labelSpec.timeLineTitleHeight = resources.getDimensionPixelSize(R.dimen.timeline_title_height);
            labelSpec.timeLineTitleFontSize = resources.getDimensionPixelSize(R.dimen.timeline_title_font_size);
            labelSpec.timeLineTitleTextColor = context.getColor(R.color.timeline_title_text_color);
            labelSpec.timeLineNumberTextColor = context.getColor(R.color.timeline_title_number_text_color);
            labelSpec.timeLineTitleBackgroundColor = context.getColor(R.color.timeline_title_background_color);
        }

        public static synchronized TimeLinePage get(Context context) {
            if (sInstance == null) {
                sInstance = new TimeLinePage(context);
            }
            return sInstance;
        }
    }
}

