package com.example.testvolley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.example.activity.PlayerActivity;
import com.example.cacheplayer.*;
import com.example.myview.RoundProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.jpush.android.api.InstrumentedActivity;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;


import com.android.volley.toolbox.Volley;
import com.example.testvolley.MyFriendsActivity;
import com.example.testvolley.MyMessageActivity;
import com.example.testvolley.MyRegisterActivity;
import com.example.testvolley.MySearchActivity;
import com.example.testvolley.MySecondMoodActivity;
import com.example.testvolley.UserInfoSettingActivity;
//import com.bluemor.reddotface.activity.MainActivity;
import com.example.myview.DragLayout.DragListener;
import com.example.myview.Util;
import com.example.myview.RedPointView;
import com.example.myview.DragLayout;
import com.example.testvolley.ExampleUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nineoldandroids.view.ViewHelper;
import com.example.service.PlayerService;
import com.example.service.PlayerService.LocalBinder;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.example.receiver.PlayerReceiver;
import com.example.request.*;
import com.fima.glowpadview.GlowPadView;
import com.fima.glowpadview.GlowPadView.OnTriggerListener;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Music;
import de.greenrobot.daoexample.MusicDao;
import de.greenrobot.daoexample.UserDao.Properties;

public class MainActivity extends InstrumentedActivity implements MainActivityCallBack,OnTriggerListener,OnPageChangeListener{
	
	//�໬���
	private DragLayout dl;
	private TextView my_name;
	public ImageView iv_icon;
	public ImageView iv_lover;
	public static boolean LoverMode=false;
	private ImageView search_icon;
	private ImageView message_icon;
	public ImageView iv_photo;
	public TextView my_mood;
	private LinearLayout ll_message;
	private LinearLayout ll_friends;
	private LinearLayout ll_lover;
	private RedPointView redPoint1;
	private RedPointView redPoint2;
    private SharedPreferences sp;
    
	private PlayerService playerservice;
	private ServiceConnection serviceConnection;
	public static boolean isForeground=true;
	private static String TAG = "main";
	private static boolean playFlag = false;
	private boolean firstFlag = true;
	private int index;
	private int duration;
	private Context context;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private ArrayList<Music> myMusicList,friendMusicList,localMusicList;
	private ArrayList<Music> playList;
	private MyApplication application;
	private DaoSession daoSession; 
	private MusicDao musicDao;
	private RemoteControlClient remoteControlClient; 
	private RequestQueue mQueue;
	
	private static final String SESSION_COOKIE = "PHPSESSID";
	private static String CACHE="cache:/sdcard/youting/";
	private static MainActivityCallBack mainActivityCallBack;
	static private final int BUFFER_SIZE= 1024;//Mb cache dir 
	static private final int NUM_FILES= 200;//Count files in cache dir
	private HttpGetProxy proxy;
	private String ftplogin = "user", ftppass = "123";
	private String BuffPath = "/youting";
	private MyPlayer myPlayer; 
	
	private View view1, view2, view3;
	private List<View> viewList;// view����
	private ViewPager viewPager; // ��Ӧ��viewPager  
	private GlowPadView mGlowPadView;
	private RoundProgressBar mRoundProgressBar1;
	private int progress = 0;
	private int page_temp = 300;
	
    private TextView song1;
    private TextView singer1;
    private ImageView cover_music1;   
    private TextView song2;
    private TextView singer2;       
    private ImageView cover_music2;   
    private TextView song3;
    private TextView singer3;       
    private ImageView cover_music3;
    private Runnable runnable;
    private Thread progressThread;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		context = this.getApplicationContext();
		mainActivityCallBack = this;
		application = (MyApplication)this.getApplicationContext();
		daoSession = application.getDaoSession(getApplicationContext());
		musicDao = daoSession.getMusicDao();
		remoteControlClient = application.getRemoteControlClient();
		mQueue = application.getRequestQueue();
		
		myMusicList = application.getMyMusicList();
		friendMusicList = application.getFriendMusicList();
		localMusicList = application.getLocalMusicList();
		
		preferences = getSharedPreferences("youting",MODE_PRIVATE);		
		index = preferences.getInt("INDEX", 0);
		if( preferences.getInt("PLAYLIST", 0)==0){
			playList = myMusicList;
		}else if(preferences.getInt("PLAYLIST", 0) == 1){
			playList = friendMusicList;
		}else{
			playList = localMusicList;
		}
		Log.v(TAG,"playList"+playList.size()+"myMusicList:"+myMusicList.size());
		
		viewPager = (ViewPager) findViewById(R.id.mini_music);
		miniControlViewInit();	//mini���Ž����ʼ��
		//New Adding
		Util.initImageLoader(this);
		initDragLayout();
		initView();
		
		 //��service
		serviceConnection=new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d("onServiceConnected", "connected");
				playerservice=((LocalBinder)service).getService();
				MyApplication.get().setService(playerservice);
			}
		};
		
		Intent i=new Intent(this,PlayerService.class);
		bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
		
		//���½�����
		runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						progress = playerservice.getProgress();
						mRoundProgressBar1.setProgress(progress);
						Thread.sleep(1000);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		};
		progressThread = new Thread(runnable);
				
		//Բ�ν��Ȼ�
		mRoundProgressBar1 = (RoundProgressBar) findViewById(R.id.roundProgressBar1);				
		
		mRoundProgressBar1.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if(playerservice.isFirstFlag()) playerservice.setFirstFlag(false);
				 
				if(playList.isEmpty()){
					Toast.makeText(getApplicationContext(), "δѡ���κθ���", Toast.LENGTH_SHORT).show();
				}else{
					playerservice.setPlayFlag(!playerservice.isPlayFlag());
					if(playerservice.isPlayFlag()) {
						if(playerservice.isFirstFlag()){																	
							playerservice.playItems(playerservice.getIndex());						
						}else{
							playerservice.play();
						}					
					}
					else {
						playerservice.pause();
					}
				}
				
		
			}
		});
		
		//�������Ž���		
		switchToPlayer();
		//��Բ��
		mGlowPadView = (GlowPadView) findViewById(R.id.glow_pad_view);

		mGlowPadView.setOnTriggerListener(this);
		
		mGlowPadView.setShowTargetsOnIdle(true); //����״̬�Ƿ���ʾtarget
	    		 	
	}
	
    
    //New Adding
	private void initDragLayout() {
		dl = (DragLayout) findViewById(R.id.dl);
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
				//lv.smoothScrollToPosition(new Random().nextInt(30));
			}

			@Override
			public void onClose() {
				shake();
			}

			@Override
			public void onDrag(float percent) {
				ViewHelper.setAlpha(iv_icon, 1 - percent);
			}
		});
	}

	private void initView() {
		ll_message = (LinearLayout) findViewById(R.id.ll2);
		ll_friends = (LinearLayout) findViewById(R.id.ll3);
		ll_lover = (LinearLayout) findViewById(R.id.ll4);
		my_name=(TextView) findViewById(R.id.my_name);
		my_mood=(TextView) findViewById(R.id.my_mood);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		iv_lover = (ImageView) findViewById(R.id.iv_lover);
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		search_icon = (ImageView) findViewById(R.id.search_icon);
		message_icon = (ImageView) findViewById(R.id.iv_message);
		
		
		//��iv_icon����Ϣ��ʾ�ؼ�
		if(MyMessageActivity.hasmessage==true){
		redPoint1 = new RedPointView(this, iv_icon);
		redPoint1.setContent(2);
		redPoint1.setSizeContent(8);
		redPoint1.setColorContent(Color.RED);
		redPoint1.setColorBg(Color.RED);
		redPoint1.setPosition(Gravity.RIGHT, Gravity.TOP);
		
		redPoint2 = new RedPointView(this, message_icon);
		redPoint2.setContent(2);
		redPoint2.setSizeContent(8);
		redPoint2.setColorContent(Color.RED);
		redPoint2.setColorBg(Color.RED);
		redPoint2.setPosition(Gravity.RIGHT, Gravity.TOP);
		}
//		Log.v(TAG,"avatar:"+application.getLoginUser().getAvatar());
		
		//��ʾͷ��
		if(preferences.getBoolean("login", false) && application.getLoginUser()!= null){
			if(application.getLoginUser().getMood()!= "null"){
				my_mood.setText(application.getLoginUser().getMood());
			}
			my_name.setText(application.getLoginUser().getName());
			if(application.getLoginUser().getAvatar()!= "null"){
				ImageRequest avatarRequest=new ImageRequest(application.getLoginUser().getAvatar(), new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap arg0) {
						// TODO Auto-generated method stub
						Log.v("succ","111");
						iv_icon.setImageBitmap(arg0);
						iv_photo.setImageBitmap(arg0);				
					}
				}, 50, 50, Config.RGB_565, new ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
					}			
				});
				mQueue.add(avatarRequest);
			}

		}else{
			my_name.setText("USER");
			my_mood.setText("~��������~");
		}		
		
	    //�������ϲ໬ͼ������Ӧ
		iv_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dl.open();
			}
		});
		
		//�໬������ͷ������Ӧ
		iv_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
		         boolean regstate =preferences.getBoolean("login", false);
		         if(regstate==false){
		                 //��ת��register����
		                 Intent intent=new Intent(MainActivity.this,MyLoginActivity.class);
		                 startActivity(intent);  
		                 //MainActivity.this.finish();
	             }
	
                 if(regstate==true){
		               //��ת�������������ý���
		               Intent intent=new Intent(MainActivity.this,UserInfoSettingActivity.class);
		               startActivity(intent);  
	              }
			}
		});
		
		//������������ͼ������Ӧ
		search_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(MainActivity.this,MySearchActivity.class);
				startActivity(intent);  
			}
		});
		
		//�������õ����Ӧ
		my_mood.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(MainActivity.this,MySecondMoodActivity.class);
				startActivity(intent);  
			}
		});
		
		//�ҵ���Ϣ�����Ӧ
		ll_message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(MainActivity.this,MyMessageActivity.class);
				startActivity(intent);  
				if(MyMessageActivity.hasmessage==true){
				redPoint2.hide();
				redPoint1.hide();
				MyMessageActivity.hasmessage=false;
				}
			}
		});
		
		//�ҵĺ��ѵ����Ӧ
		ll_friends.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(MainActivity.this,MyFriendsActivity.class);
				startActivity(intent);  
			}
		});
		
		//����ģʽ�����Ӧ
		ll_lover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(LoverMode==true){
					iv_lover.setImageResource(R.drawable.ic_red);
					LoverMode=false;
				}
				else{
					iv_lover.setImageResource(R.drawable.ic_green);
					LoverMode=true;
				}
			}
		});
		
	}

	//���ز໬����ʱ�������涶������
	private void shake() {
		iv_icon.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
	}
    
    
    
    
    
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


	public static MainActivityCallBack getMainActivityCallBack() {
		// TODO Auto-generated method stub
		return mainActivityCallBack;
	}
	public static void setMainActivityCallBack(MainActivityCallBack mainActivityCallBack){
		MainActivity.mainActivityCallBack = mainActivityCallBack;
	}
	
	
	
	  @Override
      public void onPageScrollStateChanged(int state) {
      }

      @Override
      public void onPageScrolled(int page, float positionOffset,
              int positionOffsetPixels) {
      }

     
      //�����и�
      @Override
      public void onPageSelected(int page) {   
    	  Log.v(TAG,"page:"+page);
    	  if(!playList.isEmpty()){
    		  	  
    	  if(page<page_temp)//���󻬣�����ǰһ��
       		{
    		  playerservice.playPrevious();
              
    		   final int viewNum=(page-1)%3+1; 
    		   ImageRequest imgRequest=new ImageRequest(playerservice.getMusic_pre().getPic_url(), new Response.Listener<Bitmap>() {
    				@Override
    				public void onResponse(Bitmap arg0) {
    					// TODO Auto-generated method stub
    					Log.v("succ","111");
    					switch(viewNum){
    						case 1:
    							cover_music1.setImageBitmap(arg0);
    							break;
    						case 2:
    							cover_music2.setImageBitmap(arg0);
    							break;
    						case 3:
    							cover_music3.setImageBitmap(arg0);
    							break;
    					
    					}
    									
    				}
    			}, 50, 50, Config.RGB_565, new ErrorListener(){
    				@Override
    				public void onErrorResponse(VolleyError arg0) {
    					// TODO Auto-generated method stub					
    				}			
    			});
    			mQueue.add(imgRequest); 
    	        switch (viewNum){
    	       	case 1: 
    	       	 song1.setText(playerservice.getMusic_pre().getName()==""?"δ֪����":playerservice.getMusic_pre().getName());
    	         singer1.setText( playerservice.getMusic_pre().getArtist()==""?"δ֪������":playerservice.getMusic_pre().getArtist());
    	         break;
    	       	case 2:
    	       	 song2.setText(playerservice.getMusic_pre().getName()==""?"δ֪����":playerservice.getMusic_pre().getName());
    	         singer2.setText( playerservice.getMusic_pre().getArtist()==""?"δ֪������":playerservice.getMusic_pre().getArtist());
    	         break;
    	       	case 3:	
    	       	 song3.setText(playerservice.getMusic_pre().getName()==""?"δ֪����":playerservice.getMusic_pre().getName());
    	         singer3.setText( playerservice.getMusic_pre().getArtist()==""?"δ֪������":playerservice.getMusic_pre().getArtist());
    	       	break;
    	       	default:
    	       		
    	       	} 
       	
       	}
    	 else if(page>page_temp) {	//���һ���������һ��
    	
    	 final int viewNum=(page+1)%3+1;
		 playerservice.playNext();   		
         Log.v(TAG,"viewNum"+viewNum);
 		 ImageRequest imgRequest=new ImageRequest(playerservice.getMusic_next().getPic_url(), new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap arg0) {
				// TODO Auto-generated method stub
				Log.v("succ","111");
				switch(viewNum){
					case 1:
						cover_music1.setImageBitmap(arg0);
						break;
					case 2:
						cover_music2.setImageBitmap(arg0);
						break;
					case 3:
						cover_music3.setImageBitmap(arg0);
						break;
				}
								
			}
		}, 50, 50, Config.RGB_565, new ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub		
				cover_music1.setImageResource(R.drawable.music_cover);
			}			
		});
		mQueue.add(imgRequest);
         switch (viewNum){
	       	case 1: 
	       	 song1.setText(playerservice.getMusic_next().getName()==""?"δ֪����":playerservice.getMusic_next().getName());
	         singer1.setText(playerservice.getMusic_next().getArtist()==""?"δ֪������":playerservice.getMusic_next().getArtist());

	         break;
	       	case 2:
	       	 song2.setText(playerservice.getMusic_next().getName()==""?"δ֪����":playerservice.getMusic_next().getName());
	         singer2.setText(playerservice.getMusic_next().getArtist()==""?"δ֪������":playerservice.getMusic_next().getArtist());
	         break;
	       	case 3:	
	       	 song3.setText(playerservice.getMusic_next().getName()==""?"δ֪����":playerservice.getMusic_next().getName());
	         singer3.setText(playerservice.getMusic_next().getArtist()==""?"δ֪������":playerservice.getMusic_next().getArtist());
	       	break;
	       	default:
	       		
	       	} 
    	  	 
    	 
    	 }
    	 page_temp=page;
    }
    	 
     }
 
  
	@Override
	public void onGrabbed(View v, int handle) {
			
	}

	@Override
	public void onReleased(View v, int handle) {
	//	Toast.makeText(this, "�м䰴ť����", Toast.LENGTH_SHORT).show();
	}

	//���ఴť������
	public void onPressed(View v, int target){
		
		final int resId = mGlowPadView.getResourceIdForTarget(target);
		int listNum =0; //ѡ�ڼ���listĬ����ʾ
		Intent intent = new Intent(context,com.example.playlistactivity.MainActivity.class);
		switch (resId) {
		case R.drawable.ic_item_share:
			listNum = 1;
			intent.putExtra("listNum", listNum);
			startActivity(intent);
			break;
		case R.drawable.ic_item_collect:
			listNum = 0;
			intent.putExtra("listNum", listNum);
			startActivity(intent);			
			break;
		case R.drawable.ic_item_like:
			break;
		case R.drawable.ic_item_push:	
			break;
		default:
			// Code should never reach here.
		}	
		
	}
	  
	
	@Override
	public void onTrigger(View v, int target) {
		final int resId = mGlowPadView.getResourceIdForTarget(target);
		switch (resId) {
		case R.drawable.ic_item_share:
			
			break;

		case R.drawable.ic_item_collect:
			if(myMusicList.isEmpty()){
				Toast.makeText(getApplicationContext(), "�б�Ϊ��", Toast.LENGTH_SHORT).show();
			}else{
				if(playerservice.isFirstFlag())  MyApplication.get().setNotificationManager(showCustomView());
				playerservice.setPlayList(myMusicList);
				playerservice.playItems(index);
			}
			break;
		case R.drawable.ic_item_like:		
			break;	
		case R.drawable.ic_item_push:
			break;
		default:
			// Code should never reach here.
		}

	}

	@Override
	public void onGrabbedStateChange(View v, int handle) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFinishFinalAnimation() {
		// TODO Auto-generated method stub
	}
	 
	
	//mini��������ʼ��
	void miniControlViewInit(){
		Log.v(TAG,"mini Init");
		LayoutInflater inflater = getLayoutInflater();
				
		view1 = inflater.inflate(R.layout.layout1, null);
		view2 = inflater.inflate(R.layout.layout2, null);
		view3 = inflater.inflate(R.layout.layout3, null);
		
        song1 = (TextView) view1.findViewById(R.id.song_name);
        singer1 = (TextView) view1.findViewById(R.id.singer_name);       
        cover_music1= (ImageView) view1.findViewById(R.id.cover_music);
        
        song2 = (TextView) view2.findViewById(R.id.song_name);
        singer2= (TextView) view2.findViewById(R.id.singer_name);       
        cover_music2= (ImageView) view2.findViewById(R.id.cover_music);
        
        song3 = (TextView) view3.findViewById(R.id.song_name);
        singer3= (TextView) view3.findViewById(R.id.singer_name);       
        cover_music3= (ImageView) view3.findViewById(R.id.cover_music);
        if (!playList.isEmpty()){
        	
	        song1.setText(playList.get(index).getName()==""?"δ֪����":playList.get(index).getName());
	        singer1.setText(playList.get(index).getArtist()==""?"δ֪������":playList.get(index).getArtist());
	        int index_next = (index==(playList.size()-1))?0:index+1;
	        int index_pre = (index==0)?playList.size()-1:index-1;
	        song3.setText(playList.get(index_pre).getName()==""?"δ֪����":playList.get(index_pre).getName());
	        singer3.setText(playList.get(index_pre).getArtist()==""?"δ֪������":playList.get(index_pre).getArtist());
	        song2.setText(playList.get(index_next).getName()==""?"δ֪����":playList.get(index_next).getName());
	        singer2.setText(playList.get(index_next).getArtist()==""?"δ֪������":playList.get(index_next).getArtist());
	        ImageRequest imgRequest1=new ImageRequest(playList.get(index).getPic_url(), new Response.Listener<Bitmap>() {
				@Override
				public void onResponse(Bitmap arg0) {
					// TODO Auto-generated method stub
					Log.v("succ","111");
					cover_music1.setImageBitmap(arg0);
				}
			}, 50, 50, Config.RGB_565, new ErrorListener(){
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					cover_music1.setImageResource(R.drawable.music_cover);
				}			
			});
			ImageRequest imgRequest2=new ImageRequest(playList.get(index_next).getPic_url(), new Response.Listener<Bitmap>() {
				@Override
				public void onResponse(Bitmap arg0) {
					// TODO Auto-generated method stub
					Log.v("succ","111");
					cover_music2.setImageBitmap(arg0);
				}
			}, 50, 50, Config.RGB_565, new ErrorListener(){
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					cover_music1.setImageResource(R.drawable.music_cover);
				}			
			});
			ImageRequest imgRequest3=new ImageRequest(playList.get(index_pre).getPic_url(), new Response.Listener<Bitmap>() {
				@Override
				public void onResponse(Bitmap arg0) {
					// TODO Auto-generated method stub
					Log.v("succ","111");
					cover_music3.setImageBitmap(arg0);
				}
			}, 50, 50, Config.RGB_565, new ErrorListener(){
				@Override
				public void onErrorResponse(VolleyError arg0) {
					// TODO Auto-generated method stub
					cover_music1.setImageResource(R.drawable.music_cover);
				}			
			});
			mQueue.add(imgRequest1);
			mQueue.add(imgRequest2);
			mQueue.add(imgRequest3);
        }   
//        Log.v(TAG,"name:"+playerservice.getMusic_pre().getName());
        
		viewList = new ArrayList<View>();// ��Ҫ��ҳ��ʾ��Viewװ��������
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);

		PagerAdapter pagerAdapter = new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				//return viewList.size();
				return Integer.MAX_VALUE;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				// TODO Auto-generated method stub
			
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				try {    
					container.addView(viewList.get(position%3),0);
		        }catch(Exception e){  
		            //handler something  
		        }  
				return viewList.get(position%3);
			}
		};

		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(300);
		page_temp=300;
		viewPager.setOnPageChangeListener(this);
		
			
	}
	
	
	//��ʾNotification
	@SuppressLint("NewApi")
	public NotificationManager showCustomView() {
		final RemoteViews remoteViews = new RemoteViews(getPackageName(),
				R.layout.music_notification);
		final RemoteViews remoteViewsNormal = new RemoteViews(getPackageName(),
				R.layout.music_notification_normal);
		remoteViews.setTextViewText(R.id.notification_song_name, playList.get(index).getName()==""?"δ֪����":playList.get(index).getName()); //����textview
		remoteViewsNormal.setTextViewText(R.id.notification_song_name, playList.get(index).getName()==""?"δ֪����":playList.get(index).getName());
		remoteViews.setTextViewText(R.id.notification_singer_name,playList.get(index).getArtist()==""?"δ֪������":playList.get(index).getArtist());
		remoteViewsNormal.setTextViewText(R.id.notification_singer_name,playList.get(index).getArtist()==""?"δ֪������":playList.get(index).getArtist());
		ImageRequest imgRequest=new ImageRequest(playList.get(index).getPic_url(), new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG+"notification","onresponse");
				remoteViews.setImageViewBitmap(R.id.notification_singer_pic, arg0);
				remoteViewsNormal.setImageViewBitmap(R.id.notification_singer_pic, arg0);
				application.getNotificationManager().notify(1, application.getNotification());

			}
		}, 50, 50, Config.RGB_565, new ErrorListener(){
			@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				Log.v(TAG,"onerror");
				remoteViews.setImageViewResource(R.id.notification_singer_pic,R.drawable.music_cover);
				remoteViewsNormal.setImageViewResource(R.id.notification_singer_pic,R.drawable.music_cover);
			}			
		});
		mQueue.add(imgRequest);
	
		Builder builder = new Builder(MainActivity.this);
		builder.setContent(remoteViews).setSmallIcon(R.drawable.ic_launcher)
				.setOngoing(true)
				.setTicker("You Ting");
		Notification notification=builder.build();
		MyApplication.get().setNotification(notification);
		
		notification.contentView=remoteViewsNormal;
		notification.bigContentView = remoteViews;	
		NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(1, notification);
		Log.v(TAG,"notification");
		return manager;
	}
	
	
	public void setProgress(){
		
		if (!progressThread.isAlive()&&playerservice.isPlayFlag()){
			progressThread.start();
		}
		
		MainActivity.this.runOnUiThread(new Runnable(){
			public void run() {
		
		mRoundProgressBar1.setbackground(playerservice.isPlayFlag());
		
			}
			
		});
	
	}
	
	//�������Ž���
		public void switchToPlayer(){
			
			cover_music1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
					startActivity(intent);
				}
			});
			
			cover_music2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
					startActivity(intent);
				}
			});
			
			cover_music3.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
					startActivity(intent);
				}
			});
			
			
		}
	public void setPage(){
		Log.v(TAG,"setPage");
//		int page = viewPager.getCurrentItem();
		page_temp = 300;
		viewPager.setCurrentItem(300);
		
		MainActivity.this.runOnUiThread(new Runnable(){
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				    song1.setText(playerservice.getMusic().getName()==""?"δ֪����":playerservice.getMusic().getName());
			        singer1.setText(playerservice.getMusic().getArtist()==""?"δ֪������":playerservice.getMusic().getArtist());
			        song3.setText(playerservice.getMusic_pre().getName()==""?"δ֪����":playerservice.getMusic_pre().getName());
			        singer3.setText(playerservice.getMusic_pre().getArtist()==""?"δ֪������":playerservice.getMusic_pre().getArtist());
			        song2.setText(playerservice.getMusic_next().getName()==""?"δ֪����":playerservice.getMusic_next().getName());
			        singer2.setText(playerservice.getMusic_next().getArtist()==""?"δ֪������":playerservice.getMusic_next().getArtist());
			        ImageRequest imgRequest1=new ImageRequest(playerservice.getMusic().getPic_url(), new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap arg0) {
							// TODO Auto-generated method stub
							Log.v("succ","111");
							cover_music1.setImageBitmap(arg0);
							
						}
					}, 50, 50, Config.RGB_565, new ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError arg0) {
							// TODO Auto-generated method stub
							cover_music1.setImageResource(R.drawable.music_cover);
						}			
					});
					ImageRequest imgRequest2=new ImageRequest(playerservice.getMusic_next().getPic_url(), new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap arg0) {
							// TODO Auto-generated method stub
							Log.v("succ","111");
							cover_music2.setImageBitmap(arg0);
							
						}
					}, 50, 50, Config.RGB_565, new ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError arg0) {
							// TODO Auto-generated method stub
							cover_music2.setImageResource(R.drawable.music_cover);
						}			
					});
					ImageRequest imgRequest3=new ImageRequest(playerservice.getMusic_pre().getPic_url(), new Response.Listener<Bitmap>() {
						@Override
						public void onResponse(Bitmap arg0) {
							// TODO Auto-generated method stub
							Log.v("succ","111");
							cover_music3.setImageBitmap(arg0);
							
						}
					}, 50, 50, Config.RGB_565, new ErrorListener(){
						@Override
						public void onErrorResponse(VolleyError arg0) {
							// TODO Auto-generated method stub
							cover_music3.setImageResource(R.drawable.music_cover);
						}			
					});
					mQueue.add(imgRequest1);
					mQueue.add(imgRequest2);
					mQueue.add(imgRequest3);
			}
			
		});
       
		
   	 }
   	public void initCallBack(){
   		
   		myMusicList = application.getMyMusicList();
		friendMusicList = application.getFriendMusicList();
   		index = preferences.getInt("INDEX", 0);
		if( preferences.getInt("PLAYLIST", 0)==0){
			playList = myMusicList;
		}else if(preferences.getInt("PLAYLIST", 0) == 1){
			playList = friendMusicList;
		}else{
			playList = localMusicList;
		}
		playerservice.setPlayList(playList);
		Log.v(TAG,"playList"+playList.size()+"myMusicList:"+myMusicList.size());
		miniControlViewInit();

   	}

   	public void setUserInfo(){
   		MainActivity.this.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				initView();
			}
   			
   		});
   	}
	 

}

