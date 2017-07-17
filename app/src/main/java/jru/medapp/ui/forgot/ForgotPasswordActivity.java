package jru.medapp.ui.forgot;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import jru.medapp.R;
import jru.medapp.databinding.ActivityForgotPasswordBinding;


/**
 * Created by Mark Jansen Calderon on 1/12/2017.
 */

public class ForgotPasswordActivity extends MvpActivity<ForgotView, ForgotPresenter> implements ForgotView {

    private ActivityForgotPasswordBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        binding.setView(getMvpView());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //add text listener && disable submit button
        binding.email.addTextChangedListener(codeWatcher);
        binding.submit.setEnabled(false);
        binding.submit.setAlpha(.5f);


        //add text listener && disable submit button
        binding.etAnswer.addTextChangedListener(codeWatcher2);
        binding.submitAnswer.setEnabled(false);
        binding.submitAnswer.setAlpha(.5f);



        Spinner dropdown = binding.spinner1;
        String[] questions = getResources().getStringArray(R.array.list_questions);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, questions);
        dropdown.setAdapter(adapter);
        binding.panel2.setVisibility(View.GONE);
    }

    private final TextWatcher codeWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                //enable submit button
                binding.submit.setEnabled(true);
                binding.submit.setAlpha(1f);
            } else {
                //disable submit button
                binding.submit.setEnabled(false);
                binding.submit.setAlpha(.5f);
            }
        }

        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                //enable submit button
                binding.submit.setEnabled(true);
                binding.submit.setAlpha(1f);
            } else {
                //disable submit button
                binding.submit.setEnabled(false);
                binding.submit.setAlpha(.5f);
            }
        }
    };

    private final TextWatcher codeWatcher2 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                //enable submit button
                binding.submitAnswer.setEnabled(true);
                binding.submitAnswer.setAlpha(1f);
            } else {
                //disable submit button
                binding.submitAnswer.setEnabled(false);
                binding.submitAnswer.setAlpha(.5f);
            }
        }

        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                //enable submit button
                binding.submit.setEnabled(true);
                binding.submit.setAlpha(1f);
            } else {
                //disable submit button
                binding.submit.setEnabled(false);
                binding.submit.setAlpha(.5f);
            }
        }
    };


    @Override
    public void startLoading(String s) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ForgotPasswordActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(s);
        }
        progressDialog.show();
    }

    @Override
    public void stopLoading() {
        if (progressDialog != null) progressDialog.dismiss();
    }

    //start of view
    @Override
    public void OnButtonSubmit() {
        presenter.submitEmail(binding.email.getText().toString());
    }

    @Override
    public void showAlert(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmailExist() {
        binding.panel1.setVisibility(View.GONE);
        binding.panel2.setVisibility(View.VISIBLE);
    }

    @Override
    public void OnButtonSubmitAnswer() {
        presenter.submitAnswer(binding.email.getText().toString(),
                binding.spinner1.getSelectedItem().toString(),
                binding.etAnswer.getText().toString());
    }

    @Override
    public void onPasswordReset() {
        new AlertDialog.Builder(this)
                .setTitle("Password Reset Successful!")
                .setMessage("We sent a temporary password on your email, you can change it once you've logged in!")
                .setCancelable(false)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ForgotPasswordActivity.this.finish();
                        // Toast.makeText(RegisterActivity.this, "An email has been sent to your email for verification!", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
    //end of view

    @NonNull
    @Override
    public ForgotPresenter createPresenter() {
        return new ForgotPresenter();
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
