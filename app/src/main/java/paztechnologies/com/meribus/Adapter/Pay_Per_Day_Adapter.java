package paztechnologies.com.meribus.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import paztechnologies.com.meribus.Booking_seat;
import paztechnologies.com.meribus.DatabaseHelper.SeatDatabase;
import paztechnologies.com.meribus.R;
import paztechnologies.com.meribus.model.Pay_Per_Day_Model;

/**
 * Created by Admin on 5/31/2017.
 */

public class Pay_Per_Day_Adapter extends RecyclerView.Adapter<Pay_Per_Day_Adapter.viewholder>  {
    List<Pay_Per_Day_Model> list;
    Context ctx;
    String[] arr= {"Both Pickup and Drop","Pick Up","Drop"};
    String[] temp;
    TextCallBackListener textCallBackListener;
    HashMap<String,Integer> map= new HashMap<>();
    SharedPreferences sp;
    public Pay_Per_Day_Adapter(List<Pay_Per_Day_Model> list,Context ctx,String[] temp){
        this.list=list;
        this.ctx=ctx;
        this.temp=temp;
        sp= ctx.getSharedPreferences("app",0);


    }
    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payper_layout1,parent,false);
        return new viewholder(view,new MyCustomEditTextListener());
    }

    @Override
    public void onBindViewHolder( final viewholder holder, final int position) {
        final Pay_Per_Day_Model model= list.get(position);
        holder.srno.setText(model.getS_no());
        holder.date.setText(model.getDate());
        holder.total_seat.setText(model.getTotal_seat());
       // holder.per_seat_price.setText(model.getPer_seat_price());
        //   holder.seat.setText(model.getSeats());
        //  holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
          //holder.seat.setText(temp[holder.getAdapterPosition()]);

        final SharedPreferences.Editor editor= sp.edit();



        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //  Toast.makeText(ctx, "You have appied for maximum seats.", Toast.LENGTH_SHORT).show();
                    int totalseat = Integer.valueOf(holder.total_seat.getText().toString());
                    int seat=Integer.valueOf(holder.seat.getText().toString());
                    Log.e("seat",holder.seat.getText().toString());
                    Log.e("totalseat",(holder.total_seat.getText().toString()));
                    Log.e("per seat",String.valueOf(Integer.valueOf(holder.per_seat_price.getText().toString()) * Integer.valueOf(totalseat)));
                    --seat;
                    if (seat >= 0) {

                        if( holder.trip_type.getSelectedItem().toString().equals("Both Pickup and Drop")) {
                            holder.per_seat_price.setText(model.getPer_seat_price());
                            int total = Integer.valueOf(holder.per_seat_price.getText().toString()) * seat;
//
                            Log.e("totalllllllllll",""+total);
                            //   holder.updateText(total);
//                            editor.putInt("seat" + position, seat);
//                            editor.putInt("total" + position, total);
//                            editor.commit();
                            holder.total_price.setText(String.valueOf(total));
                            holder.seat.setText(String.valueOf(seat));
                            notifyDataSetChanged();
                        }else if( holder.trip_type.getSelectedItem().toString().equals("Pick Up") ||  holder.trip_type.getSelectedItem().toString().equals("Drop")) {
                            holder.per_seat_price.setText(model.getOne_way_price());
                            int total = Integer.valueOf(holder.per_seat_price.getText().toString()) * seat;
//
                            Log.e("totalllllllllll", "" + total);
                            //   holder.updateText(total);
//                            editor.putInt("seat" + position, seat);
//                            editor.putInt("total" + position, total);
//                            editor.commit();
                            holder.total_price.setText(String.valueOf(total));
                            holder.seat.setText(String.valueOf(seat));
                            notifyDataSetChanged();
                        }




                    }else{

                        //  Toast.makeText(ctx,"You have applied for maximum seats.",Toast.LENGTH_SHORT).show();
                    }


                }catch (Exception e){
                    e.printStackTrace();
                }

                if(holder.select_check.isChecked()){
                    if(holder.seat.getText().equals("0")) {
                        sendBroadcast(Integer.valueOf(holder.per_seat_price.getText().toString()), false);
                    }else {
                        sendBroadcast(Integer.valueOf(holder.per_seat_price.getText().toString()), false);

                    }
                }
                model.setPer_seat_total( holder.total_price.getText().toString());
            }
        });
        holder.select_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    sendBroadcast(Integer.valueOf(holder.total_price.getText().toString()), isChecked);
                    if(isChecked){
                        model.setSelect(true);
                    }else{
                        model.setSelect(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //  Toast.makeText(ctx, "You have appied for maximum seats.", Toast.LENGTH_SHORT).show();

                    int totalseat = Integer.valueOf(holder.total_seat.getText().toString());
                    int seat=Integer.valueOf(holder.seat.getText().toString());
                    ++seat;
                    if (seat <= totalseat) {
                        if( holder.trip_type.getSelectedItem().toString().equals("Both Pickup and Drop")) {
                            holder.per_seat_price.setText(model.getPer_seat_price());
                            int total = Integer.valueOf(holder.per_seat_price.getText().toString()) * seat;
//
                            Log.e("totalllllllllll", "" + total);
                            //   holder.updateText(total);
//                            editor.putInt("seat" + position, seat);
//                            editor.putInt("total" + position, total);
//                            editor.commit();
                            holder.total_price.setText(String.valueOf(total));
                            holder.seat.setText(String.valueOf(seat));
                            notifyDataSetChanged();
                        }else if( holder.trip_type.getSelectedItem().toString().equals("Pick Up") ||  holder.trip_type.getSelectedItem().toString().equals("Drop")) {
                            holder.per_seat_price.setText(model.getOne_way_price());
                            int total = Integer.valueOf(holder.per_seat_price.getText().toString()) * seat;
//
                            Log.e("totalllllllllll", "" + total);
                            //   holder.updateText(total);
//                            editor.putInt("seat" + position, seat);
//                            editor.putInt("total" + position, total);
//                            editor.commit();
                            holder.total_price.setText(String.valueOf(total));
                            holder.seat.setText(String.valueOf(seat));
                            notifyDataSetChanged();
                        }
//

                    }else{

                        Toast.makeText(ctx, "You have applied for maximum seats.", Toast.LENGTH_SHORT).show();
                    }
                    if(holder.select_check.isChecked()){
                        sendBroadcast(Integer.valueOf(holder.per_seat_price.getText().toString()), true);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            model.setPer_seat_total( holder.total_price.getText().toString());
            }
        });


        holder.trip_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( holder.trip_type.getSelectedItem().toString().equals("Both Pickup and Drop")){
                    model.setTrip_type("Both Pickup and Drop");
                    holder.per_seat_price.setText(String.valueOf(model.getPer_seat_total()));
                    if(holder.seat.getText().equals("0")) {

                    }else{

                        holder.per_seat_price.setText(String.valueOf(model.getPer_seat_price()));
                        holder.seat.setText("0");
                        holder.total_price.setText("0");
                        if(holder.select_check.isChecked()){
                            sendBroadcast(0, true);
                        }

                    }
                }else if( holder.trip_type.getSelectedItem().toString().equals("Pick Up")){
                    holder.per_seat_price.setText(String.valueOf(model.getOne_way_price()));
                    model.setTrip_type("Pick Up");
                    if(holder.select_check.isChecked()){
                        sendBroadcast(Integer.valueOf(holder.total_price.getText().toString()), false);
                    }
                    holder.seat.setText("0");
                    holder.total_price.setText("0");
                }else if(holder.trip_type.getSelectedItem().toString().equals("Drop")){
                    holder.per_seat_price.setText(String.valueOf(model.getOne_way_price()));
                    model.setTrip_type("Drop");
                    if(holder.select_check.isChecked()){
                        sendBroadcast(Integer.valueOf(holder.total_price.getText().toString()), false);
                    }
                    holder.seat.setText("0");
                    holder.total_price.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    class viewholder extends RecyclerView.ViewHolder implements TextCallBackListener{
        Spinner trip_type;
        com.rey.material.widget.CheckBox select_check;
        TextView srno,date,total_seat,per_seat_price,total_price;
        TextView seat;
        ImageView plus, minus;
        public MyCustomEditTextListener myCustomEditTextListener;
        public viewholder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);

            trip_type=(Spinner)itemView.findViewById(R.id.trip_type);
            ArrayAdapter<String> arrayAdapter= new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,arr);
            trip_type.setAdapter(arrayAdapter);
            srno=(TextView)itemView.findViewById(R.id.srno);
            date=(TextView)itemView.findViewById(R.id.date);
            seat=(TextView)itemView.findViewById(R.id.seat);
            total_seat=(TextView)itemView.findViewById(R.id.total_seat);
            per_seat_price=(TextView)itemView.findViewById(R.id.per_seat_price);
            total_price=(TextView)itemView.findViewById(R.id.total);
            select_check=( com.rey.material.widget.CheckBox)itemView.findViewById(R.id.checkbox);
            plus=(ImageView)itemView.findViewById(R.id.plus);
            minus=(ImageView)itemView.findViewById(R.id.minus);
        //    this.seat.addTextChangedListener(myCustomEditTextListener);

        }

        @Override
        public void updateText(int val) {
            total_price.setText(val);
        }
    }



    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            temp[position] = charSequence.toString();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }

    private void sendBroadcast(int total,boolean ischecked){
        Intent intent= new Intent(Booking_seat.TOTAL_STATUS_FILTER);
        intent.putExtra("ischecked",ischecked);
        intent.putExtra("total",total);
        ctx.sendBroadcast(intent);
    }
    private void sendBroadcast1(int total,boolean ischecked){
        Intent intent= new Intent(Booking_seat.TOTAL_STATUS_FILTER);
        intent.putExtra("ischecked",ischecked);
        intent.putExtra("total_deduct",total);
        ctx.sendBroadcast(intent);
    }
}
