package com.xieyuan.test;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class TileAnimationView extends View{

	private Bitmap bitmap;
	
	private int tileW;
	private int tileH;
	
	/**
	 * ����ֵ
	 * 0 ��
	 * 1 ��
	 * 2 ��
	 * 3 ��
	 */
	private int direction;
		
	
	/**
	 * �������� ���Ϊ2
	 */
	private int setpIndex;
	
	
	/**��ǰλ��*/
	private int x,y;
	
	/**�ƶ��ٶ�*/
	private int moveSpeed;
	
	/**Ŀ��λ��*/
	private Point mTarget=new Point();
	
	private Random mRandom=new Random();
	
	private boolean isStop=false;
	
	public TileAnimationView(Context context) {
		this(context,null,0);
		// TODO Auto-generated constructor stub
	}

	public TileAnimationView(Context context, AttributeSet attrs) {
		this(context, attrs,0);	
	}
	
	public TileAnimationView(Context context, AttributeSet attrs, int defStyle) {		
		super(context, attrs, defStyle);	
		init();
	}
	
	private void init(){
		bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.ddddd);
		tileW=bitmap.getWidth()/3;
		tileH=bitmap.getHeight()/4;
		
		moveSpeed=dipToPx(getContext(), 5);
		
		ai();
	}
	
	public static int dipToPx(Context context,float dip){
		 return Math.round(TypedValue.applyDimension(
	                TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics()));
	/*	float desity=context.getResources().getDisplayMetrics().density;
		return (int)(dip*desity+0.5f);*/
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){		
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int widthMeasureSpec){
		int result=0;
		int specMode=MeasureSpec.getMode(widthMeasureSpec);  
	    int specSize=MeasureSpec.getSize(widthMeasureSpec);  
	    
	    if(specMode==MeasureSpec.EXACTLY){
	    	//�������Ѿ�Ϊ�����������˳ߴ�,������Ӧ��������Щ�߽�,������������Ҫ���Ŀռ�.  ����EditTextView�е�DrawLeft
	    	result=specSize;  
	    }else{
	    	//MeasureSpec.UNSPECIFIED:  //UNSPECIFIED(δָ��),��Ԫ�ز�����Ԫ��ʩ���κ���������Ԫ�ؿ��Եõ�������Ҫ�Ĵ�С;
	    	 result=tileW+getPaddingLeft()+getPaddingRight();  
	    	 if(specMode==MeasureSpec.AT_MOST){
	    		 //AT_MOST(����)����Ԫ������ﵽָ����С��ֵ��
	    		 result = Math.min(result, specSize);
	    	 }
	    	 
	    }
	  
	    return result; 
	}
	
	private int measureHeight(int heightMeasureSpec){
		int result=0;
		int specMode=MeasureSpec.getMode(heightMeasureSpec);  
	    int specSize=MeasureSpec.getSize(heightMeasureSpec);  
 
	    if(specMode==MeasureSpec.EXACTLY){
	    	//�������Ѿ�Ϊ�����������˳ߴ�,������Ӧ��������Щ�߽�,������������Ҫ���Ŀռ�.  ����EditTextView�е�DrawLeft
	    	result=specSize;  
	    }else{
	    	//MeasureSpec.UNSPECIFIED:  //UNSPECIFIED(δָ��),��Ԫ�ز�����Ԫ��ʩ���κ���������Ԫ�ؿ��Եõ�������Ҫ�Ĵ�С; 
	    	result=tileH+getPaddingTop()+getPaddingBottom();    
	    	 if(specMode==MeasureSpec.AT_MOST){
	    		 //AT_MOST(����)����Ԫ������ﵽָ����С��ֵ��
	    		 result = Math.min(result, specSize);
	    	 }
	    	 
	    }
	  
	    return result;  
	}
	
	private void ai(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (!isStop) {
						if(x==mTarget.x&&y==mTarget.y){
							Thread.sleep(mRandom.nextInt(2000)+500);						
							mTarget.x=mRandom.nextInt(getWidth()-tileW);
							mTarget.y=mRandom.nextInt(getHeight()-tileH);
							
							mTarget.x=mTarget.x%moveSpeed==0? mTarget.x:mTarget.x-mTarget.x%moveSpeed;
							mTarget.y=mTarget.y%moveSpeed==0? mTarget.y:mTarget.y-mTarget.y%moveSpeed;
							
							changeDirectiionToDesc(mTarget.x, mTarget.y);
						}else {
							int deltX=mTarget.x-x;
							int deltY=mTarget.y-y;
							double angle=Math.atan2(deltY, deltX);
							
							int xSpeed=(int) (moveSpeed * Math.cos(angle));
	                        int ySpeed=(int) (moveSpeed * Math.sin(angle));

	                        if(xSpeed>0){
	                        	//����
	                        	x=x+xSpeed>mTarget.x? mTarget.x:x+xSpeed;
	                        }else{
	                        	//����
	                        	x=x+xSpeed<mTarget.x? mTarget.x:x+xSpeed;
	                        }
	                        
	                        if(ySpeed>0){
	                        	y=y+ySpeed>mTarget.y? mTarget.y:y+ySpeed;
	                        }else{
	                        	y=y+ySpeed<mTarget.y? mTarget.y:y+ySpeed;
	                        }
	                        
	                        //�ı䷽��
	                        //changeDirectiionToMove(xSpeed,ySpeed);
	                        
						}
						
						Thread.sleep(100);	
						postInvalidate();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}
	
	/**
	 * ����Ŀ��λ�ú͵�ǰλ�øı�HORE�ķ���
	 * @param descX
	 * @param descY
	 */
	private void changeDirectiionToDesc(int descX,int descY){
		int deltX=mTarget.x-x;
		int deltY=mTarget.y-y;
		if(Math.abs(deltX)>Math.abs(deltY)){
			if(descX>x){
				direction=2;
			}else{
				direction=1;
			}
		}else if(descY>y){			
			direction=0;
		}else if(descY<y){
			direction=3;
		}
	}
	
	private void changeDirectiionToMove(int moveX,int moveY){
		if(moveY>0){			
			direction=0;
		}else if(moveY<0){
			direction=3;
		}else{
			// y =0,move x
			if(moveX>0){
				direction=2;
			}else{
				direction=1;
			}
		}
	}

	
	@Override
	public void onDraw(Canvas mCanvas){
		setpIndex=(setpIndex+1)%3;
		int srcX=setpIndex*tileW;
		int srcY=direction*tileH;
		
		mCanvas.drawBitmap(bitmap, new Rect(srcX, srcY, srcX+tileW, srcY+tileH), new Rect(x,y,x+tileW,y+tileH), null);
		
	}
	
	@Override
	public void onDetachedFromWindow(){
		super.onDetachedFromWindow();		
		isStop=true;
	}
}
