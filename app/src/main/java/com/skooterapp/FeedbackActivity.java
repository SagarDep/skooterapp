package com.skooterapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class FeedbackActivity extends BaseActivity {
    private static final String LOG_TAG = FeedbackActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        setContentView(R.layout.activity_feedback);

        activateToolbarWithHomeEnabled("Feedback");
    }

    protected void onPause() {
        super.onPause();
        //closing transition animations
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_feedback) {
            //Upload the data to google forms
            String url = "https://docs.google.com/forms/d/1Cf3pEPNOF0tWwVmR8mYylDksf8Hs_DdgzJwEPVy0p_M/formResponse";

            final TextView email = (TextView) findViewById(R.id.email);
            final TextView feedback = (TextView) findViewById(R.id.feedback);
            final TextView name = (TextView) findViewById(R.id.name);
            final TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);

            final StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(LOG_TAG, response.substring(1700));
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    VolleyLog.d(LOG_TAG, error.getMessage());
                }
            }) {
                public byte[] getBody() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("fbzx", "3373883434178628382");
                    params.put("pageHistory", "0");
                    params.put("entry.528163506", feedback.getText().toString());
                    params.put("entry.609712554", email.getText().toString());
                    params.put("entry.480017787", name.getText().toString());
                    params.put("entry.1210654817", Build.DEVICE);
                    params.put("entry.1766166360", Integer.toString(Build.VERSION.SDK_INT));
                    params.put("entry.1977350393", phoneNumber.getText().toString());
                    params.put("entry.1872674154", Integer.toString(BaseActivity.userId));

                    if (params != null && !params.isEmpty()) {
                        return encodeParameters(params, getParamsEncoding());
                    }
                    return null;
                }


                protected byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
                    StringBuilder encodedParams = new StringBuilder();
                    try {
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                            encodedParams.append('=');
                            encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                            encodedParams.append('&');
                        }
                        return encodedParams.toString().getBytes(paramsEncoding);
                    } catch (UnsupportedEncodingException uee) {
                        throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
                    }
                }
            };
            AppController.getInstance().addToRequestQueue(jsonObjectRequest, "feedback");
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
