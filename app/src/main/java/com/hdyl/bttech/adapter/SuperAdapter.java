package com.hdyl.bttech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Wrapper of BaseAdapter.
 */
public abstract class SuperAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected int mLayoutResId;
    protected List<T> mList;
    protected IMultiItemViewType<T> mMultiItemViewType;

    protected LayoutInflater mInflater;

    public SuperAdapter(Context context, int layoutResId) {
        this(context, null, layoutResId, null);
    }

    public SuperAdapter(Context context, List<T> data, int layoutResId) {
        this(context, data, layoutResId, null);
    }

    public SuperAdapter(Context context, List<T> data, IMultiItemViewType<T> multiItemViewType) {
        this(context, data, 0, multiItemViewType);
    }

    private SuperAdapter(Context context, List<T> data, int layoutResId, IMultiItemViewType<T> multiItemViewType) {
        this.mContext = context;
        this.mList = data;
        this.mMultiItemViewType = multiItemViewType;
        this.mLayoutResId = layoutResId;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public T getItem(int position) {
        if (position >= mList.size())
            return null;
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (mMultiItemViewType != null)
            return mMultiItemViewType.getLayoutIDs().length;
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemViewType != null) {
            return mMultiItemViewType.getLayoutIDsIndex(mList.get(position), position);
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T item = getItem(position);
        final BaseViewHolder viewHolder = onCreate(getItemViewType(position), convertView, parent);
        onBind(viewHolder, item, position);
        return viewHolder.getItemView();
    }

    private BaseViewHolder onCreate(int layoutIndex, View convertView, ViewGroup parent) {
        if (mMultiItemViewType != null) {
            int ids[] = mMultiItemViewType.getLayoutIDs();
            return BaseViewHolder.get(mInflater, convertView, parent, ids[layoutIndex]);
        }
        return BaseViewHolder.get(mInflater, convertView, parent, mLayoutResId);
    }


    public void notifyDataSetChanged(View convertView, int position){
        onBind((BaseViewHolder) convertView.getTag(),getItem(position),position);
    }



    /**
     * Abstract method for binding view and data.
     *
     * @param holder   ViewHolder
     * @param position position
     * @param item     data
     */
    protected abstract void onBind(BaseViewHolder holder, T item, int position);

    public void add(T item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    public void add(T item, boolean isChanged) {
        mList.add(item);
        if (isChanged)
            notifyDataSetChanged();
    }

    public void add(int index, T item) {
        mList.add(index, item);
        notifyDataSetChanged();
    }

    public void addAll(List<T> items) {
        mList.addAll(items);
        notifyDataSetChanged();
    }

    public void setDatas(List<T> items) {
        mList = items;
        notifyDataSetChanged();
    }

    public void remove(T item) {
        mList.remove(item);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        mList.remove(index);
        notifyDataSetChanged();
    }

    public void remove(int index, boolean isChange) {
        mList.remove(index);
        if (isChange)
            notifyDataSetChanged();
    }

    public void set(T oldItem, T newItem) {
        set(mList.indexOf(oldItem), newItem);
    }

    public void set(int index, T item) {
        mList.set(index, item);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> items) {
        mList.clear();
        addAll(items);
    }

    public boolean contains(T item) {
        return mList.contains(item);
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public List<T> getAllData() {
        return mList;
    }

}
