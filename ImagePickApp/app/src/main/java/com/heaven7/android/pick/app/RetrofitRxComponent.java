package com.heaven7.android.pick.app;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.Toaster;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.base.util.Throwables;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.HttpFunc;
import common.network.HttpMethods;
import common.network.HttpResult;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;

/**
 * @author heaven7
 */
public final class RetrofitRxComponent{

    private static final String TAG = "RetrofitRxComponent";
    private final List<Disposable> mTasks = new ArrayList<>();

    public Applier ofGet(String url, HashMap<String, Object> params) {
        return new Applier(url, this, HttpMethods
                .getInstance()
                .mApi
                .get(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        );
    }

    public Applier ofCommon(String url) {
        return new Applier(url, this, HttpMethods
                .getInstance()
                .mApi
                .getCommon(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        );
    }

    public Applier ofPost(String url, HashMap<String, Object> params) {
        return new Applier(url, this, HttpMethods
                .getInstance()
                .mApi
                .post(url, params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        );
    }

    public Applier ofPostBody(String url, String json) {
        return new Applier(url, this, HttpMethods
                .getInstance()
                .mApi
                .postBody(url, json)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        );
    }

    public Applier ofPostBody(String url, Map<String, Object> param) {
        return ofPostBody(url, HttpMethods.mGson.toJson(param));
    }

    public Applier ofUploadImage(String url, MultipartBody.Part part) {
        return new Applier(url, this, HttpMethods
                .getInstance()
                .mApi
                .uploadImage(/*url,*/ part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        );
    }
    public BatchUploader2 ofUploadImages(String url, List<String> files) {
        return new BatchUploader2(url, files);
    }
    /*public BatchUploader ofUploadImages(String url, List<String> files) {
        List<MultipartBody.Part> parts = VisitServices.from(files).map(new ResultVisitor<String, MultipartBody.Part>() {
            @Override
            public MultipartBody.Part visit(String s, Object param) {
                String mine = s.endsWith(".png") || s.endsWith(".PNG") ? "image/png" : "image/jpg";
                RequestBody requestBody = RequestBody.create(MediaType.parse(mine), s);
                return MultipartBody.Part.createFormData("filedata", s, requestBody);
            }
        }).getAsList();
        return new BatchUploader(url, this, parts);
    }*/

    private void addTask(Disposable subscribe) {
        mTasks.add(subscribe);
    }

    private void removeTask(Disposable subscribe) {
        mTasks.remove(subscribe);
    }

    /*@Override
    public void onLifeCycle(Context context, int lifeCycle) {
        if (lifeCycle == LifeCycleComponent.ON_DESTROY) {
            disposeAll();
        }
    }

    @Override
    public void onLifeCycle(LifecycleOwner context, int lifeCycle) {
        if (lifeCycle == LifeCycleComponent.ON_DESTROY) {
            disposeAll();
        }
    }
*/
    public void disposeAll() {
        for (Disposable disposable : mTasks) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
        mTasks.clear();
    }

    public static class BatchUploader2{
        final ImageUploader mUploader = new ImageUploader();
        final String url;
        final List<String> files;

        Consumer<String> consumer;
        Consumer<Throwable> error;
        Runnable finishTask;

        private int currentIndex = -1;

        public BatchUploader2(String url, List<String> files) {
            this.url = url;
            this.files = files;
        }
        public BatchUploader2 jsonConsumer(Consumer<String> consumer){
            this.consumer = consumer;
            return this;
        }
        public BatchUploader2 error(Consumer<Throwable> error){
            this.error = error;
            return this;
        }
        public BatchUploader2 finishTask(Runnable finishTask){
            this.finishTask = finishTask;
            return this;
        }
        public void subscribe(){
            currentIndex ++ ;
            final int index = currentIndex;
            Map<String, String> map = new HashMap<>();
            map.put("app_token","quick_login_271df2bef668405396561b9ccf00f9a2");
            mUploader.uploadImage(url, map, new File(files.get(index)), new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    Logger.w(TAG, "accept", "" + s);
                    consumer.accept(s);
                    if(index == files.size() - 1){
                        finishTask.run();
                    }else {
                        subscribe();
                    }
                }
            },error);
        }
    }
    public static class Applier {
        final String url;
        final RetrofitRxComponent mComponent;
        final Observable<String> ob;
        Consumer<String> consumer;
        Consumer<Throwable> error;

        Runnable often;

        Disposable disposable;

        /*public*/ Applier(String url, RetrofitRxComponent mComponent, Observable<String> observable) {
            this.url = url;
            this.mComponent = mComponent;
            this.ob = observable;
        }
        public Applier mustTask(Runnable task){
            this.often = task;
            return this;
        }

        public Applier consume(Consumer<String> consume) {
            this.consumer = consume;
            return this;
        }

        public <T> Applier jsonConsume(Type type, Consumer<T> consumer) {
            return consume(s -> {
                T result;
                runMustTask();
                try {
                    HttpFunc<T> func = new HttpFunc<T>(type, url);
                    result = func.apply(s);
                } catch (Exception e) {
                    if (error != null) {
                        error.accept(e);
                    }
                    return;
                } finally {
                    removeTask();
                }
                consumer.accept(result);
            });
        }

        public <T> Applier jsonConsume(TypeToken<HttpResult<T>> tt, Consumer<T> consumer) {
            return jsonConsume(tt.getType(), consumer);
        }

        public Applier jsonConsume(Consumer<String> consumer) {
            return consume(s -> {
                runMustTask();
                try {
                    HttpFunc<String> func = new HttpFunc<String>(new TypeToken<HttpResult<String>>() {
                    }.getType());
                    consumer.accept(func.apply(s));
                } catch (Exception e) {
                    if (error != null) {
                        error.accept(e);
                    }
                } finally {
                    removeTask();
                }
            });
        }
        public Applier error(Consumer<Throwable> error) {
            this.error = error;
            return this;
        }
        public Applier error(Context context,String error) {
            this.error = new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                    if(!TextUtils.isEmpty(error)){
                        Toaster.show(context, error);
                    }
                }
            };
            return this;
        }

        public void subscribe() {
            if (error == null) {
                error = new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        Logger.w(TAG, "subscribe", "url = " + url
                                + "\n" + Logger.toString(throwable));
                    }
                };
            }
            mComponent.addTask(disposable = ob.subscribe(consumer, error));
        }

        private void runMustTask() {
            if(often != null){
                often.run();
            }
        }

        private void removeTask() {
            if (disposable != null) {
                mComponent.removeTask(disposable);
            }
        }
    }


}
