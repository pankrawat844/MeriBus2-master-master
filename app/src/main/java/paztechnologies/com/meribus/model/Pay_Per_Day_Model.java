package paztechnologies.com.meribus.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Admin on 5/31/2017.
 */

public class Pay_Per_Day_Model  implements Parcelable{
    String s_no,date,total_seat,trip_type,seats,per_seat_total,per_seat_price;
    Boolean select;
    String one_way_price;
    public Pay_Per_Day_Model(Boolean select, String s_no, String date, String total_seat, String trip_type, String seats, String per_seat_total,String per_seat_price,String one_way_price) {
        this.select = select;
        this.s_no = s_no;
        this.date = date;
        this.total_seat = total_seat;
        this.trip_type = trip_type;
        this.seats = seats;
        this.per_seat_total = per_seat_total;
        this.per_seat_price=per_seat_price;
        this.one_way_price=one_way_price;
    }

    protected Pay_Per_Day_Model(Parcel in) {
        s_no = in.readString();
        date = in.readString();
        total_seat = in.readString();
        trip_type = in.readString();
        seats = in.readString();
        per_seat_total = in.readString();
        per_seat_price = in.readString();
        one_way_price = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(s_no);
        dest.writeString(date);
        dest.writeString(total_seat);
        dest.writeString(trip_type);
        dest.writeString(seats);
        dest.writeString(per_seat_total);
        dest.writeString(per_seat_price);
        dest.writeString(one_way_price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pay_Per_Day_Model> CREATOR = new Creator<Pay_Per_Day_Model>() {
        @Override
        public Pay_Per_Day_Model createFromParcel(Parcel in) {
            return new Pay_Per_Day_Model(in);
        }

        @Override
        public Pay_Per_Day_Model[] newArray(int size) {
            return new Pay_Per_Day_Model[size];
        }
    };

    public String getOne_way_price() {
        return one_way_price;
    }

    public void setOne_way_price(String one_way_price) {
        this.one_way_price = one_way_price;
    }

    public String getPer_seat_price() {
        return per_seat_price;
    }

    public void setPer_seat_price(String per_seat_price) {
        this.per_seat_price = per_seat_price;
    }



    public Boolean getSelect() {
        return select;
    }

    public void setSelect(Boolean select) {
        this.select = select;
    }

    public String getS_no() {
        return s_no;
    }

    public void setS_no(String s_no) {
        this.s_no = s_no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal_seat() {
        return total_seat;
    }

    public void setTotal_seat(String total_seat) {
        this.total_seat = total_seat;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getPer_seat_total() {
        return per_seat_total;
    }

    public void setPer_seat_total(String per_seat_total) {
        this.per_seat_total = per_seat_total;
    }
}
