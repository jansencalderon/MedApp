package jru.medapp.ui.clinic.form;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jru.medapp.R;
import jru.medapp.app.App;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ActivityClinicAppointmentBinding;
import jru.medapp.model.data.Clinic;
import jru.medapp.utils.DateTimeUtils;

public class ClinicAppointmentActivity extends MvpActivity<ClinicAppointmentView, ClinicAppointmentPresenter> implements ClinicAppointmentView {

    ActivityClinicAppointmentBinding binding;
    final Calendar c = Calendar.getInstance();
    private Clinic clinic;
    private String pickedTime;
    private String pickedDate;
    private Date closingdate;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_clinic_appointment);
        binding.setView(getMvpView());
        presenter.onStart();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent i = getIntent();
        clinic = presenter.getClinic(i.getIntExtra(Constants.ID, 0));
        binding.setClinic(clinic);


        // set time
        final Calendar c = Calendar.getInstance();


        String pattern = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            closingdate = sdf.parse(clinic.getClinicHoursClose());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.getTime().after(closingdate)) {
            binding.timeline.setFirstVisibleDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE) + 1);
            binding.timeline.setSelectedDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE) + 1);
            pickedDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) +"-"+ c.get(Calendar.DATE);
        } else {
            binding.timeline.setFirstVisibleDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            binding.timeline.setSelectedDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            pickedDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH)+1) +"-"+ c.get(Calendar.DATE);
        }
        c.add(Calendar.DATE, 7);
        binding.timeline.setLastVisibleDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        binding.timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                pickedDate = year + "-" + (month + 1) +"-"+ day;
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }


    @Override
    public void send() {
        presenter.setAppointment(clinic.getClinicId(), App.getUser().getUserId(), pickedDate + " " + pickedTime, binding.etNote.getText().toString());
    }

    @Override
    public void pickTime() {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String selectedDate = binding.timeline.getSelectedYear() + " " + binding.timeline.getSelectedMonth() + " " + binding.timeline.getSelectedDay();

                        Calendar temp = Calendar.getInstance();
                        temp.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        temp.set(Calendar.MINUTE, minute);
                        String pattern = "HH:mm:ss";
                        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                        String time = hourOfDay + ":" + minute + ":00";
                        Date hourPicked = null, hourOpen = null, hourClose = null;
                        try {
                            hourPicked = sdf.parse(time);
                            hourOpen = sdf.parse(clinic.getClinicHoursOpen());
                            hourClose = sdf.parse(clinic.getClinicHoursClose());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        // Launch Time Picker Dialog
                        if (selectedDate.equals(c.get(Calendar.YEAR) + " " + c.get(Calendar.MONTH) + " " + c.get(Calendar.DATE))) {
                            if (temp.before(Calendar.getInstance())) {
                                showAlert("Can't select past time");
                            } else if (hourPicked != null) {
                                if (hourPicked.before(hourOpen) || hourPicked.after(hourClose)) {
                                    showAlert("Choose time between " + clinic.getClinicHours());
                                } else {
                                    Calendar datetime = Calendar.getInstance();
                                    datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    datetime.set(Calendar.MINUTE, minute);
                                    SimpleDateFormat mSDF = new SimpleDateFormat("h:mm a");
                                    binding.pickTime.setText(mSDF.format(datetime.getTime()));  // make sure this is accessible
                                    sdf = new SimpleDateFormat("HH:mm:ss");
                                    pickedTime = sdf.format(datetime.getTime());
                                }
                            }
                        } else {
                            if (hourPicked != null) {
                                if (hourPicked.before(hourOpen) || hourPicked.after(hourClose)) {
                                    showAlert("Choose time between " + clinic.getClinicHours());
                                } else {
                                    Calendar datetime = Calendar.getInstance();
                                    datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    datetime.set(Calendar.MINUTE, minute);
                                    SimpleDateFormat mSDF = new SimpleDateFormat("h:mm a");
                                    binding.pickTime.setText(mSDF.format(datetime.getTime()));  // make sure this is accessible
                                    sdf = new SimpleDateFormat("HH:mm:ss");
                                    pickedTime = sdf.format(datetime.getTime());
                                }
                            }
                        }

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void showAlert(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
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
    public void onSetSuccess() {
        showAlert("AppointmentActivity Set");
        finish();
    }

    @NonNull
    @Override
    public ClinicAppointmentPresenter createPresenter() {
        return new ClinicAppointmentPresenter();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onStop();
    }


}
