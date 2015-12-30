package com.example.andreas.securebiker;


import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import com.google.android.gms.maps.model.LatLng;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Andreas on 28.11.2015.
 */
public class HelperClass extends AppCompatActivity {

    public static ArrayList<LatLng> getExample(Context c) {

        ArrayList<LatLng> list = new ArrayList<LatLng>();
        String s = "";
        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;

        try {
            in = c.getResources().openRawResource(R.raw.examplepoints);
            inr = new InputStreamReader(in);
            br = new BufferedReader(inr);
            while ((s = br.readLine()) != null) {
                String[] temp = s.split(";");
                int num = Integer.parseInt(temp[0]);
                double lat = Double.parseDouble(temp[1]);
                double lon = Double.parseDouble(temp[2]);
                LatLng l = new LatLng(lat, lon);
                list.add(l);
            }
        } catch (IOException e) {
        }
        return list;
    }
}
