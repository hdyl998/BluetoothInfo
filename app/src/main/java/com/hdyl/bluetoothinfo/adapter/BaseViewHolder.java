package com.hdyl.bluetoothinfo.adapter;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * ViewHolder
 * <p>
 * Contains convenient method for getter, setter and listener.
 */
public class BaseViewHolder {

    private final SparseArray<View> mViews;

    private View mItemView;
    private int mLayoutResId;

    public int getLayoutResId() {
        return mLayoutResId;
    }

    public View getItemView() {
        return mItemView;
    }

    public static BaseViewHolder get(LayoutInflater mInflater, View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return new BaseViewHolder(mInflater, parent, layoutId);
        }
        BaseViewHolder existingHolder = (BaseViewHolder) convertView.getTag();
        if (existingHolder.mLayoutResId != layoutId) {
            return new BaseViewHolder(mInflater, parent, layoutId);
        }
        return existingHolder;
    }

    private BaseViewHolder(LayoutInflater mInflater, ViewGroup parent, int layoutResId) {
        this.mLayoutResId = layoutResId;
        this.mViews = new SparseArray<>();
        this.mItemView = mInflater.inflate(layoutResId, parent, false);
        mItemView.setTag(this);
    }

    //用于特殊情况，非解析的布局
    public static BaseViewHolder get(ViewGroup parent, int layoutId) {
        return new BaseViewHolder(parent, layoutId);
    }

    private BaseViewHolder(ViewGroup parent, int layoutResId) {
        this.mLayoutResId = layoutResId;
        this.mViews = new SparseArray<>();
        this.mItemView = parent.findViewById(layoutResId);
        mItemView.setTag(this);
    }

    //用于特殊情况
    public static BaseViewHolder get(View mItemView) {
        return new BaseViewHolder(mItemView);
    }

    private BaseViewHolder(View mItemView) {
        this.mViews = new SparseArray<>();
        this.mItemView = mItemView;
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mItemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public <T extends View> T getView(View parentView, int viewId) {
        View view = parentView.findViewById(viewId);
        mViews.put(viewId, view);
        return (T) view;
    }

    //������getSet����


    public BaseViewHolder setText(int viewId, CharSequence value) {
        TextView view = getView(viewId);
        view.setText(value);
        return this;
    }

    /**
     * 设置文本,如果为空,则隐藏
     *
     * @param viewId
     * @param value
     * @return
     */
    public BaseViewHolder setTextIfEmptyGone(int viewId, CharSequence value) {
        TextView view = getView(viewId);
        if (TextUtils.isEmpty(value)) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(value);
            view.setVisibility(View.VISIBLE);
        }
        return this;
    }

//    public BaseViewHolder setTextDrawable(int viewId, @DrawableRes int resID, @TextViewUtils.TextDrawableDirection int direction) {
//        TextView view = getView(viewId);
//        TextViewUtils.setDrawable(view, resID, direction);
//        return this;
//    }
//
//    public BaseViewHolder setTextDrawable(int viewId, @DrawableRes int resID) {
//        TextView view = getView(viewId);
//        TextViewUtils.setDrawable(view, resID, TextViewUtils.DIRECTION_RIGHT);
//        return this;
//    }
//
//
//    public BaseViewHolder setTextDrawableNull(int viewId) {
//        TextView view = getView(viewId);
//        TextViewUtils.setDrawableNull(view);
//        return this;
//    }


    public BaseViewHolder setEnable(int viewId, boolean isEnable) {
        View view = getView(viewId);
        view.setEnabled(isEnable);
        return this;
    }

    public BaseViewHolder setImageResource(int viewId, int imageResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    public BaseViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

//    public BaseViewHolder setImageUrl(int viewId, String url) {
//        ImageView view = getView(viewId);
//        LoaderBuilder.get(mItemView.getContext()).loadImage(view, url);
//        return this;
//    }
//
//    public BaseViewHolder setImageUrlCircle(int viewId, String url) {
//        ImageView view = getView(viewId);
//        LoaderBuilder.get(mItemView.getContext()).asCircle().loadImage(view, url);
//        return this;
//    }
//
//    public BaseViewHolder setImageUrlAsPerson(int viewId, String url) {
//        ImageView view = getView(viewId);
//        LoaderBuilder.get(mItemView.getContext()).asPerson().loadImage(view, url);
//        return this;
//    }
//
//    public BaseViewHolder setImageUrlAsPersonCircle(int viewId, String url) {
//        ImageView view = getView(viewId);
//        LoaderBuilder.get(mItemView.getContext()).asCircle().asPerson().loadImage(view, url);
//        return this;
//    }

    public BaseViewHolder setBackgroundColor(int viewId, int bgColor) {
        View view = getView(viewId);
        view.setBackgroundColor(bgColor);
        return this;
    }

    public BaseViewHolder setBackgroundRes(int viewId, int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    public BaseViewHolder setTextColor(int viewId, ColorStateList colors) {
        TextView view = getView(viewId);
        view.setTextColor(colors);
        return this;
    }

    public BaseViewHolder setColorFilter(int viewId, ColorFilter colorFilter) {
        ImageView view = getView(viewId);
        view.setColorFilter(colorFilter);
        return this;
    }

    public BaseViewHolder setColorFilter(int viewId, int colorFilter) {
        ImageView view = getView(viewId);
        view.setColorFilter(colorFilter);
        return this;
    }

    @SuppressLint("NewApi")
    public BaseViewHolder setAlpha(int viewId, float value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView(viewId).setAlpha(value);
        } else {
            AlphaAnimation alpha = new AlphaAnimation(value, value);
            alpha.setDuration(0);
            alpha.setFillAfter(true);
            getView(viewId).startAnimation(alpha);
        }
        return this;
    }

    public BaseViewHolder setVisibility(int viewId, int visible) {
        View view = getView(viewId);
        view.setVisibility(visible);
        return this;
    }

    public BaseViewHolder setMax(int viewId, int max) {
        ProgressBar view = getView(viewId);
        view.setMax(max);
        return this;
    }

    public BaseViewHolder setProgress(int viewId, int progress) {
        ProgressBar view = getView(viewId);
        view.setProgress(progress);
        return this;
    }

    public BaseViewHolder setRating(int viewId, float rating) {
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

    public BaseViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    public BaseViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    @SuppressLint("NewApi")
    public BaseViewHolder setAdapter(int viewId, BaseAdapter adapter) {
        AbsListView view = (AbsListView) getView(viewId);
        view.setAdapter(adapter);
        return this;
    }

    public BaseViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = (Checkable) getView(viewId);
        view.setChecked(checked);
        return this;
    }

    public BaseViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    public BaseViewHolder setOnCheckedChangeListener(int viewId, CompoundButton.OnCheckedChangeListener listener) {
        CompoundButton view = getView(viewId);
        view.setOnCheckedChangeListener(listener);
        return this;
    }


    public BaseViewHolder setOnTouchListener(int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    public BaseViewHolder setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

}
