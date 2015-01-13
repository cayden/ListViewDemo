package com.cayden.listview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.cayden.listview.CustomerListView.ILoadListener;
import com.cayden.listview.CustomerListView.IRefreshListener;

public class MainActivity extends Activity implements IRefreshListener,ILoadListener{
	ArrayList<ApkEntity> apk_list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setData();
		showList(apk_list);
	}

	MyAdapter adapter;
	CustomerListView listview;
	private void showList(ArrayList<ApkEntity> apk_list) {
		if (adapter == null) {
			listview = (CustomerListView) findViewById(R.id.listview);
			listview.setIRefreshInterface(this);
			listview.setILoadInterface(this);
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
			entity.setName("Ĭ������");
			entity.setDes("����һ�������Ӧ��");
			entity.setInfo("50w�û�");
			apk_list.add(entity);
		}
	}

	private void setReflashData() {
		for (int i = 0; i < 2; i++) {
			ApkEntity entity = new ApkEntity();
			entity.setName("ˢ������");
			entity.setDes("����һ�������Ӧ��");
			entity.setInfo("50w�û�");
			apk_list.add(0,entity);
		}
	}
	private void getLoadData() {
		for (int i = 0; i < 2; i++) {
			ApkEntity entity = new ApkEntity();
			entity.setName("�������");
			entity.setInfo("50w�û�");
			entity.setDes("����һ�������Ӧ�ã�");
			apk_list.add(entity);
		}
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub\
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//��ȡ��������
				setReflashData();
				//֪ͨ������ʾ
				showList(apk_list);
				//֪ͨlistview ˢ��������ϣ�
				listview.reflashComplete();
			}
		}, 2000);
		
	}

	@Override
	public void onLoad() {
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//��ȡ��������
				getLoadData();
				//����listview��ʾ��
				showList(apk_list);
				//֪ͨlistview�������
				listview.loadComplete();
			}
		}, 2000);
	}
}
