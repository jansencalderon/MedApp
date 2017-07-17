package jru.medapp.ui.map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import jru.medapp.app.App;
import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.NearestClinic;
import jru.medapp.utils.MapUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by Jansen on 6/29/2017.
 */

public class MapPresenter extends MvpNullObjectBasePresenter<MapView> {
    private Realm realm;

    public void onStart() {
        realm = Realm.getDefaultInstance();
    }

    void getClinics(final String place) {
        getView().startLoading("Getting data...");
        App.getInstance().getApiInterface().getClinics().enqueue(new Callback<List<Clinic>>() {
            @Override
            public void onResponse(Call<List<Clinic>> call, final Response<List<Clinic>> response) {
                if (response.isSuccessful()) {
                    getView().stopLoading();
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
                            getView().updateMap();
                            switch (place){
                                case "Mandaluyong":
                                    getView().setMyMarker(new LatLng(14.5846217, 121.0211748), "Mandaluyong");
                                    break;
                                case "Pasig":
                                    getView().setMyMarker(new LatLng(14.5791233, 121.0462741), "Pasig");
                                    break;
                                case "San Juan":
                                    getView().setMyMarker(new LatLng(14.6022849, 121.0307368), "San Juan");
                                    break;
                            }
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            realm.close();
                            Log.e(TAG, "onError: Unable to save Clinicaurants", error);
                        }
                    });
                } else {
                    getView().stopLoading();
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


    public void onStop() {
        realm.close();
    }


    void getNearest(double latitude, double longitude, String place) {
        getView().startLoading("calculating distance...");
        List<Clinic> clinics = realm.where(Clinic.class).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(NearestClinic.class);
            }
        });

        for (Clinic clinic : clinics) {
            Double distance = MapUtils.distance(latitude, longitude, clinic.getClinicLat(), clinic.getClinicLng());
            final NearestClinic nearest = new NearestClinic();
            nearest.setClinicId(clinic.getClinicId());
            nearest.setClinicName(clinic.getClinicName());
            nearest.setClinicAdd(clinic.getClinicAdd());
            nearest.setClinicContact(clinic.getClinicContact());
            nearest.setClinicHours(clinic.getClinicHours());
            nearest.setClinicLat(clinic.getClinicLat());
            nearest.setClinicLng(clinic.getClinicLng());
            nearest.setClinicImage(clinic.getClinicImage());
            nearest.setDistance(distance);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(nearest);
                }
            });
        }
        getView().stopLoading();

    }
}
