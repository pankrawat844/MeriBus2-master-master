package paztechnologies.com.meribus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Handler;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by Admin on 3/24/2017.
 */

public class Ride_Detail extends Fragment implements OnMapReadyCallback, View.OnClickListener, DirectionCallback {
    String Server_Key = "AIzaSyCdUU47miOD97PDox1FwhGr3SHPkbG4A2s";
    private GoogleMap googleMap;
    private MapView mMapView;
    //private LatLng camera = new LatLng(28.6158851, 77.0406466);
    private LatLng curr ;
    private LatLng des ;
    EditText coupan_code;
    private Button payment;
    private  ImageView send;
    private TextView start_time,end_time,select_route,pickup_point,drop_point,amount,distance_txt,time_txt;
    SharedPreferences sharedPreferences;
    String refferal_response,check_response,seat_type,response;
    Calendar startcalendar,endcalender;
    String start_time_str,end_time_str;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ride_detail2, container, false);
        MapsInitializer.initialize(this.getActivity());
        init(view);
             sharedPreferences= getActivity().getSharedPreferences("app",0);
            curr= new LatLng(Double.valueOf(sharedPreferences.getString("pickup_lattiude","")),Double.valueOf(sharedPreferences.getString("pickup_longitude",""))) ;
        des= new LatLng(Double.valueOf(sharedPreferences.getString("drop_latitude","28.6158851")),Double.valueOf(sharedPreferences.getString("drop_longtitude","77.0406466"))) ;
          //  Toast.makeText(getActivity(),sharedPreferences.getString("drop_latitude","28.6158851")+sharedPreferences.getString("drop_longtitude","77.0406466"),3);
        start_time.setText(sharedPreferences.getString("start_time",""));
            end_time.setText(sharedPreferences.getString("end_time",""));
            select_route.setText(sharedPreferences.getString("route_name",""));
            pickup_point.setText( sharedPreferences.getString("pick_point",""));
            drop_point.setText( sharedPreferences.getString("drop_point",""));
            amount.setText("RS "+sharedPreferences.getString("amount",""));

        if(sharedPreferences.getString("seat_type","").equals("Both Pickup and Drop"))
        {
            seat_type="Two Way MonthlyBoth Pickup and Drop";
        }else if(sharedPreferences.getString("seat_type","").equals("Pick Up"))
        {
            seat_type="One Way MonthlyPick Up";
        }else if(sharedPreferences.getString("seat_type","").equals("Drop"))
        {
            seat_type="One Way MonthlyDrop";
        }
        try {
            Calendar calendar= Calendar.getInstance();
             startcalendar = Calendar.getInstance();
            endcalender=Calendar.getInstance();
            String dat = sharedPreferences.getString("start_time", "");
            String enddat = sharedPreferences.getString("end_time", "");
            SimpleDateFormat format = new SimpleDateFormat("hh:mma", Locale.ENGLISH);
            SimpleDateFormat simpleformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.ENGLISH);
            Log.e("Start Dte111111",String.valueOf(startcalendar.getTime()));
            Date startdate = format.parse(dat);
            Date enddate=format.parse(enddat);
            startcalendar.setTime(startdate);
            startcalendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
             start_time_str=simpleformat.format(startcalendar.getTime());
            endcalender.setTime(enddate);
            endcalender.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
             this.end_time_str=simpleformat.format(endcalender.getTime());
            Log.e("Start Dte",start_time_str);
            Log.e("Start Dte",end_time_str);
            //startcalendar.setTime(startdate);


        }catch (Exception e){
            Log.e("Exception.........",e.getMessage());
        }
            SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);

             mapFragment.getMapAsync(this);
             payment.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     new Submit_Service().execute();
                 }
             });

        //        if (google == null) {
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            google = SupportMapFragment.newInstance();
//            fragmentTransaction.replace(R.id.map, google).commit();
//        }
        requestDirection();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Connectivity connectivity = new Connectivity();

                if (connectivity.isNetworkAvilable(getActivity())) {
                        new CheckCode_Service().execute();
                }else {
                    //If login fails
                    Toast.makeText(getActivity(), "Internet is not Connected,Please Try Again", Toast.LENGTH_LONG).show();
                }
            }
        });
        return view;
    }

        private void init(View v){
            start_time=(TextView)v.findViewById(R.id.start_time);
            end_time=(TextView)v.findViewById(R.id.end_time);
            select_route=(TextView)v.findViewById(R.id.select_route);
            pickup_point=(TextView)v.findViewById(R.id.current_location);
            drop_point=(TextView)v.findViewById(R.id.des);
            amount=(TextView)v.findViewById(R.id.total);
            payment = (Button) v.findViewById(R.id.submit);
            distance_txt=(TextView)v.findViewById(R.id.distance);
            time_txt=(TextView)v.findViewById(R.id.time);
            coupan_code=(EditText)v.findViewById(R.id.coupon_code);
            send=(ImageView) v.findViewById(R.id.send);
        }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                Payment payment = new Payment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, payment);
                ft.commit();
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        //  Snackbar.make(getView(), "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(), " " + direction.getStatus(), Toast.LENGTH_SHORT).show();

        if (direction.isOK()) {
            googleMap.addMarker(new MarkerOptions().position(curr).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_point)));
            googleMap.addMarker(new MarkerOptions().position(des).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_point)));
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.YELLOW));
            getdistancentime();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        //   Snackbar.make(getView(), t.getMessage(), Snackbar.LENGTH_SHORT).show();
        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        getActivity(), R.raw.maps_style));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curr, 9));

//        LatLng latlong = new LatLng(28.6158851,77.0406466);
//        googleMap.addMarker(new MarkerOptions().position(latlong)
//                .title("india"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong,10));
//        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
    }

    public void requestDirection() {
        // Snackbar.make(getView(), "Direction Requesting...", Snackbar.LENGTH_SHORT).show();
        GoogleDirection.withServerKey(Server_Key)
                .from(curr)
                .to(des)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
     //   Toast.makeText(getActivity(), "Direction Requesting...", Toast.LENGTH_SHORT).show();
       //     getLocationInfo(sharedPreferences.getString("pick_point",""));
      //  getLocationInfo_des(sharedPreferences.getString("drop_point",""));

    }




    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


    private  void getdistancentime(){
        RequestQueue requestQueue= Volley.newRequestQueue(getActivity());
        String url= "https://maps.googleapis.com/maps/api/distancematrix/json?units=km&origins="+sharedPreferences.getString("pickup_lattiude","")+","+sharedPreferences.getString("pickup_longitude","")+"&destinations="+sharedPreferences.getString("drop_latitude","28.6158851")+","+sharedPreferences.getString("drop_longtitude","28.6158851")+"&key="+Server_Key;
        StringRequest request= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respons = new JSONObject(response);
                    JSONArray row= respons.getJSONArray("rows");
                    JSONObject row_obj= row.getJSONObject(0);
                    JSONArray row_arr= row_obj.getJSONArray("elements");
                    JSONObject elem_obj=row_arr.getJSONObject(0);
                    JSONObject distance= elem_obj.getJSONObject("distance");
                    distance_txt.setText(distance.getString("text"));
                    JSONObject time= elem_obj.getJSONObject("duration");
                    time_txt.setText(time.getString("text"));

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ErrorListner",error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map= new HashMap<>();
                map.put("units","km");
                map.put("origins",sharedPreferences.getString("pickup_lattiude","")+","+sharedPreferences.getString("pickup_longitude",""));

                map.put("destinations",sharedPreferences.getString("drop_latitude","28.6158851")+","+sharedPreferences.getString("drop_longtitude","28.6158851"));
                map.put("key",Server_Key);
                return map;
            }
        };
        requestQueue.add(request);
    }

    private class Refferal_Service extends AsyncTask<String[], Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                if (s.contains("INVALID COUPON CODE")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
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

                        JSONArray jsonArray = new JSONArray(s);
                        JSONArray jsonArray1 = jsonArray.getJSONArray(0);
                            JSONObject object = jsonArray1.getJSONObject(0);
                        if(object.getString("CouponType").contains("Both") || object.getString("CouponType").contains("Monthly")){
                            String total=object.getString("CouponDiscount");
                            int tot=Integer.valueOf(sharedPreferences.getString("amount",""))+Integer.valueOf(total);
                            amount.setText(String.valueOf(tot));
                        }else {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle("Error");
                            dialog.setMessage("Coupon Code Is Valid For PayPerDay");
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                }
                            });
                            dialog.show();
                        }


                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private void refferal_code() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "getCoupon");

            PropertyInfo refer = new PropertyInfo();
            refer.setType(android.R.string.class);
            refer.setName("CouponCode");
            refer.setValue(coupan_code.getText().toString());
            request.addProperty(refer);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;

            androidHttpTransport.call("http://tempuri.org/IService1/getCoupon", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            refferal_response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + refferal_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }


    private class CheckCode_Service extends AsyncTask<String[], Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
            check_code();
            return check_response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();

            try {
                check_response="0";
                if(s.equals("0")){
                new  Refferal_Service().execute();
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Error");
                    dialog.setMessage("Code Has Already Used.");

                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();

                e.printStackTrace();
            }
        }
    }



    private void check_code() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "DiscountCheck");


            PropertyInfo id = new PropertyInfo();
            id.setType(android.R.string.class);
            id.setName("_customerId");
            id.setValue(sharedPreferences.getString("pid",""));
            request.addProperty(id);

            PropertyInfo refer = new PropertyInfo();
            refer.setType(android.R.string.class);
            refer.setName("couponcode");
            refer.setValue(coupan_code.getText().toString());
            request.addProperty(refer);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;

            androidHttpTransport.call("http://tempuri.org/IService1/DiscountCheck", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            check_response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + check_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }














    private class Submit_Service extends AsyncTask<String[], Void, String> {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
            submit_code();
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.e("resulttttt",s);
            progressDialog.dismiss();

            try {
                check_response="0";
                if(s.equals("0")){
                    new  Refferal_Service().execute();
                }else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Error");
                    dialog.setMessage("Code Has Already Used.");

                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                        }
                    });
                    dialog.show();
                }
            } catch (Exception e) {
                progressDialog.dismiss();

                e.printStackTrace();
            }
        }
    }



    private void submit_code() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "insertNewBooking");


            PropertyInfo id = new PropertyInfo();
            id.setType(android.R.string.class);
            id.setName("_customerId");
            id.setValue(sharedPreferences.getString("pid",""));
            request.addProperty(id);
            PropertyInfo route_id = new PropertyInfo();
            route_id.setType(android.R.string.class);
            route_id.setName("_routeId");
            route_id.setValue(sharedPreferences.getString("pid",""));
            request.addProperty(route_id);

            PropertyInfo route_type = new PropertyInfo();
            route_type.setType(android.R.string.class);
            route_type.setName("_routeType");
            route_type.setValue("");
            request.addProperty(route_type);

            PropertyInfo pickup_point = new PropertyInfo();
            pickup_point.setType(android.R.string.class);
            pickup_point.setName("_pickUpPoint");
            pickup_point.setValue(this.pickup_point.getText().toString());
            request.addProperty(pickup_point);

            PropertyInfo drop_point = new PropertyInfo();
            drop_point.setType(android.R.string.class);
            drop_point.setName("_dropPoint");
            drop_point.setValue(this.drop_point.getText().toString());
            request.addProperty(drop_point);

            PropertyInfo subs = new PropertyInfo();
            subs.setType(android.R.string.class);
            subs.setName("_subscriptionType");
            subs.setValue(seat_type);
            request.addProperty(subs);

            PropertyInfo _amount = new PropertyInfo();
            _amount.setType(android.R.string.class);
            _amount.setName("_amount");
            _amount.setValue(amount.getText().toString());
            request.addProperty(_amount);

            PropertyInfo _paymentMode = new PropertyInfo();
            _paymentMode.setType(android.R.string.class);
            _paymentMode.setName("_paymentMode");
            _paymentMode.setValue("Online");
            request.addProperty(_paymentMode);

            PropertyInfo _startDate = new PropertyInfo();
            _startDate.setType(android.R.string.class);
            _startDate.setName("_startDate");
            _startDate.setValue(sharedPreferences.getString("date",""));
            request.addProperty(_startDate);

            PropertyInfo _endDate = new PropertyInfo();
            _endDate.setType(android.R.string.class);
            _endDate.setName("_endDate");
            _endDate.setValue(sharedPreferences.getString("end_date",""));
            request.addProperty(_endDate);

            PropertyInfo refer = new PropertyInfo();
            refer.setType(android.R.string.class);
            refer.setName("_couponCode");
            refer.setValue(coupan_code.getText().toString());
            request.addProperty(refer);

            PropertyInfo StartShiftTime = new PropertyInfo();

            StartShiftTime.setName("StartShiftTime");
            StartShiftTime.setValue(start_time_str);
            request.addProperty(StartShiftTime);
            StartShiftTime.setType(Date.class);
            PropertyInfo EndShiftTime = new PropertyInfo();
            EndShiftTime.setType(Date.class);
            EndShiftTime.setName("EndShiftTime");
            EndShiftTime.setValue(end_time_str);
            request.addProperty(EndShiftTime);

            PropertyInfo TotalSeat = new PropertyInfo();
            TotalSeat.setType(android.R.string.class);
            TotalSeat.setName("TotalSeat");
            TotalSeat.setValue("1");
            request.addProperty(TotalSeat);

            PropertyInfo CouponDiscount = new PropertyInfo();
            CouponDiscount.setType(android.R.string.class);
            CouponDiscount.setName("CouponDiscount");
            CouponDiscount.setValue(coupan_code.getText().toString());
            request.addProperty(CouponDiscount);

            PropertyInfo AdminDiscount = new PropertyInfo();
            AdminDiscount.setType(android.R.string.class);
            AdminDiscount.setName("AdminDiscount");
            AdminDiscount.setValue(coupan_code.getText().toString());
            request.addProperty(AdminDiscount);

            PropertyInfo ReferralDiscount = new PropertyInfo();
            ReferralDiscount.setType(android.R.string.class);
            ReferralDiscount.setName("ReferralDiscount");
            ReferralDiscount.setValue(coupan_code.getText().toString());
            request.addProperty(ReferralDiscount);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport = new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;

            androidHttpTransport.call("http://tempuri.org/IService1/insertNewBooking", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + check_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }
}
