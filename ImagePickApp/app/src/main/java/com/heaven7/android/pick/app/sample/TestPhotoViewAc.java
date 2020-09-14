package com.heaven7.android.pick.app.sample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;

import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.heaven7.android.pick.app.R;
import com.heaven7.core.util.ImageParser;
import com.heaven7.core.util.Logger;

import butterknife.BindView;

public class TestPhotoViewAc extends BaseActivity {

    @BindView(R.id.pv)
    PhotoView mPv;

    private static final String TAG = "TestPhotoViewAc";
    static final String PATH = Environment.getExternalStorageDirectory() + "/DCIM/Camera/IMG_20200711_143034.jpg";

    @Override
    protected int getLayoutId() {
        return R.layout.ac_test_photo;
    }

    @Override
    protected void initialize(Context context, Bundle savedInstanceState) {

        //mPv
        /*mPv.setImageBitmap(BitmapFactory.decodeFile(PATH));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RectF displayRect = mPv.getDisplayRect();
                System.out.println("displayRect = " + displayRect);
                float scale = mPv.getScale();
                System.out.println("scale = " + scale);
            }
        });*/
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bm = BitmapFactory.decodeFile(PATH);
                    Display display = mPv.getDisplay();
                    ImageParser parser = new ImageParser(display.getWidth(), display.getHeight(), Bitmap.Config.RGB_565, true);
                    Bitmap bitmap = parser.parseToBitmap(PATH);
                    mPv.setImageBitmap(bitmap);
                    Logger.d(TAG, "bitmap info: " + String.format("raw (w, h) = (%d, %d), dst (w, h) = (%d, %d)",
                            bm.getWidth(), bm.getHeight(), bitmap.getWidth(), bitmap.getHeight()));
                    /*
                     * 1, store首次显示 缩放的比例。
                     * 2, 点击保存时，先计算贴图的实际大小。和实际位置。 (注意横竖屏)
                     * 3, 合并
                     */
                    mPv.setOnMatrixChangeListener(new OnMatrixChangedListener() {
                        @Override
                        public void onMatrixChanged(RectF rect) {
                            System.out.println("rect: " + rect);
                        }
                    });
                }
            });
        }
    }
}
