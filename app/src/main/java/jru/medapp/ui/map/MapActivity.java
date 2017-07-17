package jru.medapp.ui.map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import jru.medapp.R;
import jru.medapp.databinding.ActivityMapsBinding;
import jru.medapp.databinding.DialogMapBinding;
import jru.medapp.databinding.DialogNearestBinding;
import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.NearestClinic;
import jru.medapp.ui.clinic.ClinicActivity;
import jru.medapp.ui.main.MainActivity;
import jru.medapp.utils.BitmapUtils;

public class MapActivity extends MvpActivity<MapView, MapPresenter> implements MapView, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    private Realm realm;
    private LatLngBounds bounds;
    private View markerUserIcon, markerOphthal, markerDental, markerDerma;
    private String TAG = MapActivity.class.getSimpleName();
    private Marker myMarker = null;
    private ActivityMapsBinding binding;
    private MapListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        binding.setView(getMvpView());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        presenter.onStart();


        markerDental = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_dental, null);
        markerDerma = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_derma, null);
        markerOphthal = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_ophthal, null);
        markerUserIcon = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_user, null);

        Spinner dropdown = binding.spinner1;
        final String[] items = new String[]{
                "Mandaluyong",
                "Pasig",
                "San Juan"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showAlert(items[position]);
                switch (items[position]) {
                    case "Mandaluyong":
                        presenter.getClinics("Mandaluyong");
                        break;
                    case "Pasig":
                        presenter.getClinics("Pasig");
                        break;
                    case "San Juan":
                        presenter.getClinics("San Juan");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.buttonShowNearest.setVisibility(View.GONE);
    }

    @Override
    public void setMyMarker(LatLng latLng, String place) {
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = mMap.addMarker(new MarkerOptions().position(latLng)
                .snippet("Me")
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.createDrawableFromView(MapActivity.this, markerUserIcon))));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myMarker.getPosition(), 13));


        presenter.getNearest(latLng.latitude, latLng.longitude, place);
        binding.buttonShowNearest.setVisibility(View.VISIBLE);
        binding.buttonShowNearest.setText("Clinics near " + place);
    }


    @NonNull
    @Override
    public MapPresenter createPresenter() {
        return new MapPresenter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);

        //create markers
        List<Clinic> clinics = realm.where(Clinic.class).findAll();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions markerOptions = new MarkerOptions();
        if (!clinics.isEmpty()) {
            for (Clinic clinic : clinics) {
                markerOptions.position(new LatLng(clinic.getClinicLat(), clinic.getClinicLng()));
                markerOptions.title(clinic.getClinicName());
                markerOptions.snippet(clinic.getClinicId() + "");
                markerOptions.icon(bitmapDescriptor("user"));
                builder.include(markerOptions.getPosition());
                mMap.addMarker(markerOptions);

            }
            bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
            mMap.animateCamera(cu);
        }


    }


    @Override
    public void onShowNearest() {
        //hide green button
        binding.buttonShowNearest.setVisibility(View.GONE);

        DialogNearestBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_nearest,
                null,
                false);
        final Dialog dialog = new Dialog(MapActivity.this);
        dialog.setContentView(dialogBinding.getRoot());

        /*dialogBinding.dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });*/

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                binding.buttonShowNearest.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });


        List<NearestClinic> nearestClinic;
        nearestClinic = realm.where(NearestClinic.class).findAll()
                .where().contains("clinicAdd", binding.spinner1.getSelectedItem().toString())
                .findAllSorted("distance", Sort.ASCENDING);
        //adapter
        dialogBinding.nearestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MapListAdapter(this);
        dialogBinding.nearestRecyclerView.setAdapter(adapter);
        setNearestClinic(nearestClinic);

        if (!nearestClinic.isEmpty()) {
            adapter.setList(nearestClinic);
            dialog.show();
        } else {
            showAlert("No clinics available at " + binding.spinner1.getSelectedItem().toString());
        }
    }


    @Override
    public void setNearestClinic(List<NearestClinic> list) {
        adapter.setList(list);
    }

    @Override
    public void OnItemClicked(NearestClinic clinic) {
        Intent intent = new Intent(MapActivity.this, ClinicActivity.class);
        intent.putExtra("id", clinic.getClinicId());
        startActivity(intent);
    }

    @Override
    public void startLoading(String s) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(s);
        }
        progressDialog.show();
    }

    @Override
    public void stopLoading() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    @Override
    public void showAlert(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateMap() {
        mMap.clear();
        List<Clinic> clinics = realm.where(Clinic.class).findAll().where().contains("clinicAdd", binding.spinner1.getSelectedItem().toString()).findAll();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        MarkerOptions markerOptions = new MarkerOptions();
        if (!clinics.isEmpty()) {
            for (Clinic clinic : clinics) {
                markerOptions.position(new LatLng(clinic.getClinicLat(), clinic.getClinicLng()));
                markerOptions.title(clinic.getClinicName());
                markerOptions.snippet(clinic.getClinicId() + "");
                markerOptions.icon(bitmapDescriptor(clinic.getClinicImage()));
                builder.include(markerOptions.getPosition());
                mMap.addMarker(markerOptions);

            }
            bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
            mMap.animateCamera(cu);
        }

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.getSnippet().equals("Me")) {

            final Clinic clinic = realm.where(Clinic.class).equalTo("clinicId", Integer.parseInt(marker.getSnippet())).findFirst();
            DialogMapBinding dialogBinding = DataBindingUtil.inflate(
                    getLayoutInflater(),
                    R.layout.dialog_map,
                    null,
                    false);
            final Dialog dialog = new Dialog(MapActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogBinding.getRoot());
            dialogBinding.setClinic(clinic);

            switch (clinic.getClinicImage()) {
                case "ophthal.jpg":
                    Glide.with(this).load(R.drawable.opht).into(dialogBinding.imageView);
                    break;
                case "dental.jpg":
                    Glide.with(this).load(R.drawable.dent).into(dialogBinding.imageView);
                    break;
                case "derma.jpg":
                    Glide.with(this).load(R.drawable.derm).into(dialogBinding.imageView);
                    break;
            }

            dialogBinding.viewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MapActivity.this, ClinicActivity.class);
                    intent.putExtra("id", clinic.getClinicId());
                    startActivity(intent);
                }
            });
            dialog.show();

            return true;
        } else {


            return false;
        }
    }

    public BitmapDescriptor bitmapDescriptor(String type) {
        BitmapDescriptor bitmapDescriptor = null;
        switch (type) {
            case "ophthal.jpg":
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapUtils.createDrawableFromView(this, markerOphthal));
                break;
            case "dental.jpg":
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapUtils.createDrawableFromView(this, markerDental));
                break;
            case "derma.jpg":
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapUtils.createDrawableFromView(this, markerDerma));
                break;
            case "user":
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapUtils.createDrawableFromView(this, markerUserIcon));
                break;
        }

        return bitmapDescriptor;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
