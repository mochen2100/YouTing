package com.example.testvolley;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.example.request.JsonObjectRequest;
import com.example.testvolley.MainActivity;

import de.greenrobot.daoexample.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MyRegisterActivity extends Activity {
	private MyApplication application;
	private RequestQueue mQueue;
    private EditText newUser,newPassword,confirmPassword;
    private Button registerBtn, clearBtn;
    private ProgressDialog mDialog;
    private String responseMsg = "";
    public static boolean regstate=false;
    private SharedPreferences sharepre;
    private final static String TAG = "MyRegisterActivity";
    private final static String register_url = "http://121.42.164.7/index.php/Home/Index/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_register);
        application =  (MyApplication)this.getApplicationContext();
        mQueue = application.getRequestQueue();
        newUser = (EditText)findViewById(R.id.newUser_input);
        newPassword = (EditText)findViewById(R.id.newPassword_input);
        confirmPassword = (EditText)findViewById(R.id.Confirm_input);
        registerBtn = (Button)findViewById(R.id.registerbtn);
        clearBtn = (Button)findViewById(R.id.clearbtn);
        
        sharepre = getSharedPreferences("userdata",0);
        
        
        registerBtn.setOnClickListener(new Button.OnClickListener()
        {

            @Override
            public void onClick(View v) {
            	//��ȡ�û��������롢ȷ������
                String newusername = newUser.getText().toString();
                String newpassword = md5(newPassword.getText().toString());
                String confirmpwd = md5(confirmPassword.getText().toString());
                
                //�������ȷ��������ȣ��������������ע�����󣬷����ӡ������Ϣ
                if(newpassword.equals(confirmpwd))
                {
                    
                    //״̬��ʾ��
                    mDialog = new ProgressDialog(MyRegisterActivity.this);
                    mDialog.setTitle("��½");
                    mDialog.setMessage("���ڵ�½�����������Ժ�...");
                    mDialog.show();
                    JSONObject jObject = new JSONObject();
                    try {
						jObject.put("name", newusername);
						jObject.put("password", newpassword);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                   
                    JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST, register_url, jObject,null,new Response.Listener<JSONObject>() {  
            	        @Override  
            	        public void onResponse(JSONObject j) {
            	        	Log.v(TAG,j.toString());
            	        	String status = null;
							try {
								status = j.getString("status");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
            	        	if(status.equals("success")){
            	        		mDialog.dismiss();
            	        		Intent intent=new Intent(MyRegisterActivity.this,MyLoginActivity.class);
                				startActivity(intent);  
                				MyRegisterActivity.this.finish();
            	        	}
            	        }
            	    },  
            	    new Response.ErrorListener() {  
            	        @Override  
            	        public void onErrorResponse(VolleyError volleyError) {  
            	        	Log.v(TAG,volleyError.toString());
            	        }  
            		});
                    mQueue.add(registerRequest);
                    //����ע�����߳�
 //                   Thread loginThread = new Thread(new RegisterThread());
 //                  loginThread.start();
                }else
                {
                    Toast.makeText(getApplicationContext(), "��������������벻һ�£�", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        clearBtn.setOnClickListener(new Button.OnClickListener()
        {
            //���ð�ť��ձ༭��
            @Override
            public void onClick(View v) {
                newUser.setText("");
                newPassword.setText("");
                confirmPassword.setText("");
            } 
        });
    }
    
    //����ע��״̬
    boolean getRegState(){
    	return regstate;
    }
    
    //��������˽�����֤�������ط�������Ӧ״̬
     private boolean registerServer(String username, String password)
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
     
    //Handler
        Handler handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                Editor editor = sharepre.edit();
                switch(msg.what)
                {
                case 0:
                    mDialog.cancel();
                    Toast.makeText(getApplicationContext(), "ע��ɹ�", Toast.LENGTH_SHORT).show();
                    MyRegisterActivity.regstate=true;
                    editor.putBoolean("regstate", true);   
                    
                    //��ת����½����
    				Intent intent=new Intent(MyRegisterActivity.this,MyLoginActivity.class);
    				startActivity(intent);  
    				MyRegisterActivity.this.finish();
                    break;
                case 1:
                    mDialog.cancel();
                    editor.putBoolean("regstate", false); 
                    Toast.makeText(getApplicationContext(), "ע��ʧ��", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    mDialog.cancel();
                    editor.putBoolean("regstate", false); 
                    Toast.makeText(getApplicationContext(), "URL��֤ʧ��", Toast.LENGTH_SHORT).show();
                    break;
                }
                editor.commit();
            }
        };
        
        
        //RegisterThread�߳���
        class RegisterThread implements Runnable
        {

            @Override
            public void run() {
                String username = newUser.getText().toString();
                String password = md5(newPassword.getText().toString());
                    
                //URL�Ϸ���������һ��������֤�����Ƿ���ȷ
                //boolean registerValidate = registerServer(username, password);
                boolean registerValidate = true;
                Message msg = handler.obtainMessage();
                if(registerValidate)
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
    
    	public void ReturnToMain(View view){  
    		this.finish();
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
