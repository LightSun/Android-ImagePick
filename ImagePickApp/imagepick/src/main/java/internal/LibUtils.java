package internal;

import androidx.annotation.RestrictTo;


/**
 * internal
 * @author heaven7
 * @since 1.0.5
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class LibUtils {

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className){
        try {
            return (T) Class.forName(className).newInstance();
        } catch (Exception e) {
            if(e instanceof RuntimeException){
                throw (RuntimeException)e;
            }else {
                throw new RuntimeException(e);
            }
        }
    }
}
