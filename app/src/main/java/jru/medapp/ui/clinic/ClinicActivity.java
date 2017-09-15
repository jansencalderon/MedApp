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
import jru.medapp.ui.clinic.form.ClinicAppointmentActivity;
import jru.medapp.ui.main.MainActivity;
import jru.medapp.ui.map.MapActivity;

public class ClinicActivity extends MvpActivity<ClinicView, ClinicPresenter> implements ClinicView {

    ActivityClinicBinding binding;
    private Clinic clinic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_clinic);
        binding.setView(getMvpView());
        presenter.onStart();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        Intent i = getIntent();
        int id = i.getIntExtra(Constants.ID, -1);
        if(id == -1){
            finish();
        }

        clinic = presenter.getClinic(id);
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
        Intent intent = new Intent(ClinicActivity.this, ClinicAppointmentActivity.class);
        intent.putExtra(Constants.ID, clinic.getClinicId());
        startActivity(intent);
    }

    @NonNull
    @Override
    public ClinicPresenter createPresenter() {
        return new ClinicPresenter();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onStop();
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
