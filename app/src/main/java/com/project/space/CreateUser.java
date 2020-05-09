package com.project.space;

public class CreateUser {
    public CreateUser(){}
    public String name;
    public String email;
    public String date;
    public String code;
    public String isSharing;
    public double lat;
    public double lng;
    public String user_id;
    public int count;

    public CreateUser(String name,String email,String date,String code, String isSharing, double lat, double lng, String user_id,int count){
        this.name= name;
        this.email=email;
        this.date=date;
        this.code=code;
        this.isSharing=isSharing;
        this.lat=lat;
        this.lng=lng;
        this.user_id=user_id;
        this.count = count;
    }
}
