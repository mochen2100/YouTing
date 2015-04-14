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

public class MyMessageAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<SystemMessage> smList;
	private ArrayList<User> userList;
	private MyApplication application;
	private RequestQueue mQueue;
	private ImageLoader imageLoader;
	private SystemMessage sm;
	private ProgressDialog mDialog;
	
	private static final String confirm_friend = "http://121.42.164.7/index.php/Home/Index/confirm_friend";
	private static final String TAG = "MyMessageAdapter";
	
	public MyMessageAdapter(Context context,ArrayList<User> userList, ArrayList<SystemMessage> smList){
		this.context = context;
		this.userList = userList;
		this.smList = smList;
		application = MyApplication.get();
		mQueue = application.getRequestQueue();
		imageLoader = new ImageLoader(mQueue,new BitmapCache());
		mDialog = new ProgressDialog(context);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage("正在添加好友");

		}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return smList.size();
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
		
		convertView = LayoutInflater.from(context).inflate(R.layout.item_my_message, null);
		final Button add_button = (Button)convertView.findViewById(R.id.add);
		TextView message_view = (TextView)convertView.findViewById(R.id.message);
		TextView name_view = (TextView)convertView.findViewById(R.id.name);
		NetworkImageView avatar_view = (NetworkImageView)convertView.findViewById(R.id.avatar);
		avatar_view.setDefaultImageResId(R.drawable.ic_launcher);  
		avatar_view.setErrorImageResId(R.drawable.ic_launcher);  

		sm = smList.get(position);
		String category = sm.getCategory();
		if(!category.equals("add_friend")){
			
//			add_button.setVisibility(IGNORE_ITEM_VIEW_TYPE);
		}
		User user = userList.get(position);
		message_view.setText(sm.getMessage());
		name_view.setText(user.getName());
		String avatar = user.getAvatar();
		if(!avatar.equals("null")){
			avatar_view.setImageUrl(avatar,imageLoader);
			Log.v(TAG,"avatar:"+avatar);
		}
		add_button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long user_id = sm.getUser_id();
				long friend_id = sm.getSender_id();
				int confirm = 1;
				String confirm_url = confirm_friend+"?user_id="+user_id+"&friend_id="+friend_id+"&confirm="+confirm;
				StringRequest confirmRequest = new StringRequest(Method.GET,confirm_url,null,new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						Log.v(TAG,response);
						mDialog.dismiss();
						add_button.setBackgroundColor(00000000);
						add_button.setText("已添加");
						add_button.setClickable(false);
					}
				},new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.v(TAG,error.toString());
					}
				});
				mQueue.add(confirmRequest);
				mDialog.show();
			}
			
		});
		
		return convertView;
	}
	

}
