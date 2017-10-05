package jru.medapp.ui.clinic.form;

import com.hannesdorfmann.mosby.mvp.MvpView;

import jru.medapp.model.data.Slot;

/**
 * Created by Jansen on 7/21/2017.
 */

public interface ClinicAppointmentView extends MvpView {
    void send();



    void pickTime();

    void showAlert(String s);

    void startLoading();

    void stopLoading();

    void onSetSuccess();

    void notifTest();

    void onSlotChosed(Slot slot);

    void onSetSlots();
}
