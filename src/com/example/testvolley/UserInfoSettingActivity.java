package com.example.testvolley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageRequest;
import com.example.request.JsonObjectRequest;
import com.example.request.StringRequest;
import com.example.testvolley.MainActivity;
import com.example.myview.Tools;
import com.example.myview.Tools.*;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import de.greenrobot.daoexample.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserInfoSettingActivity extends Activity {
	private ListView lv_userinfo;
	private Button iv_return;
	private ImageView faceImage;
	private ProgressDialog mDialog;
	private MyApplication application;
	private SharedPreferences sp;
	private RequestQueue mQueue;
	private String token,key;
	private User user;
	private Drawable drawable;
	private final String TAG = "UserInfoSettingActivity";
	private final String token_url = "http://121.42.164.7/index.php/Home/index/getToken";
	private final String changeAvatar_url = "http://121.42.164.7/index.php/Home/index/change_avatar";
	private final String qiniu_url = "http://7xi2lw.com1.z0.glb.clouddn.com/";
	private String[] items = new String[] { "ѡ�񱾵�ͼƬ", "����" };
	Map<String,String> uploadParams = new HashMap<String,String>();
	/* ͷ������ */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* ������ */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info_setting);
//		initViews();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v(TAG,"onresume");
		initViews();
	}

	private void initViews() {
		application =(MyApplication) this.getApplicationContext();
		mQueue = application.getRequestQueue();
		sp = getSharedPreferences("youting",MODE_PRIVATE);		
		user = application.getLoginUser();
		String temp_name="�˻����� "+user.getName();
		String temp_mood="����ǩ�� "+user.getMood(); 
		String mood = user.getMood();
		Log.v(TAG,mood);
		String avatar = user.getAvatar();
		iv_return = (Button) findViewById(R.id.my_userinfo_return);
		faceImage = (ImageView) findViewById(R.id.iv_face);
		if(mood.equals("null")){
			temp_mood = "����ǩ�� "+"~��������~";
		}
		if(avatar.equals("null")){
			ImageRequest avatarRequest = new ImageRequest(avatar,new Response.Listener<Bitmap>() {

				@Override
				public void onResponse(Bitmap response) {
					// TODO Auto-generated method stub
					faceImage.setImageBitmap(response);
				}
			},300,200,Config.ARGB_8888,new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					// TODO Auto-generated method stub
					
				}
			});
			mQueue.add(avatarRequest);
		}


		lv_userinfo = (ListView) findViewById(R.id.lv_userinfo);
		lv_userinfo.setAdapter(new ArrayAdapter<String>(UserInfoSettingActivity.this,
				R.layout.userinfo_basic, new String[] { temp_name, temp_mood,"����ͷ��"}));
		
		//List��ͬitem�ĵ����Ӧ����
		lv_userinfo.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				switch(position){
				case 0:
					break;
				case 1:
	                Intent intent = new Intent();
	                intent.setClass(UserInfoSettingActivity.this, MyMoodActivity.class);
	                startActivity(intent);
//	                finish();
	                break;
				case 2:
					showDialog();
				}
			}
		});
		
		iv_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MainActivity.getMainActivityCallBack().setUserInfo();
                finish();
                
			}
		});
	}
	
	/**
	 * ��ʾѡ��Ի���
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("����ͷ��")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // �����ļ�����
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:

							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// �жϴ洢���Ƿ�����ã����ý��д洢
							if (Tools.hasSdcard()) {

								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(Environment
												.getExternalStorageDirectory(),
												IMAGE_FILE_NAME)));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//����벻����ȡ��ʱ��
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (Tools.hasSdcard()) {
					File tempFile = new File(
							Environment.getExternalStorageDirectory()
									+ IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(UserInfoSettingActivity.this, "δ�ҵ��洢�����޷��洢��Ƭ��",
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					//
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * �ü�ͼƬ����ʵ��
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// ���òü�
		intent.putExtra("crop", "true");
		// aspectX aspectY �ǿ�ߵı���
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY �ǲü�ͼƬ���
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 2);
	}

	/**
	 * ����ü�֮���ͼƬ����
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			drawable = new BitmapDrawable(photo);
			byte[] img = Bitmap2Bytes(photo);
			
			mDialog = new ProgressDialog(UserInfoSettingActivity.this);
			mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setMessage("�����ϴ�ͼƬ");
            mDialog.show();
			uploadPic(img);
			//�ɹ��ϴ���������
			Log.v(TAG,"succ set");

			/** ���浽SD�� */
//			  File f = new File("/sdcard/DCIM/", "zzzzz");
//			  if (f.exists()) {
//			   f.delete();
//			  }
//			  try {
//			   FileOutputStream out = new FileOutputStream(f);
//			   photo.compress(Bitmap.CompressFormat.PNG, 90, out);
//			   out.flush();
//			   out.close();
//			  } catch (FileNotFoundException e) {
//			   e.printStackTrace();
//			  } catch (IOException e) {
//			   e.printStackTrace();
//			  }
		}
	}
	//�ϴ�ͼƬ���ƶ�
	public void uploadPic(byte[] i){
		final byte[] img = i;
		key = "avatar-"+user.getUid()+"v0.png";
		if(user.getAvatar() != "null"){
			int start = user.getAvatar().lastIndexOf("v")+1;
			int end = user.getAvatar().lastIndexOf(".");
			int version = Integer.parseInt(user.getAvatar().substring(start, end))+1;
			Log.v(TAG,"version:"+version);
			key = "avatar-"+user.getUid()+"v"+version+".png";
		}
//		uploadParams.put("x:scope", "youting:"+key);
//		uploadParams.put("x:deadline", ""+3600);
//		uploadParams.put("x:mimeLimit", "image/jpeg");
		
		Log.v(TAG,uploadParams.toString());
		//��ȡtoken
		StringRequest tokenRequest = new StringRequest(Method.POST,token_url,null,new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				// TODO Auto-generated method stub
				token = response;
				Log.v(TAG,token);
				//upload
				UploadManager uploadManager = new UploadManager();
				uploadManager.put(img, key, token,
				new UpCompletionHandler() {
					@Override
					public void complete(String arg0, ResponseInfo arg1,
							JSONObject arg2) {
						// �������ݿ�
						Log.v(TAG,"upload succ"+arg0+"response:"+arg1);
						String avatar = qiniu_url+key;
						user.setAvatar(avatar);
						application.setLoginUser(user);
						changeAvatar();
						}					
				}, new UploadOptions(null, null, false,
				        new UpProgressHandler(){
		            public void progress(String key, double percent){
		            	int progress = (int)percent*100;
		            	mDialog.setProgress(progress);
		                Log.i("qiniu", key + ": " + percent);
		            }
		        }, null));
			}
		},new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				Log.v(TAG,error.toString());
			}
		});
		mQueue.add(tokenRequest);
	}
	public void changeAvatar(){
		JSONObject jObject = new JSONObject();
		try {
			jObject.put("avatar", qiniu_url+key);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonObjectRequest changeAvatar = new JsonObjectRequest(Method.POST,changeAvatar_url,jObject,null,new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				String status = null;
				try {
					status = response.getString("status");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(status.equals("success")){
					mDialog.dismiss();
					
					faceImage.setImageDrawable(drawable);
					Toast.makeText(getApplicationContext(), "�޸�ͷ��ɹ�", Toast.LENGTH_SHORT).show();
				}
			}
		},new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				
			}
		});
		mQueue.add(changeAvatar);
	}
	private byte[] Bitmap2Bytes(Bitmap bm){  
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();   
	    bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
	    return baos.toByteArray();  
	}  

}


