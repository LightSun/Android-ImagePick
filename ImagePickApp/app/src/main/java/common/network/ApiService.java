package common.network;


import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {
    @GET
    Observable<String> getCommon(@Url String url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST
    Observable<String> postBody(@Url String path, @Body String body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST
    Observable<String> postBody(@Url String path);

    @GET
    Observable<String> get(@Url String path, @QueryMap HashMap<String, Object> params);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @DELETE
    Observable<String> delete(@Url String path, @Body String body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @DELETE
    Observable<String> delete(@Url String path);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PATCH
    Observable<String> patch(@Url String path, @Body String body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PATCH
    Observable<String> patch(@Url String path);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PUT
    Observable<String> put(@Url String path, @Body String body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PUT
    Observable<String> put(@Url String path);

    @FormUrlEncoded
    @POST
    Observable<String> post(@Url String url, @FieldMap HashMap<String, Object> params);


    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @Streaming
    @POST
    Observable<ResponseBody> downloadPost(@Url String url, @Body String body);

    /**
     * 上传一张图片
     *
     * @return
     */
    @Multipart
    @POST("v1/image")
    Observable<String> uploadImage(@Part MultipartBody.Part photo);

    @Multipart
    @POST
    Observable<String> uploadImage(@Url String url, @Part("filedata") MultipartBody.Part photo);

//    /**
//     * 上传多张图片
//     *
//     * @return
//     */
//    @Multipart
//    @POST("/file/upload/image")
//    Observable<String> uploadImages(@PartMap Map<String, RequestBody> params);
}
