package android.support.v4.view;

import android.support.annotation.RestrictTo;
import android.view.View;

/**
 * for access ViewPager.LayoutParams.position. we should use same package.
 * but for prevent conflict. we add a prefix.
 * @author heaven7
 */
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class LibPick$_ViewPagerUtils {

    public static View getCurrentView(ViewPager viewPager) {
        final int currentItem = viewPager.getCurrentItem();
        int count = viewPager.getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = viewPager.getChildAt(i);
            final ViewPager.LayoutParams layoutParams = (ViewPager.LayoutParams) child.getLayoutParams();
            if (!layoutParams.isDecor && currentItem == layoutParams.position) {
                return child;
            }
        }
        return null;
    }
}
