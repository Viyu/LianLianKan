package com.viyu.alianlk.androidholoicons.views;

import java.lang.ref.WeakReference; 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.viyu.alianlk.androidholoicons.utils.DiffLevel;
import com.viyu.alianlk.androidholoicons.utils.SoundUtil;
import com.viyu.alianlk.androidholoicons.views.listener.OnHelpUsedListener;
import com.viyu.alianlk.androidholoicons.views.listener.OnStateChangedListener;
import com.viyu.alianlk.androidholoicons.views.listener.OnTimingListener;
import com.viyu.alianlk.androidholoicons.R;

public class LianlkView extends GameBoardView {

	public static final int STATE_WIN = 1;
	public static final int STATE_LOSE = 2;

	private static final int MESSAGE_WHAT_XIPAI = 1;

	private final int ID_SOUND_CHOOSE = 0;
	private final int ID_SOUND_DISAPEAR = 1;
	private final int ID_SOUND_WIN = 4;
	private final int ID_SOUND_LOSE = 5;
	private final int ID_SOUND_REFRESH = 6;
	private final int ID_SOUND_TIP = 7;
	private final int ID_SOUND_ERROR = 8;

	// count of icons
	private int iconWillUseCount = 0;
	
	private int helpLeftCount = 3;
	private int refreshLeftCount = 3;

	private final int totalTime = 100;
	private int leftTime;

	private MediaPlayer backSoundPlayer = null;
	private SoundUtil soundPlay = null;
	private boolean musicChecked = true;

	private int[] iconResIds = null;
	
	private TimeCounter timeCounter;
	private XipaiHandler xipaiHandler = null;

	//把这个变量置为false会持续计时器，置为true会结束计时器
	private boolean isStop = false;

	private OnTimingListener timerListener = null;
	private OnStateChangedListener stateListener = null;
	private OnHelpUsedListener toolsChangedListener = null;

	private List<Point> path = new ArrayList<Point>();

	public LianlkView(Context context, AttributeSet atts) {
		super(context, atts);

		// init once with on object
		soundPlay = new SoundUtil();
		soundPlay.initSounds(context);
		soundPlay.loadSfx(context, R.raw.choose, ID_SOUND_CHOOSE);
		soundPlay.loadSfx(context, R.raw.disappear1, ID_SOUND_DISAPEAR);
		soundPlay.loadSfx(context, R.raw.win, ID_SOUND_WIN);
		soundPlay.loadSfx(context, R.raw.lose, ID_SOUND_LOSE);
		soundPlay.loadSfx(context, R.raw.item1, ID_SOUND_REFRESH);
		soundPlay.loadSfx(context, R.raw.item2, ID_SOUND_TIP);
		soundPlay.loadSfx(context, R.raw.alarm, ID_SOUND_ERROR);
		// 创建背景音乐
		backSoundPlayer = MediaPlayer.create(context, R.raw.back2new);
		backSoundPlayer.setLooping(true);
		//创建bitmap icon res id数组
		iconResIds = new int[23];
		iconResIds[0] = R.drawable.icon01;
		iconResIds[1] = R.drawable.icon02;
		iconResIds[2] = R.drawable.icon03;
		iconResIds[3] = R.drawable.icon04;
		iconResIds[4] = R.drawable.icon05;
		iconResIds[5] = R.drawable.icon06;
		iconResIds[6] = R.drawable.icon07;
		iconResIds[7] = R.drawable.icon08;
		iconResIds[8] = R.drawable.icon09;
		iconResIds[9] = R.drawable.icon10;
		iconResIds[10] = R.drawable.icon11;
		iconResIds[11] = R.drawable.icon12;
		iconResIds[12] = R.drawable.icon13;
		iconResIds[13] = R.drawable.icon14;
		iconResIds[14] = R.drawable.icon15;
		iconResIds[15] = R.drawable.icon16;
		iconResIds[16] = R.drawable.icon17;
		iconResIds[17] = R.drawable.icon18;
		iconResIds[18] = R.drawable.icon19;
		
		iconResIds[19] = R.drawable.icon20;
		iconResIds[20] = R.drawable.icon21;
		iconResIds[21] = R.drawable.icon22;
		iconResIds[22] = R.drawable.icon23;
		//
		xipaiHandler = new XipaiHandler(this);
	}

	/**
	 * 每次开始一次新的前必须调用这个方法来build和start
	 * @param xCount
	 * @param yCount
	 * @param iconWillUseCount 本次将要使用的icon数
	 */
	public void buildAndStartPlay(DiffLevel level, boolean flag) {
		this.musicChecked = flag;
		// 大小和icon数量决定了难度
		this.X_COUNT = level.getxCount();
		this.Y_COUNT = level.getyCount();
		this.iconWillUseCount = level.getIconWillUseCount();
		//
		if(iconWillUseCount > iconResIds.length) {
			iconWillUseCount = iconResIds.length;
		}
		// 初始化matrix
		xYMatrix = new int[X_COUNT][Y_COUNT];
		// 计算icon应该加载的大小
		calcIconSize();
		// 按照iconWillUseCount加载需要icon
		icons = new Bitmap[iconWillUseCount];
		Resources r = getResources();
		for(int i = 0; i < this.iconWillUseCount; i++) {
			loadBitmap(i, r.getDrawable(iconResIds[i]));
		}
		// 重置道具数量
		helpLeftCount = 3;
		refreshLeftCount = 3;
		toolsChangedListener.onRefreshed(refreshLeftCount);
		toolsChangedListener.onHinted(helpLeftCount);
		// 重置时间
		leftTime = totalTime;
		// 初始化matrix map
		initMatrixMap();
		//
		restartPlay();
	}

	/**
	 * 1. 停止->继续 时调用
	 * 2. build之后第一次开始时调用
	 */
	public void restartPlay() {
		//开始计时
		isStop = false;
		timeCounter = new TimeCounter();
		timeCounter.start();
		//播放音乐
		if(musicChecked) {
			backSoundPlayer.seekTo(0);
			backSoundPlayer.start();
		}
	}

	/**
	 * 暂停/结束 游戏时都调用此方法，LianlkView的fragment会在app打开时一直持有，所以即使离开play界面，也不要release
	 */
	public void stopPlay() {
		backSoundPlayer.pause();
		isStop = true;
	}
	
	/**
	 * 游戏退出时调用此方法
	 */
	public void releasePlay() {
		if(backSoundPlayer != null) {
			backSoundPlayer.release();
			backSoundPlayer = null;
		}
		if(icons != null) {
			for(Bitmap icon : icons) {
				icon.recycle();
				icon = null;
			}
			icons = null;
		}
		isStop = true;
		Log.d("LianlkView", "LianlkView released...");
	}

	public void setOnTimerListener(OnTimingListener timerListener) {
		this.timerListener = timerListener;
	}

	public void setOnStateListener(OnStateChangedListener stateListener) {
		this.stateListener = stateListener;
	}

	public void setOnToolsChangedListener(OnHelpUsedListener toolsChangedListener) {
		this.toolsChangedListener = toolsChangedListener;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public int getTipNum() {
		return helpLeftCount;
	}

	public int getRefreshNum() {
		return refreshLeftCount;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		Point p = screenToindex(x, y);
		if (xYMatrix[p.x][p.y] > 0) {
			if (selectedIconsPointList.size() == 1) {
				if (link(selectedIconsPointList.get(0), p)) {
					selectedIconsPointList.add(p);
					drawLine(path.toArray(new Point[] {}));
					soundPlay.play(ID_SOUND_DISAPEAR, 0, musicChecked);
					xipaiHandler.sleep(500);
				} else {
					selectedIconsPointList.clear();
					selectedIconsPointList.add(p);
					soundPlay.play(ID_SOUND_CHOOSE, 0, musicChecked);
					LianlkView.this.invalidate();
				}
			} else {
				selectedIconsPointList.add(p);
				soundPlay.play(ID_SOUND_CHOOSE, 0, musicChecked);
				LianlkView.this.invalidate();
			}
		}
		return super.onTouchEvent(event);
	}

	public void initMatrixMap() {
		int x = 1;
		int y = 0;
		for (int i = 1; i < X_COUNT - 1; i++) {
			for (int j = 1; j < Y_COUNT - 1; j++) {
				xYMatrix[i][j] = x;
				if (y == 1) {
					x++;
					y = 0;
					if (x == iconWillUseCount) {
						x = 1;
					}
				} else {
					y = 1;
				}
			}
		}
		change();
	}

	/**
	 * load bitmap of icons
	 * 
	 * @param key
	 * @param d
	 */
	private void loadBitmap(int key, Drawable d) {
		Bitmap bitmap = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, iconSize, iconSize);
		d.draw(canvas);
		icons[key] = bitmap;
	}

	private void change() {
		Random random = new Random();
		int tmpV, tmpX, tmpY;
		for (int x = 1; x < X_COUNT - 1; x++) {
			for (int y = 1; y < Y_COUNT - 1; y++) {
				tmpX = 1 + random.nextInt(X_COUNT - 2);
				tmpY = 1 + random.nextInt(Y_COUNT - 2);
				tmpV = xYMatrix[x][y];
				xYMatrix[x][y] = xYMatrix[tmpX][tmpY];
				xYMatrix[tmpX][tmpY] = tmpV;
			}
		}
		if (die()) {
			change();
		}
		invalidate();
	}

	/**
	 * calc the size of icon
	 */
	private void calcIconSize() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
		iconSize = dm.widthPixels / (X_COUNT);
		expanWhenSelected = iconSize / 15;// 选中时，周边扩展icon的1/15
		if (expanWhenSelected < 5)
			expanWhenSelected = 5;// 最小为5
	}

	public void setMode(int stateMode) {
		stateListener.onStateChanged(stateMode);
	}

	private boolean die() {
		for (int y = 1; y < Y_COUNT - 1; y++) {
			for (int x = 1; x < X_COUNT - 1; x++) {
				if (xYMatrix[x][y] != 0) {
					for (int j = y; j < Y_COUNT - 1; j++) {
						if (j == y) {
							for (int i = x + 1; i < X_COUNT - 1; i++) {
								if (xYMatrix[i][j] == xYMatrix[x][y] && link(new Point(x, y), new Point(i, j))) {
									return false;
								}
							}
						} else {
							for (int i = 1; i < X_COUNT - 1; i++) {
								if (xYMatrix[i][j] == xYMatrix[x][y] && link(new Point(x, y), new Point(i, j))) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	private List<Point> p1E = new ArrayList<Point>();
	private List<Point> p2E = new ArrayList<Point>();

	private boolean link(Point p1, Point p2) {
		if (p1.equals(p2)) {
			return false;
		}
		path.clear();
		if (xYMatrix[p1.x][p1.y] == xYMatrix[p2.x][p2.y]) {
			if (linkD(p1, p2)) {
				path.add(p1);
				path.add(p2);
				return true;
			}

			Point p = new Point(p1.x, p2.y);
			if (xYMatrix[p.x][p.y] == 0) {
				if (linkD(p1, p) && linkD(p, p2)) {
					path.add(p1);
					path.add(p);
					path.add(p2);
					return true;
				}
			}
			p = new Point(p2.x, p1.y);
			if (xYMatrix[p.x][p.y] == 0) {
				if (linkD(p1, p) && linkD(p, p2)) {
					path.add(p1);
					path.add(p);
					path.add(p2);
					return true;
				}
			}
			expandX(p1, p1E);
			expandX(p2, p2E);

			for (Point pt1 : p1E) {
				for (Point pt2 : p2E) {
					if (pt1.x == pt2.x) {
						if (linkD(pt1, pt2)) {
							path.add(p1);
							path.add(pt1);
							path.add(pt2);
							path.add(p2);
							return true;
						}
					}
				}
			}

			expandY(p1, p1E);
			expandY(p2, p2E);
			for (Point pt1 : p1E) {
				for (Point pt2 : p2E) {
					if (pt1.y == pt2.y) {
						if (linkD(pt1, pt2)) {
							path.add(p1);
							path.add(pt1);
							path.add(pt2);
							path.add(p2);
							return true;
						}
					}
				}
			}
			return false;
		}
		return false;
	}

	private boolean linkD(Point p1, Point p2) {
		if (p1.x == p2.x) {
			int y1 = Math.min(p1.y, p2.y);
			int y2 = Math.max(p1.y, p2.y);
			boolean flag = true;
			for (int y = y1 + 1; y < y2; y++) {
				if (xYMatrix[p1.x][y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		if (p1.y == p2.y) {
			int x1 = Math.min(p1.x, p2.x);
			int x2 = Math.max(p1.x, p2.x);
			boolean flag = true;
			for (int x = x1 + 1; x < x2; x++) {
				if (xYMatrix[x][p1.y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		return false;
	}

	private void expandX(Point p, List<Point> l) {
		l.clear();
		for (int x = p.x + 1; x < X_COUNT; x++) {
			if (xYMatrix[x][p.y] != 0) {
				break;
			}
			l.add(new Point(x, p.y));
		}
		for (int x = p.x - 1; x >= 0; x--) {
			if (xYMatrix[x][p.y] != 0) {
				break;
			}
			l.add(new Point(x, p.y));
		}
	}

	private void expandY(Point p, List<Point> l) {
		l.clear();
		for (int y = p.y + 1; y < Y_COUNT; y++) {
			if (xYMatrix[p.x][y] != 0) {
				break;
			}
			l.add(new Point(p.x, y));
		}
		for (int y = p.y - 1; y >= 0; y--) {
			if (xYMatrix[p.x][y] != 0) {
				break;
			}
			l.add(new Point(p.x, y));
		}
	}

	private boolean win() {
		for (int x = 0; x < X_COUNT; x++) {
			for (int y = 0; y < Y_COUNT; y++) {
				if (xYMatrix[x][y] != 0) {
					return false;
				}
			}
		}
		return true;
	}

	public void autoClear() {
		if (helpLeftCount == 0) {
			soundPlay.play(ID_SOUND_ERROR, 0, musicChecked);
		} else {
			soundPlay.play(ID_SOUND_TIP, 0, musicChecked);
			helpLeftCount--;
			toolsChangedListener.onHinted(helpLeftCount);
			drawLine(path.toArray(new Point[] {}));
			xipaiHandler.sleep(500);
		}
	}

	public void refreshChange() {
		if (refreshLeftCount == 0) {
			soundPlay.play(ID_SOUND_ERROR, 0, musicChecked);
			return;
		} else {
			soundPlay.play(ID_SOUND_REFRESH, 0, musicChecked);
			refreshLeftCount--;
			toolsChangedListener.onRefreshed(refreshLeftCount);
			change();
		}
	}

	private static class XipaiHandler extends Handler {

		private final WeakReference<LianlkView> lianlkViewWeakRef;

		XipaiHandler(LianlkView lianlkView) {
			lianlkViewWeakRef = new WeakReference<LianlkView>(lianlkView);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LianlkView lianlkView = lianlkViewWeakRef.get();
			if (lianlkView != null) {
				if (msg.what == MESSAGE_WHAT_XIPAI) {
					lianlkView.invalidate();
					if (lianlkView.win()) {
						lianlkView.setMode(STATE_WIN);
						lianlkView.soundPlay.play(lianlkView.ID_SOUND_WIN, 0, lianlkView.musicChecked);
						lianlkView.isStop = true;
					} else if (lianlkView.die()) {
						lianlkView.change();
					}
				}
			}
		}

		public void sleep(int delayTime) {
			this.removeMessages(0);
			Message message = new Message();
			message.what = MESSAGE_WHAT_XIPAI;
			sendMessageDelayed(message, delayTime);
		}
	}

	private class TimeCounter extends Thread {

		public void run() {
			while (leftTime >= 0 && !isStop) {
				timerListener.onTiming(leftTime);
				leftTime--;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!isStop) {
				setMode(STATE_LOSE);
				soundPlay.play(ID_SOUND_LOSE, 0, musicChecked);
			}
		}
	}
}
