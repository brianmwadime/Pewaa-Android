package com.fortunekidew.pewaad.api;


import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.users.contacts.SyncContacts;
import com.fortunekidew.pewaad.models.users.status.EditStatus;
import com.fortunekidew.pewaad.models.users.status.StatusModel;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public interface APIContact {

    /**
     * method to  syncing all contacts
     *
     * @param listString this is parameter for  contacts method
     * @return this is return value
     */
    @POST(EndPoints.SEND_CONTACTS)
    Observable<List<ContactsModel>> contacts(@Body SyncContacts listString);

    /**
     * method to get contact info
     *
     * @param userID this is parameter for  contact method
     * @return this is return value
     */
    @GET(EndPoints.GET_CONTACT)
    Observable<ContactsModel> contact(@Path("userID") String userID);

    /**
     * method to get  user  status
     *
     * @return this is return value
     */
    @GET(EndPoints.GET_STATUS)
    Observable<List<StatusModel>> status();

    /**
     * method to delete user status
     *
     * @param statusID this is parameter for  delete status method
     * @return this is return value
     */
    @DELETE(EndPoints.DELETE_STATUS)
    Observable<StatusResponse> deleteStatus(@Path("statusID") int statusID);


    /**
     * method to delete all user status
     *
     * @return this is return value
     */
    @DELETE(EndPoints.DELETE_ALL_STATUS)
    Observable<StatusResponse> deleteAllStatus();

    /**
     * method to update user status
     *
     * @param statusID this is parameter for  update status method
     * @return this is return value
     */
    @PUT(EndPoints.UPDATE_STATUS)
    Observable<StatusResponse> updateStatus(@Path("statusID") int statusID);

    /**
     * method to edit user status
     *
     * @param editStatus this is parameter for  editStatus method
     * @return this is return value
     */
    @POST(EndPoints.EDIT_STATUS)
    Observable<StatusResponse> editStatus(@Body EditStatus editStatus);

    /**
     * method to edit username
     *
     * @param name this is parameter for  editUsername method
     * @return this is return value
     */
    @FormUrlEncoded
    @POST(EndPoints.EDIT_NAME)
    Observable<StatusResponse> editUsername(@Field("name") String name);

    /**
     * method to edit group name
     *
     * @param editStatus this is parameter for  editGroupName method
     * @return this is return value
     */
    @POST(EndPoints.EDIT_GROUP_NAME)
    Observable<StatusResponse> editGroupName(@Body EditStatus editStatus);

    /**
     * method to edit user image
     *
     * @param image this is parameter for  uploadImage method
     * @return this is return value
     */
    @Multipart
    @POST(EndPoints.UPLOAD_PROFILE_IMAGE)
    Call<StatusResponse> uploadImage(@Part("image\"; filename=\"profileImage\" ") RequestBody image);

    /**
     * method to delete account
     *
     * @param phone this is parameter for  uploadImage method
     * @return this is return value
     */

    @FormUrlEncoded
    @POST(EndPoints.DELETE_ACCOUNT)
    Observable<StatusResponse> deleteAccount(@Field("phone") String phone);
}
