package jru.medapp.ui.appointments.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
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
    String TAG = AppointmentDetailActivity.class.getSimpleName();
    private Boolean fromNotif = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment_detail);
        presenter.onStart();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

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
        Log.e(TAG, "Clinic ID: " + clinicId);
        Log.e(TAG, "Date: " + date);
        Log.e(TAG, "Time: " + time);


        binding.resPanel.setVisibility(View.GONE);

        if (i.getStringExtra("from").equals("notif")) {
            presenter.getAppointments();
            binding.resPanel.setVisibility(View.VISIBLE);
            fromNotif = true;
        }


        binding.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changeStatus(appointment.getTransId() + "", "CONFIRMED");
            }
        });
        binding.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.resPanel.setVisibility(View.GONE);
            }
        });
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
        if (appointment.getTransStatus().equals("CONFIRMED"))
            binding.resPanel.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return false;
            case R.id.refresh:
                if (fromNotif) {
                    presenter.getAppointments();
                }
                return false;

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
