package com.heaven7.android.imagepick.pub.delegate;

import android.content.Intent;
import android.content.res.Resources;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.android.imagepick.pub.AdapterManageDelegate;
import com.heaven7.android.imagepick.pub.module.GroupItem;
import com.heaven7.android.imagepick.pub.module.IImageItem;
import com.heaven7.android.imagepick.pub.module.SeeImageParameter;
import com.heaven7.java.visitor.collection.KeyValuePair;

import java.util.List;

public abstract class SeeImageDelegate {

    private Provider provider;

    public Provider getProvider() {
        return provider;
    }
    public void setProvider(Provider provider) {
        this.provider = provider;
    }
    public AppCompatActivity getActivity(){
        return provider.getActivity();
    }
    public Resources getResources(){
        return getProvider().getActivity().getResources();
    }
    public SeeImageParameter getParameter(){
        return getProvider().getParameter();
    }

    public abstract void initialize(ViewGroup headContainer, Intent intent);

    public abstract AdapterManageDelegate<IImageItem> setContentAdapter(RecyclerView rv);

    public abstract AdapterManageDelegate<GroupItem> setGroupAdapter(RecyclerView rv);

    public abstract void startScan(MediaResourceCallback callback);

    public abstract void applyGroupItem(GroupItem groupItem);

    public void onDestroy(){

    }
    public void onEmptyGroup() {

    }
    public boolean onBackPressed() {
        return false;
    }

    public interface Provider{
        AppCompatActivity getActivity();
        SeeImageParameter getParameter();
    }
    public interface MediaResourceCallback{
        void onScanFinished(List<KeyValuePair<String,List<IImageItem>>> list);
    }
}
