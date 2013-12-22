package com.viyu.alianlk.androidholoicons.fragment;

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
import com.viyu.alianlk.androidholoicons.views.listener.OnFragmentSelectChangedListener;
import com.viyu.alianlk.androidholoicons.R;

/**
 * 
 * @author Viyu_Lu
 * 
 */
public class ResultBoardFragment extends Fragment implements OnClickListener, OnFragmentSelectChangedListener {
	private TextView resultText = null;
	private TextView messageText = null;
	
	private Button replayButton = null;
	private Button nextButton = null;

	private MainActivity main = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//
		main = (MainActivity) getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_resultboard, container, false);
		resultText = (TextView) view.findViewById(R.id.resultboard_resulttext);
		messageText = (TextView) view.findViewById(R.id.resultboard_message);
		
		replayButton = (Button) view.findViewById(R.id.resultboard_replay);
		replayButton.setOnClickListener(this);
		
		nextButton = (Button)view.findViewById(R.id.resultboard_next);
		nextButton.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onFragmentSelected() {
		Animation animation = main.getScaleAnimation();
		//刷新结果
		if (main.getResultType() == MainActivity.MSG_CODE_WIN) {
			resultText.setText(getString(R.string.text_winner));
			nextButton.setVisibility(View.VISIBLE);
			nextButton.startAnimation(animation);
			
		} else /*if (main.getResultType() == MainActivity.MSG_CODE_LOSE)*/ {
			resultText.setText(getString(R.string.text_loser));
			nextButton.setVisibility(View.INVISIBLE);
			
		}
		messageText.setText(getString(R.string.text_timecosted, main.getTimeCosted()));
		//
		resultText.startAnimation(animation);
		messageText.startAnimation(animation);
		replayButton.startAnimation(animation);
	}
	
	@Override
	public void onFragmentUnSelected() {
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.resultboard_replay: {
			main.setTabIndex(MainActivity.TAB_INDEX_RESULT, MainActivity.TAB_INDEX_PLAY);
			break;
		}
		case R.id.resultboard_next: {
			main.setDiffLevel(main.getDiffLevel().nextDiffLevel());
			main.setTabIndex(MainActivity.TAB_INDEX_RESULT, MainActivity.TAB_INDEX_PLAY);
			break;
		}
		}
	}
}