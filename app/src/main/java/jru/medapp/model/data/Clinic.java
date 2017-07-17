package jru.medapp.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

/**
 * Created by Jansen on 7/9/2017.
 */

public class Clinic extends RealmObject {
    @SerializedName("clinic_id")
    @Expose
    private Integer clinicId;
    @SerializedName("clinic_name")
    @Expose
    private String clinicName;
    @SerializedName("clinic_add")
    @Expose
    private String clinicAdd;
    @SerializedName("clinic_info")
    @Expose
    private String clinicInfo;
    @SerializedName("clinic_contact")
    @Expose
    private String clinicContact;
    @SerializedName("clinic_hours")
    @Expose
    private String clinicHours;
    @SerializedName("clinic_lat")
    @Expose
    private Double clinicLat;
    @SerializedName("clinic_lng")
    @Expose
    private Double clinicLng;
    @SerializedName("clinic_slot_left")
    @Expose
    private Integer clinicSlotLeft;
    @SerializedName("clinic_slot_max")
    @Expose
    private Integer clinicSlotMax;
    @SerializedName("clinic_image")
    @Expose
    private String clinicImage;
    @SerializedName("clinic_username")
    @Expose
    private String clinicUsername;
    @SerializedName("clinic_password")
    @Expose
    private String clinicPassword;

    public Integer getClinicId() {
        return clinicId;
    }

    public void setClinicId(Integer clinicId) {
        this.clinicId = clinicId;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicAdd() {
        return clinicAdd;
    }

    public void setClinicAdd(String clinicAdd) {
        this.clinicAdd = clinicAdd;
    }

    public String getClinicInfo() {
        return clinicInfo;
    }

    public void setClinicInfo(String clinicInfo) {
        this.clinicInfo = clinicInfo;
    }

    public String getClinicContact() {
        return clinicContact;
    }

    public void setClinicContact(String clinicContact) {
        this.clinicContact = clinicContact;
    }

    public String getClinicHours() {
        return clinicHours;
    }

    public void setClinicHours(String clinicHours) {
        this.clinicHours = clinicHours;
    }

    public Double getClinicLat() {
        return clinicLat;
    }

    public void setClinicLat(Double clinicLat) {
        this.clinicLat = clinicLat;
    }

    public Double getClinicLng() {
        return clinicLng;
    }

    public void setClinicLng(Double clinicLng) {
        this.clinicLng = clinicLng;
    }

    public Integer getClinicSlotLeft() {
        return clinicSlotLeft;
    }

    public void setClinicSlotLeft(Integer clinicSlotLeft) {
        this.clinicSlotLeft = clinicSlotLeft;
    }

    public Integer getClinicSlotMax() {
        return clinicSlotMax;
    }

    public void setClinicSlotMax(Integer clinicSlotMax) {
        this.clinicSlotMax = clinicSlotMax;
    }

    public String getClinicImage() {
        return clinicImage;
    }

    public void setClinicImage(String clinicImage) {
        this.clinicImage = clinicImage;
    }

    public String getClinicUsername() {
        return clinicUsername;
    }

    public void setClinicUsername(String clinicUsername) {
        this.clinicUsername = clinicUsername;
    }

    public String getClinicPassword() {
        return clinicPassword;
    }

    public void setClinicPassword(String clinicPassword) {
        this.clinicPassword = clinicPassword;
    }
}
