package jru.medapp.ui.map;

import com.google.android.gms.maps.model.LatLng;
import com.hannesdorfmann.mosby.mvp.MvpView;

import java.util.List;

import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.NearestClinic;

/**
 * Created by Jansen on 6/29/2017.
 */

public interface MapView extends MvpView {
    void setMyMarker(LatLng latLng, String place);

    void onShowNearest();

    void setNearestClinic(List<NearestClinic> nearestClinic);

    void OnItemClicked(NearestClinic clinic);

    void startLoading(String s);

    void stopLoading();

    void showAlert(String s);

    void updateMap();
}
