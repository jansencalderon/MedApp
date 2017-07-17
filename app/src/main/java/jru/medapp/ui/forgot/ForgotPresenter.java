package jru.medapp.ui.forgot;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import jru.medapp.app.App;
import jru.medapp.model.response.ResultResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mark Jansen Calderon on 1/12/2017.
 */

public class ForgotPresenter extends MvpNullObjectBasePresenter<ForgotView> {
    private String TAG = ForgotPresenter.class.getSimpleName();

    public void submitEmail(String s) {
        getView().startLoading("Checking email...");
        App.getInstance().getApiInterface().checkEmail(s).enqueue(new Callback<ResultResponse>() {
            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                getView().stopLoading();
                if (response.body().getResult().equals("emailExisting")) {
                    getView().onEmailExist();
                } else {
                    getView().showAlert("Email does not exists");
                }
            }

            @Override
            public void onFailure(Call<ResultResponse> call, Throwable t) {
                getView().stopLoading();
                Log.e(TAG, "onFailure: Error calling register api", t);
                getView().stopLoading();
                getView().showAlert("Error Connecting to Server");
            }
        });
    }


    public void submitAnswer(String email, String question, final String answer) {
        if (answer.isEmpty()) {
            getView().showAlert("Input your answer");
        }
        {
            getView().startLoading("Checking your answer...");
            App.getInstance().getApiInterface().checkAnswer(email, question, answer).enqueue(new Callback<ResultResponse>() {
                @Override
                public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                    getView().stopLoading();
                    if(response.body().getResult().equals("success")){
                        getView().onPasswordReset();
                    }else {
                        getView().showAlert("Wrong answer/question");
                    }
                }

                @Override
                public void onFailure(Call<ResultResponse> call, Throwable t) {
                    getView().stopLoading();
                    Log.e(TAG, "onFailure: Error calling register api", t);
                    getView().stopLoading();
                    getView().showAlert("Error Connecting to Server");
                }
            });
        }
    }
}
