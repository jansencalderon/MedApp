package jru.medapp.ui.appointments.detail;

import com.hannesdorfmann.mosby.mvp.MvpView;

/**
 * Created by itsodeveloper on 05/10/2017.
 */

public interface AppointmentDetailView extends MvpView {
    void startLoading();

    void stopLoading();

    void showAlert(String s);

    void setAppointment();

    void updateAppointment(int id);
}
