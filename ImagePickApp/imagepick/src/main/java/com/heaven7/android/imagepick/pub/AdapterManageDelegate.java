package com.heaven7.android.imagepick.pub;

import java.util.List;

public interface AdapterManageDelegate<T> {

    void addItems(List<T> items);

    void setItems(List<T> items);
}
