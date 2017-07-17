package jru.medapp.ui.verify;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Jansen on 6/19/2017.
 */

public interface VerifyView extends MvpView {
    void onVerified();

    void showAlert(String message);

    void OnButtonSubmit();

    void startLoading();

    void stopLoading();
}
