package com.example.playlistactivity;

import java.util.ArrayList;

import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.example.testvolley.MyFriendsActivity;
import com.example.testvolley.R;
import com.example.testvolley.MyApplication;

public class MainActivity extends TabActivity {

	public static Context context;
	private LocalActivityManager manager;
	private TabHost tabHost;
	private ViewPager viewPager;
	public static TextView footer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.musiclist_main);
		context = MainActivity.this;
		footer = (TextView) findViewById(R.id.text_footet_playingMusic);

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		this.loadTabHost();

		this.loadViewPager();
		//选择要进入哪个链表
		Intent intent = getIntent();
		int listNum = (int)intent.getExtras().getInt("listNum");
		//Log.v("listNum","ok "+listNum);

		
		tabHost.setCurrentTab(listNum);

		footer.setOnClickListener(new OnClickListener() {

			
			public void onClick(View v) {
		//		MainActivity.context.startActivity(new Intent(MainActivity.this, PlayMusicAcitivity.class));
			}
		});

	}


	// 下面的播放小列表显示歌名
/*	@Override
  	protected void onResume() {
		if (MyApplication.playStatus != 0) {
			footer.setText(PlayService.name);
		}
		super.onResume();
	}*/

	public View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	public void loadTabHost() {
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("myFavoriteList").setIndicator("我的最爱", getResources().getDrawable(R.drawable.xin_big)).setContent(new Intent(context, MyFavoriteListActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("friendsMusicList").setIndicator("好友分享", getResources().getDrawable(R.drawable.play_tab)).setContent(new Intent(context, FriendsMusicListActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("localMusicList").setIndicator("本地音乐", getResources().getDrawable(R.drawable.all_tab)).setContent(new Intent(context, LocalMusicListActivity.class)));
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {

			
			public void onTabChanged(String tabId) {
				if (tabId.equals("myFavoriteList")) {
					viewPager.setCurrentItem(0);
				}
				if (tabId.equals("friendsMusicList")) {
					viewPager.setCurrentItem(1);
				}

				if (tabId.equals("localMusicList")) {
					viewPager.setCurrentItem(2);
				}
			}
		});
	}

	public void loadViewPager() {

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		final ArrayList<View> list = new ArrayList<View>();
		list.add(getView("myFavoriteList", new Intent(context, MyFavoriteListActivity.class)));	
		list.add(getView("friendsMusicList", new Intent(context, FriendsMusicListActivity.class)));
		list.add(getView("localMusicList", new Intent(context, LocalMusicListActivity.class)));


		viewPager.setAdapter(new PagerAdapter() {
			
			@Override
			public void destroyItem(View arg0, int arg1, Object arg2) {
				viewPager.removeView(list.get(arg1));
			}
			
			@Override
			public Object instantiateItem(View arg0, int arg1) {
				((ViewPager) arg0).addView(list.get(arg1));
				return list.get(arg1);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return list.size();
			}

			@Override
			public void finishUpdate(View arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void restoreState(Parcelable arg0, ClassLoader arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Parcelable saveState() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void startUpdate(View arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			
			public void onPageSelected(int arg0) {
				tabHost.setCurrentTab(arg0);
			}

			
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
}
