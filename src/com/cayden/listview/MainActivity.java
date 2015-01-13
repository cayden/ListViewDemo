package com.cayden.listview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.cayden.listview.ReFlashListView.IReflashListener;

public class MainActivity extends Activity implements IReflashListener{
	ArrayList<ApkEntity> apk_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setData();
		showList(apk_list);
	}

	MyAdapter adapter;
	ReFlashListView listview;
	private void showList(ArrayList<ApkEntity> apk_list) {
		if (adapter == null) {
			listview = (ReFlashListView) findViewById(R.id.listview);
			listview.setInterface(this);
			adapter = new MyAdapter(this, apk_list);
			listview.setAdapter(adapter);
		} else {
			adapter.onDateChange(apk_list);
		}
	}

	private void setData() {
		apk_list = new ArrayList<ApkEntity>();
		for (int i = 0; i < 10; i++) {
			ApkEntity entity = new ApkEntity();
			entity.setName("默认数据");
			entity.setDes("这是一个神奇的应用");
			entity.setInfo("50w用户");
			apk_list.add(entity);
		}
	}

	private void setReflashData() {
		for (int i = 0; i < 2; i++) {
			ApkEntity entity = new ApkEntity();
			entity.setName("刷新数据");
			entity.setDes("这是一个神奇的应用");
			entity.setInfo("50w用户");
			apk_list.add(0,entity);
		}
	}
	@Override
	public void onReflash() {
		// TODO Auto-generated method stub\
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//获取最新数据
				setReflashData();
				//通知界面显示
				showList(apk_list);
				//通知listview 刷新数据完毕；
				listview.reflashComplete();
			}
		}, 2000);
		
	}
}
