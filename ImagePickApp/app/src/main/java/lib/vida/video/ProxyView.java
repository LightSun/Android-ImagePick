package lib.vida.video;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * a proxy view used to draw something.
 * Created by heaven7 on 2019/4/26.
 */
public class ProxyView extends View {

    private Callback mCallback;
    private boolean mDraw;

    public ProxyView(Context context) {
        this(context, null);
    }

    public ProxyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProxyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ProxyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public void setDraw(boolean mDraw) {
        if(this.mDraw != mDraw){
            this.mDraw = mDraw;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mDraw && mCallback != null){
             mCallback.onDraw(this, canvas);
        }
    }

    public interface Callback{
        void onDraw(View view, Canvas canvas);
    }
}
