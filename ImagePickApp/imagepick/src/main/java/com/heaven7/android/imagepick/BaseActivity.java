package com.heaven7.android.imagepick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.java.base.anno.Nullable;


/*public*/ abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreSetContentView();
        setContentView(getLayoutId());
       // ButterKnife.bind(this);
        initialize(this, savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Logger.d(TAG, "onNewIntent", "" + hashCode());
        setIntent(intent);
        initialize(this, null);
    }

    protected void onPreSetContentView(){

    }

    protected abstract int getLayoutId();

    protected abstract void initialize(Context context, Bundle savedInstanceState);

}
