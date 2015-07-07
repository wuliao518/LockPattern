package com.jiang.lockpattern;

/**
 * Created by Administrator on 2015/3/15 0015.
 */
public class Point {
    public float x,y;
    public int index;
    public enum Status{
        STATE_NORMAL,STATE_PRESSED,STATE_ERROR
    }
    Status currentState=Status.STATE_NORMAL;
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public boolean isPoint(float x,float y,float r){
        return  ((x-this.x)*(x-this.x)+(y-this.y)*(y-this.y))>r*r?false:true;
    }
}
