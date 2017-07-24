package jru.medapp.ui.appointments;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import jru.medapp.app.App;
import jru.medapp.model.data.Appointment;
import jru.medapp.model.data.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jansen on 7/22/2017.
 */

class AppointmentPresenter extends MvpNullObjectBasePresenter<AppointmentView> {

    private Realm realm;
    private User user;
    private String TAG = AppointmentPresenter.class.getSimpleName();

    public void onStart() {
        realm = Realm.getDefaultInstance();
        user = App.getUser();
        getAppointments();
    }

    public void getAppointments(){
        App.getInstance().getApiInterface().getAppointments(String.valueOf(user.getUserId())).enqueue(new Callback<List<Appointment>>() {
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
                            setList();
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

    private void setList() {
        List<Appointment> list = realm.where(Appointment.class).findAll().sort("transDate", Sort.ASCENDING);
        getView().setList(list);
    }

    public void onStop() {
        realm.close();
    }

}
