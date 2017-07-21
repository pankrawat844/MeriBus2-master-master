package paztechnologies.com.meribus;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Admin on 5/29/2017.
 */

public class Debit_Card_Fragment extends Fragment {

    EditText card_no, card_name, cvv, exp;

    LinearLayout submit;

    String postdata;

    private String merchantKey = "Hf251r", userCredentials;
    SharedPreferences sp;
    String transaction_Id;
    private static String key = "gtKFFx";
    // private static String key = "0MQaQP";

    private static String user_credentials = key + ":" + "pank@pank.com";
    private String pg = "DC";
    private String bankcode = "DC";
    private static String s_Url = "https://payu.herokuapp.com/success";
    private static String f_Url = "https://payu.herokuapp.com/failure";
    private static String udf1 = "";
    private static String udf2 = "";
    private static String udf3 = "";
    private static String udf4 = "";
    private static String udf5 = "";
    private String url = "https://test.payu.in/_payment"; //for testing
    //  private String url = "https://secure.payu.in/_payment";//for production

    private static String payment_hash,card_month,card_year;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.debit_card, container, false);
        init(view);
        // String  hash = sha512(gtKFFx|1212|10|product|pankaj|pankrawat9@gmail.com|||||||||||SALT);
        sp=getActivity().getSharedPreferences("app",0);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] parts=exp.getText().toString().split("/");
                card_month=parts[0];
                card_year=parts[1];
                transaction_Id = "" + System.currentTimeMillis();
                postdata="txnid=" + transaction_Id +
                        "&device_type= android" +
                        "&ismobileview=1" +
                        "&productinfo= " +"abc"+
                        "&user_credentials=" + user_credentials +
                        "&key=" + key +
                        "&instrument_type=Put here Device info " +
                        "&surl=" + s_Url +
                        "&furl=" + f_Url + "" +
                        "&instrument_id=7dd17561243c202" +
                        "&firstname=" + sp.getString("name","") +
                        "&email=" + sp.getString("email","") +
                        "&phone=" + sp.getString("name","") +
                        "&amount=" + "100" +
                        "&ccnum=" + card_no.getText().toString() +
                        "&ccvv=" + cvv.getText().toString() +
                        "&ccexpmon=" +card_month +
                        "&ccexpyr=" + card_year +
                        "&pg=" + pg +
                        "&bankcode=" + bankcode +

//                "&bankcode=PAYUW" + //for PayU Money
//                "&pg=WALLET"+//for PayU Money
                        "&hash=";
                String salt="mDOpq5k3";
                // generateHashFromSDK(mPaymentParams,salt);
                generateHashFromServer();
            }

        });

        exp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==2){
                    s.append("/");
                }
            }
        });
        return view;
    }

    private void init(View v) {
        card_no = (EditText) v.findViewById(R.id.card_no);
        card_name = (EditText) v.findViewById(R.id.card_name);
        exp = (EditText) v.findViewById(R.id.exp);
        cvv = (EditText) v.findViewById(R.id.cvv);
        submit = (LinearLayout) v.findViewById(R.id.submit);


    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    public void generateHashFromServer() {
        //nextButton.setEnabled(false);
        // lets not allow the user to click the button again and again.
        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams("key", key));
        postParamsBuffer.append(concatParams("amount", sp.getString("amount","")));
        postParamsBuffer.append(concatParams("txnid", transaction_Id));
        postParamsBuffer.append(concatParams("email",sp.getString("email","")));
        postParamsBuffer.append(concatParams("productinfo","abc"));

        postParamsBuffer.append(concatParams("firstname", sp.getString("name","")));
        postParamsBuffer.append(concatParams("udf1",udf1));
        postParamsBuffer.append(concatParams("udf2", udf2));
        postParamsBuffer.append(concatParams("udf3", udf3));
        postParamsBuffer.append(concatParams("udf4",udf4));
        postParamsBuffer.append(concatParams("udf5", udf5));
        postParamsBuffer.append(concatParams("user_credentials",user_credentials));

        // for offer_key

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);

    }









    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            try {

                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                // get the payuConfig first
                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());


                return response.getString("payment_hash");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String payuHashes) {
            super.onPostExecute(payuHashes);

            progressDialog.dismiss();
            Intent intent = new Intent(getActivity(), Payment_webview_new.class);
            intent.putExtra("url", url);
            intent.putExtra("postData", postdata + payment_hash);
            startActivityForResult(intent, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Control will come back to this  place when transaction completed(for both fail and success)
        if (requestCode == 100) {
            if (resultCode == getActivity().RESULT_OK) {
//success
                if (data != null) {

                    /**
                     * Here, data.getStringExtra("payu_response") ---> Implicit response sent by PayU
                     * data.getStringExtra("result") ---> Response received from merchant's Surl/Furl
                     *
                     * PayU sends the same response to merchant server and in app. In response check the value of key "status"
                     * for identifying status of transaction. There are two possible status like, success or failure
                     * */

                    new AlertDialog.Builder(getActivity())
                            .setCancelable(false)

                            .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).show();
                }
            }
            if (resultCode == getActivity().RESULT_CANCELED) {
//failed
                if (data != null)
                    Toast.makeText(getActivity(), "Failed" + data.getStringExtra("result"), Toast.LENGTH_LONG).show();
            }
        }
    }

}
