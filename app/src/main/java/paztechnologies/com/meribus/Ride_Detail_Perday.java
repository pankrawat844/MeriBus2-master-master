package paztechnologies.com.meribus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import java.util.Map;

import paztechnologies.com.meribus.model.Pay_Per_Day_Model;

/**
 * Created by Admin on 3/24/2017.
 */

public class Ride_Detail_Perday extends Fragment implements OnMapReadyCallback, View.OnClickListener, DirectionCallback {
    String Server_Key = "AIzaSyCdUU47miOD97PDox1FwhGr3SHPkbG4A2s";
    private GoogleMap googleMap;
    private MapView mMapView;
    //private LatLng camera = new LatLng(28.6158851, 77.0406466);
    private LatLng curr;
    private LatLng des;
    private Button payment;
    private TextView start_time,end_time,select_route,pickup_point,drop_point,amount,distance_txt,time_txt,selected_date;
    SharedPreferences sharedPreferences;
    ArrayList<Pay_Per_Day_Model> model_list;
    String select_date_str=" ";
    String refferal_response,check_response;
    EditText coupan_code;
    ImageView send;
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
        View view = inflater.inflate(R.layout.ride_detail_perday, container, false);
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
            amount.setText("RS "+getArguments().getString("amount",""));
            model_list=getArguments().getParcelableArrayList("model");
                for(int i=0;i<model_list.size();i++){
                    Pay_Per_Day_Model model= model_list.get(i);
                    select_date_str=select_date_str.concat(model.getDate()+" ");

                }
        selected_date.setText(select_date_str);
            SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);

             mapFragment.getMapAsync(this);
             payment.setOnClickListener(this);

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
            selected_date=(TextView)v.findViewById(R.id.select_date);
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
                    new Refferal_Service().execute();
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
            refer.setValue( coupan_code.getText().toString());
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
}
