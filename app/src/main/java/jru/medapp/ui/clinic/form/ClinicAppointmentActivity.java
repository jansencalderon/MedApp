package jru.medapp.ui.clinic.form;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jru.medapp.R;
import jru.medapp.app.App;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ActivityClinicAppointmentBinding;
import jru.medapp.databinding.DialogAppointmentSuccessBinding;
import jru.medapp.databinding.DialogSlotsBinding;
import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.Slot;
import jru.medapp.ui.appointments.detail.AppointmentDetailActivity;
import jru.medapp.utils.DateTimeUtils;
import jru.medapp.utils.NotificationPublisher;

public class ClinicAppointmentActivity extends MvpActivity<ClinicAppointmentView, ClinicAppointmentPresenter> implements ClinicAppointmentView {
    private String TAG = ClinicAppointmentActivity.class.getSimpleName();
    ActivityClinicAppointmentBinding binding;
    final Calendar c = Calendar.getInstance();
    private Clinic clinic;
    private String pickedTime;
    private String pickedDate;
    private Date closingdate;
    private ProgressDialog progressDialog;
    private String timeSlot;
    private List<Slot> slots;
    private Dialog dialog;
    private int notifId;

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

        if (App.getUser().getUserId() == 3) {
            binding.test.setVisibility(View.VISIBLE);
        } else {
            binding.test.setVisibility(View.GONE);
        }


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
            pickedDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
        } else {
            binding.timeline.setFirstVisibleDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            binding.timeline.setSelectedDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            pickedDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE);
        }
        c.add(Calendar.DATE, 7);
        binding.timeline.setLastVisibleDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        binding.timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                pickedDate = year + "-" + (month + 1) + "-" + day;
                presenter.getSlotsOnServer(pickedDate, clinic.getClinicId());
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }


    @Override
    public void send() {
        if (timeSlot.equals("") || timeSlot == null) {
            showAlert("Pick Time Slot");
        } else {
            presenter.setAppointment(clinic.getClinicId(), App.getUser().getUserId(), pickedDate.trim(), timeSlot, binding.etNote.getText().toString());
        }
    }

    @Override
    public void pickTime() {
        if (slots != null) {
            dialog = new Dialog(ClinicAppointmentActivity.this);
            final DialogSlotsBinding dialogBinding = DataBindingUtil.inflate(
                    getLayoutInflater(),
                    R.layout.dialog_slots,
                    null,
                    false);

            dialogBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ClinicSlotListAdapter adapter = new ClinicSlotListAdapter(getMvpView());
            dialogBinding.recyclerView.setAdapter(adapter);
            adapter.setList(slots);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(dialogBinding.getRoot());
            dialog.show();
        } else {
            showAlert("Pick Date First");
        }

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
        DialogAppointmentSuccessBinding dialogBinding = DataBindingUtil.inflate(
                getLayoutInflater(),
                R.layout.dialog_appointment_success,
                null,
                false);
        dialogBinding.clinicName.setText(clinic.getClinicName());
        dialogBinding.patientName.setText(App.getUser().getFullName());
        dialogBinding.date.setText(pickedDate);
        dialogBinding.time.setText(binding.slotTime.getText().toString());

        final Dialog dialog = new Dialog(ClinicAppointmentActivity.this);
        dialog.setContentView(dialogBinding.getRoot());
        dialogBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ClinicAppointmentActivity.this.finish();
            }
        });
        dialog.show();

        Calendar cal = Calendar.getInstance();
        Date date1 = DateTimeUtils.StrToDate(pickedDate + " " + timeSlot);
        long date1Time = date1.getTime();
        long diff = (date1Time - 10800000) - cal.getTimeInMillis();

        notifId = presenter.generateID();
        scheduleNotification(getNotification(), diff);
        showAlert("Notification is set " + TimeUnit.MILLISECONDS.toHours(diff) + " hours from now");
      //  notifTest();
    }

    @Override
    public void notifTest() {
       /* Calendar cal = Calendar.getInstance();
        Date date1 = DateTimeUtils.StrToDate(pickedDate + " " + timeSlot);
        long date1Time = date1.getTime();
        long diff = (date1Time - 10800000) - cal.getTimeInMillis();

        Log.e("DIFF", "" + diff);
        showAlert("DIFF " + diff);
        */
        Calendar cal = Calendar.getInstance();
        notifId = presenter.generateID();
        scheduleNotification(getNotification(), 1);
    }

    private void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notifId);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notifId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delay, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + delay, pendingIntent);
        }
        Log.e(TAG, "Delay: " + delay);
    }

    private Notification getNotification() {

        Intent notificationIntent = new Intent(getApplicationContext(), AppointmentDetailActivity.class);
        notificationIntent.putExtra("from", "notif");
        notificationIntent.putExtra("clinicId", clinic.getClinicId());
        notificationIntent.putExtra("date", pickedDate);
        notificationIntent.putExtra("time", timeSlot);

        Log.e(TAG, "Clinic ID: " + clinic.getClinicId());
        Log.e(TAG, "Date: " + pickedDate);
        Log.e(TAG, "Time: " + timeSlot);


        /*notificationIntent.putExtra("from", "notif");
        notificationIntent.putExtra("clinicId", 7);
        notificationIntent.putExtra("date", "2017-10-08");
        notificationIntent.putExtra("time", "13:00:00");*/

        PendingIntent pendingIntent = PendingIntent.getActivity(this, notifId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Clinic Reservation");
        builder.setContentText("Please confirm your reservation to " + clinic.getClinicName());
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        return builder.build();
    }

    @Override
    public void onSlotChosed(Slot slot) {
        binding.slotTime.setText(DateTimeUtils.TO_AM_PM(slot.getSlotTime().trim()));
        timeSlot = slot.getSlotTime();

        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }


    @Override
    public void onSetSlots() {

        if (slots != null) {
            slots.clear();
        }

        int open = Integer.parseInt(clinic.getClinicHoursOpen().substring(0, 2));
        int close = Integer.parseInt(clinic.getClinicHoursClose().substring(0, 2));
        slots = new ArrayList<>();
        for (int i = open; i < close; i++) {
            Slot slot = new Slot();
            slot.setSlotId(i);
            slot.setSlotTime(i + ":00:00");
            slot.setSlotStatus("OPEN");
            slots.add(slot);
        }


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
