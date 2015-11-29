package com.example.andreas.securebiker;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.*;

import java.util.ArrayList;

/**
 * Created by Andreas on 28.11.2015.
 */
public class HelperClass extends AppCompatActivity {


    public ArrayList<Point> getExample() {
        ArrayList<Point> list = new ArrayList<>();
        for (int i = 0; i < getResources().getInteger(R.integer.numberofpoints); i++) {
            String path = "R.array.p" + i;
            int[] temp = getResources().getIntArray(Integer.parseInt(path));
            list.add(new Point(temp[0], temp[1]));
        }

        return list;
    }
}
