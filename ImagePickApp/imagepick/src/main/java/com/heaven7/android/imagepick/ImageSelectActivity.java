package com.heaven7.android.imagepick;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.RecyclerViewUtils;
import com.heaven7.adapter.util.ViewHelper2;
import com.heaven7.android.imagepick.pub.BigImageSelectParameter;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.ImageParameter;
import com.heaven7.android.imagepick.pub.ImagePickDelegate;
import com.heaven7.android.imagepick.pub.ImagePickManager;
import com.heaven7.android.imagepick.pub.ImageSelectParameter;
import com.heaven7.android.imagepick.pub.MediaResourceItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.core.util.ImageParser;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.Toaster;
import com.heaven7.core.util.ViewHelper;
import com.heaven7.core.util.viewhelper.action.Getters;
import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.visitor.MapResultVisitor;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Predicates;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the image select activity. used for select image and videos.
 * @author heaven7
 */
public class ImageSelectActivity extends BaseActivity implements MediaResourceHelper.Callback,
        ImagePickDelegate.OnSelectStateChangedListener, Selector.Callback<MediaResourceItem> {

    private static final String TAG = "ImageSelectAc";
    private TextView mTv_upload;
    private TextView mTv_folder;
    private RecyclerView mRv_content;
    private RecyclerView mRv_dir;

    private MediaResourceHelper mMediaHelper;
    private int mItemWidth;
    private int mItemHeight;

    private ImageSelectParameter mParam;
    private final Selector<MediaResourceItem> mSelector = new Selector<>(this);
    private final ThreadHelper mThreadHelper = new ThreadHelper();

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_ac_image_select;
    }

    @Override
    protected void init(Context context, Bundle savedInstanceState) {
        mParam = getIntent().getParcelableExtra(PickConstants.KEY_PARAMS);
        mSelector.setSingleMode(mParam.getMaxSelect() <= 1);

        mMediaHelper = new MediaResourceHelper(this);
        int width = getWidth();
        mItemWidth = (width - mParam.getSpace() * (mParam.getSpanCount() - 1)) / mParam.getSpanCount();
        //x / y = mItemWidth / mItemHeight
        mItemHeight = mParam.getAspectY() * mItemWidth / mParam.getAspectX();

        mTv_upload = findViewById(R.id.tv_upload);
        mRv_content = findViewById(R.id.rv_content);
        mRv_dir = findViewById(R.id.rv_dir);
        mTv_folder = findViewById(R.id.tv_dir);
        mTv_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MediaResourceItem> items = mSelector.getSelects();
                if(items == null || items.size() == 0){
                    setResult(RESULT_CANCELED);
                    finish();
                }else {
                    publishResult(items);
                }
            }
        });
        setAdapter();
        setSelectText();

        ImagePickDelegateImpl.getDefault().addOnSelectStateChangedListener(this);
        mMediaHelper.getMediaResource(mParam.getFlags(), this);
    }

    private void publishResult(final List<MediaResourceItem> items) {
        final ImageParameter ip = mParam.getImageParameter();
        if(ip == null){
            publishResultImpl(Utils.getFilePaths(items));
        }else {
            final ImageParser mParser = Utils.createImageParser(ip, false);
            final String cacheDir = mParam.getCacheDir();
            if(cacheDir == null){
                throw new IllegalStateException("must assign cache dir.");
            }
            File file = new File(cacheDir);
            if(file.isFile()){
                throw new IllegalStateException("must assign cache dir. not file.");
            }
            if(!file.exists()){
                file.mkdirs();
            }
            //need handle items
            final List<MediaResourceItem> imageItems = VisitServices.from(items)
                    .filter(new PredicateVisitor<MediaResourceItem>() {
                @Override
                public Boolean visit(MediaResourceItem item, Object param) {
                    if(item.isImage() && !item.isGif()){
                        if(item.getWidth() > ip.getMaxWidth() || item.getHeight() > ip.getMaxHeight()){
                            return true;
                        }
                    }
                    return false;
                }
            }).getAsList();
            if(imageItems.isEmpty()){
                publishResultImpl(Utils.getFilePaths(items));
            }else {
                ImagePickDelegateImpl.getDefault().onImageProcessStart(this, imageItems.size());
                mThreadHelper.getBackgroundHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        final Map<MediaResourceItem, String> map = new HashMap<>();
                        boolean doNext = true;
                        for (int i = 0, size = imageItems.size() ; i< size ; i ++){
                            MediaResourceItem item = imageItems.get(i);
                            Bitmap bitmap = mParser.parseToBitmap(item.getFilePath());
                            String extension = FileUtils.getFileExtension(item.getFilePath());
                            Bitmap.CompressFormat format = Utils.getCompressFormat(extension);
                            if(format == null){
                                doNext = ImagePickDelegateImpl.getDefault().onImageProcessException(ImageSelectActivity.this, i + 1, size, item, null);
                                if(!doNext){
                                    Toaster.show(getApplicationContext(), getString(R.string.lib_pick_unsupport_image_format));
                                    return;
                                }else {
                                    continue;
                                }
                            }
                            //compress io
                            File file = new File(cacheDir, System.currentTimeMillis() + "." + extension);
                            FileOutputStream fos = null;
                            try {
                                fos = new FileOutputStream(file);
                                bitmap.compress(format, 100, fos);
                                fos.flush();
                                System.out.println(file.getAbsolutePath());
                                map.put(item, file.getAbsolutePath());
                                ImagePickDelegateImpl.getDefault().onImageProcessUpdate(ImageSelectActivity.this, i + 1, size);
                            }catch (Exception e){
                                doNext = ImagePickDelegateImpl.getDefault().onImageProcessException(ImageSelectActivity.this, i + 1, size, item, e);
                                if(!doNext){
                                    Toaster.show(getApplicationContext(), getString(R.string.lib_pick_process_image_failed));
                                    return;
                                }
                            } finally {
                                IOUtils.closeQuietly(fos);
                            }
                        }
                        //map to file path
                        final List<String> list = VisitServices.from(items).map(new ResultVisitor<MediaResourceItem, String>() {
                            @Override
                            public String visit(MediaResourceItem item, Object param) {
                                String s = map.get(item);
                                if (!TextUtils.isEmpty(s)) {
                                    return s;
                                }
                                return item.getFilePath();
                            }
                        }).getAsList();
                        ImagePickDelegateImpl.getDefault().onImageProcessEnd(ImageSelectActivity.this, new Runnable() {
                            @Override
                            public void run() {
                                publishResultImpl(new ArrayList<>(list));
                            }
                        });
                    }
                });
            }
        }
    }

    private void publishResultImpl(final ArrayList<String> list) {
        setResult(RESULT_OK, new Intent().putExtra(PickConstants.KEY_RESULT, list));
        finish();
    }

    @Override
    protected void onDestroy() {
        mThreadHelper.quitNow();
        ImagePickDelegateImpl.getDefault().clearImages();
        ImagePickDelegateImpl.getDefault().removeOnSelectStateChangedListener(this);
        mMediaHelper.cancel();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            List<MediaResourceItem> items = mSelector.getSelects();
            publishResult(items);
        }
    }

    @Override
    public void onSelectStateChanged(IImageItem item, boolean select) {
        //handle in selector and ui
        MediaResourceItem mrItem = (MediaResourceItem) item;
        if(select){
            mSelector.select(mrItem);
        }else {
            mSelector.unselect(mrItem);
        }
    }
    @Keep
    public void onClickClose(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Keep
    public void onClickSwitchDir(View view) {
        if(mRv_dir.getVisibility() == View.VISIBLE){
            mTv_folder.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.lib_pick_ic_arrow_down), null);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.lib_pick_album_out);
            anim.setAnimationListener(new AnimationListenerAdapter() {
                public void onAnimationEnd(Animation animation) {
                    //for a bug with 'fillAfter/fillEnable' = true. we must clear animation.
                    mRv_dir.clearAnimation();
                    mRv_dir.setVisibility(View.GONE);
                }
            });
            mRv_dir.startAnimation(anim);
        }else {
            mRv_dir.setVisibility(View.VISIBLE);
            mTv_folder.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(R.drawable.lib_pick_ic_arrow_up), null);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.lib_pick_album_in);
            anim.setAnimationListener(new AnimationListenerAdapter(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    mRv_dir.clearAnimation();
                }
            });
            mRv_dir.startAnimation(anim);
        }
    }

    private void setAdapter() {
        //onClickSwitchDir(null);
        Utils.closeDefaultAnimator(mRv_content);
        ContentAdapter adapter = new ContentAdapter(null);
        GridLayoutManager layoutManager = RecyclerViewUtils.createGridLayoutManager(adapter, this, mParam.getSpanCount());
        mRv_content.setLayoutManager(layoutManager);
        mRv_content.setAdapter(adapter);
        mRv_content.addOnScrollListener(new OptimiseScrollListenerImpl());

        mRv_dir.setLayoutManager(new LinearLayoutManager(this));
        mRv_dir.setAdapter(new DirAdapter(null));
    }

    private int getWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
    private void setSelectText() {
        //1 no need
        if(mParam.getMaxSelect() > 1){
            List<MediaResourceItem> selects = mSelector.getSelects();
            String text = getString(R.string.lib_pick_upload) + String.format("(%d/%d)", selects.size() , mParam.getMaxSelect());
            mTv_upload.setText(text);
        }
    }
    private void showByItems(PairItem pairItem) {
        mTv_folder.setText(pairItem.dirname);
        ContentAdapter adapter = (ContentAdapter) mRv_content.getAdapter();
        adapter.getAdapterManager().replaceAllItems(pairItem.items);
    }
    private void setDirAdapter(List<PairItem> list) {
        DirAdapter adapter = (DirAdapter) mRv_dir.getAdapter();
        adapter.getAdapterManager().replaceAllItems(list);
    }

    private List<PairItem> groupItems(ArrayList<MediaResourceItem> all) {
        return VisitServices.from(all).groupService(new ResultVisitor<MediaResourceItem, String>() {
                @Override
                public String visit(MediaResourceItem item, Object param) {
                    return FileUtils.getFileDir(item.getFilePath(), 1, false);
                }
            }).map(new MapResultVisitor<String, List<MediaResourceItem>, PairItem>() {
                @Override
                public PairItem visit(KeyValuePair<String, List<MediaResourceItem>> t, Object param) {
                    return new PairItem(t.getKey(), t.getValue());
                }
            }).getAsList();
    }

    @Override
    public void onCallback(List<MediaResourceItem> photoes, List<MediaResourceItem> videoes) {
        //default is all
        ArrayList<MediaResourceItem> all = new ArrayList<>(videoes);
        all.addAll(photoes);
        if(all.isEmpty()){
            return;
        }

        List<PairItem> list = groupItems(all);
        switch (mParam.getFlags()){
            case PickConstants.FLAG_IMAGE:
                //images: images.
                if(!photoes.isEmpty()){
                    list.add(0, new PairItem(getString(R.string.lib_pick_all_images), photoes));
                }
                break;
            case PickConstants.FLAG_VIDEO:
                //videos: videos.
                if(!videoes.isEmpty()){
                    list.add(0, new PairItem(getString(R.string.lib_pick_all_video), videoes));
                }
                break;
            case PickConstants.FLAG_IMAGE_AND_VIDEO:
                //all: all, images, videos
                if(!videoes.isEmpty()){
                    list.add(0, new PairItem(getString(R.string.lib_pick_all_video), videoes));
                }
                if(!photoes.isEmpty()){
                    list.add(0, new PairItem(getString(R.string.lib_pick_all_images), photoes));
                }
                if(!all.isEmpty()){
                    list.add(0, new PairItem(getString(R.string.lib_pick_all), all));
                }
                break;
        }
        setDirAdapter(list);
        // show content as 'all'
        showByItems(list.get(0));
    }

    @Override
    public void onSelect(List<MediaResourceItem> items, MediaResourceItem item) {
        notifyItemChangeIfNeed(item);
        setSelectText();
        Logger.d(TAG, "onSelect", item.getFilePath());
    }
    @Override
    public void onUnselect(List<MediaResourceItem> items, MediaResourceItem item) {
        notifyItemChangeIfNeed(item);
        setSelectText();
        Logger.d(TAG, "onUnselect", item.getFilePath());
    }

    private void notifyItemChangeIfNeed(MediaResourceItem item) {
        ContentAdapter adapter = (ContentAdapter) mRv_content.getAdapter();
        List<MediaResourceItem> aitems = adapter.getAdapterManager().getItems();
        int index = aitems.indexOf(item);
        if(index >= 0){
            adapter.getAdapterManager().notifyItemChanged(index);
        }
    }

    private class DirAdapter extends QuickRecycleViewAdapter<PairItem>{

        public DirAdapter(List<PairItem> mDatas) {
            super(R.layout.lib_pick_item_album_dir, mDatas);
        }

        @Override
        protected void onBindData(Context context, int position, final PairItem item, int itemLayoutId, ViewHelper2 helper) {
            if (Predicates.isEmpty(item.getItems())) {
                helper.setImageResource(R.id.iv_icon, mParam.getDefaultDirIconId());
            } else {
                helper.performViewGetter(R.id.iv_icon, new Getters.ImageViewGetter() {
                    @Override
                    public void onGotView(ImageView view, ViewHelper vp) {
                        Glide.with(view.getContext())
                                .load(new File(item.getItems().get(0).getFilePath()))
                                .asBitmap()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                .into(view);
                    }
                });
            }
            helper.setText(R.id.tv_dir_name, item.getDirName())
                    .setText(R.id.tv_count, item.getItems().size() + "")
                    .setRootOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showByItems(item);
                            onClickSwitchDir(null);
                        }
                    });
        }
    }

    private class ContentAdapter extends QuickRecycleViewAdapter<MediaResourceItem> {

        public ContentAdapter(List<MediaResourceItem> mDatas) {
            super(R.layout.lib_pick_item_select_image, mDatas);
        }

        @Override
        protected void onBindData(final Context context, final int position, final MediaResourceItem item,
                                  int itemLayoutId, ViewHelper2 helper) {
            View rootView = helper.getRootView();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
            lp.width = mItemWidth;
            lp.height = mItemHeight;
            lp.setMarginStart(position != 0 ? mParam.getSpace() : 0);
            rootView.setLayoutParams(lp);

            helper.setImageResource(R.id.iv_select_state, item.isSelected() ? R.drawable.lib_pick_ic_selected : R.drawable.lib_pick_ic_unselect)
                    .performViewGetter(R.id.iv, new Getters.ImageViewGetter() {
                        @Override
                        public void onGotView(ImageView view, ViewHelper viewHelper) {
                            Glide.with(context)
                                    .load(new File(item.getFilePath()))
                                    .asBitmap()
                                    .centerCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                    .into(view);
                        }
                    }).setOnClickListener(R.id.iv_select_state, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<MediaResourceItem> items = mSelector.getSelects();
                    if(mParam.getMaxSelect() > 1 && items != null && items.size() >= mParam.getMaxSelect()){
                        if(!items.contains(item)){ //if already contains .it will be cancel
                            Toaster.show(v.getContext(), getString(R.string.lib_pick_select_reach_max));
                            return;
                        }
                    }
                    mSelector.toggleSelect(item);
                }
            }).setRootOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<MediaResourceItem> items = getAdapterManager().getItems();
                    List<MediaResourceItem> selects = mSelector.getSelects();
                    MediaResourceItem selectItem = null;

                    int flags = PickConstants.FLAG_SHOW_TOP | PickConstants.FLAG_SHOW_BOTTOM
                            | PickConstants.FLAG_SHOW_BOTTOM_END_BUTTON | PickConstants.FLAG_SHOW_TOP_END_BUTTON;
                    if(mParam.getMaxSelect() > 1){
                        flags |= PickConstants.FLAG_MULTI_SELECT;
                    }else {
                        selectItem = selects.size() > 0 ? selects.get(0) : null;
                    }
                    BigImageSelectParameter param = new BigImageSelectParameter.Builder()
                            .setCurrentOrder(position + 1)
                            .setTotalCount(items.size())
                            .setSelectCount(selects.size())
                            .setMaxSelectCount(mParam.getMaxSelect())
                            .setTopRightText(getString(R.string.lib_pick_upload))
                            .setFlags(flags)
                            .build();
                    ImagePickManager.get().getImagePickDelegate()
                            .startBrowseBigImages(ImageSelectActivity.this, param, items, selectItem);
                }
            });
        }
    }
    /*private*/ static class PairItem extends BaseSelector {

        private final String dirname;
        private final List<MediaResourceItem> items;

        public PairItem(String dirname, List<MediaResourceItem> items) {
            this.dirname = dirname;
            this.items = items;
        }
        public String getDirName() {
            return dirname;
        }
        public List<MediaResourceItem> getItems() {
            return items;
        }
    }
}
