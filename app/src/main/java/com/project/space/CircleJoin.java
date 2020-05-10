package com.project.space;

public class CircleJoin {
    public String circleMemberId;
    public int currDistance;
    public int prevDistance;


    public CircleJoin(String circleMemberId,int currDistance,int prevDistance){
        this.circleMemberId = circleMemberId;
        this.currDistance = currDistance;
        this.prevDistance = prevDistance;
    }
    public CircleJoin(){}
}
