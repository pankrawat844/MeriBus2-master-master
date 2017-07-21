package paztechnologies.com.meribus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.payu.custombrowser.Bank;
import com.payu.custombrowser.CustomBrowser;
import com.payu.custombrowser.PayUCustomBrowserCallback;
import com.payu.custombrowser.PayUWebChromeClient;
import com.payu.custombrowser.bean.CustomBrowserConfig;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Payu.PayuConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 3/9/2017.
 */

public class Payment_webview_new extends FragmentActivity {
    //payment URL
    private String url;

    //post parameters to send to PayU server
    private String postData;

    private boolean viewPortWide = false;
    private String txnId = null;
    private String merchantKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        url = getIntent().getExtras().getString("url");
        postData = getIntent().getExtras().getString("postData");

        String[] list = postData.split("&");

        for (String item : list) {
            String[] items = item.split("=");
            if (items.length >= 2) {
                String id = items[0];
                switch (id) {
                    case "txnid":
                        txnId = items[1];
                        break;
                    case "key":
                        merchantKey = items[1];
                        break;
                    case "pg":
                        if (items[1].contentEquals("NB")) {
                            viewPortWide = true;
                        }
                        break;

                }
            }
        }

        //Let us add some callbacks for Custom Browser
        PayUCustomBrowserCallback payUCustomBrowserCallback = new PayUCustomBrowserCallback() {

            /**
             * This method will be called after a failed transaction.
             *
             * @param payuResponse     response sent by PayU in App
             * @param merchantResponse response received from Furl
             */
            @Override
            public void onPaymentFailure(String payuResponse, String merchantResponse) {

                Intent intent = new Intent();
                intent.putExtra(getString(R.string.cb_result), merchantResponse);
                intent.putExtra(getString(R.string.cb_payu_response), payuResponse);
                setResult(Activity.RESULT_CANCELED, intent);
                finish();

            }

            @Override
            public void onPaymentTerminate() {
                finish();
            }

            /**
             * This method will be called after a successful transaction.
             *
             * @param payuResponse     response sent by PayU in App
             * @param merchantResponse response received from Furl
             */
            @Override
            public void onPaymentSuccess(String payuResponse, String merchantResponse) {
                Intent intent = new Intent();
                intent.putExtra(getString(R.string.cb_result), merchantResponse);
                intent.putExtra(getString(R.string.cb_payu_response), payuResponse);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onCBErrorReceived(int code, String errormsg) {
            }

            @Override
            public void setCBProperties(WebView webview, Bank payUCustomBrowser) {
                webview.setWebChromeClient(new PayUWebChromeClient(payUCustomBrowser));

                // The following setting is optional, set WV client only when using your custom WVclient
                // Also, custom WV client should inherit PayUSurePayWebViewClient in case of SurePay enabled,
                // Otherwise PayUWebViewClient.
                // webview.setWebViewClient(new PayUWebViewClient(payUCustomBrowser, merchantKey));
            }

            @Override
            public void onBackApprove() {
                Payment_webview_new.this.finish();
            }

            @Override
            public void onBackDismiss() {
                super.onBackDismiss();
            }

            /**
             * This callback method will be invoked when setDisableBackButtonDialog is set to true.
             *
             * @param alertDialogBuilder a reference of AlertDialog.Builder to customize the dialog
             */
            @Override
            public void onBackButton(AlertDialog.Builder alertDialogBuilder) {
                super.onBackButton(alertDialogBuilder);
            }

            // Below code is used only when magicRetry is set to true in customBrowserConfig

/*            @Override
            public void initializeMagicRetry(Bank payUCustomBrowser, WebView webview, MagicRetryFragment magicRetryFragment) {
                webview.setWebViewClient(new PayUWebViewClient(payUCustomBrowser, magicRetryFragment, merchantKey));
                Map<String, String> urlList = new HashMap<String, String>();
                urlList.put(url, postData);
                payUCustomBrowser.setMagicRetry(urlList);
            }*/
        };


        //Sets the configuration of custom browser
        CustomBrowserConfig customBrowserConfig = new CustomBrowserConfig(merchantKey, txnId);
        customBrowserConfig.setViewPortWideEnable(viewPortWide);

        //TODO don't forgot to set AutoApprove and AutoSelectOTP to true for One Tap payments
        customBrowserConfig.setAutoApprove(false);
        customBrowserConfig.setAutoSelectOTP(false);

        //Set below flag to true to disable the default alert dialog of Custom Browser and use your custom dialog
        customBrowserConfig.setDisableBackButtonDialog(false);

        //Below flag is used for One Click Payments. It should always be set to CustomBrowserConfig.STOREONECLICKHASH_MODE_SERVER
        customBrowserConfig.setStoreOneClickHash(CustomBrowserConfig.STOREONECLICKHASH_MODE_SERVER);

        //Set it to true to enable run time permission dialog to appear for all Android 6.0 and above devices
        customBrowserConfig.setMerchantSMSPermission(true);

        //Set it to true to enable Magic retry (If MR is enabled SurePay should be disabled and vice-versa)
        customBrowserConfig.setmagicRetry(false);

        /**
         * Maximum number of times the SurePay dialog box will prompt the user to retry a transaction in case of network failures
         * Setting the sure pay count to 0, diables the sure pay dialog
         */
        customBrowserConfig.setEnableSurePay(3);

        /**
         * set Merchant Checkout Activity(Absolute path of activity)
         * By the time CB detects good network, if CBWebview is destroyed, we resume the transaction by passing payment post data to,
         * this, merchant checkout activity.
         * */
        customBrowserConfig.setMerchantCheckoutActivityPath("paztechnologies.com.meribus.MerchantCheckoutActivity");

        customBrowserConfig.setPostURL(url);
        customBrowserConfig.setPayuPostData(postData);

        new CustomBrowser().addCustomBrowser(Payment_webview_new.this, customBrowserConfig, payUCustomBrowserCallback);

    }

    }
