package jru.medapp.ui.appointments.detail;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jru.medapp.R;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ActivityAppointmentDetailBinding;
import jru.medapp.model.data.Appointment;
import jru.medapp.ui.clinic.form.ClinicAppointmentActivity;
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
            //String[] appointmentTime = appointment.getTransTimeSlot().split("-");
            String time = appointment.getTransTimeSlot();
            /*SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm:ss");
            try {
                time = date24Format.format(date12Format.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
                time = "";
            }*/


            Date date1 = DateTimeUtils.StrToDate(DateTimeUtils.DateToStrYYYY(appointment.getTransDate()) + " " + appointment.getTransTimeSlot());
            Log.d("TIME", "DATE2" + appointment.getTransDate() + " " + time);
            long date1Time = date1.getTime();
            long diff = date1Time - DateTimeUtils.getDateToday().getTime();
            Log.d("TIME", "DATE2" + DateTimeUtils.getDateToday().getTime());
            Log.d("TIME", "DATE1" + DateTimeUtils.getDateToday().getTime());
            Log.d("TIME", "DIFF" + diff);
            if (diff <= 10800000 && appointment.getTransStatus().equals("PENDING")) {
                binding.confirm.setVisibility(View.VISIBLE);
            } else {
                binding.confirm.setVisibility(View.GONE);
            }
        }
        clinicId = i.getIntExtra("clinicId", 0);
        date = i.getStringExtra("date");
        time = i.getStringExtra("time");
        Log.e(TAG, "Clinic ID: " + clinicId);
        Log.e(TAG, "Date: " + date);
        Log.e(TAG, "Time: " + time);


        binding.confirm.setVisibility(View.GONE);

        if (i.getStringExtra("from").equals("notif")) {
            presenter.getAppointments();
            binding.confirm.setVisibility(View.VISIBLE);
            fromNotif = true;
        }


        binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changeStatus(appointment.getTransId() + "", "CONFIRMED");
            }
        });


        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentDetailActivity.this);
                builder.setTitle("Cancel Appointment");
                builder.setMessage("Are you sure you want to cancel?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        presenter.changeStatus(appointment.getTransId() + "", "CANCELLED");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        if (appointment.getTransStatus().equals("PENDING"))
            binding.confirm.setVisibility(View.VISIBLE);
        else
            binding.confirm.setVisibility(View.GONE);


        if (appointment.getTransStatus().equals("CANCELLED") || appointment.getTransStatus().equals("DENIED")) {
            binding.cancel.setVisibility(View.GONE);
            binding.reSched.setVisibility(View.GONE);
        }

        // Toast.makeText(this, appointment.getReschedDate().toString() , Toast.LENGTH_SHORT).show();
        if (appointment.getReschedDate() != null)
            binding.reSched.setVisibility(View.GONE);
        else
            binding.reSched.setVisibility(View.VISIBLE);

        binding.reSched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppointmentDetailActivity.this, ClinicAppointmentActivity.class);
                intent.putExtra(Constants.ID, appointment.getTransId());
                intent.putExtra(Constants.FROM, "FROM_APPOINTMENT");
                startActivity(intent);
                finish();
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
        if (appointment.getTransStatus().equals("PENDING"))
            binding.confirm.setVisibility(View.VISIBLE);
        else
            binding.confirm.setVisibility(View.GONE);


        if (appointment.getReschedDate() == null)
            binding.reSched.setVisibility(View.GONE);
        else
            binding.reSched.setVisibility(View.VISIBLE);


        if (appointment.getTransStatus().equals("CANCELLED") || appointment.getTransStatus().equals("DENIED")) {
            binding.cancel.setVisibility(View.GONE);
            binding.reSched.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateAppointment(int id) {
        Appointment appointment = presenter.getAppointment(id);
        binding.setAppointment(appointment);
        if (appointment.getTransStatus().equals("PENDING"))
            binding.confirm.setVisibility(View.VISIBLE);
        else
            binding.confirm.setVisibility(View.GONE);


        if (appointment.getTransStatus().equals("CANCELLED") || appointment.getTransStatus().equals("DENIED")) {
            binding.cancel.setVisibility(View.GONE);
        }
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
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

    @Override
    public AppointmentDetailPresenter createPresenter() {
        return new AppointmentDetailPresenter();
    }
}
