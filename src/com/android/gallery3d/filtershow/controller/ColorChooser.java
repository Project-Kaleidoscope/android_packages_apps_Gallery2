package com.android.gallery3d.filtershow.controller;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.gallery3d.filtershow.colorpicker.ColorListener;
import com.android.gallery3d.filtershow.colorpicker.ColorPickerDialog;
import com.android.gallery3d.filtershow.editors.Editor;

import org.codeaurora.gallery.R;

import java.util.Arrays;
import java.util.Vector;

public class ColorChooser implements Control {
    private static final int OPACITY_OFFSET = 3;
    private final String TAG = "StyleChooser";
    protected ParameterColor mParameter;
    protected LinearLayout mLinearLayout;
    protected Editor mEditor;
    protected int mLayoutID = R.layout.filtershow_control_color_chooser;
    Context mContext;
    int mSelectedButton = 0;
    private View mTopView;
    private final Vector<Button> mIconButton = new Vector<>();
    private int mTransparent;
    private int mSelected;
    private final int[] mButtonsID = {
            R.id.draw_color_button01,
            R.id.draw_color_button02,
            R.id.draw_color_button03,
            R.id.draw_color_button04,
            R.id.draw_color_button05,
    };
    private final Button[] mButton = new Button[mButtonsID.length];

    @Override
    public void setUp(ViewGroup container, Parameter parameter, Editor editor) {
        container.removeAllViews();
        Resources res = container.getContext().getResources();
        mTransparent = res.getColor(R.color.color_chooser_unslected_border);
        mSelected = res.getColor(R.color.color_chooser_slected_border);
        mEditor = editor;
        mContext = container.getContext();
        int iconDim = res.getDimensionPixelSize(R.dimen.draw_style_icon_dim);
        mParameter = (ParameterColor) parameter;
        LayoutInflater inflater =
                (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTopView = inflater.inflate(mLayoutID, container, true);
        mLinearLayout = mTopView.findViewById(R.id.listStyles);
        mTopView.setVisibility(View.VISIBLE);

        mIconButton.clear();
        LayoutParams lp = new LayoutParams(iconDim, iconDim);
        int[] palette = mParameter.getColorPalette();
        for (int i = 0; i < mButtonsID.length; i++) {
            final Button button = mTopView.findViewById(mButtonsID[i]);
            mButton[i] = button;
            float[] hsvo = new float[4];
            Color.colorToHSV(palette[i], hsvo);
            hsvo[OPACITY_OFFSET] = (0xFF & (palette[i] >> 24)) / (float) 255;
            button.setTag(hsvo);

            String colorString = "(" + Integer.toHexString(palette[i]) + ")";
            boolean colorSelect = false;
            if (parameter.getValueString().equals(colorString)) {
                mSelectedButton = i;
                colorSelect = true;
            }
            GradientDrawable sd = ((GradientDrawable) button.getBackground());
            sd.setColor(palette[i]);
            sd.setStroke(3, colorSelect ? mSelected : mTransparent);

            final int buttonNo = i;
            button.setOnClickListener(view -> selectColor(view, buttonNo));
        }
        Button button = mTopView.findViewById(R.id.draw_color_popupbutton);

        button.setOnClickListener(arg0 -> showColorPicker());

    }

    public int[] getColorSet() {
        return mParameter.getColorPalette();
    }

    public void setColorSet(int[] basColors) {
        int[] palette = mParameter.getColorPalette();
        for (int i = 0; i < palette.length; i++) {
            palette[i] = basColors[i];
            float[] hsvo = new float[4];
            Color.colorToHSV(palette[i], hsvo);
            hsvo[OPACITY_OFFSET] = (0xFF & (palette[i] >> 24)) / (float) 255;
            mButton[i].setTag(hsvo);
            GradientDrawable sd = ((GradientDrawable) mButton[i].getBackground());
            sd.setColor(palette[i]);
        }

    }

    private void resetBorders() {
        int[] palette = mParameter.getColorPalette();
        for (int i = 0; i < mButtonsID.length; i++) {
            final Button button = mButton[i];
            GradientDrawable sd = ((GradientDrawable) button.getBackground());
            sd.setColor(palette[i]);
            sd.setStroke(3, (mSelectedButton == i) ? mSelected : mTransparent);
        }
    }

    public void selectColor(View button, int buttonNo) {
        mSelectedButton = buttonNo;
        float[] hsvo = (float[]) button.getTag();
        mParameter.setValue(Color.HSVToColor((int) (hsvo[OPACITY_OFFSET] * 255), hsvo));
        resetBorders();
        mEditor.commitLocalRepresentation();
    }

    @Override
    public View getTopView() {
        return mTopView;
    }

    @Override
    public void setPrameter(Parameter parameter) {
        mParameter = (ParameterColor) parameter;
        updateUI();
    }

    @Override
    public void updateUI() {
        if (mParameter == null) {
            return;
        }
    }

    public void changeSelectedColor(float[] hsvo) {
        int[] palette = mParameter.getColorPalette();
        int c = Color.HSVToColor((int) (hsvo[3] * 255), hsvo);
        final Button button = mButton[mSelectedButton];
        GradientDrawable sd = ((GradientDrawable) button.getBackground());
        sd.setColor(c);
        palette[mSelectedButton] = c;
        mParameter.setValue(Color.HSVToColor((int) (hsvo[OPACITY_OFFSET] * 255), hsvo));
        button.setTag(hsvo);
        mEditor.commitLocalRepresentation();
        button.invalidate();
    }

    public void showColorPicker() {
        ColorListener cl = new ColorListener() {
            @Override
            public void setColor(float[] hsvo) {
                changeSelectedColor(hsvo);
            }

            @Override
            public void addColorListener(ColorListener l) {
            }
        };
        ColorPickerDialog cpd = new ColorPickerDialog(mContext, cl);
        float[] c = (float[]) mButton[mSelectedButton].getTag();
        cpd.setColor(Arrays.copyOf(c, 4));
        cpd.setOrigColor(Arrays.copyOf(c, 4));
        cpd.show();
    }
}
