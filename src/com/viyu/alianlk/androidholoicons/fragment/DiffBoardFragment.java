package com.viyu.alianlk.androidholoicons.fragment;

import java.util.ArrayList; 
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.viyu.alianlk.androidholoicons.MainActivity;
import com.viyu.alianlk.androidholoicons.utils.DiffLevel;
import com.viyu.alianlk.androidholoicons.views.listener.OnFragmentSelectChangedListener;
import com.viyu.alianlk.androidholoicons.R;

/**
 * 
 * @author Viyu_Lu
 * 
 */
public class DiffBoardFragment extends Fragment implements OnClickListener, OnFragmentSelectChangedListener {

	private MainActivity main = null;

	private List<Button> diffButtons = null;
	private List<TextView> diffTexts = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_diffboard, container, false);
		//
		diffButtons = new ArrayList<Button>(DiffLevel.values().length);
		diffButtons.add((Button) view.findViewById(R.id.diff_button_11));
		diffButtons.add((Button) view.findViewById(R.id.diff_button_12));
		diffButtons.add((Button) view.findViewById(R.id.diff_button_13));
		diffButtons.add((Button) view.findViewById(R.id.diff_button_21));
		diffButtons.add((Button) view.findViewById(R.id.diff_button_22));
		diffButtons.add((Button) view.findViewById(R.id.diff_button_23));
		diffButtons.add((Button) view.findViewById(R.id.diff_button_31));
		diffButtons.add((Button) view.findViewById(R.id.diff_button_32));
		diffButtons.add((Button) view.findViewById(R.id.diff_button_33));
		for (Button btn : diffButtons) {
			btn.setOnClickListener(this);
		}
		//
		diffTexts = new ArrayList<TextView>(DiffLevel.values().length);
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_11));
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_12));
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_13));
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_21));
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_22));
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_23));
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_31));
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_32));
		diffTexts.add((TextView) view.findViewById(R.id.diff_text_33));
		//
		return view;
	}

	@Override
	public void onFragmentSelected() {
		loadRecords();
	}

	@Override
	public void onFragmentUnSelected() {
	}

	@Override
	public void onClick(View v) {
		main.setDiffLevel(getLevel(v.getId()));
		main.setTabIndex(MainActivity.TAB_INDEX_DIFF, MainActivity.TAB_INDEX_PLAY);
	}

	private DiffLevel getLevel(int selectedId) {
		for (int i = 0; i < diffButtons.size(); i++) {
			if (diffButtons.get(i).getId() == selectedId) {
				return DiffLevel.getDiffLevel(i);
			}
		}
		return DiffLevel.Level1;
	}

	private void loadRecords() {
		Animation scaleAnimation = main.getScaleAnimation();
		// 刷新记录信息
		SharedPreferences prefers = main.getSharedPreferences(MainActivity.KEY_SHAREDPREFERENCE_RECORDS, Context.MODE_PRIVATE);
		int current = -1;
		for (int i = 0; i < diffButtons.size(); i++) {
			Button btn = diffButtons.get(i);
			TextView text = diffTexts.get(i);
			
			int record = prefers.getInt(DiffLevel.getDiffLevel(i).toString(), -1);
			if (record > 0) {// 有记录
				btn.setEnabled(true);
				text.setText(getString(R.string.text_record, record));
			} else {// 没记录的
				if (current == -1) {
					current = i;
				}
				//
				btn.setEnabled(false);
				text.setText(R.string.text_notplayed);
			}
			btn.setText(String.valueOf(i + 1));
			btn.startAnimation(scaleAnimation);
			text.startAnimation(scaleAnimation);
			
			//TODO for test
			//btn.setEnabled(true);
		}
		// 当前available的
		diffButtons.get(current).setEnabled(true);
		diffTexts.get(current).setText(R.string.text_notfinish);
	}
}