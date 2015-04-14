package com.example.testvolley;

import java.util.ArrayList;

import com.android.volley.RequestQueue;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Music;
import de.greenrobot.daoexample.MusicDao;
import de.greenrobot.daoexample.MusicMessage;
import de.greenrobot.daoexample.MusicMessageDao;
import de.greenrobot.daoexample.MusicMessageDao.Properties;
import de.greenrobot.daoexample.User;
import de.greenrobot.daoexample.UserDao;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class MusicMessageActivity extends Activity {

	private ListView musicListView;
	private MyApplication application;
	private RequestQueue mQueue;
	private DaoSession daoSession;
	private QueryBuilder qb;
	private MusicMessageDao musicMessageDao;
	private MusicDao musicDao;
	private UserDao userDao;
	private MusicMessageAdapter adapter;
	
	private ArrayList<MusicMessage> mmList = new ArrayList<MusicMessage>();
	private ArrayList<Music> musicList = new ArrayList<Music>();
	private ArrayList<User> userList = new ArrayList<User>();
	
	private final String TAG = "MusicMessageActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_message);
		Log.v(TAG,TAG+"start");
		application = (MyApplication)this.getApplicationContext();
		application.setMessage(false);
		mQueue = application.getRequestQueue();
		daoSession = application.getDaoSession(getApplicationContext());
		musicMessageDao = daoSession.getMusicMessageDao();
		userDao = daoSession.getUserDao();
		musicDao = daoSession.getMusicDao();
		

		
		initView();
		initAdapter();
	}
	public void initView(){
		
		musicListView = (ListView)findViewById(R.id.musicListView);
	}
	public void initAdapter(){
		
		long uid = application.getLoginUser().getUid();
		qb = musicMessageDao.queryBuilder();
		qb.where(Properties.User_id.eq(uid));
		long count = qb.buildCount().count();
		Log.v(TAG,"count:"+count);
		if(count>0){
			mmList = (ArrayList<MusicMessage>) qb.list();
			for(int i=0;i<mmList.size();i++){

				long sender_id = mmList.get(i).getSender_id();
				qb = userDao.queryBuilder();
				qb.where(de.greenrobot.daoexample.UserDao.Properties.Uid.eq(sender_id));
				User user = (User) qb.unique();
				userList.add(user);
			
				long music_id = mmList.get(i).getMusic_id();
				qb = musicDao.queryBuilder();
				qb.where(de.greenrobot.daoexample.MusicDao.Properties.Uid.eq(music_id));
				Music music = (Music) qb.unique();
				musicList.add(music);
				Log.v(TAG,"userList:"+music.getName());
			}
		}
		adapter = new MusicMessageAdapter(this,mmList,userList,musicList);
		musicListView.setAdapter(adapter);
	}
	
	public void ReturnToMain(View view){  
		this.finish();
	}
}

