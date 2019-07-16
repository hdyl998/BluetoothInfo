package com.hdyl.bluetoothinfo.adapter;

/**
 * Created by liugd on 2017/4/24.
 */

public abstract class IMultiItemViewType<T> {

    private int[] ids;

    public abstract int[] createLayoutIds();

    public final int[] getLayoutIDs() {
        if (ids == null) {
            ids = createLayoutIds();
        }
        return ids;
    }

    public abstract int getLayoutIDsIndex(T t, int position);
}
