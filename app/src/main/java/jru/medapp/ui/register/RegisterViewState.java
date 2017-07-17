package jru.medapp.ui.register;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;

import jru.medapp.app.Constants;


public class RegisterViewState implements RestorableViewState<RegisterView> {
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String birthday;
    private String contact;
    private String address;

    @Override
    public void saveInstanceState(@NonNull Bundle out) {
        out.putString(Constants.EMAIL, email);
        out.putString(Constants.PASSWORD, password);
        out.putString(Constants.CONFIRM_PASSWORD, confirmPassword);
        out.putString(Constants.FIRST_NAME, firstName);
        out.putString(Constants.LAST_NAME, lastName);
        out.putString(Constants.BIRTHDAY, birthday);
        out.putString(Constants.CONTACT,contact);
        out.putString(Constants.ADDRESS, address);
    }

    @Override
    public RestorableViewState<RegisterView> restoreInstanceState(Bundle in) {
        email = in.getString(Constants.EMAIL, "");
        password = in.getString(Constants.PASSWORD, "");
        confirmPassword = in.getString(Constants.CONFIRM_PASSWORD, "");
        firstName = in.getString(Constants.FIRST_NAME, "");
        lastName = in.getString(Constants.LAST_NAME, "");
        birthday = in.getString(Constants.BIRTHDAY, "");
        contact = in.getString(Constants.CONTACT, "");
        address = in.getString(Constants.ADDRESS, "");

        return this;
    }

    @Override
    public void apply(RegisterView view, boolean retained) {
        view.setEditTextValue(email, password, confirmPassword,firstName,lastName,birthday,contact,address);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
