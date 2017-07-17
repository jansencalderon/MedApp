package jru.medapp.ui.main;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.User;


/**
 * Created by Mark Jansen Calderon on 1/11/2017.
 */

public interface MainView extends MvpView {

    void stopLoading();

    void startLoading();

    void displayUserData(User user);

    void showAlert(String s);


    void refreshList();

    void setClinics(List<Clinic> clinics);

    void internet(Boolean status);

    void OnItemClicked(Clinic clinic);
}
