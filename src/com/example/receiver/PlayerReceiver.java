package com.example.receiver;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.ImageRequest;
import com.example.service.PlayerService;
import com.example.testvolley.MainActivity;
import com.example.testvolley.MyApplication;
import com.example.testvolley.R;

import de.greenrobot.daoexample.Music;

public class PlayerReceiver extends BroadcastReceiver {

	MyApplication myApplication=MyApplication.get();
	private RequestQueue mQueue;
	private String TAG ="playerreceive";
	private PlayerService playerservice ;
	private  ArrayList<Music> playList; 
	private  int index;
	
	public PlayerReceiver(){
		mQueue = myApplication.getRequestQueue();
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getStringExtra("action");
		//Log.d("action", action);
			if(action.equals("close")){
				myApplication.getNotificationManager().cancelAll();
				myApplication.getService().pause();
			    myApplication.getService().stopSelf();
				System.exit(0);
			}else if(action.equals("play")){
				
				myApplication.getNotification().contentView = new RemoteViews(myApplication.getPackageName(),
						R.layout.music_notification_normal);
				myApplication.getNotification().bigContentView = new RemoteViews(myApplication.getPackageName(),
						R.layout.music_notification);
				playerservice =myApplication.getService();
				playList=playerservice.getPlayList();
				index=playerservice.getIndex();
				myApplication.getNotification().contentView.setImageViewResource(R.id.play_pause_music, R.drawable.notification_stop);
				myApplication.getNotification().bigContentView.setImageViewResource(R.id.play_pause_music, R.drawable.notification_stop);
				myApplication.getNotification().contentView.setTextViewText(R.id.notification_song_name, playList.get(index).getName()==""?"δ֪����":playList.get(index).getName());
				myApplication.getNotification().contentView.setTextViewText(R.id.notification_singer_name,playList.get(index).getArtist()==""?"δ֪������":playList.get(index).getArtist());
				myApplication.getNotification().bigContentView.setTextViewText(R.id.notification_song_name, playList.get(index).getName()==""?"δ֪����":playList.get(index).getName());
				myApplication.getNotification().bigContentView.setTextViewText(R.id.notification_singer_name,playList.get(index).getArtist()==""?"δ֪������":playList.get(index).getArtist());
				ImageRequest imgRequest=new ImageRequest(playList.get(index).getPic_url(), new Response.Listener<Bitmap>() {
					@Override
					public void onResponse(Bitmap arg0) {
						// TODO Auto-generated method stub
						Log.v(TAG+"notification","onresponse");
						myApplication.getNotification().contentView.setImageViewBitmap(R.id.notification_singer_pic, arg0);
						myApplication.getNotification().bigContentView.setImageViewBitmap(R.id.notification_singer_pic, arg0);
						myApplication.getNotificationManager().notify(1, myApplication.getNotification());
					}
				}, 50, 50, Config.RGB_565, new ErrorListener(){
					@Override
					public void onErrorResponse(VolleyError arg0) {
						// TODO Auto-generated method stub
						Log.v(TAG,"onerror");
						myApplication.getNotification().contentView.setImageViewResource(R.id.notification_singer_pic,R.drawable.music_cover);
						myApplication.getNotification().bigContentView.setImageViewResource(R.id.notification_singer_pic,R.drawable.music_cover);
					}			
				});
				mQueue.add(imgRequest);
				setNotificationIntent(context);
					
			}else if(action.equals("pause")){
				
				myApplication.getNotification().bigContentView.setImageViewResource(R.id.play_pause_music, R.drawable.notification_play);
				myApplication.getNotification().contentView.setImageViewResource(R.id.play_pause_music, R.drawable.notification_play);
				myApplication.getNotificationManager().notify(1, myApplication.getNotification());
				Log.v(TAG,"pause");
			}
			else if(action.equals("next")){	
			
				
			}
	}
	
	public void setNotificationIntent(Context context) {
		// TODO Auto-generated method stub
		        //�������ͼƬ�ص����Ž���
				Intent reActivity=new Intent(context,MainActivity.class);
				PendingIntent pIntent=PendingIntent.getActivity(context, 0, reActivity, 0);
				myApplication.getNotification().contentView.setOnClickPendingIntent(R.id.notification_singer_pic, pIntent);
				myApplication.getNotification().bigContentView.setOnClickPendingIntent(R.id.notification_singer_pic, pIntent);
				
				//�رհ�ť
				Intent closeIntent=new Intent(context,PlayerReceiver.class); 
				closeIntent.putExtra("action", "close");
				closeIntent.setAction("com.example.receiver.PlayerReceiver");
				PendingIntent closepi=PendingIntent.getBroadcast(context, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				myApplication.getNotification().contentView.setOnClickPendingIntent(R.id.close, closepi);
				myApplication.getNotification().bigContentView.setOnClickPendingIntent(R.id.close, closepi);
				
				//��ͣ
				Intent pauaseOrStartIntent=new Intent(context,PlayerService.class); 
				pauaseOrStartIntent.putExtra("action", "pause");
				PendingIntent pausepi = PendingIntent.getService(context, 1, pauaseOrStartIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				myApplication.getNotification().contentView.setOnClickPendingIntent(R.id.play_pause_music, pausepi);//----���ö�Ӧ�İ�ťID���
				myApplication.getNotification().bigContentView.setOnClickPendingIntent(R.id.play_pause_music, pausepi);
				
				//��һ��
				Intent nextIntent=new Intent(context,PlayerService.class); 
				nextIntent.putExtra("action", "next");
				PendingIntent nextpi = PendingIntent.getService(context, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				myApplication.getNotification().contentView.setOnClickPendingIntent(R.id.next_music, nextpi);//----���ö�Ӧ�İ�ťID���
				myApplication.getNotification().bigContentView.setOnClickPendingIntent(R.id.next_music, nextpi);
	}

}
