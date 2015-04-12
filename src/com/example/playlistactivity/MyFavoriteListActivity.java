package com.example.playlistactivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.testvolley.R;
import com.example.testvolley.MyApplication;

import de.greenrobot.daoexample.Music;
/*import com.huazi.Mp3Player.R;
import com.huazi.Mp3Player.Utils.DataUtils;
import com.huazi.Mp3Player.Utils.MyApplication;
import com.huazi.Mp3Player.Utils.PlayUtils;*/

public class MyFavoriteListActivity extends Activity {

	public static ListView playMusicList;
	public static MyListAdapter myListAdapter;
	public static Context context;
	private MyApplication myApplication;
	private PlayerService playerservice;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfavoritemusic_list_layout);
		context = this;
		myApplication = (MyApplication)this.getApplicationContext();
		playerservice = myApplication.getService();
		playMusicList = (ListView) findViewById(R.id.myfavorite_music_list);

		myListAdapter = new MyListAdapter(this, myApplication.getMyMusicList());

		playMusicList.setAdapter(myListAdapter);

		playMusicList.setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Music music = myApplication.getMyMusicList().get(position);
				if (music.equals(playerservice.getMusic()) && playerservice.isPlayFlag()) {
					Toast.makeText(context, "正在播放...", 0).show();
				}else {
					playerservice.playSong(music);
				}
				
			}
		});
	}
	
	@Override
	protected void onRestart() {
		myListAdapter.notifyDataSetChanged();
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		myListAdapter.notifyDataSetChanged();
		myListAdapter.notifyDataSetInvalidated();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, R.string.text_deletePlayListMusics);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 0) {
			new emptyPlayMusicListTask(myListAdapter).execute();
		}
		return super.onOptionsItemSelected(item);
	}

	public class emptyPlayMusicListTask extends AsyncTask<Void, Void, Void> {

		MyListAdapter myListAdapter;

		public emptyPlayMusicListTask(MyListAdapter myListAdapter) {
			this.myListAdapter = myListAdapter;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			myApplication.getMyMusicList().clear();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			myListAdapter.notifyDataSetChanged();
			playerservice.pause();
			MainActivity.footer.setText("正在播放的歌曲");
			super.onPostExecute(result);
		}

	}

	public class MyListAdapter extends BaseAdapter {
		private class buttonViewHolder {
			TextView musicName;
			ImageButton delete;
		}

		private ArrayList<Music> musicList;
		private LayoutInflater mInflater;
		private Context mContext;
		private int[] valueViewID;
		private buttonViewHolder holder;

		public MyListAdapter(Context context, List<Music> appList) {
			musicList = (ArrayList<Music>) appList;
			mContext = context;
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		
		public int getCount() {
			return musicList.size();
		}

		
		public Object getItem(int position) {
			return musicList.get(position);
		}

		
		public long getItemId(int position) {
			return position;
		}
 
		public void removeItem(int position) {
			playerservice.playNext();
			musicList.remove(position);
			this.notifyDataSetChanged();
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView != null) {
				holder = (buttonViewHolder) convertView.getTag();
			} else {
				convertView = mInflater.inflate(R.layout.myfavoritemusic_list_item_layout, null);
				holder = new buttonViewHolder();
				holder.musicName = (TextView) convertView.findViewById(R.id.myfavorite_music_item_name);
				holder.delete = (ImageButton) convertView.findViewById(R.id.myfavorite_music_button_delete);
				convertView.setTag(holder);
			}

			Music itemInfo = myApplication.getMyMusicList().get(position);

			if (itemInfo != null) {
				String aname = (String) itemInfo.getName();
				holder.musicName.setText(aname);
				holder.delete.setOnClickListener(new deleOnClickListener(position));
			}
			return convertView;
		}

		class deleOnClickListener implements OnClickListener {

			private int position;

			public deleOnClickListener(int position) {
				this.position = position;
			}

		
			public void onClick(View v) {

				removeItem(position);
			}
		}

	}
}

