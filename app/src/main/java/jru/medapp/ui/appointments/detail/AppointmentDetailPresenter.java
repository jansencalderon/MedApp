package jru.medapp.ui.appointments.detail;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import io.realm.Realm;
import jru.medapp.app.App;
import jru.medapp.app.Constants;
import jru.medapp.model.data.Appointment;
import jru.medapp.utils.DateTimeUtils;

/**
 * Created by itsodeveloper on 05/10/2017.
 */

public class AppointmentDetailPresenter extends MvpNullObjectBasePresenter<AppointmentDetailView> {
    Realm realm;

    void onStart() {
        realm = Realm.getDefaultInstance();
    }

    public Appointment getAppointment(int id) {
        return realm.where(Appointment.class).equalTo("transId", id).findFirst();
    }

    public Appointment getAppointmentFromNotif(int clinicId, String date, String time) {
        return realm.where(Appointment.class)
                .equalTo("clinicId", clinicId)
                .equalTo("transDate", DateTimeUtils.StrToDateYYYY(date))
                .equalTo("transTimeSlot", time)
                .findFirst();
    }


    void onStop() {
        realm.close();
    }
}
