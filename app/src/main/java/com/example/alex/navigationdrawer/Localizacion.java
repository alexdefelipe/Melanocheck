package com.example.alex.navigationdrawer;

import android.icu.text.DecimalFormat;
import android.util.Log;

import java.text.ParseException;

import static java.lang.Math.floor;

/**
 * Created by alex on 06/08/2018.
 */

public class Localizacion {
    Double lat;
    Double lon;
    Double value;
    Boolean address = false;
    DecimalFormat df = new DecimalFormat("#.00");

//    public Localizacion(Double lat, Double lon) {
//        this.lat = Math.round(lat * 100) / 100D;
//        this.lon = Math.round(lon * 100) / 100D;
//    }

    public Localizacion(Double lat, Double lon, Double value) {
        this.lat = Math.round(lat * 100) / 100D;
        this.lon = Math.round(lon * 100) / 100D;
        this.value = Math.round(value * 100) / 100D;
    }

    public Localizacion() {
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "Localizacion{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", value=" + value +
                '}';
    }

    public Double getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = Math.round(Double.parseDouble(value) * 100) / 100D;
    }
}
