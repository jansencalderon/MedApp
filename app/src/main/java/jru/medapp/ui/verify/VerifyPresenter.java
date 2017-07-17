package jru.medapp.ui.verify;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import io.realm.Realm;
import jru.medapp.app.App;
import jru.medapp.app.Constants;
import jru.medapp.model.data.User;
import jru.medapp.model.response.ResultResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jansen on 6/19/2017.
 */

public class VerifyPresenter extends MvpNullObjectBasePresenter<VerifyView> {
    private Realm realm;
    private User user;
    private String TAG = VerifyPresenter.class.getSimpleName();

    public void checkCode(String code) {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
        if (code.isEmpty()) {
            getView().showAlert("Input code");
        } else {
            getView().startLoading();
            App.getInstance().getApiInterface().verify(user.getEmail(), code).enqueue(new Callback<ResultResponse>() {
                @Override
                public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getResult().equals(Constants.SUCCESS)) {
                            getView().onVerified();
                        } else {
                            getView().showAlert("Error: Wrong Code Entered");
                            getView().stopLoading();
                        }
                    } else {
                        Log.e(TAG, "onFailure: Error calling login api");
                        getView().stopLoading();
                        getView().showAlert("Error Connecting to Server");
                    }
                }

                @Override
                public void onFailure(Call<ResultResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: Error calling login api", t);
                    getView().stopLoading();
                    getView().showAlert("Error Connecting to Server");
                }
            });
        }
    }
}
