package com.example.dominhhoang20183922;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String BASE_API = "http://203.171.20.94:8083/weatherforecast/GetToken";
    private static final String First_Query_Param = "studentID";
    private static final String Second_Query_Param = "macAddress";
    private static String MAC = MainActivity.getMacAddress();

    static String getToken(String MaSV){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJSONString = null;
        try {
//            Uri builtURI = Uri.parse(BASE_API).buildUpon()
//                    .appendQueryParameter(First_Query_Param, MaSV)
//                    .appendQueryParameter(Second_Query_Param, MAC)
//                    .build();
//            URL requestURL = new URL(builtURI.toString());

            URL url = new URL(BASE_API);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.accumulate("studentId", MaSV);
                jsonObject.accumulate("macAddress", MAC);
            }catch (JSONException e){
                e.printStackTrace();
            }
            writer.write(jsonObject.toString());
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();

            StringBuffer sb = new StringBuffer();
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream in = urlConnection.getInputStream();
                int chr;
                while((chr = in.read()) != -1){
                    sb.append((char) chr);
                }
                in.close();
            } else{
                sb.append(urlConnection.getResponseCode());
            }

            resultJSONString = sb.toString();
            urlConnection.disconnect();

        }catch (IOException e){
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultJSONString;
    }
}
