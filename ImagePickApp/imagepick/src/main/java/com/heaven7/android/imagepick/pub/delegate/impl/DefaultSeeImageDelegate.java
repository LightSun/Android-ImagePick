package com.heaven7.android.imagepick.pub.delegate.impl;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.adapter.AdapterManager;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.RecyclerViewUtils;
import com.heaven7.adapter.util.ViewHelper2;
import com.heaven7.android.imagepick.ImagePickDelegateImpl;
import com.heaven7.android.imagepick.MediaResourceHelper;
import com.heaven7.android.imagepick.R;
import com.heaven7.android.imagepick.internal.LibUtils;
import com.heaven7.android.imagepick.pub.AdapterManageDelegate;
import com.heaven7.android.imagepick.pub.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.GroupItem;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.ImagePickManager;
import com.heaven7.android.imagepick.pub.MediaOption;
import com.heaven7.android.imagepick.pub.MediaResourceItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.SeeImageParameter;
import com.heaven7.android.imagepick.pub.delegate.SeeImageDelegate;
import com.heaven7.android.imagepick.utils.AnimationListenerAdapter;
import com.heaven7.android.imagepick.utils.SimpleAdapterManagerDelegate;
import com.heaven7.core.util.BundleHelper;
import com.heaven7.core.util.Toaster;
import com.heaven7.core.util.ViewHelper;
import com.heaven7.core.util.viewhelper.action.Getters;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Predicates;

import java.util.ArrayList;
import java.util.List;

public class DefaultSeeImageDelegate extends SeeImageDelegate {

    private Header mHeader;
    private AdapterManageDelegate<IImageItem> mContentManager;
    private MediaResourceHelper mMediaHelper;
    protected MediaOption mOption;

    @Override
    public void initialize(ViewGroup headContainer, Intent intent) {
        mHeader = new Header(headContainer);
        mOption = intent.getParcelableExtra(PickConstants.KEY_MEDIA_OPTION);
        if(mOption == null){
            mOption = MediaOption.DEFAULT;
        }
    }

    @Override
    public AdapterManageDelegate<IImageItem> setContentAdapter(RecyclerView rv) {
        ContentAdapter adapter = new ContentAdapter(null);
        GridLayoutManager gm = RecyclerViewUtils.createGridLayoutManager(adapter, rv.getContext(), getParameter().getSpanCount());
        rv.setLayoutManager(gm);
        rv.setAdapter(adapter);
        mContentManager = new SimpleAdapterManagerDelegate<>(adapter);
        return mContentManager;
    }

    @Override
    public AdapterManageDelegate<GroupItem> setGroupAdapter(RecyclerView rv) {
        mHeader.setRecyclerView(rv);
        DirAdapter dirAdapter = new DirAdapter(null);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(dirAdapter);
        return new SimpleAdapterManagerDelegate<>(dirAdapter);
    }

    @Override
    public void startScan(final MediaResourceCallback callback) {
        mMediaHelper = new MediaResourceHelper(getActivity(), mOption);
        int flags = 0;
        if(!Predicates.isEmpty(mOption.getImageMimes())){
            flags |= PickConstants.FLAG_IMAGE;
        }
        if(!Predicates.isEmpty(mOption.getVideoMimes())){
            flags |= PickConstants.FLAG_VIDEO;
        }
        mMediaHelper.getMediaResource(flags, new MediaResourceHelper.Callback() {
            @Override
            public void onCallback(List<MediaResourceItem> photoes, List<MediaResourceItem> videoes) {
                List<MediaResourceItem> items = new ArrayList<>(videoes);
                items.addAll(photoes);

                KeyValuePair<String, List<IImageItem>> p0 = KeyValuePair.create("图片和视频", VisitServices.from(items).asAnother(IImageItem.class).getAsList());
                KeyValuePair<String, List<IImageItem>> p1 = KeyValuePair.create("图片", VisitServices.from(new ArrayList<>(photoes)).asAnother(IImageItem.class).getAsList());
                KeyValuePair<String, List<IImageItem>> p2 = KeyValuePair.create("视频", VisitServices.from(new ArrayList<>(videoes)).asAnother(IImageItem.class).getAsList());
                ArrayList<KeyValuePair<String, List<IImageItem>>> list = new ArrayList<>();
                list.add(p0);
                list.add(p1);
                list.add(p2);
                callback.onScanFinished(list);
            }
        });
    }

    @Override
    public void applyGroupItem(GroupItem groupItem) {
        mHeader.mTv_folder.setText(groupItem.getGroupName());
        mContentManager.setItems(groupItem.getItems());
    }

    @Override
    public void onDestroy() {
        mMediaHelper.cancel();
        super.onDestroy();
    }
    @Override
    public void onEmptyGroup() {
        Toaster.show(getActivity(), "暂无数据.");
    }

    private class Header {

        private final View headView;
        private final TextView mTv_folder;

        private RecyclerView mRv_dir;

        public Header(ViewGroup parent) {
            this.headView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lib_pick_head_see_image, parent);
            headView.findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getProvider().getActivity().finish();
                }
            });
            mTv_folder = headView.findViewById(R.id.tv_dir);
            mTv_folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickSwitchDir(v);
                }
            });
        }

        public void setRecyclerView(RecyclerView rv) {
            mRv_dir = rv;
        }

        public void onClickSwitchDir(View view) {
            if (mRv_dir.getVisibility() == View.VISIBLE) {
                mTv_folder.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.lib_pick_ic_arrow_down), null);
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.lib_pick_album_out);
                anim.setAnimationListener(new AnimationListenerAdapter() {
                    public void onAnimationEnd(Animation animation) {
                        //for a bug with 'fillAfter/fillEnable' = true. we must clear animation.
                        mRv_dir.clearAnimation();
                        mRv_dir.setVisibility(View.GONE);
                    }
                });
                mRv_dir.startAnimation(anim);
            } else {
                mRv_dir.setVisibility(View.VISIBLE);
                mTv_folder.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(R.drawable.lib_pick_ic_arrow_up), null);
                Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.lib_pick_album_in);
                anim.setAnimationListener(new AnimationListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mRv_dir.clearAnimation();
                    }
                });
                mRv_dir.startAnimation(anim);
            }
        }
    }

    private class DirAdapter extends QuickRecycleViewAdapter<GroupItem> {

        public DirAdapter(List<GroupItem> mDatas) {
            super(R.layout.lib_pick_item_album_dir, mDatas);
        }

        @Override
        protected void onBindData(Context context, int position, final GroupItem item, int itemLayoutId, ViewHelper2 helper) {
            if (Predicates.isEmpty(item.getItems())) {
                //should not reach here
                helper.setImageResource(R.id.iv_icon, 0);
            } else {
                helper.performViewGetter(R.id.iv_icon, new Getters.ImageViewGetter() {
                    @Override
                    public void onGotView(ImageView view, ViewHelper vp) {
                        IImageItem mr = item.getItems().get(0);
                        ImagePickDelegateImpl.getDefault().getImageLoadDelegate().loadImage(getActivity(), view, mr, null);
                    }
                });
            }
            helper.setText(R.id.tv_dir_name, item.getGroupName())
                    .setText(R.id.tv_count, item.getItems().size() + "")
                    .setRootOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            applyGroupItem(item);
                            mHeader.onClickSwitchDir(null);
                        }
                    });
        }
    }

    private class ContentAdapter extends QuickRecycleViewAdapter<IImageItem> {

        private final int mItemWidth;
        private final int mItemHeight;

        public ContentAdapter(List<IImageItem> mDatas) {
            super(R.layout.lib_pick_item_see_image, mDatas);

            int width = LibUtils.getWidth(getActivity());
            SeeImageParameter mParam = getParameter();
            mItemWidth = (width - mParam.getSpace() * (mParam.getSpanCount() - 1)) / mParam.getSpanCount();
            //x / y = mItemWidth / mItemHeight
            mItemHeight = mParam.getAspectY() * mItemWidth / mParam.getAspectX();
        }

        @Override
        protected void onBindData(Context context, final int position, final IImageItem item, int itemLayoutId, ViewHelper2 helper) {
            View rootView = helper.getRootView();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
            lp.width = mItemWidth;
            lp.height = mItemHeight;
            lp.setMarginStart(position != 0 ? getParameter().getSpace() : 0);
            rootView.setLayoutParams(lp);

            helper.performViewGetter(R.id.iv, new Getters.ImageViewGetter() {
                @Override
                public void onGotView(ImageView view, ViewHelper viewHelper) {
                    ImagePickDelegateImpl.getDefault().getImageLoadDelegate().loadImage(getActivity(), view, item, null);
                }
            }).setImageResource(R.id.iv_pause, getParameter().getPauseIconRes())
                    .setVisibility(R.id.iv_pause, item.isVideo())
                    .setRootOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AdapterManager<IImageItem> am = getAdapterManager();

                            BigImageSelectParameter param = new BigImageSelectParameter.Builder()
                                    .setCurrentOrder(position + 1)
                                    .setTotalCount(am.getItemSize())
                                    .setSelectCount(0)
                                    .setMaxSelectCount(0)
                                    .setTopRightText(getResources().getString(R.string.lib_pick_upload))
                                    .setFlags(PickConstants.FLAG_SHOW_TOP)
                                    .setSupportGestureImage(true)
                                    .build();
                            Bundle extra = new BundleHelper()
                                    .putString(PickConstants.KEY_EXTRA, mHeader.mTv_folder.getText().toString())
                                    .getBundle();
                            ImagePickManager.get().getImagePickDelegate()
                                    .startBrowseBigImages(getActivity(), param, SimpleSeeBigImageDelegate.class, extra, am.getItems(), null);
                        }
                    });
        }
    }
}
