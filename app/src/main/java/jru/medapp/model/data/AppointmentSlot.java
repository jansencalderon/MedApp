package jru.medapp.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by Jansen on 7/23/2017.
 */

public class AppointmentSlot extends RealmObject{

    @SerializedName("trans_id")
    @Expose
    private Integer transId;
    @SerializedName("clinic_id")
    @Expose
    private Integer clinicId;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("trans_date")
    @Expose
    private Date transDate;
    @SerializedName("trans_time_slot")
    @Expose
    private String transTimeSlot;
    @SerializedName("trans_note")
    @Expose
    private String transNote;
    @SerializedName("trans_status")
    @Expose
    private String transStatus;
    @SerializedName("clinic")
    @Expose
    private Clinic clinic;

    public Integer getTransId() {
        return transId;
    }

    public void setTransId(Integer transId) {
        this.transId = transId;
    }

    public Integer getClinicId() {
        return clinicId;
    }

    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public String getTransNote() {
        return transNote;
    }

    public void setTransNote(String transNote) {
        this.transNote = transNote;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    public Clinic getClinic() {
        return clinic;
    }

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
    }


    public String getTransTimeSlot() {
        return transTimeSlot;
    }

    public void setTransTimeSlot(String transTimeSlot) {
        this.transTimeSlot = transTimeSlot;
    }
}
