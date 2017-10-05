package jru.medapp.ui.appointments.detail;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.util.List;

import io.realm.Realm;
import jru.medapp.app.App;
import jru.medapp.model.data.Appointment;
import jru.medapp.utils.DateTimeUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itsodeveloper on 05/10/2017.
 */

public class AppointmentDetailPresenter extends MvpNullObjectBasePresenter<AppointmentDetailView> {
    Realm realm;
    private String TAG = AppointmentDetailPresenter.class.getSimpleName();

    void onStart() {
        realm = Realm.getDefaultInstance();
    }

    public Appointment getAppointment(int id) {
        return realm.where(Appointment.class).equalTo("transId", id).findFirst();
    }

    void getAppointments(){
        getView().startLoading();
        App.getInstance().getApiInterface().getAppointments(App.getUser().getUserId()+"").enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(Call<List<Appointment>> call, final Response<List<Appointment>> response) {
                getView().stopLoading();
                if (response.isSuccessful()) {
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(Appointment.class);
                            realm.copyToRealmOrUpdate(response.body());
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            realm.close();
                            getView().setAppointment();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            realm.close();
                            Log.e(TAG, "onError: Unable to save Data", error);
                        }
                    });
                } else {
                    getView().showAlert(response.message() != null ? response.message()
                            : "Unknown Error");
                }
            }

            @Override
            public void onFailure(Call<List<Appointment>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error calling login api", t);
                getView().stopLoading();
                getView().showAlert("Error Connecting to Server");
            }
        });
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
