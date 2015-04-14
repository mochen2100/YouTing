package com.example.testvolley;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.request.*;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Music;
import de.greenrobot.daoexample.MusicDao;
import de.greenrobot.daoexample.User;
import de.greenrobot.daoexample.UserDao;
import de.greenrobot.daoexample.UserDao.Properties;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class WelcomeActivity extends Activity implements WelcomeActivityCallBack {
	private MyApplication application;
	private RequestQueue mQueue;
	private static WelcomeActivityCallBack welcomeActivityCallBack;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private DaoSession daoSession;
	private DaoMaster daoMaster;
	private UserDao userDao;
	private MusicDao musicDao;
	private QueryBuilder qb_music,qb_user;
	private JSONObject jObject;
	private String name,password,uid,status,alias;
    private ArrayList<Music> myMusicList = new ArrayList<Music>();
    private ArrayList<Music> friendMusicList = new ArrayList<Music>();
    private ArrayList<Music> localMusicList = new ArrayList<Music>();
    private ArrayList<Music> playingList = new ArrayList<Music>();
    private ArrayList<User> friendList = new ArrayList<User>();
    private Map<String,String> headers;
	private JsonObjectRequest jRequest;
	private JsonArrayRequest friendlistRequest,mymusicRequest,friendmusicRequest;
	private final static String login_url = "http://121.42.164.7/index.php/Home/Index/login";
	private final static String get_friend_url = "http://121.42.164.7/index.php/Home/Index/get_friend_list";
	private final static String get_mymusic_url = "http://121.42.164.7/index.php/Home/Index/get_my_music";
	private final static String get_friendmusic_url = "http://121.42.164.7/index.php/Home/Index/get_friend_music";
	private final static String get_playmusic_url = "http://121.42.164.7/index.php/Home/Index/get_play_music";
	private final String qiniu_url = "http://7xi2lw.com1.z0.glb.clouddn.com/";
	private final static String TAG = "welcome";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		welcomeActivityCallBack = this;
		application = (MyApplication)this.getApplicationContext();
		Log.v(TAG,"onCreate");
		mQueue = application.getRequestQueue();
		daoSession = application.getDaoSession(getApplicationContext());
		musicDao = daoSession.getMusicDao();
		userDao = daoSession.getUserDao();
		qb_music = musicDao.queryBuilder();
		// ��ȡ��������
		localMusic();
		preferences = getSharedPreferences("youting",MODE_PRIVATE);		
		editor = preferences.edit();
		// ����ϴεĲ����б�
		String playingList_string = preferences.getString("playingList", null);
		Log.v(TAG,"playingList"+playingList_string);
		if(playingList_string != null){
			try {
				JSONArray jArray = new JSONArray(playingList_string);
				for(int i=0;i<jArray.length();i++){
					JSONObject jObject = jArray.getJSONObject(i);
					if(jObject.getLong("uid") == 0){
						//��������
						String id = (String)jObject.get("local_id");
						long local_id = Long.parseLong(id);
						Uri uri = Uri.parse("content://media/external/audio/media/" + local_id);
				        ContentResolver mContentResolver = this.getContentResolver();
				        Cursor cursor = mContentResolver.query(uri, new String[]{MediaStore.Audio.Media._ID,
								MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,
								MediaStore.Audio.Media.DATA}, null, null, null);
				        if(cursor.moveToFirst()){
				        	String title = cursor.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
				        	String creator = cursor.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
				        	String filePath = cursor.getString(cursor
									.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
				        	int start = Environment.getExternalStorageDirectory().getAbsolutePath().length();
				        	int end = filePath.lastIndexOf("/");
				        	String name = filePath.substring(end);
				        	filePath = filePath.substring(start,end);
						
				        	Log.v(TAG,"filePath:"+filePath);
							String fake_url = qiniu_url+name;
							
							Music music = new Music((long) 0,title,creator,filePath,fake_url,null,null,id,true);
							playingList.add(music);
				            
				        }
				            cursor.close();
						
					}else{
						//��������
						
						long uid = jObject.getLong("uid");
						qb_music = musicDao.queryBuilder();
						qb_music.where(Properties.Uid.eq(uid));
						if(qb_music.buildCount().count()>0){
							Music music = (Music) qb_music.unique();
							playingList.add(music);
						}
					} 
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			application.setPlayingList(playingList);
		}
		
		//login
		name = preferences.getString("USER_NAME", "USER_NAME");
		password = preferences.getString("PASSWORD", "PASSWORD");
		jObject = new JSONObject();
		try {
			jObject.put("name", name);
			jObject.put("password", password);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, login_url, jObject,null,new Response.Listener<JSONObject>() {  
	        @Override  
	        public void onResponse(JSONObject j) {  
	        	Log.v("json",j.toString());
	        	try {
					status = j.getString("status");										
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	if (status.equals("success")){
	        		// ��½�ɹ���
	        		// ע��jpush��alias
	        		editor.putBoolean("login", true);
	        		editor.putString("user", j.toString());
	        		editor.commit();
	        		try {
						uid = j.getString("uid");
						String name = j.getString("name");
						String mood = j.getString("mood");
						String sex = j.getString("sex");
						String avatar = j.getString("avatar");
						User user = new User(Long.parseLong(uid),name,sex,mood,avatar);
						editor.putString("USER_NAME", name);
						editor.putString("PASSWORD", j.getString("password"));
						editor.commit();
						application.setLoginUser(user);
						application.setIsLogin(true);
						Log.v(TAG+"login",application.getLoginUser().getAvatar());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	        		
	        		setAlias(uid);
	        		setFriendList(uid);
	        		setMyMusic(uid);
	        		//setFriendMusic(uid);
	        		
	        	new Handler().postDelayed(new Runnable(){    
	                    public void run() {    
	        	            Intent intent=new Intent();
	        	            intent.setClass(WelcomeActivity.this, MainActivity.class);
	        	            startActivity(intent); 
	                    }    
	                 }, 2000);
	        	}else{
	        		Log.v(TAG,"log fail");
	        		editor.putBoolean("login", false);
	        		editor.commit();
	        		Toast.makeText(getApplicationContext(), "��½ʧ�ܣ������µ�½", Toast.LENGTH_LONG).show();
	        		Intent intent = new Intent();
	        		intent.setClass(WelcomeActivity.this,MyLoginActivity.class);
	        		startActivity(intent);
	        	}
	        }
	    },  
	    new Response.ErrorListener() {  
	        @Override  
	        public void onErrorResponse(VolleyError volleyError) {  
	        	String sb = volleyError.toString(); 
	        	Log.v(TAG+"login",sb);
	        	Toast.makeText(getApplicationContext(), sb, Toast.LENGTH_SHORT).show();
	        	
        		setFriendList(null);
	        	setMyMusic(null);
        		//setFriendMusic(uid);
	        	//��ȡpreferences�����user��Ϣ��application��
	        	String s = preferences.getString("user", null);
	        	if(s != null){
	        		try {
						JSONObject j = new JSONObject(s);
						String uid = j.getString("uid");
						String name = j.getString("name");
						String mood = j.getString("mood");
						String sex = j.getString("sex");
						String avatar = j.getString("avatar");
						User user = new User(Long.parseLong(uid),name,sex,mood,avatar);
						application.setLoginUser(user);
						application.setIsLogin(true);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        	new Handler().postDelayed(new Runnable(){    
                    public void run() {    
        	            Intent intent=new Intent();
        	            intent.setClass(WelcomeActivity.this, MainActivity.class);
        	            startActivity(intent); 
                    }    
                 }, 2000);
	        }  
		});
		
		if(!preferences.getBoolean("login", false)){
			new Handler().postDelayed(new Runnable(){    
                public void run() {    
    	            Intent intent=new Intent();
    	            intent.setClass(WelcomeActivity.this, MainActivity.class);
    	            startActivity(intent); 
                }    
             }, 2000);
		}else{
			mQueue.add(jRequest);
		}
		
	}
	public void setAlias(String uid){
		JPushInterface.setAliasAndTags(getApplicationContext(), uid, null, new TagAliasCallback(){
			@Override
			public void gotResult(int code, String arg1, Set<String> arg2) {
				// TODO Auto-generated method stub
				String logs ;
	            switch (code) {
	            case 0:
	                logs = "Set tag and alias success";
	                Log.i(TAG, logs);
	                break;	                
	            case 6002:
	                logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
	                Log.i(TAG, logs);
	                break;	            
	            default:
	                logs = "Failed with errorCode = " + code;
	                Log.e(TAG, logs);
	            }	            
	            ExampleUtil.showToast(logs, getApplicationContext());
			}			
		});
	}
	public void setFriendList(String uid){
		String url = get_friend_url+"?uid="+uid;
		Log.v(TAG,url);
		friendlistRequest = new JsonArrayRequest(url,null,new Response.Listener<JSONArray>() {  
	        @Override  
			public void onResponse(JSONArray response) {
				// TODO Auto-generated method stub
	        	HashSet friendset=new HashSet();
				for(int i=0;i<response.length();i++){
					String s = null;
					try {
						s = (response.getJSONObject(i)).getString("uid");
						long uid = Long.parseLong(s);
						String  name = (response.getJSONObject(i)).getString("name");
						String sex = (response.getJSONObject(i)).getString("sex");
						String mood = (response.getJSONObject(i)).getString("mood");
						String avatar = (response.getJSONObject(i)).getString("avatar");

						// ȡ�����ݱ������ֻ����ݿ���
						userDao = daoSession.getUserDao();
						//userDao.deleteAll();
						User user = new User(uid,name,sex,mood,avatar);
						qb_user = userDao.queryBuilder();
						qb_user.where(Properties.Uid.eq(uid));
						long count = qb_user.buildCount().count();
						Log.v(TAG,"uid:"+uid);
						if (count > 0){
							User user1 = new User(uid,name,sex,mood,avatar);
							userDao.update(user1);
							Log.v(TAG,"update");
						}else{
							
							userDao.insert(user);
						}
						friendList.add(user);
						Log.v(TAG+"dao","inset new"+user.getUid());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					friendset.add(s);
				}
				
				editor.putStringSet("FRIEND_SET", friendset);
				editor.commit();
				application.setFriendList(friendList);
				Log.v(TAG,"friendlist");
//				HashSet set= preferences.getStringSet("FRIEND_SET", null);
//				Log.v("TAG",set.toString());
			}

	    },  
	    new Response.ErrorListener() {  
	        @Override  
	        public void onErrorResponse(VolleyError volleyError) {  
	        	String sb = volleyError.toString(); 
	        	Log.v(TAG+"frienderr",sb);
	        	HashSet set= (HashSet) preferences.getStringSet("FRIEND_SET", null);
	        	if (set != null){
	        		Iterator iterator = set.iterator();
	        		while(iterator.hasNext()){
	        			long uid = Long.parseLong((String)iterator.next());
	        			qb_user = userDao.queryBuilder();
	        			qb_user.where(Properties.Uid.eq(uid));
						User user =(User) qb_user.uniqueOrThrow();
						friendList.add(user);
	        		}
	        		application.setFriendList(friendList);
	        	}
	        	
	        }  
	    });
		mQueue.add(friendlistRequest);
	}
	public void setMyMusic(String uid){
		String url = get_mymusic_url+"?uid="+uid;
		Log.v(TAG+"mymusic",url);
		mymusicRequest = new JsonArrayRequest(url, null,new Response.Listener<JSONArray>() {  
	        @Override  
			public void onResponse(JSONArray response) {
				// TODO Auto-generated method stub
	        	if(response.length() == 0){
	        		
	        	}
				for(int i=0;i<response.length();i++){
					String s = null;
					try {
						s = ( response.getJSONObject(i)).getString("uid");
						long uid = Long.parseLong(s);
						String  name = (response.getJSONObject(i)).getString("name");
						String artist = (response.getJSONObject(i)).getString("artist");
						String url = (response.getJSONObject(i)).getString("url");
						String lrc_url = (response.getJSONObject(i)).getString("lrc_url");
						String pic_url = (response.getJSONObject(i)).getString("pic_url");
						// ȡ�����ݱ������ֻ����ݿ���
						musicDao = daoSession.getMusicDao();
						//userDao.deleteAll();
						Music music = new Music(uid,name,artist,null,url,lrc_url,null,pic_url,false);
						qb_music = musicDao.queryBuilder();
						qb_music.where(Properties.Uid.eq(uid));
						long count = qb_music.buildCount().count();
						Log.v(TAG+"mymusic","uid:"+uid);
						if (count > 0){
							Music music_tmp = (Music) qb_music.unique();
							String lrc_cache_url = music_tmp.getLrc_cache_url();
							music = new Music(uid,name,artist,null,url,lrc_url,lrc_cache_url,pic_url,false);
							musicDao.update(music);
							Log.v(TAG,"update");
						}else{
							
							musicDao.insert(music);
						}
						myMusicList.add(music);
						Log.v(TAG+"dao","inset new"+music.getUid());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					
				}
				editor.putString("MY_MUSIC", response.toString());
				editor.commit();
				Log.v(TAG+"mymusic","mymusic");
				application.setMyMusicList(myMusicList);
				if(MainActivity.getMainActivityCallBack()!=null){
					MainActivity.getMainActivityCallBack().initCallBack();
				}
				
//				Set set= preferences.getStringSet("MY_MUSIC_SET", null);
//				Log.v(TAG+"mymusic",set.toString());
			}
	    },  
	    new Response.ErrorListener() {  
	        @Override  
	        public void onErrorResponse(VolleyError volleyError) {  
	        	String sb = volleyError.toString(); 
	        	Log.v(TAG+"mymusic",sb);
	        	String tmp = preferences.getString("MY_MUSIC", null);
	        	if (tmp != null){
		        	JSONArray response = new JSONArray();
		        	try {
						response = new JSONArray(tmp);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	for(int i=0;i<response.length();i++){
						String s = null;
						try {
							s = ( response.getJSONObject(i)).getString("uid");
							long uid = Long.parseLong(s);
							String  name = (response.getJSONObject(i)).getString("name");
							String artist = (response.getJSONObject(i)).getString("artist");
							String url = (response.getJSONObject(i)).getString("url");
							String lrc_url = (response.getJSONObject(i)).getString("lrc_url");
							String pic_url = (response.getJSONObject(i)).getString("pic_url");
							Music music = new Music(uid,name,artist,null,url,lrc_url,null,pic_url,false);
							myMusicList.add(music);
							Log.v(TAG+"dao","inset new"+music.getUid());
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	
						
					}
	        	}
	        	application.setMyMusicList(myMusicList);
	        	
	        }  
	    });
		mQueue.add(mymusicRequest);
	}
	public void setFriendMusic(String uid){
		String url = get_friendmusic_url+"?uid="+uid;
		Log.v(TAG,url);
		friendmusicRequest = new JsonArrayRequest(url, null,new Response.Listener<JSONArray>() {  
	        @Override  
			public void onResponse(JSONArray response) {
				// TODO Auto-generated method stub
	        	Set friendmusic_set=new HashSet();
				for(int i=0;i<response.length();i++){
					String s = null;
					try {
						s = ( response.getJSONObject(i)).getString("uid");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					friendmusic_set.add(s);
				}
				editor.putStringSet("FRIEND_MUSIC_SET", friendmusic_set);
				editor.commit();
				Log.v(TAG,"friendmusic");
				Set set= preferences.getStringSet("FRIEND_MUSIC_SET", null);
				Log.v("TAG",set.toString());
			}
	    },  
	    new Response.ErrorListener() {  
	        @Override  
	        public void onErrorResponse(VolleyError volleyError) {  
	        	String sb = volleyError.toString(); 
	        	Log.v(TAG,sb);
	        }  
	    });
		mQueue.add(friendmusicRequest);
	}
	//��ȡδ�����notification
	public void getNotification(){
		
	}
	//��ȡ�������ֲ�������localMusiclist��
	public void localMusic(){
		Cursor cursor;
		Music music;
		String[] audioColumns = { MediaStore.Audio.Media._ID,
								MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,
								MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.ALBUM};
		cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				audioColumns, null, null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (cursor.moveToFirst()){
			do{		
					String id = ""+cursor.getInt(cursor
							.getColumnIndex(MediaStore.Audio.Media._ID));
					String title = cursor.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
					String creator = cursor.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
					String filePath = cursor.getString(cursor
								.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
					int start = Environment.getExternalStorageDirectory().getAbsolutePath().length();
					int end = filePath.lastIndexOf("/");
					String name = filePath.substring(end);
					filePath = filePath.substring(start,end);
					
					Log.v(TAG,"filePath:"+filePath);
					String fake_url = qiniu_url+name;
					Log.v(TAG,"id:"+id);
					music = new Music((long) 0,title,creator,filePath,fake_url,null,null,id,true);
					localMusicList.add(music);
			}while(cursor.moveToNext());
			application.setLocalMusicList(localMusicList);
		}
	}
	public static void setWelcomeActivityCallBack(WelcomeActivityCallBack welcomeActivityCallBack){
		WelcomeActivity.welcomeActivityCallBack = welcomeActivityCallBack;
	}
	public static WelcomeActivityCallBack getWelcomeActivityCallBack(){
		return welcomeActivityCallBack;
	}
}
