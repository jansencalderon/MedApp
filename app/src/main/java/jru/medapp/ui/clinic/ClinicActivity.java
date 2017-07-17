package jru.medapp.ui.clinic;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import io.realm.Realm;
import jru.medapp.R;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ActivityClinicBinding;
import jru.medapp.databinding.DialogAppointmentFormBinding;
import jru.medapp.databinding.DialogNearestBinding;
import jru.medapp.model.data.Clinic;
import jru.medapp.ui.map.MapActivity;

public class ClinicActivity extends MvpActivity<ClinicView, ClinicPresenter> implements ClinicView {

    ActivityClinicBinding binding;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_clinic);
        binding.setView(getMvpView());
        realm = Realm.getDefaultInstance();


        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        Intent i = getIntent();
        Clinic clinic = realm.where(Clinic.class).equalTo("clinicId", i.getIntExtra("id", 0)).findFirst();
        binding.setClinic(clinic);

        switch (clinic.getClinicImage()) {
            case "ophthal.jpg":
                Glide.with(this).load(R.drawable.opht).into(binding.imageView);
                break;
            case "dental.jpg":
                Glide.with(this).load(R.drawable.dent).into(binding.imageView);
                break;
            case "derma.jpg":
                Glide.with(this).load(R.drawable.derm).into(binding.imageView);
                break;
        }


    }

    @Override
    public void setAppointment() {
        DialogAppointmentFormBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_appointment_form,
                null,
                false);
        final Dialog dialog = new Dialog(ClinicActivity.this);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ClinicActivity.this, "Feature not available yet", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @NonNull
    @Override
    public ClinicPresenter createPresenter() {
        return new ClinicPresenter();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
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
