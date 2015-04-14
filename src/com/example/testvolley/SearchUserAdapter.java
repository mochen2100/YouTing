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
import android.widget.Toast;

public class SearchUserAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<User> userList;
	private MyApplication application;
	private RequestQueue mQueue;
	private ImageLoader imageLoader;
	private StringRequest addRequest; 
	private MusicMessage mm;
	private ProgressDialog mDialog;
	private User user;
	
	private static final String add_friend = "http://121.42.164.7/index.php/Home/Index/add_friend";
	private static final String TAG = "SearchUserAdapter";
	
	public SearchUserAdapter(Context context,ArrayList<User> userList){
		this.context = context;
		this.userList = userList;
		
		application = MyApplication.get();
		mQueue = application.getRequestQueue();
		imageLoader = new ImageLoader(mQueue,new BitmapCache());
		mDialog = new ProgressDialog(context);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("正在发送请求");

		}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return userList.size();
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
		
		convertView = LayoutInflater.from(context).inflate(R.layout.item_search_user, null);
		ImageButton add_friend_button = (ImageButton)convertView.findViewById(R.id.add_friend);
		TextView name_view = (TextView)convertView.findViewById(R.id.name);
		NetworkImageView avatar_view = (NetworkImageView)convertView.findViewById(R.id.avatar);
		avatar_view.setDefaultImageResId(R.drawable.ic_launcher);  
		avatar_view.setErrorImageResId(R.drawable.ic_launcher);  

		user = userList.get(position);
		name_view.setText(user.getName());
		
		String avatar = user.getAvatar();
		if(!avatar.equals("null")){
			avatar_view.setImageUrl(avatar,imageLoader);
			Log.v(TAG,"avatar:"+avatar);
		}
		
		add_friend_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String add_friend_url = add_friend+"?friend_id="+user.getUid();
				addRequest = new StringRequest(Method.GET,add_friend_url,null,new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						// TODO Auto-generated method stub
						Log.v(TAG,response);
						mDialog.dismiss();
					}
				},new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Toast.makeText(context, "发送请求失败", Toast.LENGTH_SHORT).show();
						mDialog.dismiss();
					}
				});
				mDialog.show();
				mQueue.add(addRequest);
			}
			
		});
		return convertView;
	}
	

}
