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

package com.android.gallery3d.ui;

import android.content.Context;
import android.os.StatFs;

import com.android.gallery3d.app.AbstractGalleryActivity;
import com.android.gallery3d.util.ThreadPool.JobContext;

import java.io.File;

public class CacheStorageUsageInfo {
    @SuppressWarnings("unused")
    private static final String TAG = "CacheStorageUsageInfo";

    // number of bytes the storage has.
    private long mTotalBytes;

    // number of bytes already used.
    private long mUsedBytes;

    // number of bytes used for the cache (should be less then usedBytes).
    private long mUsedCacheBytes;

    // number of bytes used for the cache if all pending downloads (and removals) are completed.
    private long mTargetCacheBytes;

    private final AbstractGalleryActivity mActivity;
    private final Context mContext;
    private long mUserChangeDelta;

    public CacheStorageUsageInfo(AbstractGalleryActivity activity) {
        mActivity = activity;
        mContext = activity.getAndroidContext();
    }

    public void increaseTargetCacheSize(long delta) {
        mUserChangeDelta += delta;
    }

    public void loadStorageInfo(JobContext jc) {
        File cacheDir = mContext.getCacheDir();
        if (cacheDir == null) {
            cacheDir = mContext.getCacheDir();
        }

        String path = cacheDir.getAbsolutePath();
        StatFs stat = new StatFs(path);
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();

        mTotalBytes = blockSize * totalBlocks;
        mUsedBytes = blockSize * (totalBlocks - availableBlocks);
        mUsedCacheBytes = mActivity.getDataManager().getTotalUsedCacheSize();
        mTargetCacheBytes = mActivity.getDataManager().getTotalTargetCacheSize();
    }

    public long getTotalBytes() {
        return mTotalBytes;
    }

    public long getExpectedUsedBytes() {
        return mUsedBytes - mUsedCacheBytes + mTargetCacheBytes + mUserChangeDelta;
    }

    public long getUsedBytes() {
        // Should it be usedBytes - usedCacheBytes + targetCacheBytes ?
        return mUsedBytes;
    }

    public long getFreeBytes() {
        return mTotalBytes - mUsedBytes;
    }
}
