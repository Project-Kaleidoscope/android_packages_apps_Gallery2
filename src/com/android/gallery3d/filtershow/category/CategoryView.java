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

package com.android.gallery3d.filtershow.category;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;

import com.android.gallery3d.filtershow.FilterShowActivity;
import com.android.gallery3d.filtershow.filters.FilterRepresentation;
import com.android.gallery3d.filtershow.ui.SelectionRenderer;

import org.codeaurora.gallery.R;

public class CategoryView extends IconView
        implements View.OnClickListener, SwipableView, View.OnLongClickListener, PopupMenu.OnMenuItemClickListener {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    private static final String TAG = "CategoryView";
    private final Paint mPaint = new Paint();
    private final Paint mSelectPaint;
    private final int mSelectionStroke;
    private final Paint mBorderPaint;
    private final int mBorderStroke;
    private final Context mContext;
    private final float mDeleteSlope = 20;
    private final long mDoubleTapDelay = 250;
    CategoryAdapter mAdapter;
    private Action mAction;
    private float mStartTouchX = 0;
    private float mStartTouchY = 0;
    private final int mSelectionColor;
    private final int mSpacerColor;
    private boolean mCanBeRemoved = false;
    private long mDoubleActionLast = 0;

    public CategoryView(Context context) {
        super(context);
        mContext = context;
        setOnClickListener(this);
        setOnLongClickListener(this);
        Resources res = getResources();
        mSelectionStroke = res.getDimensionPixelSize(R.dimen.thumbnail_margin);
        mSelectPaint = new Paint();
        mSelectPaint.setStyle(Paint.Style.FILL);
        mSelectionColor = res.getColor(R.color.filtershow_category_selection);
        mSpacerColor = res.getColor(R.color.filtershow_categoryview_text);

        mSelectPaint.setColor(mSelectionColor);
        mBorderPaint = new Paint(mSelectPaint);
        mBorderPaint.setColor(res.getColor(R.color.filtershow_info_test));
        mBorderStroke = mSelectionStroke / 3;
    }

    @Override
    public boolean isHalfImage() {
        if (mAction == null) {
            return false;
        }
        if (mAction.getType() == Action.CROP_VIEW) {
            return true;
        }
        return mAction.getType() == Action.ADD_ACTION;
    }

    private boolean canBeRemoved() {
        return mCanBeRemoved;
    }

    private void drawSpacer(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mSpacerColor);
        if (getOrientation() == CategoryView.VERTICAL) {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 5, mPaint);
        } else {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 5, mPaint);
        }
    }

    @Override
    public boolean needsCenterText() {
        if (mAction != null && mAction.getType() == Action.ADD_ACTION) {
            return true;
        }
        return super.needsCenterText();
    }

    public void onDraw(Canvas canvas) {
        if (mAction != null) {
            if (mAction.getType() == Action.SPACER) {
                drawSpacer(canvas);
                return;
            }
            if (mAction.isDoubleAction()) {
                return;
            }
            mAction.setImageFrame(new Rect(0, 0, getWidth(), getHeight()), getOrientation());
            if (mAction.getImage() != null) {
                setBitmap(mAction.getImage());
            }
        }
        super.onDraw(canvas);
        if (mAction.getRepresentation() == null) {
            return;
        }
        if (mAdapter.isSelected(this)) {
            mAction.setClickAction();
            if (mAction.getRepresentation().getFilterType() != FilterRepresentation.TYPE_WATERMARK_CATEGORY
                    && mAction.getRepresentation().getFilterType() != FilterRepresentation.TYPE_WATERMARK) {
                SelectionRenderer.drawSelection(canvas, getMargin() / 2, getMargin(),
                        getWidth() - getMargin() / 2, getHeight() - getMargin(),
                        mSelectionStroke, mSelectPaint, mBorderStroke, mBorderPaint);
            }
        } else {
            mAction.clearClickAction();
        }
        mAction.drawOverlay();
    }

    public void setAction(Action action, CategoryAdapter adapter) {
        mAction = action;
        setText(mAction.getName());
        mAdapter = adapter;
        mCanBeRemoved = action.canBeRemoved();
        setUseOnlyDrawable(false);
        if (mAction.getType() == Action.ADD_ACTION) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.filtershow_add_new);
            setBitmap(bitmap);
            //   setUseOnlyDrawable(true);
            //   setText(getResources().getString(R.string.filtershow_add_button_looks));
        } else {
            setBitmap(mAction.getImage());
        }
        invalidate();
    }

    @Override
    public void onClick(View view) {
        FilterShowActivity activity = (FilterShowActivity) getContext();
        if (mAction.getType() == Action.ADD_ACTION) {
            activity.addNewPreset();
        } else if (mAction.getType() != Action.SPACER) {
            if (mAction.isDoubleAction()) {
                long current = System.currentTimeMillis() - mDoubleActionLast;
                if (current < mDoubleTapDelay) {
                    activity.showRepresentation(mAction.getRepresentation());
                }
                mDoubleActionLast = System.currentTimeMillis();
            } else {
                activity.showRepresentation(mAction.getRepresentation());
            }
            mAdapter.setSelected(this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        FilterShowActivity activity = (FilterShowActivity) getContext();

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            activity.startTouchAnimation(this, event.getX(), event.getY());
        }
        if (!canBeRemoved() || checkPreset()) {
            return ret;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mStartTouchY = event.getY();
            mStartTouchX = event.getX();

        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            setTranslationX(0);
            setTranslationY(0);
        }
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            float delta = event.getY() - mStartTouchY;
            if (getOrientation() == CategoryView.VERTICAL) {
                delta = event.getX() - mStartTouchX;
            }
            if (Math.abs(delta) > mDeleteSlope) {
                activity.setHandlesSwipeForView(this, mStartTouchX, mStartTouchY);
            }
        }
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        if (canBeRemoved()) {
            mAdapter.setSelected(this);
            PopupMenu popup = new PopupMenu(getContext(), view);
            popup.getMenuInflater().inflate(R.menu.filtershow_menu_edit, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        FilterShowActivity activity = (FilterShowActivity) getContext();
        switch (item.getItemId()) {
            case R.id.deleteButton:
                activity.handlePreset(mAction, this, R.id.deleteButton);
                return true;
            case R.id.renameButton:
                activity.handlePreset(mAction, this, R.id.renameButton);
                return true;
        }
        return false;
    }


    @Override
    public void delete() {
        mAdapter.remove(mAction);
    }

    @Override
    protected void drawBottomRect(Canvas canvas) {
        super.drawBottomRect(canvas);
        FilterRepresentation filterRepresentation = mAction.getRepresentation();
        if (filterRepresentation != null) {
            if (filterRepresentation.getFilterType() == FilterRepresentation.TYPE_WATERMARK
                    || filterRepresentation.getFilterType() == FilterRepresentation.TYPE_WATERMARK_CATEGORY) {
                return;
            }
            if (filterRepresentation.getFilterType() == FilterRepresentation.TYPE_FX) {
                mPaint.setColor(getResources().getColor(filterRepresentation.getColorId()));
            } else {
                mPaint.setColor(
                        getContext().getResources().getColor(R.color.iconview_bottom_color));
            }
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(getBitmapBounds().left,
                    getBitmapBounds().bottom - getBottomRectHeight(),
                    getBitmapBounds().right,
                    getBitmapBounds().bottom,
                    mPaint);
        }
    }

    private boolean checkPreset() {
        FilterRepresentation filterRepresentation = mAction.getRepresentation();
        if (filterRepresentation != null) {
            return filterRepresentation.getFilterType() == FilterRepresentation.TYPE_PRESETFILTER;
        }
        return false;
    }

}
