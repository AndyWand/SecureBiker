package com.example.andreas.securebiker;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.*;

import java.util.ArrayList;

/**
 * Created by Andreas on 28.11.2015.
 */
public class HelperClass extends AppCompatActivity {


    public ArrayList<LatLng> getExample() {
        ArrayList<LatLng> list = new ArrayList<>();
        for (double i = 0; i < getResources().getInteger(R.integer.numberofpoints); i++) {
            String path = "R.array.p" + i;
            String[] temp = getResources().getStringArray(Integer.parseInt(path));
            Double[] t= new Double[2];
            t[0] = Double.parseDouble(temp[0]);
            t[1] = Double.parseDouble(temp[1]);
            list.add(new LatLng(t[0], t[1]));
        }

        return list;
    }
}
