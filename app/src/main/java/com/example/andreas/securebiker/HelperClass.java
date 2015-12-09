package com.example.andreas.securebiker;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Andreas on 28.11.2015.
 */
public class HelperClass extends AppCompatActivity {


    public ArrayList<LatLng> getExample() {

        ArrayList<LatLng> list = new ArrayList<LatLng>();
        String s = "";
        InputStream in = null;
        InputStreamReader inr = null;
        BufferedReader br = null;

        try {
            in = getResources().openRawResource(R.raw.examplepoints);
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
        /**
         ArrayList<LatLng> list = new ArrayList<>();
         for (double i = 0; i < getResources().getInteger(R.integer.numberofpoints); i++) {
         String path = "R.array.p" + i;
         String[] temp = getResources().getStringArray(Integer.parseInt(path));
         Double[] t= new Double[2];
         t[0] = Double.parseDouble(temp[0]);
         t[1] = Double.parseDouble(temp[1]);
         list.add(new LatLng(t[0], t[1]));
         }
         **/
        return list;
    }
}
