package com.viyu.alianlk.androidholoicons;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.viyu.alianlk.androidholoicons.fragment.DiffBoardFragment;
import com.viyu.alianlk.androidholoicons.fragment.PlayBoardFragment;
import com.viyu.alianlk.androidholoicons.fragment.ResultBoardFragment;
import com.viyu.alianlk.androidholoicons.fragment.StartBoardFragment;
import com.viyu.alianlk.androidholoicons.utils.DiffLevel;
import com.viyu.alianlk.androidholoicons.views.listener.OnFragmentSelectChangedListener;

/**
 * 
 * @author Viyu_Lu
 * 
 */
public class MainActivity extends FragmentActivity {

	public static final String KEY_SHAREDPREFERENCE_RECORDS = "Records";
	public static final String KEY_SHAREDPREFERENCE_GAMESETTINGS = "GameSettings";
	public static final String KEY_SHAREDPREFERENCE_GAMESETTINGS_BACKSOUNDCHECK = "GameSettings_BacksoundCheck";
	public static final String KEY_SHAREDPREFERENCE_GAMESETTINGS_GAMESOUNDCHECK = "GameSettings_GamesoundCheck";

	public static final int TAB_INDEX_START = 0;
	public static final int TAB_INDEX_DIFF = 1;
	public static final int TAB_INDEX_PLAY = 2;
	public static final int TAB_INDEX_RESULT = 3;

	public static final int DEFAULT_XIPAI_COUNT = 3;
	public static final int DEFAULT_TISHI_COUNT = 3;

	public static final int MSG_CODE_WIN = 0;
	public static final int MSG_CODE_LOSE = 1;

	public static final int CHECK_ON = 100;
	public static final int CHECK_OFF = 101;
	
	// fragment之间的通信变量
	private DiffLevel diffLevel = DiffLevel.Level1;
	private int resultType = MSG_CODE_LOSE;
	private int timeCosted = 0;

	private ViewPager viewPager = null;

	private OnFragmentSelectChangedListener start = null;
	private OnFragmentSelectChangedListener diff = null;
	private OnFragmentSelectChangedListener play = null;
	private OnFragmentSelectChangedListener result = null;

	// 动画
	private Animation animationShake = null;
	// Animation animationTransIn = null;
	private Animation scaleAnimation = null;
	//
	private MediaPlayer mainBackSoundPlayer = null;

	private boolean isBacksoundChecked = true;
	private boolean isGamesoundChecked = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		setContentView(R.layout.activity_main);
		//
		SharedPreferences prefers = getSharedPreferences(KEY_SHAREDPREFERENCE_GAMESETTINGS, Context.MODE_PRIVATE);
		isBacksoundChecked = prefers.getInt(KEY_SHAREDPREFERENCE_GAMESETTINGS_BACKSOUNDCHECK, CHECK_ON) == CHECK_ON ? true : false;
		isGamesoundChecked = prefers.getInt(KEY_SHAREDPREFERENCE_GAMESETTINGS_GAMESOUNDCHECK, CHECK_ON) == CHECK_ON ? true : false;
		//
		viewPager = (ViewPager) findViewById(R.id.main_viewpager);
		viewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager()));
		viewPager.setOffscreenPageLimit(4);

		animationShake = AnimationUtils.loadAnimation(this, R.anim.shake);
		scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
		//
		mainBackSoundPlayer = MediaPlayer.create(this, R.raw.bg);
		mainBackSoundPlayer.setLooping(true);
	}

	public void setDiffLevel(DiffLevel type) {
		diffLevel = type;
	}

	public DiffLevel getDiffLevel() {
		return diffLevel;
	}

	public void setResultType(int type) {
		resultType = type;
	}

	public int getResultType() {
		return resultType;
	}

	public void setTimeCosted(int time) {
		timeCosted = time;
	}

	public int getTimeCosted() {
		return timeCosted;
	}

	public void setTabIndex(int oldIndex, int newIndex) {
		if (oldIndex == newIndex)
			return;

		// 离开fragment处理现场
		switch (oldIndex) {
		case TAB_INDEX_START: {
			start.onFragmentUnSelected();
			break;
		}
		case TAB_INDEX_DIFF: {
			diff.onFragmentUnSelected();
			break;
		}
		case TAB_INDEX_PLAY: {
			play.onFragmentUnSelected();
			// 开始播放main音乐
			if(isBacksoundChecked) {
				mainBackSoundPlayer.start();
			}
			break;
		}
		case TAB_INDEX_RESULT: {
			result.onFragmentUnSelected();
			break;
		}
		}
		// 进入fragment准备现场
		switch (newIndex) {
		case TAB_INDEX_START: {
			start.onFragmentSelected();
			break;
		}
		case TAB_INDEX_DIFF: {
			diff.onFragmentSelected();
			break;
		}
		case TAB_INDEX_PLAY: {
			// 停止main音乐
			mainBackSoundPlayer.pause();
			play.onFragmentSelected();
			break;
		}
		case TAB_INDEX_RESULT: {
			result.onFragmentSelected();
			break;
		}
		default: {
			start.onFragmentSelected();
			break;
		}
		}
		viewPager.setCurrentItem(newIndex);
	}

	public void setBacksoundChecked(boolean flag) {
		isBacksoundChecked = flag;
		if(isBacksoundChecked) {
			mainBackSoundPlayer.start();
		} else {
			mainBackSoundPlayer.pause();
		}
	}
	
	public void setGamesoundChecked(boolean flag) {
		this.isGamesoundChecked = flag;
	}
	
	public boolean isBacksoundChecked() {
		return this.isBacksoundChecked;
	}
	
	public boolean isGamesoundChecked() {
		return this.isGamesoundChecked;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(mainBackSoundPlayer != null) {
			mainBackSoundPlayer.pause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (viewPager.getCurrentItem() == TAB_INDEX_PLAY) {

		} else {
			if(isBacksoundChecked) {
				mainBackSoundPlayer.start();
			}
		}
	}

	@Override
	public void onBackPressed() {
		switch (viewPager.getCurrentItem()) {
		case TAB_INDEX_START: {// 在start page退出程序
			//
			clearApp();
			//
			finish();
			//
			/*
			 * ConfirmDialog confirm = new ConfirmDialog(this,
			 * getString(R.string.text_quitapp_confirm)) {
			 * 
			 * @Override protected void onYesClicked() { finish(); } };
			 * confirm.show();
			 */
			break;
		}
		case TAB_INDEX_DIFF: {// 回到start
			setTabIndex(TAB_INDEX_DIFF, TAB_INDEX_START);
			break;
		}
		case TAB_INDEX_PLAY: {// 回到diff
			setTabIndex(TAB_INDEX_PLAY, TAB_INDEX_DIFF);
			break;
		}
		case TAB_INDEX_RESULT: {// 回到diff
			setTabIndex(TAB_INDEX_RESULT, TAB_INDEX_DIFF);
			break;
		}
		}
	}

	private void clearApp() {
		if (mainBackSoundPlayer != null) {
			mainBackSoundPlayer.release();
			mainBackSoundPlayer = null;
		}
		SharedPreferences prefers = getSharedPreferences(KEY_SHAREDPREFERENCE_GAMESETTINGS, Context.MODE_PRIVATE);
		Editor edit = prefers.edit();
		edit.putInt(KEY_SHAREDPREFERENCE_GAMESETTINGS_BACKSOUNDCHECK, isBacksoundChecked ? CHECK_ON : CHECK_OFF);
		edit.putInt(KEY_SHAREDPREFERENCE_GAMESETTINGS_GAMESOUNDCHECK, isGamesoundChecked ? CHECK_ON : CHECK_OFF);
		edit.commit();
	}
	
	@Override
	protected void onDestroy() {
		clearApp();
		//
		super.onDestroy();
	}

	public Animation getAnimationShake() {
		return animationShake;
	}

	public Animation getScaleAnimation() {
		return scaleAnimation;
	}

	class MainFragmentPagerAdapter extends FragmentPagerAdapter {
		private int tabCount = 4;
		private List<Fragment> fragmentList = null;

		public MainFragmentPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
			fragmentList = new ArrayList<Fragment>(tabCount);
			start = new StartBoardFragment();
			diff = new DiffBoardFragment();
			play = new PlayBoardFragment();
			result = new ResultBoardFragment();

			fragmentList.add((Fragment) start);
			fragmentList.add((Fragment) diff);
			fragmentList.add((Fragment) play);
			fragmentList.add((Fragment) result);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return tabCount;
		}
	}
}