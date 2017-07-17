package jru.medapp.ui.register;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Mark Jansen Calderon on 1/11/2017.
 */

public interface RegisterView extends MvpView {

    void onSubmit();

    void showAlert(String message);

    void setEditTextValue(String email, String password, String confirmPassword, String firstName, String lastName, String birthday, String contact, String address);

    void startLoading();

    void stopLoading();

    void onRegistrationSuccess();

    void onSecret();

    void onBirthdayClicked();
}
