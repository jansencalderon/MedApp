package jru.medapp.ui.clinic;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import io.realm.Realm;
import jru.medapp.app.Constants;
import jru.medapp.model.data.Clinic;

/**
 * Created by Jansen on 7/11/2017.
 */

class ClinicPresenter extends MvpNullObjectBasePresenter<ClinicView> {
    private Realm realm;

    void onStart() {
        realm = Realm.getDefaultInstance();
    }

    Clinic getClinic(int id){
        Clinic clinic = realm.where(Clinic.class).equalTo(Constants.CLINIC_ID, id).findFirst();
        return realm.copyToRealmOrUpdate(clinic);
    }

    void onStop() { realm.close();
    }
}
