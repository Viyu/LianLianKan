package com.viyu.alianlk.androidholoicons.fragment;


import android.content.Context;  
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.viyu.alianlk.androidholoicons.MainActivity;
import com.viyu.alianlk.androidholoicons.views.LianlkView;
import com.viyu.alianlk.androidholoicons.views.listener.OnFragmentSelectChangedListener;
import com.viyu.alianlk.androidholoicons.views.listener.OnHelpUsedListener;
import com.viyu.alianlk.androidholoicons.views.listener.OnStateChangedListener;
import com.viyu.alianlk.androidholoicons.views.listener.OnTimingListener;
import com.viyu.alianlk.androidholoicons.R;

/**
 * 
 * @author Viyu_Lu
 * 
 */
public class PlayBoardFragment extends Fragment implements OnClickListener, OnTimingListener, OnStateChangedListener, OnHelpUsedListener, OnFragmentSelectChangedListener {

	private LianlkView lianlkView = null;
	//
	private ProgressBar progress = null;
	private TextView timeLeftTextView = null;

	private Button refreshButton = null;
	private Button hintButton = null;
	private TextView refreshText = null;
	private TextView hintText = null;

	private MainActivity main = null;
	
	private boolean isFragmentInited = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity)getActivity();
		//
		isFragmentInited = false;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_playboard, container, false);
		
		refreshButton = (Button) view.findViewById(R.id.playboard_refreshbutton);
		hintButton = (Button) view.findViewById(R.id.playboard_hintbutton);
		refreshText = (TextView) view.findViewById(R.id.playboard_refreshnum);
		hintText = (TextView) view.findViewById(R.id.playboard_hintnum);
		refreshButton.setOnClickListener(this);
		hintButton.setOnClickListener(this);
		refreshButton.setText(getString(R.string.text_xipai, MainActivity.DEFAULT_XIPAI_COUNT));
		hintButton.setText(getString(R.string.text_tishi, MainActivity.DEFAULT_TISHI_COUNT));
		//
		lianlkView = (LianlkView) view.findViewById(R.id.playboard_lianlkview);
		lianlkView.setOnTimerListener(this);
		lianlkView.setOnStateListener(this);
		lianlkView.setOnToolsChangedListener(this);
		//
		timeLeftTextView = (TextView) view.findViewById(R.id.playboard_timeleftview);
		timeLeftTextView.setText(lianlkView.getTotalTime() + "s");
		//
		progress = (ProgressBar) view.findViewById(R.id.playboard_timeleftprogress);
		progress.setMax(lianlkView.getTotalTime());
		//
		return view;
	}
	
	@Override
	public void onFragmentSelected() {
		lianlkView.buildAndStartPlay(main.getDiffLevel(), main.isGamesoundChecked());
		lianlkView.startAnimation(main.getScaleAnimation());
		isFragmentInited = true;
	}
	
	@Override
	public void onFragmentUnSelected() {
		isFragmentInited = false;
		lianlkView.stopPlay();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(isFragmentInited) {
			lianlkView.stopPlay();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(isFragmentInited) {
			lianlkView.restartPlay();
		}
	}

	@Override
	public void onDestroy() {
		lianlkView.releasePlay();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.playboard_refreshbutton: {
			refreshButton.startAnimation(main.getAnimationShake());
			lianlkView.refreshChange();
			break;
		}
		case R.id.playboard_hintbutton: {
			hintButton.startAnimation(main.getAnimationShake());
			lianlkView.autoClear();
			break;
		}
		}
	}

	final Handler myHandler = new Handler();
	private int leftTime = 0;

	final Runnable myRunnable = new Runnable() {
		public void run() {
			timeLeftTextView.setText(leftTime + "s");
		}
	};

	@Override
	public void onTiming(int leftTime) {
		this.leftTime = leftTime;
		// this is updated in timer thread
		progress.setProgress(leftTime);
		// update view in UI thread
		myHandler.post(myRunnable);
	}

	@Override
	public void onStateChanged(int StateMode) {
		switch (StateMode) {
		case LianlkView.STATE_WIN: {
			// 记录下record
			int record = lianlkView.getTotalTime() - progress.getProgress();
			// 如果成功，记录record
			writeRecord(record);
			//
			main.setResultType(MainActivity.MSG_CODE_WIN);
			main.setTimeCosted(record);
			main.setTabIndex(MainActivity.TAB_INDEX_PLAY, MainActivity.TAB_INDEX_RESULT);
			break;
		}
		case LianlkView.STATE_LOSE: {
			int record = lianlkView.getTotalTime() - progress.getProgress();
			main.setResultType(MainActivity.MSG_CODE_LOSE);
			main.setTimeCosted(record);
			main.setTabIndex(MainActivity.TAB_INDEX_PLAY, MainActivity.TAB_INDEX_RESULT);
			break;
		}
		}
	}
	
	private void writeRecord(int record) {
		SharedPreferences prefers = main.getSharedPreferences(MainActivity.KEY_SHAREDPREFERENCE_RECORDS, Context.MODE_PRIVATE);
		Editor edit = prefers.edit();
		//更新数值
		int old = prefers.getInt(main.getDiffLevel().toString(), -1);
		if (old < 1 || record < old) {
			edit.putInt(main.getDiffLevel().toString(), record);
		}
		edit.commit();
	}

	@Override
	public void onRefreshed(int count) {
		refreshText.setText(String.valueOf(lianlkView.getRefreshNum()));
	}

	@Override
	public void onHinted(int count) {
		hintText.setText(String.valueOf(lianlkView.getTipNum()));
	}
}