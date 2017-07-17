package jru.medapp.ui.verify;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import io.realm.Realm;
import jru.medapp.R;
import jru.medapp.databinding.ActivityVerifyBinding;
import jru.medapp.ui.login.LoginActivity;
import jru.medapp.ui.main.MainActivity;

/**
 * Created by Jansen on 6/19/2017.
 */


public class VerifyActivity extends MvpActivity<VerifyView, VerifyPresenter> implements VerifyView {
    private ActivityVerifyBinding binding;
    private ProgressDialog progressDialog;
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verify);
        binding.setView(getMvpView());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Checking code...");
    }


    @Override
    public void onVerified() {
        Intent i = new Intent(VerifyActivity.this, MainActivity.class);
        // set the new task and clear flags
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void showAlert(String message) {
        Toast.makeText(VerifyActivity.this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void OnButtonSubmit() {
        presenter.checkCode(binding.code.getText().toString());
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


    @NonNull
    @Override
    public VerifyPresenter createPresenter() {
        return new VerifyPresenter();
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
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Verification")
                .setMessage("Are you sure you want to go back?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Realm realm = Realm.getDefaultInstance();
                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.deleteAll();
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                realm.close();
                                // TODO: 12/4/2016 add flag to clear all task
                                startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
                                VerifyActivity.this.finish();
                            }
                        }, new Realm.Transaction.OnError() {
                            @Override
                            public void onError(Throwable error) {
                                realm.close();
                            }
                        });
                        finish();
                    }
                })
                .show();
    }
}