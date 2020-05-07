package com.heaven7.android.pick.app.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.heaven7.android.pick.app.R;
import com.heaven7.core.util.DimenUtil;

import org.heaven7.core.view.SlidingTabLayout;

import butterknife.BindView;

/**
 * 仿抖音ui .
 */
//TODO not done
public class TestFangDouYinActivity extends BaseActivity {

    @BindView(R.id.slidingLayout)
    SlidingTabLayout mSlidingTabLayout;

    @BindView(R.id.vp)
    ViewPager mVp;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_test_f_douyin;
    }

    @Override
    protected void initialize(Context context, Bundle savedInstanceState) {
        initSlidingTabLayout();
      //  mVp.setAdapter(mAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), createFragmentListData()));
        mVp.setOffscreenPageLimit(3);
        mSlidingTabLayout.setViewPager(mVp);
        mVp.setCurrentItem(0);
    }

    private void initSlidingTabLayout() {
        FragmentActivity activity = this;
        mSlidingTabLayout.setDrawBottomUnderLine(false);
        mSlidingTabLayout.setDrawHorizontalIndicator(true);
        mSlidingTabLayout.setSelectIndicatorHeight(DimenUtil.dip2px(activity, 2));
        //  mSlidingTabLayout.setSelectRelativeTextColorsRes(R.color.colorTheme, R.color.colorSecond);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.AbsTabColorizer(activity) {
            @Override
            protected int getIndicatorColorRes(int position) {
                return R.color.colorTheme;
            }

            @Override
            protected int getDividerColorRes(int position) {
                return android.R.color.transparent;
            }
        });
        mSlidingTabLayout.setCustomTabView(R.layout.tab_transport_bill, R.id.tv_tab);
        mSlidingTabLayout.setTabSelectDecoration(new SlidingTabLayout.TabSelectDecoration() {
            @Override
            public void onDecorate(TextView title, int position, boolean selected) {
                int color = selected ? getResources().getColor(R.color.colorTheme)
                        :  getResources().getColor(R.color.colorSecond);
                title.setTextColor(color);
                title.getPaint().setFakeBoldText(selected);
            }
        });
        mSlidingTabLayout.setOnPageChangeListener(new SlidingTabLayout.SlidingPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mSlidingTabLayout.toggleSelect(position);
            }
        });
        mSlidingTabLayout.setTabDecoration(new SlidingTabLayout.TabDecoration() {
            @Override
            public boolean drawHorizontalIndicator(Canvas canvas, Paint paint, Rect rect) {
                int width = rect.width();
                rect.inset(width / 6, 0);
                //hold 2/3
                canvas.drawRect(rect, paint);
                return true;
            }
            @Override
            public boolean drawBottomUnderline(Canvas canvas, Paint paint, Rect rect) {
                return false;
            }
            @Override
            public boolean drawVerticalIndicator(Canvas canvas, ViewGroup tabStrip, Paint dividerPaint, int separatorTop, int dividerHeightPx) {
                return false;
            }
        });
    }
}
