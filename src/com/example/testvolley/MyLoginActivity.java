package com.example.testvolley;

import java.security.MessageDigest;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.request.JsonObjectRequest;
import com.example.testvolley.MainActivity;

import de.greenrobot.daoexample.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MyLoginActivity extends Activity {
    /** Called when the activity is first created. */
    private Button loginBtn;
    private Button registerBtn;
    private EditText inputUsername;
    private EditText inputPassword;
    private CheckBox saveInfoItem;
    private ProgressDialog mDialog;
    private String responseMsg = "";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String username,password,status,uid;
    private MyApplication application;
    private RequestQueue mQueue;
    
    private final static String TAG = "MyLoginActivity";
	private final static String login_url = "http://121.42.164.7/index.php/Home/Index/login";
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);
        
        application = (MyApplication)this.getApplicationContext();
        mQueue = application.getRequestQueue();
        
        loginBtn = (Button)findViewById(R.id.login_btn_login);
        registerBtn = (Button)findViewById(R.id.login_btn_zhuce);
        inputUsername = (EditText)findViewById(R.id.login_edit_account);
        inputPassword = (EditText)findViewById(R.id.login_edit_pwd);
        saveInfoItem = (CheckBox)findViewById(R.id.login_cb_savepwd);
        
		sp = getSharedPreferences("youting",MODE_PRIVATE);	
		editor = sp.edit();
        //��ʼ������
        LoadUserdata();

        //�������
        CheckNetworkState();

       //������ס����ѡ��
        saveInfoItem.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()  
        {  
            @Override  
            public void onCheckedChanged(CompoundButton buttonView,  boolean isChecked) {  
                //�����û���Ϣ
                
                if(saveInfoItem.isChecked())
                {
                     //��ȡ�Ѿ����ڵ��û���������
                    String realUsername = sp.getString("username", "");
                    String realPassword = sp.getString("password", "");
                    editor.putBoolean("checkstatus", true);
                    editor.commit();
                     
                     if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))
                     {
                        //��������
                        inputUsername.setText("");
                          inputPassword.setText("");
                          //��������ֵ
                         inputUsername.setText(realUsername);
                         inputPassword.setText(realPassword);
                     }
                }else
                {
                    editor.putBoolean("checkstatus", false);
                    editor.commit();
                    //��������
                    inputUsername.setText("");
                     inputPassword.setText("");
                }
            }  
        });  
        
        //��¼
        loginBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
            	//��ʾ��¼״̬��
                mDialog = new ProgressDialog(MyLoginActivity.this);
                mDialog.setTitle("��½");
                mDialog.setMessage("���ڵ�½�����������Ժ�...");
                mDialog.show();
                
                username = inputUsername.getText().toString();
                password = md5(inputPassword.getText().toString());
                
                //�������
                
                JSONObject jObject = new JSONObject();
                try {
					jObject.put("name", username);
					jObject.put("password", password);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
                
        		JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.POST, login_url, jObject,null,new Response.Listener<JSONObject>() {  
        	        @Override  
        	        public void onResponse(JSONObject j) {  
        	        	editor.putString("user", j.toString());
        	        	Log.v(TAG+"json",j.toString());

        	        	try {
        					status = j.getString("status");										
        				} catch (JSONException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        	        	if (status.equals("success")){
        	        		// ��½�ɹ���
        	        		// ע��jpush��alias
        	        		editor.putBoolean("login", true);
        	        		editor.commit();
        	        		try {
        						uid = j.getString("uid");
        						String name = j.getString("name");
        						String mood = j.getString("mood");
        						String sex = j.getString("sex");
        						String avatar = j.getString("avatar");
        						User user = new User(Long.parseLong(uid),name,sex,mood,avatar);
        						editor.putString("USER_NAME", name);
        						editor.putString("PASSWORD", j.getString("password"));
        						editor.commit();
        						application.setLoginUser(user);
        						application.setIsLogin(true);
 //       						Log.v(TAG+"login",application.getLoginUser().getAvatar());
        					} catch (JSONException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}	        		
        	        		WelcomeActivity.getWelcomeActivityCallBack().setAlias(uid);
        	        		WelcomeActivity.getWelcomeActivityCallBack().setFriendList(uid);
        	        		WelcomeActivity.getWelcomeActivityCallBack().setMyMusic(uid);
        	        		//WelcomeActivity.getWelcomeActivityCallBack().setFriendMusic(uid);
        		        	new Handler().postDelayed(new Runnable(){    
        	                    public void run() {    
                	        		mDialog.dismiss();
                	        		Intent intent=new Intent();
                	        	    intent.setClass(MyLoginActivity.this, MainActivity.class);
                	        	    startActivity(intent); 
        	                    }    
        	                 }, 2000);

       	     
        	        	}else{
        	        		Log.v(TAG,"log fail");
        	        		editor.putBoolean("login", false);
        	        		editor.commit();
        	        		Toast.makeText(getApplicationContext(), "�û��������벻ƥ��", Toast.LENGTH_LONG).show();

        	        	}
        	        }
        	    },  
        	    new Response.ErrorListener() {  
        	        @Override  
        	        public void onErrorResponse(VolleyError volleyError) {  
        	        	String sb = volleyError.toString(); 
        	        	Log.v(TAG+"login",sb);
        	        	Toast.makeText(getApplicationContext(), sb, Toast.LENGTH_SHORT).show();
//        	        	WelcomeActivity.getWelcomeActivityCallBack().setMyMusic(null);
//        	        	WelcomeActivity.getWelcomeActivityCallBack().setFriendMusic(null);
        	        }  
        		});
                mQueue.add(jRequest);
                //�����¼�߳�
//                Thread loginThread = new Thread(new LoginThread());
//                loginThread.start();
            }
        });
        
        //�����ע��������ע�����
        registerBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(MyLoginActivity.this, MyRegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    
    //��������˽�����֤�������ط�������Ӧ״̬
    private boolean loginServer(String username, String password)
    {
        boolean loginValidate = false;
        
        /*            //����û���������

        try
        {
            //�������������
            
            //�ж��Ƿ�����ɹ�
            if()
            {
                loginValidate = true;
                //�����Ӧ��Ϣ
                responseMsg = "";
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }*/
        
        return loginValidate;
    }
    
    //��ʼ���û�����
    private void LoadUserdata()
    {
        boolean checkstatus = sp.getBoolean("checkstatus", false);
        //����ѡ�˼�ס���룬�������Ѵ���û��������룬������ʾ����
        if(checkstatus)
        {
            saveInfoItem.setChecked(true);
            //�����û���Ϣ
             //��ȡ�Ѿ����ڵ��û���������
            String realUsername = sp.getString("username", "");
            String realPassword = sp.getString("password", "");
            if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))
            {
                inputUsername.setText("");
                inputPassword.setText("");
                inputUsername.setText(realUsername);
                inputPassword.setText(realPassword);
            }    
        }else
        {
            saveInfoItem.setChecked(false);
            inputUsername.setText("");
            inputPassword.setText("");
        }
    }
    
    //�������״̬
    public void CheckNetworkState()
    {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(
                Context.CONNECTIVITY_SERVICE);
        State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        //���3G��wifi��2G������״̬�����ӵģ����˳���������ʾ��ʾ��Ϣ�����������ý���
        if(mobile == State.CONNECTED||mobile==State.CONNECTING)
        return;
        if(wifi == State.CONNECTED||wifi==State.CONNECTING)
        return;
        showTips();
    }
    
    private void showTips()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle("û�п�������");
        builder.setMessage("��ǰ���粻���ã��Ƿ��������磿");
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ���û���������ӣ�������������ý���
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                MyLoginActivity.this.finish();
            }
        });
        builder.create();
        builder.show();
    }
    
    
    //Handler
    Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch(msg.what)
            {
            case 0:
                mDialog.cancel();
                Toast.makeText(getApplicationContext(), "��¼�ɹ���", Toast.LENGTH_SHORT).show();
                //��¼�ɹ���ת��������
                Intent intent = new Intent();
                intent.setClass(MyLoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
            case 1:
                mDialog.cancel();
                Toast.makeText(getApplicationContext(), "�������", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                mDialog.cancel();
                Toast.makeText(getApplicationContext(), "URL��֤ʧ��", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
    //LoginThread�߳���
    class LoginThread implements Runnable
    {
        @Override
        public void run() {
            String username = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();    
            boolean checkstatus = sp.getBoolean("checkstatus", false);
            if(checkstatus)
            {
                 //��ȡ�Ѿ����ڵ��û���������
                String realUsername = sp.getString("username", "");
                String realPassword = sp.getString("password", "");
                if((!realUsername.equals(""))&&!(realUsername==null)||(!realPassword.equals(""))||!(realPassword==null))
                {
                    if(username.equals(realUsername)&&password.equals(realPassword))
                    {
                        username = inputUsername.getText().toString();
                        password = inputPassword.getText().toString();
                    }
                }
            }else
            {
                password = md5(password);
            }
            System.out.println("username="+username+":password="+password);
                
            //URL�Ϸ���������һ��������֤�����Ƿ���ȷ
            //boolean loginValidate = loginServer(username, password);
            boolean loginValidate = true;
            System.out.println("----------------------------bool is :"+loginValidate+"----------response:"+responseMsg);
            Message msg = handler.obtainMessage();
            if(loginValidate)
            {
                if(responseMsg.equals("success"))
                {
                    msg.what = 0;
                    handler.sendMessage(msg);
                }else
                {
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            }else
            {
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }  
    }
    
    
    /**
     * MD5������ܣ�32λ�����ڼ������룬��Ϊ�����������ŵ��д��䲻��ȫ�����ı����ڱ���Ҳ����ȫ  
     * @param str
     * @return
     */
    public static String md5(String str)  
    {  
        MessageDigest md5 = null;  
        try  
        {  
            md5 = MessageDigest.getInstance("MD5");  
        }catch(Exception e)  
        {  
            e.printStackTrace();  
            return "";  
        }  
          
        char[] charArray = str.toCharArray();  
        byte[] byteArray = new byte[charArray.length];  
          
        for(int i = 0; i < charArray.length; i++)  
        {  
            byteArray[i] = (byte)charArray[i];  
        }  
        byte[] md5Bytes = md5.digest(byteArray);  
          
        StringBuffer hexValue = new StringBuffer();  
        for( int i = 0; i < md5Bytes.length; i++)  
        {  
            int val = ((int)md5Bytes[i])&0xff;  
            if(val < 16)  
            {  
                hexValue.append("0");  
            }  
            hexValue.append(Integer.toHexString(val));  
        }  
        return hexValue.toString();  
    }  
   
    
}

