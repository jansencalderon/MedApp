package jru.medapp.ui.appointments;

import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import jru.medapp.model.data.Appointment;

/**
 * Created by Jansen on 7/22/2017.
 */

public interface AppointmentView extends MvpView {

    void startLoading();

    void stopLoading();

    void showAlert(String s);

    void setList(List<Appointment> list);

    void OnItemClicked(Appointment item);
}
