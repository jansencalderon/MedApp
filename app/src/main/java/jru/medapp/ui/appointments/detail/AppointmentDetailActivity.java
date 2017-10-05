package jru.medapp.ui.appointments.detail;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import jru.medapp.R;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ActivityAppointmentDetailBinding;
import jru.medapp.model.data.Appointment;

public class AppointmentDetailActivity extends MvpActivity<AppointmentDetailView,AppointmentDetailPresenter> implements AppointmentDetailView{

    ActivityAppointmentDetailBinding binding;
    Appointment appointment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_appointment_detail);
        presenter.onStart();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        if(i.getStringExtra("from").equals("list")){
            appointment= presenter.getAppointment(i.getIntExtra(Constants.ID, -1));
            binding.setAppointment(appointment);
        }


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
    @NonNull
    @Override
    public AppointmentDetailPresenter createPresenter() {
        return new AppointmentDetailPresenter();
    }
}
