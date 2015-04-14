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

import de.greenrobot.daoexample.Music;
import de.greenrobot.daoexample.MusicMessage;
import de.greenrobot.daoexample.SystemMessage;
import de.greenrobot.daoexample.User;
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

public class MusicMessageAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<MusicMessage> mmList;
	private ArrayList<User> userList;
	private ArrayList<Music> musicList;
	private MyApplication application;
	private RequestQueue mQueue;
	private ImageLoader imageLoader;
	private MusicMessage mm;
	
	private static final String confirm_friend = "http://121.42.164.7/index.php/Home/Index/confirm_friend";
	private static final String TAG = "MyMessageAdapter";
	
	public MusicMessageAdapter(Context context,ArrayList<MusicMessage> mmList,ArrayList<User> userList,ArrayList<Music> musicList){
		this.context = context;
		this.userList = userList;
		this.mmList = mmList;
		this.musicList = musicList;
		
		application = MyApplication.get();
		mQueue = application.getRequestQueue();
		imageLoader = new ImageLoader(mQueue,new BitmapCache());


		}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mmList.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		convertView = LayoutInflater.from(context).inflate(R.layout.item_music_message, null);
		final ImageButton play_button = (ImageButton)convertView.findViewById(R.id.play);
		TextView message_view = (TextView)convertView.findViewById(R.id.message);
		TextView name_view = (TextView)convertView.findViewById(R.id.name);
		TextView music_name_view = (TextView)convertView.findViewById(R.id.music_name);
		NetworkImageView music_pic_view = (NetworkImageView)convertView.findViewById(R.id.music_pic);
		NetworkImageView avatar_view = (NetworkImageView)convertView.findViewById(R.id.avatar);
		music_pic_view.setDefaultImageResId(R.drawable.music_cover);
		music_pic_view.setErrorImageResId(R.drawable.music_cover);
		avatar_view.setDefaultImageResId(R.drawable.ic_launcher);  
		avatar_view.setErrorImageResId(R.drawable.ic_launcher);  

		mm = mmList.get(position);
		String category = mm.getCategory();
		
		User user = userList.get(position);
		
		message_view.setText(mm.getMessage());
	
		name_view.setText(user.getName());
		String avatar = user.getAvatar();
		if(!avatar.equals("null")){
			avatar_view.setImageUrl(avatar,imageLoader);
			Log.v(TAG,"avatar:"+avatar);
		}
		
		Music music = musicList.get(position);
		music_name_view.setText(music.getName());
		String pic_url = music.getPic_url();
		if(!pic_url.equals("null")){
			music_pic_view.setImageUrl(pic_url, imageLoader);
		}
		play_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		return convertView;
	}
	

}
