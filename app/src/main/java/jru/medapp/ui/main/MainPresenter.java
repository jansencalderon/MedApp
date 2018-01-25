package jru.medapp.ui.main;

import android.util.Log;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import jru.medapp.app.App;
import jru.medapp.model.data.Clinic;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mark Jansen Calderon on 1/11/2017.
 */

class MainPresenter extends MvpNullObjectBasePresenter<MainView> {
    private Realm realm;
    private String TAG = MainPresenter.class.getSimpleName();
    private RealmResults<Clinic> Clinics;

    void onStart() {
        realm = Realm.getDefaultInstance();
        getClinics();
        Clinics = realm.where(Clinic.class).findAll();

    }

    void getClinics() {
        getView().startLoading();
        App.getInstance().getApiInterface().getClinics().enqueue(new Callback<List<Clinic>>() {
            @Override
            public void onResponse(Call<List<Clinic>> call, final Response<List<Clinic>> response) {
                getView().stopLoading();
                if (response.isSuccessful()) {
                    final Realm realm = Realm.getDefaultInstance();
                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.delete(Clinic.class);
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
            public void onFailure(Call<List<Clinic>> call, Throwable t) {
                Log.e(TAG, "onFailure: Error calling login api", t);
                getView().stopLoading();
                getView().showAlert("Error Connecting to Server");
            }
        });

    }

    private void setList() {
        List<Clinic> clinics;
        clinics = realm.copyFromRealm(Clinics);
        getView().setClinics(clinics, false);

    }


    public void onStop() {
        realm.close();
    }


    public void filterList(String type, String city) {
        List<Clinic> clinics;
        Log.e(TAG, "Type: "+type);
        Log.e(TAG, "City: "+city);
        if (city.equals("") && type.equals("")) {
            clinics = realm.copyFromRealm(Clinics);
        } else if (city.equals("") && !type.equals("")) {
            clinics = realm.copyFromRealm(Clinics.where().equalTo("clinicImage", type).findAll());
        } else if (!city.equals("") && type.equals("")) {
            clinics = realm.copyFromRealm(Clinics.where().contains("clinicAdd", city).findAll());
        } else {
            clinics = realm.copyFromRealm(Clinics.where()
                    .contains("clinicAdd", city)
                    .equalTo("clinicImage", type)
                    .findAll());
        }
        getView().setClinics(clinics, true);
    }
}
