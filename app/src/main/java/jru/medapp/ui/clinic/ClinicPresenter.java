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
        return realm.where(Clinic.class).equalTo(Constants.CLINIC_ID, id).findFirst();
    }

    void onStop() { realm.close();
    }
}
