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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import jru.medapp.R;
import jru.medapp.databinding.ActivityMainBinding;
import jru.medapp.model.data.Clinic;
import jru.medapp.model.data.User;
import jru.medapp.ui.appointments.AppointmentActivity;
import jru.medapp.ui.clinic.ClinicActivity;
import jru.medapp.ui.login.LoginActivity;
import jru.medapp.ui.map.MapActivity;
import jru.medapp.ui.profile.ProfileActivity;


public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private MainListAdapter adapter;
    public String type = "";
    public String city = "";

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
        binding.navigationView.getHeaderView(0).findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });


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


        final List<String> items = new ArrayList<>();
        items.add("All");
        items.add("Ophthalmology");
        items.add("Dentistry");
        items.add("Dermatology");
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        binding.spinnerType.setAdapter(stringAdapter);
        binding.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (items.get(position)) {
                    case "Ophthalmology":
                        type = "ophthal.jpg";
                        break;
                    case "Dentistry":
                        type = "dental.jpg";
                        break;
                    case "Dermatology":
                        type = "derma.jpg";
                        break;
                    case "All":
                        type = "";
                        break;
                }
                presenter.filterList(type, city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final List<String> items2 = new ArrayList<>();
        items2.add("All");
        items2.add("Mandaluyong");
        items2.add("Pasig");
        items2.add("San Juan");
        ArrayAdapter<String> stringAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        binding.spinnerCity.setAdapter(stringAdapter2);
        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (items2.get(position).equals("All")) {
                    city = "";
                } else {
                    city = items2.get(position);
                }
                presenter.filterList(type, city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    public void setClinics(List<Clinic> clinics, Boolean filtered) {
        if(!filtered){
            binding.spinnerCity.setSelection(0);
            binding.spinnerType.setSelection(0);
        }
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
    public void onDestroy() {
        super.onDestroy();
        presenter.onStop();
    }

}
