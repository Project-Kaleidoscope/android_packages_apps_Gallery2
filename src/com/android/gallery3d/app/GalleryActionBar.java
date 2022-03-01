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

package com.android.gallery3d.app;

import android.app.ActionBar;
import android.app.ActionBar.OnMenuVisibilityListener;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import org.codeaurora.gallery.R;

import java.util.ArrayList;

public class GalleryActionBar {
    @SuppressWarnings("unused")
    private static final String TAG = "GalleryActionBar";

    private ClusterRunner mClusterRunner;
    private CharSequence[] mTitles;
    private ArrayList<Integer> mActions;
    private Context mContext;
    private LayoutInflater mInflater;
    private AbstractGalleryActivity mActivity;
    private ActionBar mActionBar;
    private int mCurrentIndex;
    private OnAlbumModeSelectedListener mAlbumModeListener;
    private int mLastAlbumModeSelected;
    private CharSequence[] mAlbumModes;
    public static final int ALBUM_FILMSTRIP_MODE_SELECTED = 0;
    public static final int ALBUM_GRID_MODE_SELECTED = 1;

    public interface ClusterRunner {

        void doCluster(int id);

    }

    public interface OnAlbumModeSelectedListener {

        void onAlbumModeSelected(int mode);

    }

    private static class ActionItem {
        public int action;
        public boolean enabled;
        public boolean visible;
        public int spinnerTitle;
        public int dialogTitle;
        public int clusterBy;

        public ActionItem(int action, boolean applied, boolean enabled, int title, int clusterBy) {
            this(action, applied, enabled, title, title, clusterBy);
        }

        public ActionItem(int action, boolean applied, boolean enabled, int spinnerTitle, int dialogTitle, int clusterBy) {
            this.action = action;
            this.enabled = enabled;
            this.spinnerTitle = spinnerTitle;
            this.dialogTitle = dialogTitle;
            this.clusterBy = clusterBy;
            this.visible = true;
        }
    }

    private static final ActionItem[] sClusterItems = new ActionItem[] {
        new ActionItem(FilterUtils.CLUSTER_BY_ALBUM, true, false, R.string.albums, R.string.group_by_album),
        new ActionItem(FilterUtils.CLUSTER_BY_LOCATION, true, false, R.string.locations, R.string.location, R.string.group_by_location),
        new ActionItem(FilterUtils.CLUSTER_BY_TIME, true, false, R.string.times, R.string.time, R.string.group_by_time),
        new ActionItem(FilterUtils.CLUSTER_BY_FACE, true, false, R.string.people, R.string.group_by_faces),
        new ActionItem(FilterUtils.CLUSTER_BY_TAG, true, false, R.string.tags, R.string.group_by_tags)
    };

    public static String getClusterByTypeString(Context context, int type) {
        for (ActionItem item : sClusterItems) {
            if (item.action == type) {
                return context.getString(item.clusterBy);
            }
        }
        return null;
    }

    public GalleryActionBar(AbstractGalleryActivity activity) {
        mActionBar = activity.getActionBar();
        mContext = activity.getAndroidContext();
        mActivity = activity;
        mInflater = mActivity.getLayoutInflater();
        mCurrentIndex = 0;
    }

    private void createDialogData() {
        ArrayList<CharSequence> titles = new ArrayList<>();
        mActions = new ArrayList<>();
        for (ActionItem item : sClusterItems) {
            if (item.enabled && item.visible) {
                titles.add(mContext.getString(item.dialogTitle));
                mActions.add(item.action);
            }
        }
        mTitles = new CharSequence[titles.size()];
        titles.toArray(mTitles);
    }

    public int getHeight() {
        return mActionBar != null ? mActionBar.getHeight() : 0;
    }

    private void setHomeButtonEnabled(boolean enabled) {
        if (mActionBar != null) mActionBar.setHomeButtonEnabled(enabled);
    }

    public void setDisplayOptions(boolean displayHomeAsUp, boolean showTitle) {
        if (mActionBar == null) return;
        int options = 0;
        if (displayHomeAsUp) options |= ActionBar.DISPLAY_HOME_AS_UP;
        if (showTitle) options |= ActionBar.DISPLAY_SHOW_TITLE;

        mActionBar.setDisplayOptions(options,
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE);
        mActionBar.setHomeButtonEnabled(displayHomeAsUp);
    }

    public void setDisplayHome(boolean displayHome, boolean showTitle) {
        if (mActionBar == null) return;
        int options = 0;
        if (displayHome) options |= ActionBar.DISPLAY_SHOW_HOME;
        if (showTitle) options |= ActionBar.DISPLAY_SHOW_TITLE;

        mActionBar.setDisplayOptions(options,
                ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        mActionBar.setHomeButtonEnabled(displayHome);
    }

    public void setTitle(String title) {
        if (mActionBar != null) mActionBar.setTitle(title);
    }

    public void setTitle(int titleId) {
        if (mActionBar != null) {
            mActionBar.setTitle(mContext.getString(titleId));
        }
    }

    public void setSubtitle(String title) {
        if (mActionBar != null) mActionBar.setSubtitle(title);
    }

    public void show() {
        if (mActionBar != null) mActionBar.show();
    }

    public void hide() {
        if (mActionBar != null) mActionBar.hide();
    }

    public void addOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
        if (mActionBar != null) mActionBar.addOnMenuVisibilityListener(listener);
    }

    public void removeOnMenuVisibilityListener(OnMenuVisibilityListener listener) {
        if (mActionBar != null) mActionBar.removeOnMenuVisibilityListener(listener);
    }

    private Menu mActionBarMenu;
    private Intent mSharePanoramaIntent;
    private Intent mShareIntent;

    public void createActionBarMenu(int menuRes, Menu menu) {
        mActivity.getMenuInflater().inflate(menuRes, menu);
        mActionBarMenu = menu;

        MenuItem item = menu.findItem(R.id.action_share_panorama);
        if (item != null) {
            item.setOnMenuItemClickListener(item1 -> {
                if (mSharePanoramaIntent != null) {
                    Intent intent = Intent.createChooser(mSharePanoramaIntent, null);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
                return true;
            });
        }

        item = menu.findItem(R.id.action_share);
        if (item != null) {
            item.setOnMenuItemClickListener(item1 -> {
                if (mShareIntent != null) {
                    Intent intent = Intent.createChooser(mShareIntent, null);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
                return true;
            });
        }
    }

    public Menu getMenu() {
        return mActionBarMenu;
    }

    public void setShareIntents(Intent sharePanoramaIntent, Intent shareIntent) {
        mSharePanoramaIntent = sharePanoramaIntent;
        mShareIntent = shareIntent;
    }

}
