package lib.vida.video;

import androidx.annotation.IntDef;

/**
 * Created by heaven7 on 2018/11/9 0009.
 */
public class MediaViewCons {

    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_COVER = 2;
    public static final int TYPE_PAUSE = 3;
    public static final int TYPE_COVER_PAUSE = 4;

    @IntDef({TYPE_VIDEO, TYPE_COVER, TYPE_PAUSE , TYPE_COVER_PAUSE})
    public @interface TypeDef{

    }
}
