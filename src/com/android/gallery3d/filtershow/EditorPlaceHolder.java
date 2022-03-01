package com.android.gallery3d.filtershow;

import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.android.gallery3d.filtershow.editors.Editor;
import com.android.gallery3d.filtershow.imageshow.ImageShow;

import java.util.HashMap;
import java.util.Vector;

public class EditorPlaceHolder {
    private static final String TAG = "EditorPlaceHolder";

    private final FilterShowActivity mActivity;
    private FrameLayout mContainer = null;
    private final HashMap<Integer, Editor> mEditors = new HashMap<>();
    private Vector<ImageShow> mOldViews = new Vector<>();

    public EditorPlaceHolder(FilterShowActivity activity) {
        mActivity = activity;
    }

    public void setContainer(FrameLayout container) {
        mContainer = container;
    }

    public void addEditor(Editor c) {
        mEditors.put(c.getID(), c);
    }

    public boolean contains(int type) {
        return mEditors.get(type) != null;
    }

    public Editor showEditor(int type) {
        Editor editor = mEditors.get(type);
        if (editor == null) {
            return null;
        }

        editor.createEditor(mActivity, mContainer);
        editor.getImageShow().attach();
        mContainer.setVisibility(View.VISIBLE);
        mContainer.removeAllViews();
        View view = editor.getTopLevelView();
        ViewParent parent = view.getParent();

        if (parent instanceof FrameLayout) {
            ((FrameLayout) parent).removeAllViews();
        }

        mContainer.addView(view);
        hideOldViews();
        editor.setVisibility(View.VISIBLE);
        return editor;
    }

    public void setOldViews(Vector<ImageShow> views) {
        mOldViews = views;
    }

    public void hide() {
        if (mContainer != null)
            mContainer.setVisibility(View.GONE);
    }

    public void hideOldViews() {
        for (View view : mOldViews) {
            view.setVisibility(View.GONE);
        }
    }

    public Editor getEditor(int editorId) {
        return mEditors.get(editorId);
    }

}
