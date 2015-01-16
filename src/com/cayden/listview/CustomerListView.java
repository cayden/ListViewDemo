package com.cayden.listview;





import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * �Զ���ListView ʵ������ˢ�£����ظ���Ч�� 
 * @author cuiran
 * @version 1.0.0
 */
public class CustomerListView extends ListView implements OnScrollListener {
	private static final String TAG="CustomerListView";
	View header;// ���������ļ���
	int headerHeight;// ���������ļ��ĸ߶ȣ�
	int firstVisibleItem;// ��ǰ��һ���ɼ���item��λ�ã�
	int scrollState;// listview ��ǰ����״̬��
	boolean isRemark;// ��ǣ���ǰ����listview������µģ�
	int startY;// ����ʱ��Yֵ��
	int state;// ��ǰ��״̬��

	final int NONE = 0;// ����״̬��
	final int PULL = 1;// ��ʾ����״̬��
	final int RELESE = 2;// ��ʾ�ͷ�״̬��
	final int REFLASHING = 3;// ˢ��״̬��
	IRefreshListener iRefreshListener;//ˢ�����ݵĽӿ�
	
	
	View footer;// �ײ����֣�
	int totalItemCount;// ��������
	int lastVisibleItem;// ���һ���ɼ���item��
	boolean isLoading;// ���ڼ��أ�
	LinearLayout linearLayout=null;
	ILoadListener iLoadListener;
	
	private RoundProgressBar roundProgressBar=null;	//�Զ��������
	private TextView tip;
	private int progress = 0;                     	//����ֵ
	boolean running=false;							  //����߳�����״̬
	
	
	public CustomerListView(Context context) {
		super(context);
		initView(context);
	}

	public CustomerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public CustomerListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * ��ʼ�����棬��Ӷ��������ļ��� listview
	 * 
	 * @param context
	 */
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		header = inflater.inflate(R.layout.header_layout, null);
		
		tip = (TextView) header.findViewById(R.id.tip);
		roundProgressBar = (RoundProgressBar) header.findViewById(R.id.roundProgressBar);
		
		measureView(header);
		headerHeight = header.getMeasuredHeight();
		Log.i("tag", "headerHeight = " + headerHeight);
		topPadding(-headerHeight);
		this.addHeaderView(header);
		
		inflater = LayoutInflater.from(context);
		footer = inflater.inflate(R.layout.footer_layout, null);
		linearLayout=(LinearLayout)footer.findViewById(R.id.load_layout);
		linearLayout.setVisibility(View.GONE);
		this.addFooterView(footer);
		
		this.setOnScrollListener(this);
	}

	/**
	 * ֪ͨ�����֣�ռ�õĿ��ߣ�
	 * 
	 * @param view
	 */
	private void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int height;
		int tempHeight = p.height;
		if (tempHeight > 0) {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	/**
	 * ����header ���� �ϱ߾ࣻ
	 * 
	 * @param topPadding
	 */
	private void topPadding(int topPadding) {
		header.setPadding(header.getPaddingLeft(), topPadding,
				header.getPaddingRight(), header.getPaddingBottom());
		header.invalidate();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.firstVisibleItem = firstVisibleItem;
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.scrollState = scrollState;
		if (totalItemCount == lastVisibleItem
				&& scrollState == SCROLL_STATE_IDLE) {
			if (!isLoading) {
				isLoading = true;
				footer.findViewById(R.id.load_layout).setVisibility(
						View.VISIBLE);
				// ���ظ���
				iLoadListener.onLoad();
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (firstVisibleItem == 0) {
				isRemark = true;
				startY = (int) ev.getY();
				increment();
			}
			break;

		case MotionEvent.ACTION_MOVE:
			onMove(ev);
			break;
		case MotionEvent.ACTION_UP:
			spin();
			Log.i(TAG, "MotionEvent.ACTION_UP ");
			if (state == RELESE) {
				state = REFLASHING;
				progress=360;
				
				// �����������ݣ�
				reflashViewByState();
				iRefreshListener.onRefresh();
			} else if (state == PULL) {
				state = NONE;
				isRemark = false;
				reflashViewByState();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	int moveY=0;
	/**
	 * �ж��ƶ����̲�����
	 * 
	 * @param ev
	 */
	private void onMove(MotionEvent ev) {
		if (!isRemark) {
			return;
		}
		
		int tempY = (int) ev.getY();
		if(tempY<startY)return;
		int space = tempY - startY;
		int topPadding = space - headerHeight;
		if(tempY>=moveY){
			progress=progress+10;
		}else{
			progress=progress-10;
		}
		
		switch (state) {
		case NONE:
			if (space > 0) {
				state = PULL;
				reflashViewByState();
			}
			break;
		case PULL:
			topPadding(topPadding);//+ 30
		
			if (space > headerHeight 
					&& scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				state = RELESE;
				reflashViewByState();
			}
			break;
		case RELESE:
			topPadding(topPadding);
			if (space < headerHeight + 30) {
				state = PULL;
				reflashViewByState();
			} else if (space <= 0) {
				state = NONE;
				isRemark = false;
				reflashViewByState();
			}
			break;
		}
		moveY=tempY;
	}
	Thread s=null;
	/**
	 * ������������ʾ
	 *
	 */
	private void increment(){
		if(!running) {
			running = true;
			progress = 0;
			roundProgressBar.resetCount();
			s = new Thread(r);
			s.start();
		}
	}
	/**
	 * ����������ת��
	 *
	 */
	private void spin(){
		Log.i(TAG, "spin");
//		if(!running) {
			if(roundProgressBar.isSpinning){
				roundProgressBar.stopSpinning();
			}
			roundProgressBar.resetCount();
			roundProgressBar.spin();
//		}
	}
	
	  final Runnable r = new Runnable() {
			public void run() {
			
				while(progress<361) {
					if(progress<=0){
						progress=0;
					}
					roundProgressBar.setProgress(progress);
					roundProgressBar.incrementProgress();
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				running = false;
			}
      };
      
	/**
	 * ���ݵ�ǰ״̬���ı������ʾ��
	 */
	private void reflashViewByState() {
		 
		switch (state) {
		case NONE:
			topPadding(-headerHeight);
			roundProgressBar.setVisibility(View.GONE);
			tip.setVisibility(View.GONE);
			break;
		case PULL:
		case RELESE:
			roundProgressBar.setVisibility(View.VISIBLE);
			tip.setText("����ˢ��");
			tip.setVisibility(View.VISIBLE);
			break;
		case REFLASHING:
			
			topPadding(50);
			roundProgressBar.setVisibility(View.VISIBLE);
			tip.setText("����ˢ��...");
			tip.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * ��ȡ�����ݣ�
	 */
	public void reflashComplete() {
		state = NONE;
		isRemark = false;
		reflashViewByState();
	
	}
	/**
	 * ��������ˢ�»ص�����
	 * 
	 * @param iRefreshListener
	 *
	 */
	public void setIRefreshInterface(IRefreshListener iRefreshListener){
		this.iRefreshListener = iRefreshListener;
	}
	
	/**
	 * �������
	 */
	public void loadComplete(){
		isLoading = false;
		footer.findViewById(R.id.load_layout).setVisibility(
				View.GONE);
	}
	/**
	 * ���ü��ظ������ݻص�����
	 * 
	 * @param iLoadListener
	 *
	 */
	public void setILoadInterface(ILoadListener iLoadListener){
		this.iLoadListener = iLoadListener;
	}
	/**
	 * ˢ�����ݽӿ�
	 * @author cuiran
	 */
	public interface IRefreshListener{
		public void onRefresh();
	}
	
	/**
	 * ���ظ������ݵĻص��ӿ�
	 * @author cuiran
	 */
	public interface ILoadListener{
		public void onLoad();
	}
}
