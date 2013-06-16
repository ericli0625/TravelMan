package com.project2.travelman;

public class spotAddress {

    private String longitude,latitude;
    private String name;

    spotAddress(String x,String y,String z){
        latitude = x;
        longitude = y;
        name = z;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getLatitude(){
        return latitude;
    }

    public void setLongitude(String x){
        longitude = x;
    }

    public void setLatitude(String x){
        latitude = x;
    }

    public String getName(){
        return latitude;
    }

    public void setName(String x){
        name = x;
    }

}
