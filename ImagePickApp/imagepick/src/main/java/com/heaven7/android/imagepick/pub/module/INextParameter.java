package com.heaven7.android.imagepick.pub.module;

import android.os.Bundle;

/**
 * the next parameter delegate
 * @since 2.0.0
 */
public interface INextParameter {

    /**
     * the parameter which will dispatch to next ui(like Activity.)
     * @return the bundle
     */
    Bundle getNext();
}
