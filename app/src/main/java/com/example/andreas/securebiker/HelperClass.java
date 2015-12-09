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
        int r = getResources().getInteger(R.integer.numberofpoints);
        for (int i = 0; i < r; i++) {
            String path = "R.array.p" + i;
            String[] t = getResources().getStringArray(Integer.parseInt(path));
            double x = Double.parseDouble(t[0]);
            double y = Double.parseDouble(t[1]);
            list.add(new LatLng(x,y));
        }
        return list;
    }
}
