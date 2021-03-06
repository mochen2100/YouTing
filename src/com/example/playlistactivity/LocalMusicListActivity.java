package com.example.playlistactivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.service.PlayerService;
import com.example.testvolley.MyApplication;
import com.example.testvolley.R;

import de.greenrobot.daoexample.Music;


public class LocalMusicListActivity extends ListActivity {

	private ListView listView;
	private static MyListAdapter listAdapter;
	public static Context context;
	private MyApplication myApplication;
	private PlayerService playerservice;
	private ClearEditText mClearEditText;
	private CharacterParser characterParser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localmusic_list_layout);
		getWindow().setBackgroundDrawable(null);
		listView = (ListView) findViewById(android.R.id.list);
		mClearEditText = (ClearEditText)findViewById(R.id.filter_edit);
		characterParser = CharacterParser.getInstance();
		
		context = this;
		myApplication = (MyApplication)this.getApplicationContext();
		playerservice = myApplication.getService();
		listAdapter = new MyListAdapter(this, myApplication.getLocalMusicList());
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Music music = myApplication.getLocalMusicList().get(position);
				
				if (music.equals(playerservice.getMusic()) && playerservice.isPlayFlag()) {
					Toast.makeText(context, "正在播放...", 0).show();
				}else {
						playerservice.playSong(music);
						System.out.println("TAG!!!!!!!!,sizeofplaylist"+playerservice.getPlayList().size());
						System.out.println("TAG!!!!!!!!,sizeofmymusiclist"+myApplication.getMyMusicList().size());
		
				}
			}
		});
		
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
				System.out.println("addTextChangedListener word"  );
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
			
		});
	}
	
	private void filterData(String filterStr){
		ArrayList<Music> filterDateList = new ArrayList<Music>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = myApplication.getLocalMusicList();
		}else{
			filterDateList.clear();
			for(Music Music : myApplication.getLocalMusicList()){
				String name = Music.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(Music);
				}
			}
		}
		
		// 根据a-z进行排序
		//Collections.sort(filterDateList, pinyinComparator);
		//listAdapter.updateListView(filterDateList);
		listAdapter = new MyListAdapter(this, filterDateList);
		listView.setAdapter(listAdapter);
		System.out.println("filterData word"  );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "播放全部");
		menu.add(0, 1, 1, "更新");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case 0:
		//	PlayUtils.turnToPlay_List(context, MyApplication.getLocalMusicList());
			break;
		case 1:
			new AllMusic_List_asyncTask(listAdapter).execute();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public class MyListAdapter extends BaseAdapter {
		private class buttonViewHolder {
			TextView musicName;
			ImageButton add;
			ImageButton collect;
		}

		private ArrayList<Music> musicList;
		private LayoutInflater mInflater;
		private Context mContext;
		private buttonViewHolder holder;
		
/*		public void updateListView(ArrayList<Music> list){
			this.musicList =  (ArrayList<Music>)list.clone();
			System.out.println("updateListView word" + musicList.size() );
			listAdapter.notifyDataSetChanged();
		}	*/	
		public MyListAdapter(Context context, List<Music> appList) {
			this.musicList =  (ArrayList<Music>)appList;
			mContext = context;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			return this.musicList.size();
		}

		public Object getItem(int position) {
			return this.musicList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public void removeItem(int position) {
			this.musicList.remove(position);
			this.notifyDataSetChanged();
		} 

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView != null) {
				holder = (buttonViewHolder) convertView.getTag();
			} else {
				convertView = mInflater.inflate(R.layout.localmusic_list_item_layout, null);
				holder = new buttonViewHolder();
				holder.musicName = (TextView) convertView.findViewById(R.id.text_musicName);
				holder.add = (ImageButton) convertView.findViewById(R.id.button_add);
				holder.collect = (ImageButton) convertView.findViewById(R.id.button_collect);
				convertView.setTag(holder);
			}

			Music itemInfo = musicList.get(position);

			if (itemInfo != null) {
				String aname = (String) itemInfo.getName();
				holder.musicName.setText(aname);
				holder.add.setOnClickListener(new addOnClickListener(position));
				holder.collect.setOnClickListener(new collectOnClickListener(position));
			}
			return convertView;
		}

		class collectOnClickListener implements OnClickListener {

			int position;

			public collectOnClickListener(int position) {
				this.position = position;
			}

			
			public void onClick(View v) {
				Music map = myApplication.getLocalMusicList().get(position);
				//boolean b;
				myApplication.addToMyMusicList(map);
				MyFavoriteListActivity.myListAdapter.notifyDataSetChanged();
			}
		}

		class addOnClickListener implements OnClickListener {

			private int position;

			public addOnClickListener(int position) {
				this.position = position;
			}

			//这里是要分享的，现在这部分还没做好
			public void onClick(View v) {
				Music map = myApplication.getLocalMusicList().get(position);
				FriendsMusicListActivity.myListAdapter.notifyDataSetChanged();
			}
		}

	}
	
	

	public class AllMusic_List_asyncTask extends AsyncTask<String, Void, Void> {

		private ProgressDialog progressDialog;
		private MyListAdapter listAdapter;

		public AllMusic_List_asyncTask(MyListAdapter listAdapter) {
			this.listAdapter = listAdapter;
			progressDialog = new ProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("扫描所有音乐...");
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			return null;			
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

		@Override
		protected void onPostExecute(Void result) {
			listAdapter.notifyDataSetChanged();
			progressDialog.dismiss();
		}
	}
}
