package com.heaven7.android.imagepick.pub;

public interface PickConstants {

    String KEY_RESULT          = "result";
    String KEY_PARAMS           = "params";
    String KEY_SINGLE_ITEM       = "single_item";//only for single select

    int FLAG_SHOW_TOP             = 0x0001;
    int FLAG_SHOW_BOTTOM          = 0x0002;
    int FLAG_SHOW_TOP_END_BUTTON         = 0x0004;
    int FLAG_SHOW_BOTTOM_END_BUTTON      = 0x0008;
    int FLAG_MULTI_SELECT         = 0x0010;


    int REQ_GALLERY = 998;
    int REQ_CAMERA  = 999;

}
