package com.heaven7.android.imagepick.utils;

import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.adapter.AdapterManager;
import com.heaven7.adapter.ISelectable;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.QuickRecycleViewAdapter2;
import com.heaven7.android.imagepick.pub.AdapterManageDelegate;

import java.util.List;

/**
 * the simple adapter manager delegate
 * @param <T> the data
 * @author heaven7
 * @since 2.0.0
 */
public class SimpleAdapterManagerDelegate<T extends ISelectable>  implements AdapterManageDelegate<T> {

    private final AdapterManager<T> mAM;

    public SimpleAdapterManagerDelegate(RecyclerView.Adapter adapter) {
       if(adapter instanceof QuickRecycleViewAdapter){
           mAM = ((QuickRecycleViewAdapter) adapter).getAdapterManager();
       }else if(adapter instanceof QuickRecycleViewAdapter2){
           mAM = ((QuickRecycleViewAdapter2) adapter).getAdapterManager();
       }else {
           throw new UnsupportedOperationException();
       }
    }
    @Override
    public void addItems(List<T> items) {
        mAM.addItems(items);
    }
    @Override
    public void setItems(List<T> items) {
        mAM.replaceAllItems(items);
    }
}
