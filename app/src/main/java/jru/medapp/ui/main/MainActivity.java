package jru.medapp.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;
import jru.medapp.R;
import jru.medapp.app.Constants;
import jru.medapp.databinding.ActivityMainBinding;
import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.User;
import jru.medapp.ui.appointments.AppointmentActivity;
import jru.medapp.ui.clinic.ClinicActivity;
import jru.medapp.ui.login.LoginActivity;
import jru.medapp.ui.map.MapActivity;


public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private MainListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setView(getMvpView());
        presenter.onStart();
        setSupportActionBar(binding.toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);


        //display data
      /* binding.navigationView.getHeaderView(0).findViewById(R.id.viewProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
        */

        binding.navigationView.getMenu().getItem(0).setChecked(true);

        //adapter
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainListAdapter(getMvpView());
        binding.recyclerView.setAdapter(adapter);


        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.getClinics();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        final Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        if (user != null)
            displayUserData(user);
        realm.close();
    }


    @Override
    public void startLoading() {
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stopLoading() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }


    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter();
    }

    @Override
    public void displayUserData(User user) {
        // TextView email = (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.email);
        TextView name = (TextView) binding.navigationView.getHeaderView(0).findViewById(R.id.name);
        ImageView circleImageView = (ImageView) binding.navigationView.getHeaderView(0).findViewById(R.id.userImage);
        // email.setText(user.getEmail());
        name.setText(user.getFullName());

    }

    @Override
    public void showAlert(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void refreshList() {

    }

    @Override
    public void setClinics(List<Clinic> clinics) {
        adapter.setList(clinics);

    }


    @Override
    public void internet(Boolean status) {
        if (status) {
            binding.noInternet.noInternetLayout.setVisibility(View.VISIBLE);
        } else {
            binding.noInternet.noInternetLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnItemClicked
            (Clinic clinic) {
        Intent intent = new Intent(MainActivity.this, ClinicActivity.class);
        intent.putExtra("id", clinic.getClinicId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {

        } else if (id == R.id.map) {
            startActivity(new Intent(this, MapActivity.class));
            binding.navigationView.getMenu().getItem(0).setChecked(true);
        } else if (id == R.id.appointments) {
            startActivity(new Intent(this, AppointmentActivity.class));
            binding.navigationView.getMenu().getItem(0).setChecked(true);
        } else if (id == R.id.logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Log Out");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog
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
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            MainActivity.this.finish();
                        }
                    }, new Realm.Transaction.OnError() {
                        @Override
                        public void onError(Throwable error) {
                            realm.close();
                            Log.e(TAG, "onError: Error Logging out (deleting all data)", error);
                        }
                    });
                    finish();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

}
