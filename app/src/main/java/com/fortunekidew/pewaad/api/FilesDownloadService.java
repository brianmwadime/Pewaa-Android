package com.fortunekidew.pewaad.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public interface FilesDownloadService {

    /**
     * method to download a small file size
     *
     * @param fileName this is  parameter for  downloadSmallFileSizeUrlSync method
     * @return this is return value
     */
    @GET
    Call<ResponseBody> downloadSmallFileSizeUrlSync(@Url String fileName);

    /**
     * method to download a large file size
     *
     * @param fileName this is   parameter for  downloadLargeFileSizeUrlSync method
     * @return this is return value
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadLargeFileSizeUrlSync(@Url String fileName);
}
