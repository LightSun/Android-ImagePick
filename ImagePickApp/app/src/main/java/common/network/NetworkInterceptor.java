package common.network;

import android.os.Build;
import android.text.TextUtils;

import java.io.IOException;

import common.utils.DeviceUtil;
import common.utils.NetworkUtil;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class NetworkInterceptor implements Interceptor {
    private static final ResponseBody EMPTY_BODY = new ResponseBody() {
        @Override
        public MediaType contentType() {
            return null;
        }

        @Override
        public long contentLength() {
            return 0;
        }

        @Override
        public BufferedSource source() {
            return new Buffer();
        }
    };

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request requestOrigin = chain.request();
        Headers headersOrigin = requestOrigin.headers();

        Headers.Builder set = headersOrigin.newBuilder()
                .set("sys_platform", "android")
                .set("sys_version", String.valueOf(Build.VERSION.SDK_INT))
                .set("sys_versioncode", Build.VERSION.RELEASE)
                .set("sys_deviceid", DeviceUtil.getDeviceId())
                .set("app_channel", NetworkConfig.getChannel())
                .set("app_versionname", String.valueOf(NetworkConfig.getChannel()));
        String token = NetworkConfig.getToken();
        if (!TextUtils.isEmpty(token)) {
            set.set("app_token", String.valueOf(token));
        }
//        // TODO: 2017/9/7 需要删除
//        set.set("userId", "0e767ba59402470abe9cde81225caf70");
//        set.set("userId", "0023b5eb68914adfa73b081290ec629e");
        Headers build = set.build();
        Request request = requestOrigin.newBuilder().headers(build).build();
        Response response = null;
        try {
            response = chain.proceed(request);
        } finally {
            if (response == null) {
                if (NetworkUtil.getConnectivityStatus(NetworkConfig.getContext()) == NetworkUtil.TYPE_NOT_CONNECTED) {
                    return new Response.Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(504)
                            .message("网络环境较差，请检查网络配置")
                            .body(EMPTY_BODY)
                            .sentRequestAtMillis(-1L)
                            .receivedResponseAtMillis(System.currentTimeMillis())
                            .build();
                } else {
                    return new Response.Builder()
                            .request(chain.request())
                            .protocol(Protocol.HTTP_1_1)
                            .code(504)
                            .message("Unsatisfiable Request (only-if-cached)")
                            .body(EMPTY_BODY)
                            .sentRequestAtMillis(-1L)
                            .receivedResponseAtMillis(System.currentTimeMillis())
                            .build();
                }
            } else {
                return response;
            }
        }
    }
}