package jru.medapp.ui.login;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import io.realm.Realm;
import jru.medapp.R;
import jru.medapp.app.App;
import jru.medapp.app.Constants;
import jru.medapp.model.data.User;
import jru.medapp.model.response.LoginResponse;
import jru.medapp.model.response.ResultResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginPresenter extends MvpNullObjectBasePresenter<LoginView> {
    private int login_counter = 0;
    private static final String TAG = LoginPresenter.class.getSimpleName();

    public void login(final String email, final String password) {
        if (email.isEmpty() || email.equals("")) {
            getView().showAlert("Please enter email");
        } else if (password.isEmpty() || password.equals("")) {
            getView().showAlert("Please enter Password");
        } else {
            getView().startLoading();
            App.getInstance().getApiInterface().login(email, password)
                    .enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call,
                                               final Response<LoginResponse> response) {
                            getView().stopLoading();
                            if (response.isSuccessful()) {
                                try {
                                    switch (response.body().getResult()) {
                                        case Constants.SUCCESS:
                                            final Realm realm = Realm.getDefaultInstance();
                                            realm.executeTransactionAsync(new Realm.Transaction() {
                                                @Override
                                                public void execute(Realm realm) {
                                                    User user = response.body().getUser();
                                                    realm.copyToRealmOrUpdate(user);
                                                    getView().onLoginSuccess(user);
                                                }
                                            }, new Realm.Transaction.OnSuccess() {
                                                @Override
                                                public void onSuccess() {
                                                    realm.close();
                                                }
                                            }, new Realm.Transaction.OnError() {
                                                @Override
                                                public void onError(Throwable error) {
                                                    realm.close();
                                                    Log.e(TAG, "onError: Unable to save USER", error);
                                                    getView().showAlert("Error Saving API Response");
                                                }
                                            });
                                            break;
                                        case Constants.NOT_EXIST:
                                            getView().showAlert("Email does not exist");
                                        case Constants.WRONG_PASSWORD:
                                            getView().showAlert("Wrong Password");
                                            login_counter = login_counter + 1;
                                            if (login_counter == 10) {
                                                App.getInstance().getApiInterface().passwordAlert(email).enqueue(new Callback<ResultResponse>() {
                                                    @Override
                                                    public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                                                        if (response.body().getResult().equals(Constants.SUCCESS)) {
                                                            getView().showAlert("Admin has been notified of your suspicious activity!");
                                                        }

                                                    }

                                                    @Override
                                                    public void onFailure(Call<ResultResponse> call, Throwable t) {

                                                    }
                                                });
                                            }
                                            break;
                                        default:
                                            getView().showAlert(String.valueOf(R.string.oops));
                                            break;

                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    getView().showAlert("Oops");
                                }
                            } else {
                                getView().showAlert(response.message() != null ? response.message()
                                        : "Unknown Error");
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Log.e(TAG, "onFailure: Error calling login api", t);
                            getView().stopLoading();
                            getView().showAlert("Error Connecting to Server");
                        }
                    });
        }
    }
}
