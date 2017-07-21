package paztechnologies.com.meribus;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 2/21/2017.
 */

public class Profile extends Fragment {
    SharedPreferences sp;
    EditText name,email,alt_email,mobile_no,res_address,ofc_address,old_pass,new_pass,con_pass;
    String response,update_res;
    RadioButton male,female;
    Button update_profile;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.profile,container,false);
        sp=getActivity().getSharedPreferences("app",0);
        init(view);
        new Profile_Details().execute();
        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new Update_Profile().execute();
            }
        });
        return view;
    }

    private void init(View v){
        name=(EditText)v.findViewById(R.id.activity_register_username);
        email=(EditText)v.findViewById(R.id.activity_register_email);
        alt_email=(EditText)v.findViewById(R.id.activity_alt_email);
        mobile_no=(EditText)v.findViewById(R.id.activity_phone_no);
        res_address=(EditText)v.findViewById(R.id.activity_residence_add);
        ofc_address=(EditText)v.findViewById(R.id.activity_office_add);
        male=(RadioButton)v.findViewById(R.id.male);
        female=(RadioButton)v.findViewById(R.id.female);
        update_profile=(Button)v.findViewById(R.id.update_profile);
    }
    private class Profile_Details extends AsyncTask<String[], Void, String> {

        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading....");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String[]... params) {
            setSelect_route();
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();

            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);

                JSONObject jsonObject = arr.getJSONObject(0);
                name.setText(jsonObject.getString("name"));
                email.setText(jsonObject.getString("emailid"));
                mobile_no.setText(jsonObject.getString("phonenumber"));
                alt_email.setText(jsonObject.getString("altemail"));
                res_address.setText(jsonObject.getString("homeaddress"));
                ofc_address.setText(jsonObject.getString("officeaddress"));
                if(jsonObject.getString("Gender").equals("Male")){
                    male.setChecked(true);
                }else if(jsonObject.getString("Gender").equalsIgnoreCase("Female")){
                    female.setChecked(true);
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }
    private void setSelect_route() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "getAccountDetails");

            PropertyInfo Orderid = new PropertyInfo();
            Orderid.setType(android.R.string.class);
            Orderid.setName("_customerId");
            Orderid.setValue(sp.getString("pid",""));
            request.addProperty(Orderid);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/getAccountDetails", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }



    private class Update_Profile extends AsyncTask<String[], Void, String> {

        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading....");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String[]... params) {
            update();
            return update_res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();

            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);

                JSONObject jsonObject = arr.getJSONObject(0);
                name.setText(jsonObject.getString("name"));
                email.setText(jsonObject.getString("emailid"));
                mobile_no.setText(jsonObject.getString("phonenumber"));
                alt_email.setText(jsonObject.getString("altemail"));
                res_address.setText(jsonObject.getString("homeaddress"));
                ofc_address.setText(jsonObject.getString("officeaddress"));
                if(jsonObject.getString("Gender").equals("Male")){
                    male.setChecked(true);
                }else if(jsonObject.getString("Gender").equalsIgnoreCase("Female")){
                    female.setChecked(true);
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void update() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "updateAccount");

            PropertyInfo Orderid = new PropertyInfo();
            Orderid.setType(android.R.string.class);
            Orderid.setName("pid");
            Orderid.setValue(sp.getString("pid",""));
            request.addProperty(Orderid);

            PropertyInfo name = new PropertyInfo();
            name.setType(android.R.string.class);
            name.setName("FullName");
            name.setValue(this.name.getText().toString());
            request.addProperty(name);

            PropertyInfo email = new PropertyInfo();
            email.setType(android.R.string.class);
            email.setName("Email");
            email.setValue(this.email.getText().toString());
            request.addProperty(email);

            PropertyInfo mobile = new PropertyInfo();
            mobile.setType(android.R.string.class);
            mobile.setName("Mobile");
            mobile.setValue(this.mobile_no.getText().toString());
            request.addProperty(mobile);

            PropertyInfo aemail = new PropertyInfo();
            aemail.setType(android.R.string.class);
            aemail.setName("AlterNateEmail");
            aemail.setValue(this.alt_email.getText().toString());
            request.addProperty(aemail);

            PropertyInfo res_add = new PropertyInfo();
            res_add.setType(android.R.string.class);
            res_add.setName("ResAddress");
            res_add.setValue(this.res_address.getText().toString());
            request.addProperty(res_add);


            PropertyInfo ofc_add = new PropertyInfo();
            ofc_add.setType(android.R.string.class);
            ofc_add.setName("officeAddress");
            ofc_add.setValue(this.ofc_address.getText().toString());
            request.addProperty(ofc_add);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/updateAccount", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            update_res = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + update_res);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }
}
