package com.example.testvolley;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MySearchActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_search);
		// ��ȡ��Activity�����TabHost���
		TabHost tabHost = getTabHost();
		// ������һ��Tabҳ
		TabSpec tab1 = tabHost.newTabSpec("tab1")
			.setIndicator("����") // ���ñ���
			.setContent(R.id.tab01); //��������
		// ��ӵ�һ����ǩҳ
		tabHost.addTab(tab1);
		TabSpec tab2 = tabHost.newTabSpec("tab2")
			// �ڱ�ǩ�����Ϸ���ͼ��
			.setIndicator("�û�")
			.setContent(R.id.tab02);
		// ��ӵڶ�����ǩҳ
		tabHost.addTab(tab2);
	}
	
	public void ReturnToMain(View view){  
		//Intent intent=new Intent(MySearchActivity.this,MainActivity.class);
		//intent.putExtra(EXTRA_MESSAGE, "startaaa");
		//startActivity(intent);  
		finish();
	}
}

