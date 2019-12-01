package com.heaven7.android.imagepick.pub;

import com.heaven7.android.imagepick.MediaResourceHelper;

public interface PickConstants {

    String KEY_RESULT          = "result";
    String KEY_PARAMS           = "params";
    String KEY_SINGLE_ITEM       = "single_item";//only for single select

    /** the flags for see-big-image */
    int FLAG_SHOW_TOP             = 0x0001;
    int FLAG_SHOW_BOTTOM          = 0x0002;
    int FLAG_SHOW_TOP_END_BUTTON         = 0x0004;
    int FLAG_SHOW_BOTTOM_END_BUTTON      = 0x0008;
    int FLAG_MULTI_SELECT         = 0x0010;

    /** the flags for select in image-select */
    int FLAG_IMAGE = MediaResourceHelper.FLAG_IMAGE;
    int FLAG_VIDEO = MediaResourceHelper.FLAG_VIDEO;
    int FLAG_IMAGE_AND_VIDEO = MediaResourceHelper.FLAG_IMAGE_AND_VIDEO;

    int REQ_GALLERY = 998;
    int REQ_CAMERA  = 999;
    /** the request code indicate browse the big images. */
    int REQ_BROWSE_BIG_IMAGE  = 997;

}
