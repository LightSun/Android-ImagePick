package com.heaven7.android.imagepick;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.android.imagepick.internal.LibUtils;
import com.heaven7.android.imagepick.pub.AdapterManageDelegate;
import com.heaven7.android.imagepick.pub.GroupItem;
import com.heaven7.android.imagepick.pub.IImageItem;
import com.heaven7.android.imagepick.pub.PickConstants;
import com.heaven7.android.imagepick.pub.SeeImageParameter;
import com.heaven7.android.imagepick.pub.delegate.SeeImageDelegate;
import com.heaven7.android.imagepick.utils.OptimiseScrollListenerImpl;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

/**
 * see image/videos.
 * @author heaven7
 */
public class SeeImageActivity extends BaseActivity implements SeeImageDelegate.MediaResourceCallback {

    private RecyclerView mRv_content;
    private RecyclerView mRv_dir;
    private ViewGroup mHeaderView;

    private SeeImageParameter mParam;

    private SeeImageDelegate mDelegate;
    private AdapterManageDelegate<IImageItem> mContentDelegate;
    private AdapterManageDelegate<GroupItem> mGroupDelagate;

    @Override
    protected int getLayoutId() {
        return R.layout.lib_pick_ac_see_image;
    }

    @Override
    protected void init(Context context, Bundle savedInstanceState) {
        mRv_content = findViewById(R.id.rv_content);
        mRv_dir = findViewById(R.id.rv_dir);
        mHeaderView = findViewById(R.id.vg_top);

        mParam = getIntent().getParcelableExtra(PickConstants.KEY_PARAMS);
        mDelegate = LibUtils.newInstance(getIntent().getStringExtra(PickConstants.KEY_DELEGATE));
        mDelegate.setProvider(new Provider0());
        mDelegate.initialize(mHeaderView, getIntent());

        setAdapter();

        mDelegate.startScan(this);
    }

    @Override
    public void finish() {
        mDelegate.onDestroy();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if(mDelegate.onBackPressed()){
            return;
        }
        super.onBackPressed();
    }

    private void setAdapter() {
        Utils.closeDefaultAnimator(mRv_content);
        mContentDelegate = mDelegate.setContentAdapter(mRv_content);
        mRv_content.addOnScrollListener(new OptimiseScrollListenerImpl(this));
        mGroupDelagate = mDelegate.setGroupAdapter(mRv_dir);
    }
    @Override
    public void onScanFinished(List<KeyValuePair<String, List<IImageItem>>> list) {
        final List<GroupItem> pairs = VisitServices.fromPairs(list).map(
                new ResultVisitor<KeyValuePair<String, List<IImageItem>>, GroupItem>() {
            @Override
            public GroupItem visit(KeyValuePair<String, List<IImageItem>> pair, Object param) {
                return new GroupItem(pair);
            }
        }).getAsList();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(pairs.isEmpty()){
                    mDelegate.onEmptyGroup();
                }else {
                    mGroupDelagate.addItems(pairs);
                    mDelegate.applyGroupItem(pairs.get(0));
                    mContentDelegate.setItems(pairs.get(0).getItems());
                }
            }
        });
    }

    private class Provider0 implements SeeImageDelegate.Provider{
        @Override
        public AppCompatActivity getActivity() {
            return SeeImageActivity.this;
        }
        @Override
        public SeeImageParameter getParameter() {
            return mParam;
        }
    }
}
