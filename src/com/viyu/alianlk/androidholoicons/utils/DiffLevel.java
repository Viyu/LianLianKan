package com.viyu.alianlk.androidholoicons.utils;

public enum DiffLevel {

	Level1(1, 8, 9, 15), 
	Level2(2, 9, 10, 16), 
	Level3(3, 10, 11, 17), 
	Level4(4, 11, 12, 18), 
	Level5(5, 11, 12, 19), 
	Level6(6, 12, 13, 20), 
	Level7(7, 12, 13, 21), 
	Level8(8, 13, 14, 22), 
	Level9(9, 13, 14, 23);

	private static final String KEY_SHAREDPREFERENCE_RECORDS_PREFIX = "RecordLevel_";
	
	private int level = 1;
	private int xCount = 8;
	private int yCount = 9;
	private int iconWillUseCount = 6;

	private DiffLevel(int level, int xCount, int yCount, int iconWillUseCount) {
		this.level = level;
		this.xCount = xCount;
		this.yCount = yCount;
		this.iconWillUseCount = iconWillUseCount;
	}

	public int getLevel() {
		return level;
	}

	public int getxCount() {
		return xCount;
	}

	public int getyCount() {
		return yCount;
	}

	public int getIconWillUseCount() {
		return iconWillUseCount;
	}
	
	@Override
	public String toString() {
		return KEY_SHAREDPREFERENCE_RECORDS_PREFIX + String.valueOf(level);
	}
	
	public DiffLevel nextDiffLevel() {
		return getDiffLevel(level % 9);//循环
	}

	public static DiffLevel getDiffLevel(int index) {
		switch(index) {
		case 0: return Level1;
		case 1: return Level2;
		case 2: return Level3;
		case 3: return Level4;
		case 4: return Level5;
		case 5: return Level6;
		case 6: return Level7;
		case 7: return Level8;
		case 8: return Level9;
		}
		return Level1;
	}
}
