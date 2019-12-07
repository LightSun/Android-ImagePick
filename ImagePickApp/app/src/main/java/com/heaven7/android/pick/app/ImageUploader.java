package com.heaven7.android.pick.app;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heaven7.core.util.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import common.network.HttpLogInterceptor;
import common.network.HttpResult;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class ImageUploader{

    public static final int DEFAULT_TIMEOUT = 40;
    private final OkHttpClient mClient;

    public ImageUploader() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        if(BuildConfig.DEBUG){
            builder.addInterceptor(new HttpLogInterceptor());
        }
        mClient = builder.build();
    }

    public void uploadImage(String url, Map<String, String> headers, File file, Consumer<String> consumer, Consumer<Throwable> error) {
        uploadImage(url, headers, file, new SimpleDataConsumerCallback(consumer, error));
    }
    public void uploadImage(String url, Map<String, String> headers, File file, Callback callback) {
        String mime = getMime(file);
        RequestBody fileBody = RequestBody.create( mime != null ? MediaType.parse(mime) : null, file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("filedata", file.getAbsolutePath(), fileBody)
                .build();
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(requestBody);
        for (Map.Entry<String, String> en : headers.entrySet()){
            builder.addHeader(en.getKey(), en.getValue());
        }
        mClient.newCall(builder.build()).enqueue(callback);
    }

    private static String getMime(File file) {
        String path = file.getAbsolutePath();
        if(path.endsWith(".png") || path.endsWith(".PNG")){
            return "image/png";
        }
        if(path.endsWith(".jpg") || path.endsWith(".JPG")){
            return "image/jpg";
        }
        if(path.endsWith(".jpeg") || path.endsWith(".JPEG")){
            return "image/jpg";
        }
        if(path.endsWith(".gif") || path.endsWith(".GIF")){
            return "image/gif";
        }
        return null;
    }
    private static class ConsumerCallback implements Callback{

        private Consumer<String> consumer;
        private Consumer<Throwable> error;

        public ConsumerCallback(Consumer<String> consumer, Consumer<Throwable> error) {
            this.consumer = consumer;
            this.error = error;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            try {
                error.accept(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            try {
                ResponseBody body = response.body();
                if(body == null){
                    consumer.accept(null);
                }else {
                    String string = body.string();
                    Logger.d("ConsumerCallback", "onResponse", "" + string);
                    consumer.accept(string);
                }
            }catch (Exception e){
                try {
                    error.accept(e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    private static class SimpleDataConsumerCallback extends ConsumerCallback{

        public SimpleDataConsumerCallback(final Consumer<String> consumer, Consumer<Throwable> error) {
            super(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    HttpResult<String> result = new Gson().fromJson(s, new TypeToken<HttpResult<String>>() {
                    }.getType());
                    consumer.accept(result.getResult());
                }
            }, error);
        }
    }
}