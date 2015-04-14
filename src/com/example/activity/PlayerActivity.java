package com.example.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.List;

import org.apache.http.Header;

import com.example.service.PlayerService;
import com.example.service.PlayerService.LocalBinder;
import com.example.service.PlayerService.PlayMode;
import com.example.testvolley.MainActivity;
import com.example.testvolley.MyApplication;
import com.example.testvolley.PlayerActivityCallBack;
import com.example.testvolley.R;
import com.example.util.TimeHelper;

import de.greenrobot.daoexample.Music;

import com.example.myview.ILrcView;
import com.example.myview.DefaultLrcBuilder;
import com.example.myview.ILrcBuilder;
import com.example.myview.LrcRow;
import com.example.myview.ILrcView.LrcViewListener;
import com.example.myview.LrcView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerActivity extends FragmentActivity implements PlayerActivityCallBack{

	public static final String TAG = PlayerActivity.class.getSimpleName();

	public static final int MSG_SET_LYRIC_INDEX = 1;

	private ImageButton mView_ib_back = null;
	private ImageButton mView_ib_more_functions = null;
	private TextView mView_tv_songtitle = null;
	private TextView mView_tv_current_time = null;
	private TextView mView_tv_total_time = null;
	private TextView mView_tv_lyric_empty = null;
	private SeekBar mView_sb_song_progress = null;
	private ImageButton mView_ib_play_mode = null;
	private ImageButton mView_ib_play_previous = null;
	private ImageButton mView_ib_play_or_pause = null;
	private ImageButton mView_ib_play_next = null;
	private ImageButton mView_ib_playqueue = null;
	private  LrcView mLrcView;

	private PopupMenu mOverflowPopupMenu = null;
	private PlayerService playerService;
	private ServiceConnection serviceConnection;
	private boolean threadFlag;
	 private Runnable runnable;
	 private int progress = 0;
	 private Thread progressThread;
     private static PlayerActivityCallBack playerActivityCallBack;
     private ILrcBuilder builder;
     
     //������ļ�ת��Ϊ�ַ���
     public String transFileToString(String fileName){
    	 
         try {
        	 File file = new File(fileName);
        	 Log.v(TAG,"succ");
        	 BufferedReader bufReader = new BufferedReader(new FileReader(file) );
             String line="";
             String Result="";
             while((line = bufReader.readLine()) != null){
             	if(line.trim().equals(""))
             		continue;
             	Result += line + "\r\n";
             }
             return Result;
         } catch (Exception e) {
             e.printStackTrace();
         }
         return "";
     }
     
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViews();
		initViewsSetting();
		playerActivityCallBack = this;
	
		
		//��service
		serviceConnection=new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
			}			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.v("onServiceConnected", "connected");
				playerService=((LocalBinder)service).getService();		
				initCurrentPlayInfo();
				
			}
		};
		
		Intent i=new Intent(this,PlayerService.class);
		bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
		
		//���ø�ʼ���
		mLrcView.setListener(new LrcViewListener() {

			public void onLrcSeeked(int newPosition, LrcRow row) {
				if (playerService.myPlayer != null) {
					Log.d(TAG, "onLrcSeeked:" + row.time);
					playerService.seekToSpecifiedPosition((int)row.time);
				}
			}
		});
		
		//���½�����
				runnable = new Runnable() {
					@Override
					public void run() {
						while (playerService.isPlayFlag()) {
							try {
								progress = playerService.myPlayer.getCurrentPosition();
								
								// ���µ�ǰ���Ž���
								PlayerActivity.this.runOnUiThread(new Runnable(){

									@Override
									public void run() {
										// TODO Auto-generated method stub
										mView_sb_song_progress.setProgress(progress
												* mView_sb_song_progress.getMax()
												/ (int) playerService.myPlayer.getDuration());
										mView_tv_current_time.setText(TimeHelper
												.milliSecondsToFormatTimeString(progress));
										mLrcView.seekLrcToTime(progress);
									}
									
								});
								
							//	Log.v(TAG, "progress "+mView_sb_song_progress.getProgress());
								Thread.sleep(1000);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				};
				
				progressThread = new Thread(runnable);		
				if(playerService.isPlayFlag()) progressThread.start();
	}

   

	@Override
	public void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();

		
	}

	


	/** �Ը����ؼ�������ز������������� */
	private void initViewsSetting() {
			
		// ��ǰ������Ϣ-----------------------------------------------------
		mView_tv_current_time.setText(TimeHelper
				.milliSecondsToFormatTimeString(0));
		mView_tv_total_time.setText(TimeHelper
				.milliSecondsToFormatTimeString(0));

		// ���˰���----------------------------------------------------------
		mView_ib_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switchToMain();
			}
		});

		// ���ſ���-----------------------------------------------------------------
		
		// ����ģʽ--
		mView_ib_play_mode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playerService != null) {
					playerService.changePlayMode();
				}
			}
		});

		// ��һ��--
		mView_ib_play_previous.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playerService.playPrevious();
				
			}
		});

		// ���š���ͣ
		mView_ib_play_or_pause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playerService.isPlayFlag()) {
					playerService.pause();
					mView_ib_play_or_pause.setImageResource(R.drawable.button_play);				
				} else {
					playerService.play();
					mView_ib_play_or_pause.setImageResource(R.drawable.button_pause);	
				}
			}
		});

		// ��һ��--
		mView_ib_play_next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playerService.playNext();
				
			}
		});

		
		// ���϶��Ľ�����
		mView_sb_song_progress
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// �϶����Ž�����������Ϣ������ˣ�ָʾ��ָ�����ȿ�ʼ����
						if (playerService != null ) {
							playerService.seekToSpecifiedPosition(seekBar
									.getProgress()
									* (int) playerService.myPlayer.getDuration()/seekBar.getMax());
							Log.v(TAG, ""+seekBar.getProgress());
						}
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {		
							// ���ݻ����Ľ��ȼ������Ӧ�Ĳ���ʱ��
							mView_tv_current_time.setText(TimeHelper
									.milliSecondsToFormatTimeString(progress
											* playerService.myPlayer.getDuration()
											/ seekBar.getMax()));
							mLrcView.seekLrcToTime(progress);
						
					}
				});}

		
		
	/**
	 * ���ݲ���ģʽ���ò���ģʽ��ť��ͼ��
	 * 
	 * @param mode
	 *            ���ֲ���ģʽ
	 * */
	private void setPlayModeImage(int mode) {
		switch (mode) {
		case PlayMode.REPEAT_SINGLE:
			mView_ib_play_mode
					.setImageResource(R.drawable.button_playmode_repeat_single);
			break;
		case PlayMode.REPEAT:
			mView_ib_play_mode
					.setImageResource(R.drawable.button_playmode_repeat);
			break;
		case PlayMode.SEQUENTIAL:
			mView_ib_play_mode
					.setImageResource(R.drawable.button_playmode_sequential);
			break;
		case PlayMode.SHUFFLE:
			mView_ib_play_mode
					.setImageResource(R.drawable.button_playmode_shuffle);
			break;
		default:
			break;
		}
	}

	
	/** ��ʼ����ǰ������Ϣ */
	private void initCurrentPlayInfo() {
		
	// ���ø������⡢ʱ������ǰ����ʱ�䡢��ǰ���Ž��ȡ����
    	Music myMusic = playerService.getMusic();
    	if (myMusic != null) {
			mView_tv_total_time.setText(TimeHelper
					.milliSecondsToFormatTimeString(playerService.myPlayer.getDuration()));
			Log.v(TAG,"songduration"+playerService.myPlayer.getDuration());
			mView_tv_songtitle.setText(playerService.getMusic().getName().equals("")?"δ֪����":playerService.getMusic().getName());
			mView_tv_current_time.setText(TimeHelper
					.milliSecondsToFormatTimeString(playerService.myPlayer.getCurrentPosition()));
			mView_sb_song_progress.setProgress(playerService.myPlayer.getCurrentPosition()
					* mView_sb_song_progress.getMax()
				/ (int) playerService.myPlayer.getDuration());
			playerService.loadLyric(myMusic);   
		}

    	if (playerService.isPlayFlag()) {
			mView_ib_play_or_pause.setImageResource(R.drawable.button_pause);				
		} else {
			mView_ib_play_or_pause.setImageResource(R.drawable.button_play);	
		}
		// ���ò���ģʽ��ťͼƬ
		setPlayModeImage(playerService.mPlayMode);
	}

	
	private void findViews() {
		setContentView(R.layout.layout_musicplay);
		mView_ib_back = (ImageButton) findViewById(R.id.play_button_back);
		mView_ib_more_functions = (ImageButton) findViewById(R.id.play_more_functions);
		mView_ib_playqueue = (ImageButton) findViewById(R.id.play_list);
		mView_ib_play_mode = (ImageButton) findViewById(R.id.play_mode);
		mView_ib_play_next = (ImageButton) findViewById(R.id.play_playnext);
		mView_ib_play_previous = (ImageButton) findViewById(R.id.play_playprevious);
		mView_ib_play_or_pause = (ImageButton) findViewById(R.id.play_playbutton);
		mView_sb_song_progress = (SeekBar) findViewById(R.id.play_progress);
		mView_tv_current_time = (TextView) findViewById(R.id.play_current_time);
		mView_tv_total_time = (TextView) findViewById(R.id.play_song_total_time);
		mView_tv_songtitle = (TextView) findViewById(R.id.play_song_title);
		mLrcView = (LrcView) findViewById(R.id.lyricshow);
		mView_tv_lyric_empty = (TextView) findViewById(R.id.lyric_empty);
		
	}


		
	private void switchToMain() {
		//Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
		//startActivity(intent);
		this.finish();
		if(MainActivity.getMainActivityCallBack()!=null)
		MainActivity.getMainActivityCallBack().setPage();
	}



	public static PlayerActivityCallBack getPlayerActivityCallBack() {
		return playerActivityCallBack;
	}



	public void setPlayerActivityCallBack(
			PlayerActivityCallBack playerActivityCallBack) {
		this.playerActivityCallBack = playerActivityCallBack;
	}



	@Override
	public void setProgress() {
		// TODO Auto-generated method stub
		if (playerService.isPlayFlag()){
			Log.v(TAG,"playerthread");
			progressThread = new Thread(runnable);	
			progressThread.start();
			
		}
	}

	public void onPlayModeChanged(int playMode) {
		setPlayModeImage(playMode);
	}

    //�и�ʱˢ�¸�����Ϣ
	@Override
	public void refreshview() {
		final Music myMusic = playerService.getMusic();
		
		PlayerActivity.this.runOnUiThread(new Runnable(){
		// TODO Auto-generated method stub
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (myMusic != null) {
				mView_ib_play_or_pause.setImageResource(R.drawable.button_pause);
				mView_tv_total_time.setText(TimeHelper
						.milliSecondsToFormatTimeString(playerService.myPlayer.getDuration()));
				//Log.v(TAG,"songduration"+playerService.myPlayer.getDuration());
				mView_tv_songtitle.setText(playerService.getMusic().getName().equals("")?"δ֪����":playerService.getMusic().getName());
				mView_tv_current_time.setText(TimeHelper
						.milliSecondsToFormatTimeString(playerService.myPlayer.getCurrentPosition()));
//				mView_sb_song_progress.setProgress(playerService.myPlayer.getCurrentPosition()
//						* mView_sb_song_progress.getMax()
//					/ (int) playerService.myPlayer.getDuration());		
				mView_sb_song_progress.setProgress(0);
				mLrcView.setLrc(null);//��ո��
		        Log.v(TAG,"refresh");
		        playerService.loadLyric(myMusic);     
			}
		}
		});
    	
	}

	//��ʾ���
	public void showLrc(){
		if(playerService.mHasLyric){
			Log.v(TAG,"haslrc");
		String lrc = transFileToString(playerService.getMusic().getLrc_cache_url());
		
		builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrc);
        mLrcView.mHignlightRow=0;
        mLrcView.setLrc(rows);
       
		}
		else  {
			mLrcView.setLoadingTipText("û�и��");
			mLrcView.setLrc(null);
		}
        }
		
		
}

