package paztechnologies.com.meribus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketTimeoutException;
import java.util.Random;

public class OTP extends AppCompatActivity {
    String strResponce;
    int randomInt;
    Intent intent;
    EditText code;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        code=(EditText)findViewById(R.id.code);
        button=(Button)findViewById(R.id.update_profile);
       intent=getIntent();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (intent.getIntExtra("randomint", 0) == Integer.valueOf(code.getText().toString())) {

                        new Call_Service().execute();
                    } else {

                        Toast.makeText(getApplicationContext(), "One Time Password is not Correct,Please Try Again.", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Enter Only Numbers.", Toast.LENGTH_LONG).show();

                }
            }
        });

    }



    private class Call_Service extends AsyncTask<String[], Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(OTP.this);
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
            savePropData();
            return strResponce;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();



            try {
                if (s.contains("Data Insert Succesfully")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(OTP.this);
                    dialog.setTitle("Register");
                    dialog.setMessage("Register Succesfully.");

                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(OTP.this, MainActivity.class);
                            dialog.dismiss();
                            finish();

                            startActivity(intent);
                        }
                    });
                    dialog.show();

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
                    dialog.setMessage("Something Went Wrong, Please Try Again.");
                    dialog.setTitle("Register");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    private void savePropData() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "insertNewRegistration");

            PropertyInfo username = new PropertyInfo();
            username.setType(android.R.string.class);
            username.setName("_name");
            username.setValue(intent.getStringExtra("name"));
            request.addProperty(username);
            PropertyInfo Orderid = new PropertyInfo();
            Orderid.setType(android.R.string.class);
            Orderid.setName("_email");
            Orderid.setValue(intent.getStringExtra("email"));
            request.addProperty(Orderid);

            PropertyInfo aa123 = new PropertyInfo();
            aa123.setType(android.R.string.class);
            aa123.setName("_password");
            aa123.setValue(intent.getStringExtra("password"));
            request.addProperty(aa123);



            PropertyInfo phone = new PropertyInfo();
            phone.setType(android.R.string.class);
            phone.setName("_phonenumber");
            phone.setValue(intent.getStringExtra("phonenumber"));
            request.addProperty(phone);

            PropertyInfo gender = new PropertyInfo();
            gender.setType(android.R.string.class);
            gender.setName("_gender");
            gender.setValue(intent.getStringExtra("gender"));
            request.addProperty(gender);

            PropertyInfo refer = new PropertyInfo();
            refer.setType(android.R.string.class);
            refer.setName("referralcode");
            refer.setValue(intent.getStringExtra("refer"));
            request.addProperty(refer);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;

            androidHttpTransport.call("http://tempuri.org/IService1/insertNewRegistration", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            strResponce = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + strResponce);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }
}
