package com.example.myview;


import com.example.testvolley.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * ��iphone�����ȵĽ��������̰߳�ȫ��View����ֱ�����߳��и��½���
 *
 */
public class RoundProgressBar extends View {

	private Paint paint;
	private int roundProgressColor;
	private float roundWidth;
	private int max;
	private int progress;
	private int style;
	public static final int STROKE = 0;
	public static final int FILL = 1;
	Resources res = getResources();
	
	Drawable btnDrawable_play = res.getDrawable(R.drawable.mini_play);
	Drawable btnDrawable_stop = res.getDrawable(R.drawable.mini_stop);
	
	public RoundProgressBar(Context context) {
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		paint = new Paint();

		
		this.setBackgroundDrawable(btnDrawable_play);
			
		//��ȡ�Զ������Ժ�Ĭ��ֵ
		roundProgressColor = R.color.green;
		roundWidth = 6;
		max = 100;
		style = 0;
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);		
		int centre = getWidth()/2; //��ȡԲ�ĵ�x����
		int radius = (int) (centre - roundWidth/2); //Բ���İ뾶
	   	
		//���ý�����ʵ�Ļ��ǿ���
		paint.setStrokeWidth(roundWidth); //����Բ���Ŀ��
		paint.setColor(roundProgressColor);  //���ý��ȵ���ɫ
		RectF oval = new RectF(centre - radius, centre - radius, centre+ radius, centre + radius);  //���ڶ����Բ������״�ʹ�С�Ľ���
		
		switch (style) {
		case STROKE:{
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawArc(oval, -90, 360 * progress / max, false, paint);  //���ݽ��Ȼ�Բ��
			break;
		}
		case FILL:{
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			if(progress !=0)
				canvas.drawArc(oval, 0, 360 * progress / max, true, paint);  //���ݽ��Ȼ�Բ��
			break;
		}
		}
		
	}
	
	public void setbackground(boolean playFlag){
		
		if(playFlag)
			this.setBackgroundDrawable(btnDrawable_stop);
		else 
			this.setBackgroundDrawable(btnDrawable_play);
		
	}
	
	public synchronized int getMax() {
		return max;
	}

	
	public synchronized void setMax(int max) {
		if(max < 0){
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	
	public synchronized int getProgress() {
		return progress;
	}

	/**
	 * ���ý��ȣ���Ϊ�̰߳�ȫ�ؼ������ڿ��Ƕ��ߵ����⣬��Ҫͬ��
	 * ˢ�½������postInvalidate()���ڷ�UI�߳�ˢ��
	 * @param progress
	 */
	public synchronized void setProgress(int progress) {
		if(progress < 0){
			throw new IllegalArgumentException("progress not less than 0");
		}
		if(progress > max){
			progress = max;
		}
		if(progress <= max){
			this.progress = progress;
			postInvalidate();
		}
		
	}
	

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

   

}
