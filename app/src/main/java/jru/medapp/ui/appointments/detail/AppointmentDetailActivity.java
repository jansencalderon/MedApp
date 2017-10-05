package jru.medapp.ui.appointments.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.Date;

import jru.medapp.R;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ActivityAppointmentDetailBinding;
import jru.medapp.model.data.Appointment;
import jru.medapp.utils.DateTimeUtils;

public class AppointmentDetailActivity extends MvpActivity<AppointmentDetailView, AppointmentDetailPresenter> implements AppointmentDetailView {

    ActivityAppointmentDetailBinding binding;
    Appointment appointment;
    private ProgressDialog progressDialog;
    private int clinicId;
    private String date;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail);
        presenter.onStart();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        if (i.getStringExtra("from").equals("list")) {
            appointment = presenter.getAppointment(i.getIntExtra(Constants.ID, -1));
            binding.setAppointment(appointment);
            Date date1 = DateTimeUtils.StrToDate(DateTimeUtils.DateToStrYYYY(appointment.getTransDate()) + " " + DateTimeUtils.TO_HH_MM_SS(appointment.getTransTimeSlot()));
            Log.d("TIME", "DATE2" + appointment.getTransDate() + " " + appointment.getTransTimeSlot());
            long date1Time = date1.getTime();
            long diff = date1Time - DateTimeUtils.getDateToday().getTime();
            Log.d("TIME", "DATE2" + DateTimeUtils.getDateToday().getTime());
            Log.d("TIME", "DATE1" + DateTimeUtils.getDateToday().getTime());
            Log.d("TIME", "DIFF" + diff);
            if (diff <= 10800000) {
                binding.resPanel.setVisibility(View.VISIBLE);
            } else {
                binding.resPanel.setVisibility(View.GONE);
            }
        }
        clinicId = i.getIntExtra("clinicId", 0);
        date = i.getStringExtra("date");
        time = i.getStringExtra("time");


        binding.resPanel.setVisibility(View.GONE);

        if (i.getStringExtra("from").equals("notif")) {
            Toast.makeText(this, "FROM NOTIF", Toast.LENGTH_SHORT).show();
            presenter.getAppointments();
            binding.resPanel.setVisibility(View.VISIBLE);
        }



        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("wait...");

    }


    @Override
    public void startLoading() {
        if (progressDialog != null) {
            progressDialog.show();
        }
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
    public void setAppointment() {
        appointment = presenter.getAppointmentFromNotif(clinicId, date, time);
        binding.setAppointment(appointment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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
