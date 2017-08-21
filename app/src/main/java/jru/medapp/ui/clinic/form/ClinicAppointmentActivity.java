package jru.medapp.ui.clinic.form;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jru.medapp.R;
import jru.medapp.app.App;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ActivityClinicAppointmentBinding;
import jru.medapp.databinding.DialogSlotsBinding;
import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.Slot;

public class ClinicAppointmentActivity extends MvpActivity<ClinicAppointmentView, ClinicAppointmentPresenter> implements ClinicAppointmentView {

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
                presenter.getSlotsOnServer(pickedDate, clinic.getClinicId());
            }
        });


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");
    }


    @Override
    public void send() {
       if(timeSlot.equals("")|| timeSlot == null){
            showAlert("Pick Time Slot");
       }else {
           presenter.setAppointment(clinic.getClinicId(), App.getUser().getUserId(), pickedDate.trim(), timeSlot, binding.etNote.getText().toString());
       }
    }

    @Override
    public void pickTime() {
        if(slots !=null){
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
        }
        else {
            showAlert("Pick Date First");
        }



         /*dialogBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialogBinding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changePassword(dialogBinding.etCurrPassword.getText().toString(),
                        dialogBinding.etNewPassword.getText().toString(),
                        dialogBinding.etConfirmPass.getText().toString());
            }
        });*/
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
        finish();
    }

    @Override
    public void onSlotChosed(Slot slot){
        binding.slotTime.setText(slot.getSlotTime().trim());
        timeSlot = slot.getSlotTime();

        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }


    @Override
    public void onSetSlots() {

        if(slots!=null){
            slots.clear();
        }

        slots = new ArrayList<>();
        Slot slot1 = new Slot();
        slot1.setSlotId(1);
        slot1.setSlotTime("9:00 AM - 9:30 AM");
        slot1.setSlotStatus("OPEN");
        slots.add(slot1);

        Slot slot2 = new Slot();
        slot2.setSlotId(2);
        slot2.setSlotTime("9:30 AM - 10:00 AM");
        slot2.setSlotStatus("OPEN");
        slots.add(slot2);

        Slot slot3 = new Slot();
        slot3.setSlotId(3);
        slot3.setSlotTime("10:00 AM - 10:30 AM");
        slot3.setSlotStatus("OPEN");
        slots.add(slot3);

        Slot slot4 = new Slot();
        slot4.setSlotId(4);
        slot4.setSlotTime("10:30 AM - 11:00 AM");
        slot4.setSlotStatus("OPEN");
        slots.add(slot4);

        Slot slot5 = new Slot();
        slot5.setSlotId(5);
        slot5.setSlotTime("11:00 AM - 11:30 AM");
        slot5.setSlotStatus("OPEN");
        slots.add(slot5);

        Slot slot6 = new Slot();
        slot6.setSlotId(6);
        slot6.setSlotTime("11:30 AM - 12:00 PM");
        slot6.setSlotStatus("OPEN");
        slots.add(slot6);

        Slot slot7 = new Slot();
        slot7.setSlotId(7);
        slot7.setSlotTime("12:00 PM - 12:30 PM");
        slot7.setSlotStatus("OPEN");
        slots.add(slot7);

        Slot slot8 = new Slot();
        slot8.setSlotId(8);
        slot8.setSlotTime("12:30 PM - 1:30 PM");
        slot8.setSlotStatus("OPEN");
        slots.add(slot8);


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
