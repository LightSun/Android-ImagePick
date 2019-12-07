package common.network;

import com.google.gson.annotations.SerializedName;

public class HttpResult<T> {
    private static final String TAG = HttpResult.class.getName();
    @SerializedName(value = "code")
    private int code;
    @SerializedName(value = "message")
    private String msg;
    @SerializedName(value = "data")
    private T result;

    private int mStatusSuccess = -1000;
    private int mStatusNoAccess = -1000;
    private int mStatuErrorAccess = -1000;

    public HttpResult() {
    }

    public HttpResult(int code, String msg, T result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    /**
     * @param statusSuccess  请求成功状态码
     * @param statusNoAccess 没有登录权限的状态码
     */
    public void initCode(int statusSuccess, int statusNoAccess, int statuErrorAccess) {
        mStatusSuccess = statusSuccess;
        mStatusNoAccess = statusNoAccess;
        mStatuErrorAccess = statuErrorAccess;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public boolean isSuccess() {
        if (-1000 == mStatusSuccess) {
            mStatusSuccess = NetworkConfig.getDefaultPlatformCode().getSuccess();
        }
        return mStatusSuccess == code;
    }

    public boolean isAccess() {
        if (-1000 == mStatusNoAccess) {
            mStatusNoAccess = NetworkConfig.getDefaultPlatformCode().getNoToken();
        }
        if (-1000 == mStatuErrorAccess) {
            mStatuErrorAccess = NetworkConfig.getDefaultPlatformCode().getErrorToken();
        }
//        return  3303 != code ;
        //402,401
        return (mStatusNoAccess != code) && (mStatuErrorAccess != code) && (21000 != code) && (3303 != code);
    }
}
