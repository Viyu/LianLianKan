package com.viyu.alianlk.androidholoicons.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.viyu.alianlk.androidholoicons.MainActivity;
import com.viyu.alianlk.androidholoicons.R;
import com.viyu.alianlk.androidholoicons.views.listener.OnFragmentSelectChangedListener;

/**
 * 
 * @author Viyu_Lu
 * 
 */
public class StartBoardFragment extends Fragment implements OnClickListener, OnFragmentSelectChangedListener, OnCheckedChangeListener {

	private Button startButton = null;
	
	private CheckBox backSoundCheck = null;
	private CheckBox gameSoundCheck = null;
	
	private MainActivity main = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_startboard, container, false);
		//
		startButton = (Button) view.findViewById(R.id.startboard_startplay);
		startButton.setOnClickListener(this);
		//
		backSoundCheck = (CheckBox) view.findViewById(R.id.startboard_backsoundcheck);
		backSoundCheck.setChecked(main.isBacksoundChecked());
		gameSoundCheck = (CheckBox) view.findViewById(R.id.startboard_gamesoundscheck);
		gameSoundCheck.setChecked(main.isGamesoundChecked());
		//
		backSoundCheck.setOnCheckedChangeListener(this);
		gameSoundCheck.setOnCheckedChangeListener(this);

		backSoundCheck.startAnimation(main.getScaleAnimation());
		gameSoundCheck.startAnimation(main.getScaleAnimation());
		startButton.startAnimation(main.getScaleAnimation());
		return view;
	}

	@Override
	public void onFragmentSelected() {
		startButton.startAnimation(main.getScaleAnimation());
	}

	@Override
	public void onFragmentUnSelected() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startboard_startplay:
			main.setTabIndex(MainActivity.TAB_INDEX_START, MainActivity.TAB_INDEX_DIFF);
			break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.startboard_backsoundcheck: {
			main.setBacksoundChecked(isChecked);
			break;
		}
		case R.id.startboard_gamesoundscheck: {
			main.setGamesoundChecked(isChecked);
			break;
		}
		}
	}
}