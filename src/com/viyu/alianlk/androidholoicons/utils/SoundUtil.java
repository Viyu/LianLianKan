package com.viyu.alianlk.androidholoicons.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;
/**
 * 
 * @author Viyu_Lu
 *
 */
public class SoundUtil {

	private int streamVolume;

	private SoundPool soundPool;

	private SparseIntArray soundPoolArray;

	public void initSounds(Context context) {
		soundPool = new SoundPool(25, AudioManager.STREAM_MUSIC, 100);

		soundPoolArray = new SparseIntArray();

		AudioManager mgr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public void loadSfx(Context context, int raw, int ID) {
		soundPoolArray.put(ID, soundPool.load(context, raw, 1));
	}

	public void play(int sound, int uLoop, boolean flag) {
		if(flag) {
			soundPool.play(soundPoolArray.get(sound), streamVolume, streamVolume, 1, uLoop, 1f);
		}
	}
}
