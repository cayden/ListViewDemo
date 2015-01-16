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
 * 自定义ListView 实现下拉刷新，加载更多效果 
 * @author cuiran
 * @version 1.0.0
 */
public class CustomerListView extends ListView implements OnScrollListener {
	private static final String TAG="CustomerListView";
	View header;// 顶部布局文件；
	int headerHeight;// 顶部布局文件的高度；
	int firstVisibleItem;// 当前第一个可见的item的位置；
	int scrollState;// listview 当前滚动状态；
	boolean isRemark;// 标记，当前是在listview最顶端摁下的；
	int startY;// 摁下时的Y值；
	int state;// 当前的状态；

	final int NONE = 0;// 正常状态；
	final int PULL = 1;// 提示下拉状态；
	final int RELESE = 2;// 提示释放状态；
	final int REFLASHING = 3;// 刷新状态；
	IRefreshListener iRefreshListener;//刷新数据的接口
	
	
	View footer;// 底部布局；
	int totalItemCount;// 总数量；
	int lastVisibleItem;// 最后一个可见的item；
	boolean isLoading;// 正在加载；
	LinearLayout linearLayout=null;
	ILoadListener iLoadListener;
	
	private RoundProgressBar roundProgressBar=null;	//自定义进度条
	private TextView tip;
	private int progress = 0;                     	//进度值
	boolean running=false;							  //标记线程运行状态
	
	
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
	 * 初始化界面，添加顶部布局文件到 listview
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
	 * 通知父布局，占用的宽，高；
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
	 * 设置header 布局 上边距；
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
				// 加载更多
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
				
				// 加载最新数据；
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
	 * 判断移动过程操作；
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
	 * 进度条递增显示
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
	 * 进度条快速转动
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
	 * 根据当前状态，改变界面显示；
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
			tip.setText("下拉刷新");
			tip.setVisibility(View.VISIBLE);
			break;
		case REFLASHING:
			
			topPadding(50);
			roundProgressBar.setVisibility(View.VISIBLE);
			tip.setText("正在刷新...");
			tip.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * 获取完数据；
	 */
	public void reflashComplete() {
		state = NONE;
		isRemark = false;
		reflashViewByState();
	
	}
	/**
	 * 设置下拉刷新回调方法
	 * 
	 * @param iRefreshListener
	 *
	 */
	public void setIRefreshInterface(IRefreshListener iRefreshListener){
		this.iRefreshListener = iRefreshListener;
	}
	
	/**
	 * 加载完毕
	 */
	public void loadComplete(){
		isLoading = false;
		footer.findViewById(R.id.load_layout).setVisibility(
				View.GONE);
	}
	/**
	 * 设置加载更多数据回调方法
	 * 
	 * @param iLoadListener
	 *
	 */
	public void setILoadInterface(ILoadListener iLoadListener){
		this.iLoadListener = iLoadListener;
	}
	/**
	 * 刷新数据接口
	 * @author cuiran
	 */
	public interface IRefreshListener{
		public void onRefresh();
	}
	
	/**
	 * 加载更多数据的回调接口
	 * @author cuiran
	 */
	public interface ILoadListener{
		public void onLoad();
	}
}
