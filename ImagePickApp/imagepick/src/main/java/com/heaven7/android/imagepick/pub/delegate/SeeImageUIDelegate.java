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

/**
 * the delegate for {@linkplain com.heaven7.android.imagepick.SeeImageActivity}.
 * @since 2.0.0
 */
public abstract class SeeImageUIDelegate {

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
    /**
     * called on initialize
     * @param headContainer the head container
     * @param intent the intent from activity
     */
    public abstract void initialize(ViewGroup headContainer, Intent intent);
    /**
     * set the content adapter which is used by browse medias.
     * @param rv the recycler view
     * @return the adapter manager delegate
     */
    public abstract AdapterManageDelegate<IImageItem> setContentAdapter(RecyclerView rv);
    /**
     * set the group adapter which is used by browse dirs.
     * @param rv the recycler view
     * @return the adapter manager delegate
     */
    public abstract AdapterManageDelegate<GroupItem> setGroupAdapter(RecyclerView rv);

    /**
     * call this to scan media data.
     * @param callback the scan callback
     */
    public abstract void startScan(MediaResourceCallback callback);

    /**
     * apply group item which is called on switch dir
     * @param groupItem the group item
     */
    public abstract void applyGroupItem(GroupItem groupItem);

    /**
     * called on empty group
     */
    public void onEmptyGroup() {

    }
    public void onDestroy(){

    }
    public boolean onBackPressed() {
        return false;
    }

    /**
     * the provider which provide something for delegate
     */
    public interface Provider{
        AppCompatActivity getActivity();
        SeeImageParameter getParameter();
    }

    /**
     * the media resource callback
     */
    public interface MediaResourceCallback{
        /**
         * called on scan finished
         * @param list the media data
         */
        void onScanFinished(List<KeyValuePair<String,List<IImageItem>>> list);
    }
}
