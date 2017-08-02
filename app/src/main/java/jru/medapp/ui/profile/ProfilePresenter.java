package jru.medapp.ui.profile;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import io.realm.Realm;
import io.realm.RealmResults;
import jru.medapp.R;
import jru.medapp.app.App;
import jru.medapp.app.Constants;
import jru.medapp.model.data.User;
import jru.medapp.model.response.ResultResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mark Jansen Calderon on 1/12/2017.
 */

public class ProfilePresenter extends MvpNullObjectBasePresenter<ProfileView> {

    private static final String TAG = ProfilePresenter.class.getSimpleName();
    private Realm realm;
    private User user;

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
    }


    public void updateUser(String userId, String firstName, String lastName, String contact, String birthday, String address) {
        if (firstName.equals("") || lastName.equals("") || birthday.equals("") || contact.equals("") || address.equals("")) {
            getView().showAlert("Fill-up all fields");
        } else {
            getView().startLoading();
            App.getInstance().getApiInterface().updateUser(userId, firstName, lastName, contact, birthday, address)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, final Response<User> response) {
                            getView().stopLoading();
                            if (response.isSuccessful() && response.body().getUserId() == user.getUserId()) {
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        realm.copyToRealmOrUpdate(response.body());
                                        getView().finishAct();
                                    }
                                });
                            } else {
                                getView().showAlert("Oops something went wrong");
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            Log.e(TAG, "onFailure: Error calling login api", t);
                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }


    public void onStop() {
        realm.close();
    }

    void changePassword(String currPass, String newPass, String confirmNewPass) {
        final User user = App.getUser();
        if (currPass.equals(user.getPassword())) {

            if (confirmNewPass.equals(confirmNewPass.toLowerCase())) {
                getView().showAlert("Password must have Uppercase!");
            } else if (confirmNewPass.length() < 8) {
                getView().showAlert("Password must be atleast 8 characters");
            } else if (confirmNewPass.matches("[A-Za-z0-9 ]*")) {
                getView().showAlert("Password must have at least 1 numeric and special character");
            } else if (newPass.equals(confirmNewPass)) {
                getView().startLoading();
                App.getInstance().getApiInterface().changePassword(user.getUserId() + "", newPass).enqueue(new Callback<ResultResponse>() {
                    @Override
                    public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                        getView().stopLoading();
                        if (response.isSuccessful()) {
                            if (response.body().getResult().equals(Constants.SUCCESS)) {
                                getView().onPasswordChanged();
                            } else {
                                getView().showAlert(String.valueOf(R.string.oops));
                            }
                        } else {
                            getView().showAlert(response.message() != null ? response.message()
                                    : "Unknown Error");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultResponse> call, Throwable t) {
                        getView().stopLoading();
                        Log.e(TAG, "onFailure: Error calling login api", t);
                        getView().stopLoading();
                        getView().showAlert("Error Connecting to Server");
                    }
                });
            } else {
                getView().showAlert("New Password Mismatch");
            }
        } else {
            getView().showAlert("Wrong Current Password!");
        }
    }
}
