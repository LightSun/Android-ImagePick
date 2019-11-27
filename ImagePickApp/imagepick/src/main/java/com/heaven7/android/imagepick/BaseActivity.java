package com.heaven7.android.imagepick;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/*public*/ abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
       // ButterKnife.bind(this);
        init(this, savedInstanceState);

       /* View top = findViewById(R.id.vg_content);
        if(top != null){
            fixActionBarHeight(top);
        }*/
    }

    protected abstract int getLayoutId();

    protected abstract void init(Context context, Bundle savedInstanceState);

}
