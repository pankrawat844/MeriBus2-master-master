package paztechnologies.com.meribus;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Book_Per_Day extends Fragment {


    Spinner current_location, des_location, select_route, select_end_time;
    android.widget.Spinner select_start_time;
    Button place_ride;

    String startshiftresponse, endshiftresponse, route_response, pickup_response, drop_response,rate_response ,seat_type = "Both Pickup and Drop";
    List<String> route_id = new ArrayList<>();
    ProgressDialog progressDialog;
    RadioGroup radio_grp;
    RadioButton both, drop, pickup;
    DatePickerDialog start_date_picker, end_date_picker;
    SimpleDateFormat dateFormatter;
    TextView start_date,amount;
    List<String> pickupid_array,start_time_id,end_time_id;
    SharedPreferences sp;
    int total_amount,one_way_total;
    ToggleButton non_ac_toggle,ac_toggle,cab_toggle;
    List<String> pickup_latitude;
    List<String> pickup_longtitude;
    HashMap<String,String> drop_longtitude= new HashMap<>();
    HashMap<String,String> drop_latitude= new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pay_per_day, container, false);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        sp = getActivity().getSharedPreferences("app", 0);
        progressDialog = new ProgressDialog(getActivity());
        init(view);
        drop_longtitude.put("Cyber City Gurgaon","77.0861363");
        drop_longtitude.put("Shankar Chowk/cyber city","77.0872363");
        drop_longtitude.put("Park centra","77.03814");
        drop_longtitude.put("Signature tower","77.051284");
        drop_longtitude.put("Iffco chowk","77.0710421");
        drop_longtitude.put("Airtel footover bridge","77.0816811");

        drop_latitude.put("Cyber City Gurgaon","28.4936018");
        drop_latitude.put("Shankar Chowk/cyber city","28.498576");
        drop_latitude.put("Park centra","28.4602778");
        drop_latitude.put("Signature tower","28.4650399");
        drop_latitude.put("Iffco chowk","28.4707409");
        drop_latitude.put("Airtel footover bridge","28.4905563");
        new  Start_Shift_Time().execute();

        ac_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                non_ac_toggle.setChecked(false);
                cab_toggle.setChecked(false);
            }
        });
        non_ac_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ac_toggle.setChecked(false);
                cab_toggle.setChecked(false);

            }
        });

        cab_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac_toggle.setChecked(false);
                non_ac_toggle.setChecked(false);
            }
        });


        place_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sp.edit();
                Calendar calendar = Calendar.getInstance();

                int total_days=0;
                int  per_day_total,perday_oneway;

                start_date.setText(dateFormatter.format(calendar.getTime()));
                if(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)==30) {


                    for(int i=1;i<=30;i++){
                        calendar.set(Calendar.DAY_OF_MONTH,i);

                        if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){

                        }
                        else{
                            total_days+=1;
                        }

                    }
                    per_day_total=(int)(total_amount/total_days)+1;
                    perday_oneway=(int) (one_way_total/total_days)+1;

                    //amount.setText(String.valueOf(per_day_total * total_days));
                }
                else if(calendar.getActualMaximum(Calendar.DAY_OF_MONTH)<=29)
                {
                    for(int i=1;i==29;i++){
                        calendar.set(Calendar.DAY_OF_MONTH,i);
                        if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){

                        }
                        else{
                            total_days+=1;
                        }

                    }
                    per_day_total=(int)(total_amount/total_days)+1;
                    perday_oneway=(int) (one_way_total/total_days)+1;

                    //amount.setText(String.valueOf(per_day_total * total_days));
                }
                else
                {
                    for(int i=1;i<=31;i++){
                        calendar.set(Calendar.DAY_OF_MONTH,i);
                        if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY || calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){

                        }
                        else{
                            total_days+=1;
                        }

                    }
                    per_day_total=(int)(total_amount/total_days)+1;
                    perday_oneway=(int) (one_way_total/total_days)+1;
                    // amount.setText(String.valueOf(per_day_total * total_days));
                }



                    if (select_start_time.getSelectedItem() != null && select_end_time.getSelectedItem() != null && select_route.getSelectedItem() != null && current_location.getSelectedItem() != null && des_location.getSelectedItem() != null ) {

                        editor.putString("start_time", select_start_time.getSelectedItem().toString());
                        editor.putString("end_time", select_end_time.getSelectedItem().toString());

                        editor.putString("route_name", select_route.getSelectedItem().toString());
                        editor.putString("route_id", route_id.get(select_route.getSelectedItemPosition()));
                        editor.putString("pick_point", current_location.getSelectedItem().toString());
                        editor.putString("drop_point", des_location.getSelectedItem().toString());
                        editor.putString("date", start_date.getText().toString());

                        editor.putString("pickup_lattiude",pickup_latitude.get(current_location.getSelectedItemPosition()-1));
                        editor.putString("pickup_longitude",pickup_longtitude.get(current_location.getSelectedItemPosition()-1));
                        editor.putString("drop_latitude",drop_latitude.get(des_location.getSelectedItem().toString()));
                        editor.putString("drop_longtitude",drop_longtitude.get(des_location.getSelectedItem().toString()));
                        editor.putString("amount",amount.getText().toString());
                        editor.commit();
                        Booking_seat ride_detail = new Booking_seat();
                        Bundle args = new Bundle();
                        args.putString("_routeId",route_id.get(select_route.getSelectedItemPosition() - 1));
                        args.putString("StartShiftTime",start_time_id.get(select_start_time.getSelectedItemPosition()-1));
                        args.putString("EndShiftTime",end_time_id.get(select_end_time.getSelectedItemPosition()-1));
                        args.putString("Seattype",seat_type);
                        args.putString("perseat_price",String.valueOf(per_day_total));
                        args.putString("one_way_amount",String.valueOf(perday_oneway));
                        ride_detail.setArguments(args);
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.container, ride_detail).addToBackStack("");
                        fragmentTransaction.commit();

                    } else {

                        Toast.makeText(getActivity(), "All Fields are mandatory,Please Fill All Details", 3).show();
                    }


            }
        });

        radio_grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (both.isChecked()) {
                    seat_type = "Both Pickup and Drop";
                    select_start_time.setVisibility(View.VISIBLE);
                    select_end_time.setVisibility(View.VISIBLE);
                    new  Pickup_Point().execute();

                }
            }
        });
//        select_start_time.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(Spinner parent, View view, int position, long id) {
//            parent.setSelection(position);
//            }
//        });
//        current_location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // findPlace(v);
//            }
//        });


        select_end_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    new Select_Route().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        select_route.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                new  Pickup_Point().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        current_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    new Drop_Point().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        des_location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(current_location.getSelectedItem()!=null) {
                    new Rate().execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_date_picker.show();
            }
        });
        Calendar newCalendar = Calendar.getInstance();
        start_date_picker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        return view;
    }


    void init(View v) {
        current_location = (Spinner) v.findViewById(R.id.current_location);
        des_location = (Spinner) v.findViewById(R.id.des_location);
        select_route = (Spinner) v.findViewById(R.id.select_route);
        select_start_time = (android.widget.Spinner) v.findViewById(R.id.start_time);

        select_end_time = (Spinner) v.findViewById(R.id.end_time);
        radio_grp = (RadioGroup) v.findViewById(R.id.radio_grp);
        both = (RadioButton) v.findViewById(R.id.both);
        pickup = (RadioButton) v.findViewById(R.id.pickup);
        drop = (RadioButton) v.findViewById(R.id.drop);
        place_ride = (Button) v.findViewById(R.id.submit);
        start_date = (TextView) v.findViewById(R.id.start_date);
        non_ac_toggle=(ToggleButton)v.findViewById(R.id.nonac_toggle);
        ac_toggle=(ToggleButton)v.findViewById(R.id.ac_toggle);
        cab_toggle=(ToggleButton)v.findViewById(R.id.e_riksha_toggle);
        amount=(TextView)v.findViewById(R.id.amount);
        ArrayAdapter<CharSequence> current_adapter = ArrayAdapter.createFromResource(getActivity(), R.array.arr, R.layout.support_simple_spinner_dropdown_item);
        // current_location.setAdapter(new NothingSelectedSpinnerAdapter(current_adapter, R.layout.nothing_selected_pickup, getActivity()));
        // des_location.setAdapter(new NothingSelectedSpinnerAdapter(current_adapter, R.layout.nothing_selected_drop_point, getActivity()));
    }

    private void startshifttime() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "_getShiftTime");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/_getShiftTime", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            startshiftresponse = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + startshiftresponse);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }

    private void endshifttime() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "_getEndTime");


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/_getEndTime", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            endshiftresponse = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + endshiftresponse);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }

    private void setSelect_route() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "payperday_pps_getEndShift_RouteBind");

            PropertyInfo Orderid = new PropertyInfo();
            Orderid.setType(android.R.string.class);
            Orderid.setName("shifttime");
            Orderid.setValue(start_time_id.get(select_start_time.getSelectedItemPosition()-1));
            request.addProperty(Orderid);

            PropertyInfo aa123 = new PropertyInfo();
            aa123.setType(android.R.string.class);
            aa123.setName("EndTime");
            aa123.setValue(end_time_id.get(select_end_time.getSelectedItemPosition()-1));
            request.addProperty(aa123);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/payperday_pps_getEndShift_RouteBind", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            route_response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + route_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }

    private void setpickup_point() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "PPS_BindPickPoint_Dropdown_GetDropPoint__when_Drop_Booking_not");

            PropertyInfo Orderid = new PropertyInfo();
            Orderid.setType(android.R.string.class);
            Orderid.setName("_routeId");
            Orderid.setValue(route_id.get(select_route.getSelectedItemPosition() - 1));
            request.addProperty(Orderid);

            PropertyInfo aa123 = new PropertyInfo();
            aa123.setType(android.R.string.class);
            aa123.setName("Seattype");
            aa123.setValue(seat_type);
            request.addProperty(aa123);
            //Toast.makeText(getActivity(),route_id.get(select_route.getSelectedItemPosition())+" "+seat_type,3).show();

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/PPS_BindPickPoint_Dropdown_GetDropPoint__when_Drop_Booking_not", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            pickup_response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + pickup_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }

    }

    private void setdrop_point(){
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "getDropList");

            PropertyInfo Orderid = new PropertyInfo();
            Orderid.setType(android.R.string.class);
            Orderid.setName("_routeId");
            Orderid.setValue(route_id.get(select_route.getSelectedItemPosition() - 1));
            request.addProperty(Orderid);

            PropertyInfo aa123 = new PropertyInfo();
            aa123.setType(android.R.string.class);
            aa123.setName("Seattype");
            aa123.setValue(seat_type);
            request.addProperty(aa123);
            //Toast.makeText(getActivity(),route_id.get(select_route.getSelectedItemPosition())+" "+seat_type,3).show();

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/getDropList", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            drop_response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + drop_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }

    }

    private class Start_Shift_Time extends AsyncTask<String[], Void, String> {

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
            startshifttime();
            return startshiftresponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();

            try {
                List<String> time = new ArrayList<>();
                //   Toast.makeText(getActivity(),s,3).show();
                start_time_id= new ArrayList<>();
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    time.add(jsonObject.getString("StartShiftTime"));
                    start_time_id.add(jsonObject.getString("ShiftId"));
                }
                ArrayAdapter<String> start_adapter = new ArrayAdapter<String>(getActivity(), R.layout.nothing_selected_drop_point, time);
                start_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                select_start_time.setAdapter(new NothingSelectedSpinnerAdapter(start_adapter, R.layout.nothing_selected_shift_start, getContext()));
                new  End_Shift_Time().execute();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private class End_Shift_Time extends AsyncTask<String[], Void, String> {

        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String[]... params) {
            endshifttime();
            return endshiftresponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            //   progressDialog.dismiss();

            try {
                List<String> time = new ArrayList<>();
                end_time_id=new ArrayList<>();
                //  Toast.makeText(getActivity(),s,3).show();
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    time.add(jsonObject.getString("EndShiftTime"));
                    end_time_id.add(jsonObject.getString("ShiftId"));
                }
                ArrayAdapter<String> end_adapter = new ArrayAdapter<String>(getActivity(), R.layout.select_end_time, time);
                end_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                select_end_time.setAdapter(new NothingSelectedSpinnerAdapter(end_adapter, R.layout.nothing_selected_shift_end_time, getActivity()));

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private class Select_Route extends AsyncTask<String[], Void, String> {

        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String[]... params) {
            setSelect_route();
            return route_response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();

            try {
                List<String> time = new ArrayList<>();
                HashMap<String, String> hash = new HashMap<>();
                //   Toast.makeText(getActivity(),s,3).show();
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    time.add(jsonObject.getString("RouteName"));
                    route_id.add(jsonObject.getString("RouteID"));
                }
                ArrayAdapter<String> route_adapter = new ArrayAdapter<String>(getActivity(),R.layout.select_route_list, time);
                route_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                route_adapter.notifyDataSetChanged();
                select_route.setAdapter(new NothingSelectedSpinnerAdapter(route_adapter, R.layout.nothing_selected_select_route, getContext()));

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }




    private class Pickup_Point extends AsyncTask<String[], Void, String> {

        ProgressDialog dialog;
        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String[]... params) {
            setpickup_point();
            return pickup_response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            dialog.dismiss();

            try {
                List<String> time = new ArrayList<>();
                pickupid_array= new ArrayList<>();
                pickup_latitude= new ArrayList<>();
                pickup_longtitude= new ArrayList<>();
                //   Toast.makeText(getActivity(),s,3).show();
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    time.add(jsonObject.getString("PickUpName"));
                    pickupid_array.add(jsonObject.getString("PickUpId"));
                    pickup_latitude.add(jsonObject.getString("Lat"));
                    pickup_longtitude.add(jsonObject.getString("Long"));
                }
                ArrayAdapter<String> route_adapter = new ArrayAdapter<String>(getActivity(), R.layout.selected_pickup_location, time);
                route_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                route_adapter.notifyDataSetChanged();
                current_location.setAdapter(new NothingSelectedSpinnerAdapter(route_adapter, R.layout.nothing_selected_pickup, getContext()));
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }



    private class Drop_Point extends AsyncTask<String[], Void, String> {

        ProgressDialog dialog;
        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String[]... params) {
            setdrop_point();
            return drop_response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            dialog.dismiss();

            try {
                List<String> time = new ArrayList<>();
                //   Toast.makeText(getActivity(),s,3).show();
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    time.add(jsonObject.getString("PickUpName"));
                }
                ArrayAdapter<String> route_adapter = new ArrayAdapter<String>(getActivity(), R.layout.selected_pickup_location, time);
                route_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                route_adapter.notifyDataSetChanged();
                des_location.setAdapter(new NothingSelectedSpinnerAdapter(route_adapter, R.layout.nothing_selected_drop_point, getContext()));

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }






    private class Rate extends AsyncTask<String[], Void, String> {

        ProgressDialog dialog;
        private Login activity;
        private String soapAction;
        private String methodName;
        private String paramsName;
        JSONObject jsonObject;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String[]... params) {
            setrate();
            return rate_response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            dialog.dismiss();

            try {
                List<String> time = new ArrayList<>();
                //   Toast.makeText(getActivity(),s,3).show();
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);
                for (int i = 0; i < arr.length(); i++) {
                    jsonObject = arr.getJSONObject(0);
                    time.add(jsonObject.getString("GrandTotal"));
                    time.add(jsonObject.getString("OnWay"));
                }
                if(seat_type.equals("Both Pickup and Drop")){
                    amount.setText(jsonObject.getString("GrandTotal"));
                    total_amount=Integer.valueOf(jsonObject.getString("GrandTotal"));
                    one_way_total=Integer.valueOf(jsonObject.getString("OnWay"));

                }else{
                    amount.setText(jsonObject.getString("OnWay"));
                    total_amount=Integer.valueOf(jsonObject.getString("GrandTotal"));
                    one_way_total=Integer.valueOf(jsonObject.getString("OnWay"));
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }


    private void setrate() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "_getZoneRate_When_RadioButton_Both_Pickup_nd_Drop_and_Also_Pickup_use_PickupPoint_DropDown");

            PropertyInfo Orderid = new PropertyInfo();
            Orderid.setType(android.R.string.class);
            Orderid.setName("_pickUpId");
            Orderid.setValue(pickupid_array.get(current_location.getSelectedItemPosition() - 1));
            request.addProperty(Orderid);
            Log.e("pickup id",pickupid_array.get(current_location.getSelectedItemPosition() - 1));

            //Toast.makeText(getActivity(),route_id.get(select_route.getSelectedItemPosition())+" "+seat_type,3).show();

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/_getZoneRate_When_RadioButton_Both_Pickup_nd_Drop_and_Also_Pickup_use_PickupPoint_DropDown", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            rate_response = soapPrimitive.toString();
            Log.e("TAG", "Soap rate" + rate_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap rate2" + e.toString());
        }

    }


    }
