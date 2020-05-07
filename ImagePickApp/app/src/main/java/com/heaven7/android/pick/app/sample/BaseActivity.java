package com.heaven7.android.pick.app.sample;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);

        initialize(this, savedInstanceState);
    }

    protected abstract int getLayoutId();

    protected abstract void initialize(Context context, Bundle savedInstanceState);

}
