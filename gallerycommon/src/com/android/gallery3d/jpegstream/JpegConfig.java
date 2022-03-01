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

package com.android.gallery3d.jpegstream;

public interface JpegConfig {
    // Pixel formats
    int FORMAT_GRAYSCALE = 0x001; // 1 byte/pixel
    int FORMAT_RGB = 0x003; // 3 bytes/pixel RGBRGBRGBRGB...
    int FORMAT_RGBA = 0x004; // 4 bytes/pixel RGBARGBARGBARGBA...
    int FORMAT_ABGR = 0x104; // 4 bytes/pixel ABGRABGRABGR...

    // Jni error codes
    int J_SUCCESS = 0;
    int J_ERROR_FATAL = -1;
    int J_ERROR_BAD_ARGS = -2;
    int J_EXCEPTION = -3;
    int J_DONE = -4;
}
