package jru.medapp.ui.clinic.form;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import jru.medapp.R;
import jru.medapp.app.App;
import jru.medapp.app.Constants;
import jru.medapp.model.data.AppointmentSlot;
import jru.medapp.model.data.Clinic;
import jru.medapp.model.response.ResultResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jansen on 7/21/2017.
 */

public class ClinicAppointmentPresenter extends MvpNullObjectBasePresenter<ClinicAppointmentView> {
    private Realm realm;
    private String TAG = ClinicAppointmentPresenter.class.getSimpleName();

    void onStart() {
        realm = Realm.getDefaultInstance();
    }

    Clinic getClinic(int id){
        return realm.where(Clinic.class).equalTo(Constants.CLINIC_ID, id).findFirst();
    }

    void onStop() { realm.close();
    }

    public void setAppointment(int clinicId, int userId, String date, String time_slot,String note) {
        getView().showAlert(clinicId +" "+userId+" "+date+" "+note);
        getView().startLoading();
        Map<String, String> params = new HashMap<>();
        params.put("clinic_id", clinicId+"");
        params.put("user_id", userId+"");
        params.put("trans_date", date);
        params.put("trans_time_slot", time_slot);
        params.put("trans_note", note);
        App.getInstance().getApiInterface().setAppointment(params).enqueue(new Callback<ResultResponse>() {
            @Override
            public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {
                getView().stopLoading();
                if (response.isSuccessful()) {
                    switch (response.body().getResult()) {
                        case Constants.SUCCESS:
                            getView().onSetSuccess();
                            break;
                        case Constants.EMAIL_EXIST:
                            getView().showAlert("Email already exists");
                            break;
                        default:
                            getView().showAlert(String.valueOf(R.string.oops));
                            break;
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        getView().showAlert(errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "onResponse: Error parsing error body as string", e);
                        getView().showAlert(response.message() != null ?
                                response.message() : "Unknown Exception");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResultResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: Error calling register api", t);
                getView().stopLoading();
                getView().showAlert("Error Connecting to Server");
            }
        });
    }

    void getSlotsOnServer(String date, int clinicId){
        getView().slotStartLoading();
        App.getInstance().getApiInterface().getSlots(date,clinicId).enqueue(new Callback<List<AppointmentSlot>>() {
            @Override
            public void onResponse(Call<List<AppointmentSlot>> call, final Response<List<AppointmentSlot>> response) {
                getView().slotStopLoading();
                if (response.isSuccessful()) {
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(AppointmentSlot.class);
                            realm.copyToRealmOrUpdate(response.body());
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            realm.close();
                            getView().onSetSlots();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            realm.close();
                            Log.e(TAG, "onError: Unable to save Data", error);
                        }
                    });
                }else {
                    try {
                        String errorBody = response.errorBody().string();
                        getView().showAlert(errorBody);
                    } catch (IOException e) {
                        Log.e(TAG, "onResponse: Error parsing error body as string", e);
                        getView().showAlert(response.message() != null ?
                                response.message() : "Unknown Exception");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<AppointmentSlot>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error calling register api", t);
                getView().slotStopLoading();
                getView().showAlert("Error Connecting to Server");
            }
        });
    }

    List<AppointmentSlot> appointmentSlotsPM(){
        return realm.where(AppointmentSlot.class).equalTo("transTimeSlot", "PM").findAll();
    }

    List<AppointmentSlot> appointmentSlotsAM(){
        return realm.where(AppointmentSlot.class).equalTo("transTimeSlot", "AM").findAll();
    }
}
