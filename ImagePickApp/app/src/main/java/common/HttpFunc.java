package common;


import android.text.TextUtils;

import com.heaven7.core.util.Logger;

import java.lang.reflect.Type;

import common.network.ApiException;
import common.network.HttpMethods;
import common.network.HttpResult;
import common.network.NetworkConfig;
import common.network.PlatformWraper;
import io.reactivex.functions.Function;

/**
 * Created by Administrator on 2017/2/16.
 */
public class HttpFunc<R> implements Function<String, R> {

    private Type mType;
    private String url;

    public HttpFunc(Type mType, String url) {
        this.mType = mType;
        this.url = url;
    }

    public HttpFunc(Type type) {
        mType = type;
    }

    @Override
    public R apply(String s) throws Exception {
        Logger.w("HttpFunc", "apply", "url = " + url + "\n" + s);
        if (TextUtils.isEmpty(s)) return null;
        HttpResult<R> result = HttpMethods.mGson.fromJson(s, mType);
        PlatformWraper.PlatformCode platformCode = NetworkConfig.getPlatformCode(PlatformWraper.API_PLATFORM_VALUE_PURCHASE);
        result.initCode(platformCode.getSuccess(), platformCode.getNoToken(), platformCode.getErrorToken());

        if (!result.isSuccess()) {
            throw new ApiException(result.getCode(), result.getMsg());
        }
        return result.getResult();
    }
}