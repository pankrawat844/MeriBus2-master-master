package paztechnologies.com.meribus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Signup extends AppCompatActivity {
    AwesomeValidation mAwesomeValidation;
    Button register;
    String strResponce, refferal_response,duplicater_response;
    EditText username, email, phoneno, password, refferal;
    RadioGroup gender;
    ImageView refferal_btn;
    String gender_string = "male";
    int randomInt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        init();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           //     mAwesomeValidation.validate();
                Connectivity connectivity = new Connectivity();

                if (connectivity.isNetworkAvilable(Signup.this)) {
                    // new Call_Service().execute();
                   new Check_Duplicate().execute();
                } else {
                    //If login fails
                    Toast.makeText(Signup.this, "Internet is not Connected,Please Try Again", Toast.LENGTH_LONG).show();
                }
            }
        });
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton male = (RadioButton) findViewById(R.id.male);
                if (male.isChecked()) {
                    gender_string = "male";
                } else {
                    gender_string = "female";
                }
            }
        });

        refferal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Refferal_Service().execute();
            }
        });
    }

    private void init() {
        register = (Button) findViewById(R.id.update_profile);
        mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(this, R.id.activity_register_username, "^[A-Za-z\\\\s]{1,}[\\\\.]{0,1}[A-Za-z\\\\s]{0,}$", R.string.username_error);
        mAwesomeValidation.addValidation(this, R.id.activity_register_email, "[a-zA-Z\\s]+", R.string.email_error);
        mAwesomeValidation.addValidation(this, R.id.activity_register_email, Patterns.EMAIL_ADDRESS, R.string.email_error1);

        mAwesomeValidation.addValidation(this, R.id.activity_phone_no, "^[2-9]{2}[0-9]{8}$", R.string.phno_error);
        mAwesomeValidation.addValidation(this, R.id.activity_register_password, "[a-zA-Z\\s]+", R.string.password_error);
//        mAwesomeValidation.addValidation(this, R.id.activity_residence_add, "[a-zA-Z\\s]+", R.string.address_error);
//        mAwesomeValidation.addValidation(this, R.id.activity_office_add, "[a-zA-Z\\s]+", R.string.address_error);

        username = (EditText) findViewById(R.id.activity_register_username);
        email = (EditText) findViewById(R.id.activity_register_email);
        password = (EditText) findViewById(R.id.activity_register_password);
        phoneno = (EditText) findViewById(R.id.activity_phone_no);
        gender = (RadioGroup) findViewById(R.id.radio_grp);
        refferal_btn = (ImageView) findViewById(R.id.send);
        refferal = (EditText) findViewById(R.id.activity_residence_add);
    }

//    private void savePropData() {
//        try {
//            SoapObject request = new SoapObject("http://tempuri.org/", "insertNewRegistration");
//
//            PropertyInfo username = new PropertyInfo();
//            username.setType(android.R.string.class);
//            username.setName("_name");
//            username.setValue(this.username.getText().toString());
//            request.addProperty(username);
//            PropertyInfo Orderid = new PropertyInfo();
//            Orderid.setType(android.R.string.class);
//            Orderid.setName("_email");
//            Orderid.setValue(email.getText().toString());
//            request.addProperty(Orderid);
//
//            PropertyInfo aa123 = new PropertyInfo();
//            aa123.setType(android.R.string.class);
//            aa123.setName("_password");
//            aa123.setValue(password.getText().toString());
//            request.addProperty(aa123);
//
//
//
//            PropertyInfo phone = new PropertyInfo();
//            phone.setType(android.R.string.class);
//            phone.setName("_phonenumber");
//            phone.setValue(phoneno.getText().toString());
//            request.addProperty(phone);
//
//            PropertyInfo gender = new PropertyInfo();
//            gender.setType(android.R.string.class);
//            gender.setName("_gender");
//            gender.setValue(gender_string);
//            request.addProperty(gender);
//
//            PropertyInfo refer = new PropertyInfo();
//            refer.setType(android.R.string.class);
//            refer.setName("referralcode");
//            refer.setValue(this.refferal.getText().toString());
//            request.addProperty(refer);
//            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//            envelope.dotNet = true;
//            envelope.setOutputSoapObject(request);
//            HttpTransportSE androidHttpTransport =
//                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
//            androidHttpTransport.debug = true;
//
//            androidHttpTransport.call("http://tempuri.org/IService1/insertNewRegistration", envelope);
//            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
//
//            strResponce = soapPrimitive.toString();
//            Log.e("TAG", "Soap primitive1" + strResponce);
//        } catch (SocketTimeoutException e) {
//
//        } catch (Exception e) {
//            Log.e("TAG", "Soap Exception" + e.toString());
//        }
//    }

    private void refferal_code() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "checkReferalCode");


            PropertyInfo refer = new PropertyInfo();
            refer.setType(android.R.string.class);
            refer.setName("referralcode");
            refer.setValue(refferal.getText().toString());
            request.addProperty(refer);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;

            androidHttpTransport.call("http://tempuri.org/IService1/checkReferalCode", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            refferal_response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + refferal_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }



    private void check_duplicate() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "checkDuplicateRegister");


            PropertyInfo refer = new PropertyInfo();
            refer.setType(android.R.string.class);
            refer.setName("_email");
            refer.setValue(email.getText().toString());
            request.addProperty(refer);

            PropertyInfo refer1 = new PropertyInfo();
            refer1.setType(android.R.string.class);
            refer1.setName("_mobile");
            refer1.setValue(phoneno.getText().toString());
            request.addProperty(refer1);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;

            androidHttpTransport.call("http://tempuri.org/IService1/checkDuplicateRegister", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            duplicater_response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + duplicater_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }
//    private class Call_Service extends AsyncTask<String[], Void, String> {
//        ProgressDialog progressDialog = new ProgressDialog(Signup.this);
//        private Login activity;
//        private String soapAction;
//        private String methodName;
//        private String paramsName;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog.setMessage("Loading....");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected String doInBackground(String[]... params) {
//            savePropData();
//            return strResponce;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            //Log.e("resulttttt",s);
//            progressDialog.dismiss();
//
//
//            otp_code();
//            try {
//                if (s.contains("Data Insert Succesfully")) {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(Signup.this);
//                    dialog.setTitle("Register");
//                    dialog.setMessage("Register Succesfully.");
//
//                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(Signup.this, OTP.class);
//
//                            intent.putExtra("randomint",randomInt);
//                            startActivity(intent);
//                            dialog.dismiss();
//                            finish();
//                        }
//                    });
//                    dialog.show();
//
//                } else {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
//                    dialog.setMessage("Something Went Wrong, Please Try Again.");
//                    dialog.setTitle("Register");
//                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            dialog.dismiss();
//
//                        }
//                    });
//                    dialog.show();
//                }
//
//            } catch (Exception e) {
//
//                e.printStackTrace();
//            }
//        }
//    }

    private class Refferal_Service extends AsyncTask<String[], Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(Signup.this);
        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String[]... params) {
            refferal_code();
            return refferal_response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();

            try {
                if (s.contains("Error")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(Signup.this);
                    dialog.setTitle("Error");
                    dialog.setMessage(s);

                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                } else {
                    Toast.makeText(getApplicationContext(),"Refferal Code Saved.",3).show();
                     }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }



    private class Check_Duplicate extends AsyncTask<String[], Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(Signup.this);
        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading....");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String[]... params) {
            check_duplicate();
            return duplicater_response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();

            try {
                if (s.contains("Error")) {
                    Toast.makeText(getApplicationContext(),"Already Registered with Meribus.Com!!!, Please Login with your EmailId And Password.",3).show();

                } else {
                    Random random= new Random();
                    randomInt = random.nextInt(100000);

                    otp_code();
                  //  Toast.makeText(getApplicationContext(),"Refferal Code Saved.",3).show();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    private void otp_code() {
       final ProgressDialog dialog= new ProgressDialog(Signup.this);
        dialog.setMessage("Loading....");
        dialog.show();
        RequestQueue volley= Volley.newRequestQueue(Signup.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Intent intent = new Intent(Signup.this, OTP.class);

                intent.putExtra("randomint",randomInt);
                intent.putExtra("name",username.getText().toString());
                intent.putExtra("email",email.getText().toString());
                intent.putExtra("password",password.getText().toString());
                intent.putExtra("phonenumber",phoneno.getText().toString());
                intent.putExtra("gender",gender_string);
                intent.putExtra("refer",refferal.getText().toString());



                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Intent intent = new Intent(Signup.this, OTP.class);

                intent.putExtra("randomint",randomInt);
                intent.putExtra("name",username.getText().toString());
                intent.putExtra("email",email.getText().toString());
                intent.putExtra("password",password.getText().toString());
                intent.putExtra("phonenumber",phoneno.getText().toString());
                intent.putExtra("gender",gender_string);
                intent.putExtra("refer",refferal.getText().toString());



                startActivity(intent);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> param= new HashMap<>();
                param.put("user","Meribus");
                param.put("pass","meribus$123");
                param.put("sender","MERBUS");
                param.put("phone",phoneno.getText().toString());
                param.put("text","Dear "+ username.getText().toString()+", you one time password is :"+randomInt);
                param.put("priority","ndnd");
                param.put("stype","normal");

                return param;

            }
        };
        volley.add(stringRequest);
    }
}
