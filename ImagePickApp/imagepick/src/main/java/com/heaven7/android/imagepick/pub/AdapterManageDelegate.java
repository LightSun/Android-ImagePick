package com.heaven7.android.imagepick.pub;

import java.util.List;

/**
 * the adapter manager delegate
 * @param <T> the data type
 * @since 1.0.5
 */
public interface AdapterManageDelegate<T> {

    /**
     * add items
     * @param items the items
     */
    void addItems(List<T> items);

    /**
     * clear and set new items
     * @param items the items
     */
    void setItems(List<T> items);
}
