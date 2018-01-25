package jru.medapp.model.response;

import jru.medapp.model.data.Appointment;

/**
 * Created by Jansen on 10/6/2017.
 */

public class AppointmentResponse {
    public String result;
    public Appointment data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Appointment getData() {
        return data;
    }

    public void setData(Appointment data) {
        this.data = data;
    }
}
