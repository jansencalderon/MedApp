package jru.medapp.ui.profile;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by Mark Jansen Calderon on 1/12/2017.
 */

public interface ProfileView extends MvpView{


    void showAlert(String message);

    void onEdit();

    void startLoading();

    void stopLoading();

    void onChangePassword();

    void finishAct();

    void onBirthdayClicked();

    void finish();

    void onPasswordChanged();
}
