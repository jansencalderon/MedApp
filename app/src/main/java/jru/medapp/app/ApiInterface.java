package jru.medapp.app;


import java.util.List;
import java.util.Map;

import jru.medapp.model.data.Appointment;
import jru.medapp.model.data.AppointmentSlot;
import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.User;
import jru.medapp.model.response.AppointmentResponse;
import jru.medapp.model.response.LoginResponse;
import jru.medapp.model.response.ResultResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface ApiInterface {

    @FormUrlEncoded
    @POST(Endpoints.LOGIN)
    Call<LoginResponse> login(@Field(Constants.EMAIL) String username,
                              @Field(Constants.PASSWORD) String password);

    @FormUrlEncoded
    @POST(Endpoints.SAVE_USER_TOKEN)
    Call<ResultResponse> saveUserToken(@Field(Constants.USER_ID) String username,
                                       @Field("reg_token") String reg_token);

    @FormUrlEncoded
    @POST(Endpoints.DELETE_USER_TOKEN)
    Call<ResultResponse> deleteUserToken(@Field("reg_token") String reg_token);

    @FormUrlEncoded
    @POST(Endpoints.REGISTER)
    Call<ResultResponse> register(@Field(Constants.EMAIL) String username,
                                  @Field(Constants.PASSWORD) String password,
                                  @Field(Constants.FIRST_NAME) String firstName,
                                  @Field(Constants.LAST_NAME) String lastName,
                                  @Field(Constants.CONTACT) String contact,
                                  @Field(Constants.BIRTHDAY) String birthday,
                                  @Field(Constants.ADDRESS) String address,
                                  @Field(Constants.QUESTION) String question,
                                  @Field(Constants.ANSWER) String answer
    );

    @FormUrlEncoded
    @POST(Endpoints.VERIFY)
    Call<ResultResponse> verify(@Field(Constants.EMAIL) String email,
                                @Field(Constants.VER_CODE) String code);


    @Multipart
    @POST("updateUserWithImage")
    Call<User> updateUserWithImage(@Part MultipartBody.Part image,
                                   @Part(Constants.USER_ID) RequestBody user_id,
                                   @Part(Constants.FIRST_NAME) RequestBody first_name,
                                   @Part(Constants.LAST_NAME) RequestBody last_name,
                                   @Part(Constants.CONTACT) RequestBody contact,
                                   @Part(Constants.BIRTHDAY) RequestBody birthday,
                                   @Part(Constants.ADDRESS) RequestBody address);


    @FormUrlEncoded
    @POST("updateUser")
    Call<User> updateUser(@Field(Constants.USER_ID) String user_id,
                          @Field(Constants.FIRST_NAME) String first_name,
                          @Field(Constants.LAST_NAME) String last_name,
                          @Field(Constants.CONTACT) String contact,
                          @Field(Constants.BIRTHDAY) String birthday,
                          @Field(Constants.ADDRESS) String address);


    @FormUrlEncoded
    @POST("changePassword")
    Call<ResultResponse> changePassword(@Field(Constants.USER_ID) String user_id,
                                        @Field(Constants.PASSWORD) String password);


    @FormUrlEncoded
    @POST("passwordAlert")
    Call<ResultResponse> passwordAlert(@Field(Constants.EMAIL) String email);

    @FormUrlEncoded
    @POST("checkEmail")
    Call<ResultResponse> checkEmail(@Field(Constants.EMAIL) String email);


    @FormUrlEncoded
    @POST("checkAnswer")
    Call<ResultResponse> checkAnswer(@Field(Constants.EMAIL) String email,
                                     @Field(Constants.QUESTION) String question,
                                     @Field(Constants.ANSWER) String answer);

    @FormUrlEncoded
    @POST(Endpoints.VERIFY_RESEND_EMAIL)
    Call<ResultResponse> verifyResendEmail(@Field(Constants.USER_ID) String user_id);


    @Multipart
    @POST("upload.php")
    Call<ResultResponse> uploadImage(@Part MultipartBody.Part image);

    @POST("getClinics")
    Call<List<Clinic>> getClinics();


    @POST("setAppointment")
    @FormUrlEncoded
    Call<ResultResponse> setAppointment(@FieldMap Map<String, String> params);


    @POST("getAppointments")
    @FormUrlEncoded
    Call<List<Appointment>> getAppointments(@Field("user_id") String user_id);

    @POST("getSlots")
    @FormUrlEncoded
    Call<List<AppointmentSlot>> getSlots(@Field("dates") String dates,
                                         @Field("clinic_id") int clinic_id);



    @POST("changeStatus")
    @FormUrlEncoded
    Call<AppointmentResponse> changeStatus(@Field("trans_id") String trans_id,
                                           @Field("status") String status);
}
