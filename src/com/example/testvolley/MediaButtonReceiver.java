package com.example.testvolley;

import com.example.service.PlayerService;

import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.Intent;  
import android.util.Log;  
import android.view.KeyEvent;  
  
public class MediaButtonReceiver extends BroadcastReceiver {  
    private static String TAG = "MediaButtonReceiver"; 
    @Override  
    public void onReceive(Context context, Intent intent) {  
    	PlayerService service = MyApplication.get().getService();
        // ���Action  
        String intentAction = intent.getAction();  
        // ���KeyEvent����  
        KeyEvent keyEvent = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);  
  
        Log.v(TAG, "Action ---->" + intentAction + "  KeyEvent----->"+ keyEvent.toString());  
  
        if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)) {  
            // ��ð����ֽ���  
            int keyCode = keyEvent.getKeyCode();  
            // ���� / �ɿ� ��ť  
            int keyAction = keyEvent.getAction();  
            // ����¼���ʱ��  
            long downtime = keyEvent.getEventTime();  
  
            // ��ȡ������ keyCode  
            StringBuilder sb = new StringBuilder();  
            // ��Щ���ǿ��ܵİ����� �� ��ӡ�����û����µļ�  
            if(keyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode && keyEvent.ACTION_UP == keyAction){
            	Log.v(TAG,"play previous");
//            	MainActivity.getMainActivityCallBack().playPrevious();
            	service.playPrevious();
            	
            }
            if(keyEvent.KEYCODE_MEDIA_NEXT == keyCode && keyEvent.ACTION_UP == keyAction){
//            	MainActivity.getMainActivityCallBack().playNext();
            	service.playNext();
            }
            if(keyEvent.KEYCODE_MEDIA_PLAY_PAUSE == keyCode && keyEvent.ACTION_UP == keyAction){
//            	MainActivity.getMainActivityCallBack().play_pause();
            	service.play_pause();
            }

            if (KeyEvent.KEYCODE_MEDIA_NEXT == keyCode) {  
                sb.append("KEYCODE_MEDIA_NEXT");  
            }  
            // ˵���������ǰ���MEDIA_BUTTON�м䰴ťʱ��ʵ�ʳ������� KEYCODE_HEADSETHOOK ������  
            // KEYCODE_MEDIA_PLAY_PAUSE  
            if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == keyCode) {  
                sb.append("KEYCODE_MEDIA_PLAY_PAUSE");  
            }  
            if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode) {  
                sb.append("KEYCODE_HEADSETHOOK");  
            }  
            if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode) {  
                sb.append("KEYCODE_MEDIA_PREVIOUS");  
            }  
            if (KeyEvent.KEYCODE_MEDIA_STOP == keyCode) {  
                sb.append("KEYCODE_MEDIA_STOP");  
            }  
            // �������İ�����  
            Log.v(TAG, sb.toString());  
        }  
    }  
}  