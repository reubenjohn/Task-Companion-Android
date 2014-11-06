package com.reubenjohn.studytimer.session;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.reubenjohn.studytimer.R;
import com.reubenjohn.studytimer.StudyTimer;
import com.reubenjohn.studytimer.R.id;
import com.reubenjohn.studytimer.R.layout;
import com.reubenjohn.studytimer.R.string;
import com.reubenjohn.studytimer.StudyTimer.keys;
import com.reubenjohn.studytimer.StudyTimer.keys.extras;
import com.reubenjohn.studytimer.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SessionComplete extends ActionBarActivity implements
		OnClickListener {

	Button bStartNew, bRepeat;
	Toast invalid_back_error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.session_complete);

		bridgeXML();
		initializeFeilds();
		setOnClickListeners();
	}

	@Override
	public void onBackPressed() {
		if (invalid_back_error != null)
			invalid_back_error.show();
	}

	protected void bridgeXML() {
		bStartNew = (Button) findViewById(R.id.b_new_session);
		bRepeat = (Button) findViewById(R.id.b_repeat_session);
	}

	protected void initializeFeilds() {
		invalid_back_error = Toast.makeText(SessionComplete.this,
				R.string.session_complete_back_invalid_message, Toast.LENGTH_SHORT);
	}

	private void setOnClickListeners() {
		bStartNew.setOnClickListener(this);
		bRepeat.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		Intent result = new Intent();
		switch (view.getId()) {
		case R.id.b_new_session:
		case R.id.b_repeat_session:
			result.putExtra(
					StudyTimer.keys.extras.session_complete_proceedings,
					view.getId());
			setResult(RESULT_OK, result);
			break;
		default:
			setResult(RESULT_CANCELED);
		}
		finish();
	}

}
