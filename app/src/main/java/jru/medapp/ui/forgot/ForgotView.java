package jru.medapp.ui.forgot;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Mark Jansen Calderon on 1/12/2017.
 */

public interface ForgotView extends MvpView {
    //start of view
    void OnButtonSubmit();

    void startLoading(String s);

    void stopLoading();

    void showAlert(String s);

    void onEmailExist();

    void OnButtonSubmitAnswer();

    void onPasswordReset();
}
