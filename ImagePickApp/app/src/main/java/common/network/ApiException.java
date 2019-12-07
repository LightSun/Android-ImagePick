package common.network;

/**
 * Created by liukun on 16/3/10.
 */
public class ApiException extends RuntimeException {

    private int mErrorCode;

    public ApiException(int resultCode, String msg) {
        super(msg);
        this.mErrorCode = resultCode;
    }

    public int getmErrorCode() {
        return mErrorCode;
    }
}

