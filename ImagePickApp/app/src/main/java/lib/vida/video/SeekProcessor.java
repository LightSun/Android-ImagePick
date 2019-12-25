package lib.vida.video;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;

import java.util.LinkedList;

/**
 * Created by heaven7 on 2018/12/8 0008.
 */
public class SeekProcessor  {

    private static final String TAG = "SeekProcessor";
    private static final long TIME_OUT = 50;
    private final LinkedList<Long> mSeekPositions = new LinkedList<>();
    private boolean mSeekHandled = true;
    private boolean mSeeking = false;

    protected final ExoPlayerManager mPlayerM;

    public SeekProcessor(ExoPlayerManager epm) {
        this.mPlayerM = epm;
    }

    public void markSeeking(boolean seeking){
        mSeeking = seeking;
    }

    public void seekTo(long position){
        if(mSeekHandled){
            mSeekHandled = false;
            mPlayerM.seekToImpl(position);
            MainWorker.postDelay(TIME_OUT, new Runnable() {
                @Override
                public void run() {
                    if(!mPlayerM.isReleased()){
                        startSeekNextIfNeed(position);
                    }
                }
            });
        }else{
            mSeekPositions.addFirst(position);
        }
    }
    public void seekToNextIfNeed() {
        Long pos = mSeekPositions.pollLast();
        if(pos != null){
            Logger.d(TAG, "seekToNextIfNeed", "pos = " + pos);
            seekTo(pos);
        }
    }
    public void startSeekNextIfNeed(){
        startSeekNextIfNeed(-1);
    }
    private void startSeekNextIfNeed(long position) {
        if(mSeeking){
            if(!mSeekHandled){
                if(position >= 0) {
                    Logger.d(TAG, "startSeekNextIfNeed", "TIME_OUT. position = " + position);
                }
                mSeekHandled = true;
                seekToNextIfNeed();
            }
        }else{
            Long head = mSeekPositions.pollFirst();
            if(head != null){
                mSeekPositions.clear();
                seekTo(head);
            }
        }
    }
}
