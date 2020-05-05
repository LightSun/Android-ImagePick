package com.heaven7.android.imagepick.pub.delegate;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.android.imagepick.R;
import com.heaven7.android.imagepick.page.MediaPageProviderManager;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.module.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.module.IImageItem;

/**
 * the delegate which is used by {@linkplain com.heaven7.android.imagepick.SeeBigImageActivity}.
 * @author heaven7
 * @since 2.0.0
 */
public abstract class SeeBigImageUIDelegate implements MediaPageProviderManager.Callback {

    private static final Object UNSET = new Object();
    private ViewGroup parent;
    private Provider mProvider;
    private Object mTopBinder = UNSET;
    private Object mBottomBinder = UNSET;

    public void setProvider(Provider provider) {
        this.mProvider = provider;
    }
    public Resources getResource(){
        return getProvider().getActivity().getResources();
    }
    public ViewGroup getViewParent() {
        return parent;
    }
    public Provider getProvider() {
        return mProvider;
    }
    public BigImageSelectParameter getParameter(){
        return getProvider().getParameter();
    }
    /**
     * get the top binder
     * @return the top binder
     */
    public ViewBinder getTopBinder(){
        return mTopBinder != UNSET ? (ViewBinder) mTopBinder : null;
    }

    /**
     * get the bottom binder
     * @return the bottom binder
     */
    public ViewBinder getBottomBinder(){
        return mBottomBinder != UNSET ? (ViewBinder) mBottomBinder : null;
    }
    /**
     * called to get view pager from target root view. this give you a chance to change view-pager,such as you vertical-ViewPager2
     * @param root the root view
     * @return the view pager
     */
    public View getViewPager(View root){
        return root.findViewById(R.id.lib_pick_vp);
    }

    /**
     * called on destroy
     */
    public void onDestroy(){
        ViewBinder binder = getTopBinder();
        if(binder != null){
            binder.onDestroy();
        }
        binder = getBottomBinder();
        if(binder != null){
            binder.onDestroy();
        }
    }

    /**
     * called on initialize the delegate.
     * @param context the context
     * @param parent the view parent
     * @param intent the intent from activity
     */
    public void initialize(Context context, ViewGroup parent, Intent intent){
        this.parent = parent;
        ViewBinder topBinder = onCreateTopBinder(context, parent, intent);
        ViewBinder bottomBinder = onCreateBottomBinder(context, parent, intent);
        if(topBinder != null){
            parent.addView(topBinder.getView());
            topBinder.onBind();
        }
        if(bottomBinder != null){
            parent.addView(bottomBinder.getView());
            bottomBinder.onBind();
        }

        this.mTopBinder = topBinder;
        this.mBottomBinder = bottomBinder;
    }

    /**
     * called on set selected text. such as 'upload(4/10)'
     */
    public void setSelectedText() {
        ViewBinder binder = getTopBinder();
        if(binder != null){
            binder.setSelectedText();
        }
        binder = getBottomBinder();
        if(binder != null){
            binder.setSelectedText();
        }
    }

    /**
     * called on set select order which often show af top start.
     */
    public void setSelectOrder() {
        ViewBinder binder = getTopBinder();
        if(binder != null){
            binder.setSelectOrder();
        }
        binder = getBottomBinder();
        if(binder != null){
            binder.setSelectOrder();
        }
    }

    /**
     * called on set select state
     * @param select true if select
     */
    public void setSelectState(boolean select) {
        ViewBinder binder = getTopBinder();
        if(binder != null){
            binder.setSelectState(select);
        }
        binder = getBottomBinder();
        if(binder != null){
            binder.setSelectState(select);
        }
    }

    /**
     * called on set ui state. this is called on final initialize of activity or {@linkplain #onClickPageImageView(ImageView, int, int, IImageItem)}.
     */
    public void setUiState() {
        ViewBinder topBinder = getTopBinder();
        if(topBinder != null){
            boolean visible = getParameter().hasFlag(PickConstants.FLAG_SHOW_TOP);
            topBinder.getView().setVisibility(visible ? View.VISIBLE : View.GONE );
        }

        ViewBinder bottomBinder = getBottomBinder();
        if(bottomBinder != null){
            boolean visible = getParameter().hasFlag(PickConstants.FLAG_SHOW_BOTTOM);
            bottomBinder.getView().setVisibility(visible ? View.VISIBLE : View.GONE );
            //show bottom end or not.
            visible = getParameter().hasFlag(PickConstants.FLAG_SHOW_BOTTOM_END_BUTTON);
            bottomBinder.setBottomEndVisible(visible);
        }
    }

    /**
     * called on click image view ,which is a child of viewpager.
     * @param v the image view
     * @param pos the pos
     * @param realPos the realPos
     * @param data the image data.
     */
    @Override
    public void onClickPageImageView(ImageView v, int pos, int realPos, IImageItem data) {
        BigImageSelectParameter mParam = getParameter();
        if (mParam.hasFlag(PickConstants.FLAG_SHOW_TOP) || mParam.hasFlag(PickConstants.FLAG_SHOW_BOTTOM)) {
            mParam.deleteFlags(PickConstants.FLAG_SHOW_TOP | PickConstants.FLAG_SHOW_BOTTOM);
        } else {
            mParam.addFlags(PickConstants.FLAG_SHOW_TOP | PickConstants.FLAG_SHOW_BOTTOM);
        }
        setUiState();
    }

    /**
     * called on bind image item to view
     * @param iv the image view
     * @param pos the pos
     * @param realPos the realPos
     * @param data the data
     * @return true if bind success.
     */
    @Override
    public boolean bindImageItem(ImageView iv, int pos, int realPos, IImageItem data) {
        return false;
    }

    /**
     * called on create top binder
     * @param context the context
     * @param parent the view parent
     * @param intent the intent
     * @return the top binder. can be null
     */
    protected abstract ViewBinder onCreateTopBinder(Context context, ViewGroup parent, Intent intent);
    /**
     * called on create bottom binder
     * @param context the context
     * @param parent the view parent
     * @param intent the intent
     * @return the bottom binder. can be null
     */
    protected abstract ViewBinder onCreateBottomBinder(Context context, ViewGroup parent, Intent intent);
    /**
     * the view binder of {@linkplain SeeBigImageUIDelegate}. you can use this as the top ,bottom binder.
     */
    public abstract static class ViewBinder{

        private final SeeBigImageUIDelegate mDelegate;
        private final View mView;

        public ViewBinder(SeeBigImageUIDelegate delegate, ViewGroup parent) {
            this.mDelegate = delegate;
            this.mView = onCreateView(parent);
        }
        public Resources getResource(){
            return mDelegate.getResource();
        }
        public SeeBigImageUIDelegate getDelegate(){
            return mDelegate;
        }
        public Provider getProvider(){
            return mDelegate.getProvider();
        }
        public BigImageSelectParameter getParameter(){
            return mDelegate.getParameter();
        }
        public ViewGroup getViewParent() {
            return mDelegate.getViewParent();
        }
        public View getView(){
            return mView;
        }

        /**
         * called on create view of this binder
         * @param parent the view parent
         * @return the content view of this binder
         */
        protected View onCreateView(ViewGroup parent){
            return LayoutInflater.from(parent.getContext()).inflate(getLayoutId(), parent, false);
        }
        protected abstract int getLayoutId();

        public abstract void onBind();

        public void onDestroy(){

        }
        public void setSelectedText() {

        }
        public void setSelectOrder() {

        }
        public void setSelectState(boolean select) {

        }
        public void setBottomEndVisible(boolean visible) {

        }
    }

    /**
     * the provider which provide something for delegate
     */
    public interface Provider{
        AppCompatActivity getActivity();
        BigImageSelectParameter getParameter();
        IImageItem getImageItem(int index);
        void onClickSelect(View v);
    }

}