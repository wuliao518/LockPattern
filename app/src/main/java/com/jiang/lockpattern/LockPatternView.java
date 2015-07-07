package com.jiang.lockpattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuiao on 2015/3/15 0015.
 */
public class LockPatternView extends View{
    private boolean isInit=true;
    private Point[][] points=new Point[3][3];
    private Bitmap pointNormal,pointPressed,pointError,linePressed,lineError;
    private Paint paint;
    //半径
    private float r;
    //手指所在位置
    private float x,y;
    private boolean isFinish=false;
    private boolean isChecked=false;
    private List<Point> pointList=new ArrayList<>();
    private boolean movingNoPoint=true;

    public LockPatternView(Context context) {
        super(context,null);
    }

    public LockPatternView(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public LockPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint=new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isInit){
            isInit=false;
            initPoint();
        }
        canvasLine(canvas);
        canvasPoint(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x=event.getX();
        y=event.getY();
        Point point = null;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                resetPoints();
                isFinish=false;
                point=checkPointSelected(x,y,r);
                if(point!=null){
                    isChecked=true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                point=checkPointSelected(x,y,r);
                if(point==null){
                    movingNoPoint=true;
                }else{
                    isChecked=true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isFinish=true;
                isChecked=false;
                movingNoPoint=false;
                break;
        }
        if(isChecked&&!isFinish&&point!=null){
            if(!isContains(point)){
                pointList.add(point);
                point.currentState= Point.Status.STATE_PRESSED;
            }else{
                movingNoPoint=true;
            }
        }
        if(isFinish){
            setPointError();
        }
        invalidate();
        return true;
    }

    private void canvasLine(Canvas canvas) {
        Point a;
        if(pointList!=null&&pointList.size()>0){
            a=pointList.get(0);
            for(int i=0;i<pointList.size();i++){
                Point b=pointList.get(i);
                canvasPointLine(canvas, a, b);
                a=b;
            }
            //绘制鼠标点
            if(movingNoPoint){
                canvasPointLine(canvas, a, new Point(x,y));
            }
        }
    }

    private void canvasPointLine(Canvas canvas,Point a, Point b) {
        double degree=getDegree(a,b);
        Log.e("jiang","degree="+degree);
        Matrix matrix=new Matrix();
        matrix.setScale((int)(Math.ceil(getDistance(a, b)+r)/(float)(lineError.getWidth())),1);
        matrix.postTranslate(a.x,a.y-linePressed.getHeight()/2);
        canvas.rotate((float)degree,a.x,a.y);
        if(a.currentState== Point.Status.STATE_PRESSED){
            canvas.drawBitmap(linePressed,matrix,paint);
        }else{
            canvas.drawBitmap(lineError,matrix,paint);
        }
        canvas.rotate(-(float)degree,a.x,a.y);
    }

    private double getDistance(Point a,Point b){
        return Math.sqrt((b.x-a.x)*(b.x-a.x)+(b.y-a.y)*(b.y-a.y));
    }
//    private double getDegree(Point a,Point b){
//        if(b.y==a.y){
//            if(a.x>b.x){
//                return 180;
//            }else if(a.x<b.x){
//                return 0;
//            }
//        }else if(a.x==b.x){
//            if(a.y>b.y){
//                return 270;
//            }else if(a.y<b.y){
//                return 90;
//            }
//        }
//        return 0;
//    }
    public float getDegree(Point pointA, Point pointB) {
        return (float) Math.toDegrees(Math.atan2(pointB.y - pointA.y, pointB.x - pointA.x));
    }


    private void resetPoints() {
        for(int i=0;i<pointList.size();i++){
            pointList.get(i).currentState= Point.Status.STATE_NORMAL;
        }
        pointList.clear();
    }
    private void setPointError(){
        for(int i=0;i<pointList.size();i++){
            pointList.get(i).currentState= Point.Status.STATE_ERROR;
        }
    }

    private boolean isContains(Point point){
        if(pointList.contains(point)){
            return true;
        }
        return false;
    }

    private Point checkPointSelected(float x,float y,float r) {
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                Point point=points[i][j];
                if(point.isPoint(x,y,r)){
                    return point;
                }
            }
        return null;
    }

    private void canvasPoint(Canvas canvas) {
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++){
                Point point=points[i][j];
                if(point.currentState== Point.Status.STATE_NORMAL){
                    canvas.drawBitmap(pointNormal,point.x-r,point.y-r,paint);
                }else if(point.currentState== Point.Status.STATE_PRESSED){
                    canvas.drawBitmap(pointPressed,point.x-r,point.y-r,paint);
                }else if(point.currentState== Point.Status.STATE_ERROR){
                    canvas.drawBitmap(pointError,point.x-r,point.y-r,paint);
                }
            }
    }

    private void initPoint() {
        int width=getWidth();
        int height=getHeight();
        int offsetX=0,offsetY=0;
        if(width>height){
            offsetX=(width-height)/2;
            width=height;
        }else{
            offsetY=(height-width)/2;
            height=width;
        }
        pointNormal=BitmapFactory.decodeResource(getResources(),R.mipmap.oval_normal);
        pointPressed=BitmapFactory.decodeResource(getResources(),R.mipmap.oval_pressed);
        pointError=BitmapFactory.decodeResource(getResources(),R.mipmap.oval_error);

        linePressed=BitmapFactory.decodeResource(getResources(),R.mipmap.line_pressed);
        lineError=BitmapFactory.decodeResource(getResources(),R.mipmap.line_error);
        r=pointNormal.getWidth()/2;
        points[0][0]=new Point(offsetX+width/4,offsetY+width/4);
        points[0][1]=new Point(offsetX+width/2,offsetY+width/4);
        points[0][2]=new Point(offsetX+width/4*3,offsetY+width/4);

        points[1][0]=new Point(offsetX+width/4,offsetY+width/2);
        points[1][1]=new Point(offsetX+width/2,offsetY+width/2);
        points[1][2]=new Point(offsetX+width/4*3,offsetY+width/2);

        points[2][0]=new Point(offsetX+width/4,offsetY+width/4*3);
        points[2][1]=new Point(offsetX+width/2,offsetY+width/4*3);
        points[2][2]=new Point(offsetX+width/4*3,offsetY+width/4*3);
    }
}
