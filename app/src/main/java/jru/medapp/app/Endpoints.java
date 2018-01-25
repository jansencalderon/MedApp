package jru.medapp.app;

/**
 * Created by Cholo Mia on 12/4/2016.
 */

public class Endpoints {

    public static final String _ID = "{id}/";
    public static final String BASE_URL = "http://clinicappjru.000webhostapp.com";
    //public static final String BASE_URL = "http://127.0.0.1:8000";

    public static final String API_URL = BASE_URL + "/src/v1/";
    public static final String IMAGE_UPLOAD = BASE_URL + "/src/v1/";

    public static final String LOGIN = "loginUser";

    public static final String REGISTER = "registerUser";

    public static final String VERIFY = "verify";
    public static final String VERIFY_RESEND_EMAIL ="resendEmail";

    public static final String SAVE_USER_TOKEN = "saveUserToken";
    public static final String DELETE_USER_TOKEN = "deleteUserToken";
}
