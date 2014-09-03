package com.reubenjohn.studytimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.session_complete);

		bridgeXML();
		initializeFeilds();
		setOnClickListeners();
	}

	protected void bridgeXML() {
		bStartNew = (Button) findViewById(R.id.b_new_session);
		bRepeat = (Button) findViewById(R.id.b_repeat_session);
	}

	protected void initializeFeilds() {
	}

	private void setOnClickListeners() {
		bStartNew.setOnClickListener(this);
		bRepeat.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		Intent result=new Intent();
		switch(view.getId()){
		case R.id.b_new_session:
		case R.id.b_repeat_session:
			result.putExtra(StudyTimer.keys.extras.session_complete_proceedings, view.getId());
			setResult(RESULT_OK, result);
			break;
		default:
			setResult(RESULT_CANCELED);
		}
		finish();
	}

}
