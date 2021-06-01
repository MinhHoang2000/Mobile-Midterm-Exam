package com.example.dominhhoang20183922;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static String getMacAddress(){
        try{
            List<NetworkInterface> networkInterfaceList =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            String stringMac = "";
            for(NetworkInterface networkInterface : networkInterfaceList)
            {
                if(networkInterface.getName().equalsIgnoreCase("wlan0"))
                {
                    for(int i = 0 ;i <networkInterface.getHardwareAddress().length; i++){
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i]&
                                0xFF);
                        if(stringMacByte.length() == 1)
                        {
                            stringMacByte = "0" +stringMacByte;
                        }
                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                    }
                    break;
                }
            }
            stringMac = stringMac.substring(0, stringMac.length() - 1);
            return stringMac;
        }catch (SocketException e)
        {
            e.printStackTrace();
        }
        return "0";
    }

    private String mMACString = getMacAddress();
    private EditText mMaSV;
    private  EditText mMAC;
    private TextView mLocalDate;
    private  TextView mServerDate;
    private  TextView mToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMaSV = (EditText)findViewById(R.id.masv_editText);
        mMAC = (EditText)findViewById(R.id.MAC_editText);
        mLocalDate = (TextView)findViewById(R.id.datelocal_textView);
        mServerDate = (TextView)findViewById(R.id.dateserver_textView);
        mToken = (TextView)findViewById(R.id.tokenTW);

        mMAC.setText(mMACString);
    }


    public class FetchToken extends AsyncTask<String, Void, String>{

        private WeakReference<TextView> mToken;
        private WeakReference<TextView> mServerDate;
        private  WeakReference<TextView> mLocalDate;

        public FetchToken(TextView Token, TextView ServerDate){
            this.mToken = new WeakReference<>(Token);
            this.mServerDate = new WeakReference<>(ServerDate);
        }

        @Override
        protected String doInBackground(String... strings) {
            return NetworkUtils.getToken(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                String studentToken = null;
                String serverTime = null;

                studentToken = jsonObject.getString("studentToken");
                serverTime = jsonObject.getString("serverTime");

                if(studentToken != null && serverTime != null){
                    mToken.get().setText(studentToken);
                    mServerDate.get().setText(serverTime);
                }else{
                    mToken.get().setText("ERROR");
                    mServerDate.get().setText("ERROR");
                }
            }catch (JSONException e){
                mToken.get().setText("ERROR");
                mServerDate.get().setText("ERROR");
            }
        }
    }

    public void getToken(View view) {
        String MaSVInput = mMaSV.getText().toString();
        Calendar calendar = Calendar.getInstance();
        int hour12hrs = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String hh = Integer.toString(hour12hrs);
        String mm = Integer.toString(minutes);
        String ss = null;
        if(seconds < 10){
            ss = "0" + Integer.toString(seconds);
        }else{
            ss = Integer.toString(seconds);
        }
        String curr_time = hh+":"+mm+":"+ss;
        mLocalDate.setText(curr_time);

        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null ) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        if (networkInfo != null && networkInfo.isConnected() && MaSVInput.length() != 0) {
            new FetchToken(mToken, mServerDate).execute(MaSVInput);
            mToken.setText("Loading...");
            mServerDate.setText("Loading...");
        }
    }
}