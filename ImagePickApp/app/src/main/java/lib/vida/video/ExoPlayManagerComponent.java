package lib.vida.video;

import android.content.Context;
import android.net.Uri;

import com.heaven7.android.component.lifecycle.LifeCycleComponent;

/**
 * Created by heaven7 on 2018/11/23 0023.
 */
public class ExoPlayManagerComponent implements LifeCycleComponent, ExoPlayerManager.Callback {

    private ExoPlayerManager mPM;
    private Object tag;
    private long mCurrentTime;

    public void setExoPlayerManager(ExoPlayerManager pm){
        this.mPM = pm;
        this.mCurrentTime = 0;
    }

    public ExoPlayerManager getExoPlayerManager() {
        return mPM;
    }
    /** in us */
    public long getCurrentTime() {
        return mCurrentTime;
    }
    public Object getTag() {
        return tag;
    }
    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void pause(){
        if(mPM != null){
            mPM.onPause();
        }
    }

    public void resume(){
        if(mPM != null){
            mPM.onResume();
        }
    }

    public void destroy(){
        if(mPM != null){
            mPM.release();
        }
    }

    public boolean hasPlayer() {
        return mPM != null;
    }

   /* public void cache(String url){
        DataSpec ds = new DataSpec(Uri.parse(url), 0, C.LENGTH_UNSET,
                url, DataSpec.FLAG_ALLOW_GZIP | DataSpec.FLAG_ALLOW_CACHING_UNKNOWN_LENGTH);
        //Cache cache = new SimpleCache(mPM.getContext().getCacheDir(),)
       // CacheUtil.cache();
    }*/

    @Override
    public void onLifeCycle(Context context, int lifeCycle) {
        switch (lifeCycle){
            case ON_PAUSE:
                pause();
                break;

            case ON_RESUME:
                resume();
                break;

            case ON_DESTROY:
                destroy();
                break;
        }
    }

    @Override
    public Uri getFileUri(Context context, String file) {
        return null;
    }
    @Override
    public void onPlayTimeChanged(long positionUs) {
        mCurrentTime = positionUs;
    }
}
