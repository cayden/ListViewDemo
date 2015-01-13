package com.cayden.listview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;


/**
 * �����ȵĽ��������̰߳�ȫ��View����ֱ�����߳��и��½���
 * @author cuiran
 *
 */
public class RoundProgressBar extends View {
	/**
	 * ���ʶ��������
	 */
	private Paint paint;
	
	/**
	 * Բ������ɫ
	 */
	private int roundColor;
	
	/**
	 * Բ�����ȵ���ɫ
	 */
	private int roundProgressColor;
	
	/**
	 * �м���Ȱٷֱȵ��ַ�������ɫ
	 */
	private int textColor;
	
	/**
	 * �м���Ȱٷֱȵ��ַ���������
	 */
	private float textSize;
	
	/**
	 * Բ���Ŀ��
	 */
	private float roundWidth;
	
	/**
	 * ������
	 */
	private int max;
	
	/**
	 * ��ǰ����
	 */
	private int progress;
	/**
	 * �Ƿ���ʾ�м�Ľ���
	 */
	private boolean textIsDisplayable;
	
	/**
	 * ���ȵķ��ʵ�Ļ��߿���
	 */
	private int style;
	
	public static final int STROKE = 0;
	public static final int FILL = 1;
	/**�Ƿ�ѭ��**/
	public  boolean isSpinning = false;
	
	public RoundProgressBar(Context context) {
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		paint = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.RoundProgressBar);
		
		//��ȡ�Զ������Ժ�Ĭ��ֵ
		roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
		roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
		textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
		textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
		roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 360);
		textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
		style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
		
		mTypedArray.recycle();//����Recycle
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		/**
		 * �������Ĵ�Բ��
		 */
		int centre = getWidth()/2; //��ȡԲ�ĵ�x����
		int radius = (int) (centre - roundWidth/2); //Բ���İ뾶
		paint.setColor(roundColor); //����Բ������ɫ
		paint.setStyle(Paint.Style.STROKE); //���ÿ���
		paint.setStrokeWidth(roundWidth); //����Բ���Ŀ��
		paint.setAntiAlias(true);  //������� 
		canvas.drawCircle(centre, centre, radius, paint); //����Բ��
		
		/**
		 * �����Ȱٷֱ� ����ȥ���ĳɻ�ͼƬ
		 */
//		paint.setStrokeWidth(0); 
//		paint.setColor(textColor);
//		paint.setTextSize(textSize);
//		paint.setTypeface(Typeface.DEFAULT_BOLD); //��������
//		int percent = (int)(((float)progress / (float)max) * 100);  //�м�Ľ��Ȱٷֱȣ���ת����float�ڽ��г������㣬��Ȼ��Ϊ0
//		float textWidth = paint.measureText(percent + "%");   //���������ȣ�������Ҫ��������Ŀ��������Բ���м�
//		
//		if(textIsDisplayable && percent != 0 && style == STROKE){
//			canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize/2, paint); //�������Ȱٷֱ�
//		}

			if(isSpinning){
				/**
				 * ��ͼƬ
				 */
				Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.loading02);
				int width=bitmap.getWidth();
				int height=bitmap.getHeight();
				canvas.drawBitmap(bitmap, centre-width/2, centre-height/2 , paint);
				
				bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.loading06);
				width=bitmap.getWidth();
				height=bitmap.getHeight();
				canvas.drawBitmap(bitmap, centre-width/2, centre-height/2 , paint);
				/**
				 * ��Բ�� ����Բ���Ľ���
				 */
				//���ý�����ʵ�Ļ��ǿ���
				paint.setStrokeWidth(roundWidth); //����Բ���Ŀ��
				paint.setColor(roundProgressColor);  //���ý��ȵ���ɫ
				RectF oval = new RectF(centre - radius, centre - radius, centre
						+ radius, centre + radius);  //���ڶ����Բ������״�ʹ�С�Ľ���
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawArc(oval, progress-90, 320 , false, paint);  //���ݽ��Ȼ�Բ��
			}else{
				/**
				 * ��ͼƬ
				 */
				Bitmap bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.loading02);
				int width=bitmap.getWidth();
				int height=bitmap.getHeight();
				canvas.drawBitmap(bitmap, centre-width/2, centre-height/2 , paint);
				 bitmap=BitmapFactory.decodeResource(getResources(), R.drawable.loading05);
				 width=bitmap.getWidth();
				 height=bitmap.getHeight();
				 canvas.drawBitmap(bitmap, centre-width/2, centre-height/2 , paint);
				/**
				 * ��Բ�� ����Բ���Ľ���
				 */
				//���ý�����ʵ�Ļ��ǿ���
				paint.setStrokeWidth(roundWidth); //����Բ���Ŀ��
				paint.setColor(roundProgressColor);  //���ý��ȵ���ɫ
				RectF oval = new RectF(centre - radius, centre - radius, centre
						+ radius, centre + radius);  //���ڶ����Բ������״�ʹ�С�Ľ���
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawArc(oval, -90, progress , false, paint);  //���ݽ��Ȼ�Բ��
			}
				
			
	
		
	}
	
	
	public synchronized int getMax() {
		return max;
	}

	/**
	 * ���ý��ȵ����ֵ
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if(max < 0){
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	/**
	 * ��ȡ����.��Ҫͬ��
	 * @return
	 */
	public synchronized int getProgress() {
		return progress;
	}
	  /**
     * Reset the count (in increment mode)
     */
    public void resetCount() {
        progress = 0;
        invalidate();
    }
    /**
     * Turn off spin mode
     */
    public void stopSpinning() {
        isSpinning = false;
        progress = 0;
        spinHandler.removeMessages(0);
    }
    /**
     * Puts the view on spin mode
     */
    public void spin() {
        isSpinning = true;
        spinHandler.sendEmptyMessage(0);
    }
    /**
     * Increment the progress by 1 (of 360)
     */
    public void incrementProgress() {
        isSpinning = false;
        if (progress > 360)
            progress = 0;
        spinHandler.sendEmptyMessage(0);
    }
	 private Handler spinHandler = new Handler() {
	        /**
	         * This is the code that will increment the progress variable
	         * and so spin the wheel
	         */
	        @Override
	        public void handleMessage(Message msg) {
	        		invalidate();
	        		if(isSpinning){
	        			  progress += 10;
	  	                if (progress > 360) {
	  	                    progress = 0;
	  	                }
	  	                spinHandler.sendEmptyMessageDelayed(0, 0);
	        		}
	            //super.handleMessage(msg);
	        }
	    };
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
	
	
	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}



}
