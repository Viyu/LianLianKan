package com.viyu.alianlk.androidholoicons.views;

import android.app.Dialog; 
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.viyu.alianlk.androidholoicons.R;

public class ConfirmDialog extends Dialog {

	private String message = null;

	public ConfirmDialog(Context context, String message) {
		super(context, R.style.DialogTheme);
		this.message = message;
		this.setCancelable(false);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirm);

		TextView messageTextView = (TextView) findViewById(R.id.dialog_confirm_message);
		messageTextView.setText(message);
		//
		Button buttonYes = (Button) findViewById(R.id.dialog_confirm_ok);
		buttonYes.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				onYesClicked();
				dismiss();
			}
		});
		Button buttonNo = (Button) findViewById(R.id.dialog_confirm_cancel);
		buttonNo.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		dismiss();
	}
	
	protected void onYesClicked() {

	}
}