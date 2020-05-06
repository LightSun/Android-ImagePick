package com.heaven7.android.pick.app.sample;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.adapter.page.PageDataProvider;
import com.heaven7.adapter.page.PageViewProvider;
import com.heaven7.adapter.page.ViewPagerDelegate;
import com.heaven7.android.pick.app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class TestViewPagerRecyclerViewActivity extends AppCompatActivity {

    @BindView(R.id.vp)
    ViewPager mVP;

    private static final String[] ARRAY = {
            "http://img5.imgtn.bdimg.com/it/u=3050590552,1445108891&fm=11&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3356601016,43598563&fm=26&gp=0.jpg",
            "http://hbimg.b0.upaiyun.com/5507f33cdf77233ec4816d8e57407517a0f2477925557-FGnaJI_fw658",

            "http://img5.imgtn.bdimg.com/it/u=3050590552,1445108891&fm=11&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=3356601016,43598563&fm=26&gp=0.jpg",
            "http://hbimg.b0.upaiyun.com/5507f33cdf77233ec4816d8e57407517a0f2477925557-FGnaJI_fw658",
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ac_vp_nested_rv);

        ViewPagerDelegate.get(mVP).setAdapter(this,
                new PageDataProvider0(this),
                new PageViewProvider0(this),
                false);
    }

    private class PageDataProvider0 extends PageDataProvider<List<String>> {

        public PageDataProvider0(Context context) {
            super(context);
        }
        @Override
        public int getItemCount() {
            return 3;
        }
        @Override
        public List<String> getItem(int i) {
            return new ArrayList<>(Arrays.asList(ARRAY));
        }
    }
    private class PageViewProvider0 extends PageViewProvider<List<String>>{

        public PageViewProvider0(Context context) {
            super(context);
        }

        @Override
        public View createItemView(ViewGroup viewGroup, int i, int i1, List<String> s) {
            RecyclerView rv = new RecyclerView(getContext());
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            return rv;
        }
        @Override
        public void onBindItemView(View view, int i, int i1, List<String> s) {
            RecyclerView rv = (RecyclerView) view;

        }
    }
    public static final class Item extends BaseSelector{

    }

    //private class Adapter0 extends QuickRecycleViewAdapter<>
}
