package com.example.testvolley;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.example.request.StringRequest;
import com.example.service.PlayerService;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.daoexample.DaoSession;
import de.greenrobot.daoexample.Music;
import de.greenrobot.daoexample.MusicDao;
import de.greenrobot.daoexample.MusicMessage;
import de.greenrobot.daoexample.SystemMessage;
import de.greenrobot.daoexample.User;
import de.greenrobot.daoexample.MusicDao.Properties;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SearchMusicAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<Music> musicList;
	private MyApplication application;
	private RequestQueue mQueue;
	private ImageLoader imageLoader;
	private MusicMessage mm;
	private DaoSession daoSession;
	private MusicDao musicDao;
	private QueryBuilder qb;
	private StringRequest addRequest,delRequest;
	private PlayerService service;
	private Music music;
	private int play_position = 10000;


	private static final String TAG = "SearchMusicAdapter";
	private static final String add_my_music = "http://121.42.164.7/index.php/Home/Index/add_my_music";
	private static final String delete_my_music = "http://121.42.164.7/index.php/Home/Index/delete_my_music";
	
	public SearchMusicAdapter(Context context,ArrayList<Music> musicList){
		this.context = context;
		this.musicList = musicList;
		
		application = MyApplication.get();
		mQueue = application.getRequestQueue();
		imageLoader = new ImageLoader(mQueue,new BitmapCache());
		daoSession = application.getDaoSession(context);
		musicDao = daoSession.getMusicDao();
		service = application.getService();
		

		}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return musicList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		convertView = LayoutInflater.from(context).inflate(R.layout.item_search_music, null);
		ImageButton play_button = (ImageButton)convertView.findViewById(R.id.play);
		ImageButton like_button = (ImageButton)convertView.findViewById(R.id.like);
		TextView name_view = (TextView)convertView.findViewById(R.id.name);
		TextView artist_view = (TextView)convertView.findViewById(R.id.artist);
		
		if(position == play_position){
			play_button.setImageResource(R.drawable.mini_stop);
		}
		like_button.setTag("0");
		
		music = musicList.get(position);
		long uid = music.getUid();
		qb =musicDao.queryBuilder();
		qb.where(Properties.Uid.eq(uid));
		if(qb.buildCount().count()>0){
			music = (Music)qb.unique();
			Log.v(TAG,music.getName());
		}
		name_view.setText(music.getName());
		artist_view.setText(music.getArtist());
		Log.v(TAG,application.getMyMusicList().get(3).getName());
		if(application.getMyMusicList().contains(music)){
			Log.v(TAG,"contains");
			like_button.setImageResource(R.drawable.ic_lover);
			like_button.setTag("1");
		}

		like_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
//				final ImageButton button = (ImageButton) v;
				String tag = (String)v.getTag();
				Log.v(TAG,"position:"+position+"tag:"+tag);
				music = musicList.get(position);
				long uid = music.getUid();
				String add_url = add_my_music+"?music_id="+uid;
				String del_url = delete_my_music+"?music_id="+uid;
				
				addRequest = new StringRequest(Method.GET,add_url,null,new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						((ImageButton)v).setImageResource(R.drawable.ic_lover);
						v.setTag("1");
						music = musicList.get(position);
						application.addToMyMusicList(music);
					}
				},new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						
					}
				});
				delRequest = new StringRequest(Method.GET,del_url,null,new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						((ImageButton)v).setImageResource(R.drawable.ic_red);
						v.setTag("0");
						music = musicList.get(position);
						long uid = music.getUid();
						qb =musicDao.queryBuilder();
						qb.where(Properties.Uid.eq(uid));
						if(qb.buildCount().count()>0){
							music = (Music)qb.unique();
							Log.v(TAG,music.getName());
						}
						application.RemoveFromMyMusicList(music);
					}
				},new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						
					}
				});
				if(tag.equals("0")){
					mQueue.add(addRequest);
				}else{
					mQueue.add(delRequest);
				}
			}
			
		});
		play_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				music = musicList.get(position);
				ImageButton button = (ImageButton)v;
				Log.v(TAG,"position"+music.getName());
				if(service.getMusic().equals(music)){
					if(service.isPlayFlag()){
						Log.v(TAG,"1");
						service.pause();
						button.setImageResource(R.drawable.mini_play);
					}else{
						Log.v(TAG,"2");
						service.play();
						button.setImageResource(R.drawable.mini_stop);
					}
					
				}else{
					Log.v(TAG,"3");
					SearchMusicAdapter.this.notifyDataSetChanged();
					service.playSong(music);
					MainActivity.getMainActivityCallBack().setPage();
					button.setImageResource(R.drawable.mini_stop);
					
					play_position = position;
					
				}
				
			}
			
		});
		return convertView;
	}
	

}
