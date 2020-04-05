package com.heaven7.android.imagepick;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.anno.Nullable;


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
