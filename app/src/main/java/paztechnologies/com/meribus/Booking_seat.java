package paztechnologies.com.meribus;


import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.List;

import paztechnologies.com.meribus.Adapter.Pay_Per_Day_Adapter;
import paztechnologies.com.meribus.model.Pay_Per_Day_Model;


/**
 * A simple {@link Fragment} subclass.
 */
public class Booking_seat extends Fragment {
        RecyclerView recyclerView;
        List<Pay_Per_Day_Model> list= new ArrayList<>();
        Pay_Per_Day_Adapter adapter;
    String pickup_response,_routeId,StartShiftTime,EndShiftTime,Seattype,per_seat_price;
    String one_way_total;
    public static final String TOTAL_STATUS_FILTER = "paztechnologies.com.meribus.totalfilter";
    String[] temp=new String[]{};
    List<String> date_selected= new ArrayList<>();
    List<String> seat_selected_price= new ArrayList<>();
    List<String> seat_type= new ArrayList<>();
    ArrayList<Pay_Per_Day_Model> model_obj= new ArrayList<>();
    int total=0;
    TextView total_txt;
    Button submit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_booking_seat, container, false);
        _routeId=getArguments().getString("_routeId");
        StartShiftTime=getArguments().getString("StartShiftTime");
        EndShiftTime=getArguments().getString("EndShiftTime");
        Seattype=getArguments().getString("Seattype");
        per_seat_price=getArguments().getString("perseat_price");
        one_way_total=getArguments().getString("one_way_amount");
         recyclerView=(RecyclerView)view.findViewById(R.id.recycler_view);
        submit=(Button)view.findViewById(R.id.submit);
        total_txt=(TextView)view.findViewById(R.id.total);
        adapter= new Pay_Per_Day_Adapter(list,getActivity(),temp);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        new Seat_Count().execute();
        getActivity().registerReceiver(update_total,new IntentFilter(TOTAL_STATUS_FILTER));
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<list.size();i++){
                    model_obj.clear();
                    Pay_Per_Day_Model pay_per_day_model= list.get(i);
                    if(pay_per_day_model.getSelect()){
                           model_obj.add(pay_per_day_model);
//                        date_selected.add(pay_per_day_model.getDate());
//                        seat_selected_price.add(pay_per_day_model.getPer_seat_total());
//                        seat_type.add(pay_per_day_model.getTrip_type());
                    }
                }
                Bundle args = new Bundle();
                args.putParcelableArrayList("model",model_obj);
                args.putString("_routeId",_routeId);
                args.putString("StartShiftTime",StartShiftTime);
                args.putString("EndShiftTime",EndShiftTime);
                args.putString("Seattype",Seattype);
                args.putString("perseat_price","");
                args.putString("amount",total_txt.getText().toString());
                Ride_Detail_Perday ride= new Ride_Detail_Perday();
                ride.setArguments(args);
                android.support.v4.app.FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container,ride);
                ft.commit();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(update_total);
    }

    private class Seat_Count extends AsyncTask<String[], Void, String> {

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

                //   Toast.makeText(getActivity(),s,3).show();
                JSONArray jsonArray = new JSONArray(s);
                JSONArray arr = jsonArray.getJSONArray(0);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonObject = arr.getJSONObject(i);
                    Pay_Per_Day_Model model= new Pay_Per_Day_Model(false,jsonObject.getString("S.No"),jsonObject.getString("Date"),jsonObject.getString("Total Seat"),"Both Pickup and Drop","","",per_seat_price,one_way_total);
                    list.add(model);
                }
                 adapter.notifyDataSetChanged();
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }



    private void setpickup_point() {
        try {
            SoapObject request = new SoapObject("http://tempuri.org/", "PPS_BindGrid_For_Seats_when_RadioButton");

            PropertyInfo Orderid = new PropertyInfo();
            Orderid.setType(android.R.string.class);
            Orderid.setName("_routeId");
            Orderid.setValue(_routeId);
            request.addProperty(Orderid);


            PropertyInfo start = new PropertyInfo();
            start.setType(android.R.string.class);
            start.setName("StartShiftTime");
            start.setValue(StartShiftTime);
            request.addProperty(start);

            PropertyInfo end = new PropertyInfo();
            end.setType(android.R.string.class);
            end.setName("EndShiftTime");
            end.setValue(EndShiftTime);
            request.addProperty(end);

            PropertyInfo aa123 = new PropertyInfo();
            aa123.setType(android.R.string.class);
            aa123.setName("Seattype");
            aa123.setValue(Seattype);
            request.addProperty(aa123);
            //Toast.makeText(getActivity(),route_id.get(select_route.getSelectedItemPosition())+" "+seat_type,3).show();

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE androidHttpTransport =
                    new HttpTransportSE("http://sales.meribus.com/Service1.svc", 5000);
            androidHttpTransport.debug = true;
            androidHttpTransport.call("http://tempuri.org/IService1/PPS_BindGrid_For_Seats_when_RadioButton", envelope);
            SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();

            pickup_response = soapPrimitive.toString();
            Log.e("TAG", "Soap primitive1" + pickup_response);
        } catch (SocketTimeoutException e) {

        } catch (Exception e) {
            Log.e("TAG", "Soap Exception" + e.toString());
        }
    }


    BroadcastReceiver update_total= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getBooleanExtra("ischecked", false)) {
                    total = total+intent.getIntExtra("total", 0);
                    total_txt.setText(String.valueOf(total));
                } else {
                // if (Integer.valueOf(total_txt.getText().toString()) >= 0)
                    total = total - intent.getIntExtra("total", 0);
                    total_txt.setText(String.valueOf(total));

               }
            }catch (Exception e){

                e.printStackTrace();
            }
        }
    };
}

