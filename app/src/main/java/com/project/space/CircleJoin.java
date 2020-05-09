package com.project.space;

public class CircleJoin {
    public String circleMemberId;
    public double currDistance;
    public double prevDistance;


    public CircleJoin(String circleMemberId,double currDistance,double prevDistance){
        this.circleMemberId = circleMemberId;
        this.currDistance = currDistance;
        this.prevDistance = prevDistance;
    }
    public CircleJoin(){}
}
