package jru.medapp.ui.register;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.viewstate.MvpViewStateActivity;
import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import jru.medapp.R;
import jru.medapp.databinding.ActivityRegisterBinding;


/**
 * Created by Mark Jansen Calderon on 1/11/2017.
 */

public class RegisterActivity extends MvpViewStateActivity<RegisterView, RegisterPresenter> implements RegisterView, TextWatcher {
    private ActivityRegisterBinding binding;
    private ProgressDialog progressDialog;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRetainInstance(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setView(getMvpView());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Spinner dropdown = binding.spinner1;
        String[] questions = getResources().getStringArray(R.array.list_questions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, questions);
        dropdown.setAdapter(adapter);

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

    /***
     * Start of MvpViewStateActivity
     ***/

    @NonNull
    @Override
    public RegisterPresenter createPresenter() {
        return new RegisterPresenter();
    }

    @NonNull
    @Override
    public ViewState<RegisterView> createViewState() {
        setRetainInstance(true);
        return new RegisterViewState();
    }

    @Override
    public void onNewViewStateInstance() {
        initializeViewStateValues();

    }


    /***
     * End of MvpViewStateActivity
     ***/


    @Override
    public void onSubmit() {
        presenter.register(
                binding.etEmail.getText().toString(),
                binding.etPassword.getText().toString(),
                binding.etConfirmPass.getText().toString(),
                binding.etFName.getText().toString(),
                binding.etLName.getText().toString(),
                binding.etBday.getText().toString(),
                binding.etContact.getText().toString(),
                binding.etAddress.getText().toString(),
                binding.spinner1.getSelectedItem().toString(),
                binding.etAnswer.getText().toString()
        );
    }

    @Override
    public void showAlert(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setEditTextValue(String email, String password, String confirmPassword, String firstName, String lastName, String birthday, String contact, String address) {
        binding.etEmail.setText(email);
        binding.etPassword.setText(password);
        binding.etConfirmPass.setText(confirmPassword);
        binding.etFName.setText(firstName);
        binding.etLName.setText(lastName);
        binding.etBday.setText(birthday);
        binding.etContact.setText(contact);
        binding.etAddress.setText(address);
    }

    @Override
    public void startLoading() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Signing up...");
        }
        progressDialog.show();
    }

    @Override
    public void stopLoading() {
        if (progressDialog != null) progressDialog.dismiss();
    }


    @Override
    public void onRegistrationSuccess() {
        new AlertDialog.Builder(this)
                .setTitle("Register Successful")
                .setMessage("Go Back to Login Activity\nAn email has been sent to your email for verification!")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RegisterActivity.this.finish();
                        // Toast.makeText(RegisterActivity.this, "An email has been sent to your email for verification!", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }


    @Override
    public void onSecret() {

    }

    @Override
    public void onBirthdayClicked() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                if ((Calendar.getInstance().get(Calendar.YEAR)-year) >= 8) {
                    binding.etBday.setText(dateFormatter.format(newDate.getTime()));
                } else {
                    showAlert("You must be 8 years old and above");
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    /***
     * End of RegisterView
     ***/

    /***
     * Start of TextWatcher
     ***/

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        initializeViewStateValues();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    /***
     * End of TextWatcher
     ***/

    private void initializeViewStateValues() {
        RegisterViewState registerViewState = (RegisterViewState) getViewState();
        registerViewState.setEmail(binding.etEmail.getText().toString());
        registerViewState.setPassword(binding.etPassword.getText().toString());
        registerViewState.setConfirmPassword(binding.etConfirmPass.getText().toString());
        registerViewState.setFirstName(binding.etFName.getText().toString());
        registerViewState.setLastName(binding.etLName.getText().toString());
        registerViewState.setContact(binding.etContact.getText().toString());
        registerViewState.setBirthday(binding.etContact.getText().toString());
        registerViewState.setAddress(binding.etAddress.getText().toString());

    }

}
