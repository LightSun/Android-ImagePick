package com.heaven7.android.pick.app;

import com.heaven7.android.pick.app.sample.TestImagePickActivity;
import com.heaven7.android.pick.app.sample.TestViewPagerRecyclerViewActivity;

import java.util.List;

/**
 * Created by heaven7 on 2017/5/28.
 */
public class MainActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(TestImagePickActivity.class));
        list.add(new ActivityInfo(TestViewPagerRecyclerViewActivity.class));
    }
}
