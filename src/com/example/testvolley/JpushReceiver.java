package com.example.testvolley;



import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoMaster;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Music;
import de.greenrobot.daoexample.MusicDao;
import de.greenrobot.daoexample.MusicMessage;
import de.greenrobot.daoexample.MusicMessageDao;
import de.greenrobot.daoexample.SystemMessage;
import de.greenrobot.daoexample.SystemMessageDao;
import de.greenrobot.daoexample.User;
import de.greenrobot.daoexample.UserDao;
import de.greenrobot.daoexample.UserDao.Properties;

/**
 * �Զ��������
 * 
 * ������������ Receiver����
 * 1) Ĭ���û����������
 * 2) ���ղ����Զ�����Ϣ
 */
public class JpushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";
	private String category;
	private MyApplication application = MyApplication.get();
	private DaoSession daoSession;
	private DaoMaster daoMaster;
	private UserDao userDao;
	private MusicDao musicDao;
	private SystemMessageDao systemMessageDao;
	private MusicMessageDao musicMessageDao;
	private QueryBuilder qb_user,qb_music,qb_systemMessage,qb_musicMessage;
	
	public JpushReceiver(){
		daoSession = application.getDaoSession(application.getApplicationContext());
		userDao = daoSession.getUserDao();
		musicDao = daoSession.getMusicDao();
		systemMessageDao = daoSession.getSystemMessageDao();
		musicMessageDao = daoSession.getMusicMessageDao();	
		qb_user = userDao.queryBuilder();
		qb_music = musicDao.queryBuilder();
		qb_systemMessage = systemMessageDao.queryBuilder();
		qb_musicMessage = musicMessageDao.queryBuilder();
	}
	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[JpushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[JpushReceiver] ����Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[JpushReceiver] ���յ������������Զ�����Ϣ: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	Log.v(TAG,"process");
        	processCustomMessage(context, bundle);
        	application.setMessage(true);
    		if(MainActivity.getMainActivityCallBack()!=null){
    			MainActivity.getMainActivityCallBack().setUserInfo();
    		}
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[JpushReceiver] ���յ�����������֪ͨ");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[JPushReceiver] ���յ�����������֪ͨ��ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[JpushReceiver] �û��������֪ͨ");
            String j = bundle.getString("cn.jpush.android.EXTRA");
            try {
				JSONObject jObject = new JSONObject(j);
				category = jObject.getString("category");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            Log.v(TAG,category);
        	//���Զ����Activity
           if(category.equals("add_friend")){
        	   Intent i = new Intent(context,MyMessageActivity.class);
        	   if(context == null){
        		   Log.v(TAG,"context null");
        	   }
      	    	i.putExtras(bundle);
            	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
         		context.startActivity(i);
           }
           else if (category.equals("confirm_friend")){
        	   Intent i = new Intent(context,MyMessageActivity.class);
        	   if(context == null){
        		   Log.v(TAG,"context null");
        	   }
      	    	i.putExtras(bundle);
            	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
         		context.startActivity(i);
           }else if(category.equals("share_one")){
        	   Intent i = new Intent(context,MusicMessageActivity.class);
        	   i.putExtras(bundle);
           	   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           	   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
           	   context.startActivity(i);
           }
           
//        	   switch(category){
//            	case "add_friend":
//            		Intent i = new Intent(context,MyMessageActivity.class);
//            		i.putExtras(bundle);
//                	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//            		context.startActivity(i);
//            		break;
//            	case "confirm_friend":
//            		break;
//            	case "share_one":
//            		break;
//
//            }

        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] �û��յ���RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //��������� JPushInterface.EXTRA_EXTRA �����ݴ�����룬������µ�Activity�� ��һ����ҳ��..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// ��ӡ���е� intent extra ����
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		Log.v(TAG,"processCustomMessage");
		//�������ݣ�������greedDao���ݿ�
		category = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		if(!ExampleUtil.isEmpty(extras)){
			try {
				JSONObject extraJson = new JSONObject(extras);
				
				if (null != extraJson && extraJson.length() > 0) {
					if(category.equals("add_friend")){
						// ����û������ݿ�
						Log.v(TAG,extraJson.toString());
						long uid = Long.parseLong(extraJson.getString("uid"));
						long user_id = Long.parseLong(extraJson.getString("user_id"));
						String name = extraJson.getString("name");
						String sex = extraJson.getString("sex");
						String avatar = extraJson.getString("avatar");
						String mood = extraJson.getString("mood");
						String message = extraJson.getString("message");
						User user = new User(uid,name,sex,mood,avatar);
						qb_user.where(Properties.Uid.eq(uid));
						long count = qb_user.buildCount().count();
						if (count > 0){
							
							userDao.update(user);
							Log.v(TAG,"update");
						}else{
							
							userDao.insert(user);
						}
						//���systemMessage�����ݿ�
						SystemMessage systemMessage = new SystemMessage(null,user_id,uid,category,message,null);
						systemMessageDao.insert(systemMessage);
					}else if(category.equals("confirm_friend")){
						// ����û������ݿ�
						long uid = Long.parseLong(extraJson.getString("uid"));
						long user_id = Long.parseLong(extraJson.getString("user_id"));
						String name = extraJson.getString("name");
						String sex = extraJson.getString("sex");
						String avatar = extraJson.getString("avatar");
						String mood = extraJson.getString("mood");
						String message = extraJson.getString("message");
						User user = new User(uid,name,sex,mood,avatar);
						qb_user.where(Properties.Uid.eq(uid));
						long count = qb_user.buildCount().count();
						if (count > 0){
							
							userDao.update(user);
							Log.v(TAG,"update");
						}else{
							
							userDao.insert(user);
						}
						//���systemMessage�����ݿ�
						SystemMessage systemMessage = new SystemMessage(null,user_id,uid,category,message,null);
						systemMessageDao.insert(systemMessage);

					}else if(category.equals("share_one")){
						// ����û������ݿ�
						long uid = Long.parseLong(extraJson.getString("uid"));
						long user_id = Long.parseLong(extraJson.getString("user_id"));
						
						String name = extraJson.getString("name");

						String sex = extraJson.getString("sex");
						String avatar = extraJson.getString("avatar");
						String mood = extraJson.getString("mood");
						String message = extraJson.getString("message");
						User user = new User(uid,name,sex,mood,avatar);
						qb_user.where(Properties.Uid.eq(uid));
						long count = qb_user.buildCount().count();
						if (count > 0){
							
							userDao.update(user);
							Log.v(TAG,"update");
						}else{
							
							userDao.insert(user);
						}
						// ����¸��������ݿ�
						long music_id = Long.parseLong(extraJson.getString("music_id"));
						String  music_name = (extraJson.getString("music_name"));
						String music_artist = (extraJson.getString("music_artist"));
						String music_url = (extraJson.getString("music_url"));
						String music_lrc_url = (extraJson.getString("music_lrc_url"));
						String music_pic_url = (extraJson.getString("music_pic_url"));
						Music music = new Music(music_id,music_name,music_artist,null,music_url,music_lrc_url,null,music_pic_url,false);
						qb_music.where(de.greenrobot.daoexample.MusicDao.Properties.Uid.eq(music_id));
						long music_count = qb_music.buildCount().count();
		
						if (music_count > 0){
							Music music_tmp = (Music)qb_music.unique();
							if(music_tmp.getLrc_cache_url() != null){
								String lrc_cache_url = music_tmp.getLrc_cache_url();
								music = new Music(music_id,music_name,music_artist,null,music_url,music_lrc_url,lrc_cache_url,music_pic_url,false);
								musicDao.update(music);
								Log.v(TAG,"update");
							}
							
						}else{
							
							musicDao.insert(music);
						}
						//���MusicMessage�����ݿ�
						//String pic_url = extraJson.getString("pic_url");
						
						MusicMessage musicMessage = new MusicMessage(null,user_id,uid,music_id,category,message,null);
						musicMessageDao.insert(musicMessage);
						Log.v(TAG,"musicMessageDao count"+musicMessageDao.count());
						
					}else if(category.equals("share_all")){
						// ����û������ݿ�
						long user_id = application.getLoginUser().getUid();
						long uid = Long.parseLong(extraJson.getString("uid"));
						String name = extraJson.getString("name");
						String sex = extraJson.getString("sex");
						String avatar = extraJson.getString("avatar");
						String mood = extraJson.getString("mood");
						String message = extraJson.getString("message");
						User user = new User(uid,name,sex,mood,avatar);
						qb_user.where(Properties.Uid.eq((long) 2));
						long count = qb_user.buildCount().count();
						
						if (count > 0){	
							Log.v(TAG,((User)qb_user.unique()).getName());
							userDao.update(user);
							Log.v(TAG,"update");
						}else{							
							userDao.insert(user);
						}
						// ����¸��������ݿ�
						long music_id = Long.parseLong(extraJson.getString("music_id"));
						String  music_name = (extraJson.getString("music_name"));
						String music_artist = (extraJson.getString("music_artist"));
						String music_url = (extraJson.getString("music_url"));
						String music_lrc_url = (extraJson.getString("music_lrc_url"));
						String music_pic_url = (extraJson.getString("music_pic_url"));
						Music music = new Music(music_id,music_name,music_artist,null,music_url,music_lrc_url,null,music_pic_url,false);
						qb_music.where(de.greenrobot.daoexample.MusicDao.Properties.Uid.eq(music_id));
						long music_count = qb_music.buildCount().count();
						if (music_count > 0){
							Music music_tmp = (Music)qb_music.unique();
							String lrc_cache_url = music_tmp.getLrc_cache_url();
							music = new Music(music_id,music_name,music_artist,null,music_url,music_lrc_url,lrc_cache_url,music_pic_url,false);
							musicDao.update(music);
							Log.v(TAG,"update");
						}else{
							
							musicDao.insert(music);
						}
						//���systemMessage�����ݿ�
						//String pic_url = extraJson.getString("pic_url");
						MusicMessage musicMessage = new MusicMessage(null,user_id,uid,music_id,category,message,null);
						Log.v(TAG,"name"+uid);
						musicMessageDao.insert(musicMessage);

					}else{
						
					}
				}
			} catch (JSONException e) {

			}
		}
//		if (MainActivity.isForeground) {
//			
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			category = message;
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
////			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
////			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (null != extraJson && extraJson.length() > 0) {
////						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
////			context.sendBroadcast(msgIntent);
//		}
	}
}
