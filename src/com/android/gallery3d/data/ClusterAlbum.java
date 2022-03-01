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

package com.android.gallery3d.data;

import java.util.ArrayList;

public class ClusterAlbum extends MediaSet implements ContentListener {
    @SuppressWarnings("unused")
    private static final String TAG = "ClusterAlbum";
    private final int INVALID_COUNT = -1;
    private ArrayList<Path> mPaths = new ArrayList<>();
    private String mName = "";
    private final DataManager mDataManager;
    private final MediaSet mClusterAlbumSet;
    private MediaItem mCover;
    private int mImageCount = INVALID_COUNT;
    private int mVideoCount = INVALID_COUNT;
    private final int mKind;


    private final TimeLineTitleMediaItem mTimelineTitleMediaItem;

    public ClusterAlbum(Path path, DataManager dataManager,
                        MediaSet clusterAlbumSet, int kind) {
        super(path, nextVersionNumber());
        mDataManager = dataManager;
        mClusterAlbumSet = clusterAlbumSet;
        mClusterAlbumSet.addContentListener(this);
        mKind = kind;
        mTimelineTitleMediaItem = new TimeLineTitleMediaItem(path);
    }

    public static ArrayList<MediaItem> getMediaItemFromPath(
            ArrayList<Path> paths, int start, int count,
            DataManager dataManager) {
        if (start >= paths.size() || start < 0) {
            return new ArrayList<>();
        }
        int end = Math.min(start + count, paths.size());
        ArrayList<Path> subset = new ArrayList<>(paths.subList(start, end));
        final MediaItem[] buf = new MediaItem[end - start];
        ItemConsumer consumer = (index, item) -> buf[index] = item;
        dataManager.mapMediaItems(subset, consumer, 0);
        ArrayList<MediaItem> result = new ArrayList<>(end - start);
        for (MediaItem mediaItem : buf) {
            if (mediaItem != null) {
                result.add(mediaItem);
            }
        }
        return result;
    }

    @Override
    public MediaItem getCoverMediaItem() {
        return mCover != null ? mCover : super.getCoverMediaItem();
    }

    public void setCoverMediaItem(MediaItem cover) {
        mCover = cover;
    }

    public ArrayList<Path> getMediaItems() {
        return mPaths;
    }

    void setMediaItems(ArrayList<Path> paths) {
        mPaths = paths;
    }

    @Override
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
        mTimelineTitleMediaItem.setTitle(name);
        /*if (mKind == ClusterSource.CLUSTER_ALBUMSET_TIME) {
            mTimelineTitleMediaItem = new TimeLineTitleMediaItem(name);
        }*/
    }

    @Override
    public int getMediaItemCount() {
        if (MediaSet.isShowAlbumsetTimeTitle()) {
            return mPaths.size() + 1;
        }
        return mPaths.size();
    }

    @Override
    public int getSelectableItemCount() {
        return mPaths.size();
    }

    private void updateItemCounts() {
        setImageItemCount(mImageCount);
        setVideoItemCount(mVideoCount);
    }

    @Override
    public ArrayList<MediaItem> getMediaItem(int start, int count) {
        //return getMediaItemFromPath(mPaths, start, count, mDataManager);
        updateItemCounts();
        if (MediaSet.isShowAlbumsetTimeTitle()) {
            if (mPaths.size() <= 0) return null;
            if (start == 0) {
                ArrayList<MediaItem> mediaItemList = new ArrayList<>(getMediaItemFromPath(mPaths, start, count - 1, mDataManager));
                mediaItemList.add(0, mTimelineTitleMediaItem);
                return mediaItemList;
            } else {
                return getMediaItemFromPath(mPaths, start - 1, count, mDataManager);
            }
        } else {
            return getMediaItemFromPath(mPaths, start, count, mDataManager);
        }
    }

    @Override
    public int getImageItemCount() {
        return mImageCount;
    }

    public void setImageItemCount(int count) {
        mImageCount = count;
        if (mTimelineTitleMediaItem != null && MediaSet.isShowAlbumsetTimeTitle()) {
            mTimelineTitleMediaItem.setImageCount(count);
        }
    }

    @Override
    public int getVideoItemCount() {
        return mVideoCount;
    }

    public void setVideoItemCount(int count) {
        mVideoCount = count;
        if (mTimelineTitleMediaItem != null && MediaSet.isShowAlbumsetTimeTitle()) {
            mTimelineTitleMediaItem.setVideoCount(count);
        }
    }

    @Override
    protected int enumerateMediaItems(ItemConsumer consumer, int startIndex) {
        mDataManager.mapMediaItems(mPaths, consumer, startIndex);
        return mPaths.size();
    }

    @Override
    public int getTotalMediaItemCount() {
        if (MediaSet.isShowAlbumsetTimeTitle()) {
            return mPaths.size() + 1;
        }
        return mPaths.size();
    }

    @Override
    public int getMediaType() {
        // return correct type of Timeline Title.
        if (MediaSet.isShowAlbumsetTimeTitle()) {
            return MEDIA_TYPE_TIMELINE_TITLE;
        }
        return super.getMediaType();
    }

    @Override
    public boolean isLoading() {
        return mClusterAlbumSet.isLoading();
    }

    @Override
    public long reload() {
        long version = mClusterAlbumSet.reload();
        if (version == INVALID_DATA_VERSION) {
            return INVALID_DATA_VERSION;
        }
        if (version > mDataVersion) {
            mDataVersion = nextVersionNumber();
        }
        return mDataVersion;
    }

    @Override
    public void onContentDirty() {
        notifyContentChanged();
    }

    @Override
    public int getSupportedOperations() {
        // Timeline title item doesn't support anything, just its sub objects supported.
        if (MediaSet.isShowAlbumsetTimeTitle()) {
            return 0;
        }
        return SUPPORT_SHARE | SUPPORT_DELETE | SUPPORT_INFO;
    }

    @Override
    public void delete() {
        if ((getSupportedOperations() & MediaObject.SUPPORT_DELETE) == 0) return;
        ItemConsumer consumer = (index, item) -> {
            if ((item.getSupportedOperations() & SUPPORT_DELETE) != 0) {
                item.delete();
            }
        };
        mDataManager.mapMediaItems(mPaths, consumer, 0);
    }

    @Override
    public boolean isLeafAlbum() {
        return true;
    }

    public TimeLineTitleMediaItem getTimelineTitle() {
        return mTimelineTitleMediaItem;
    }

}
