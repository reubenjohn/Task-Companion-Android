package com.reubenjohn.studytimer.session.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.reubenjohn.studytimer.R;

public class SessionSetup extends ActionBarActivity {

	SessionSetupFragment sessionSetupF;

	ActionMode sessionCreateActionMode;
	private ActionMode.Callback sessionCreateActionModeCallBack = new ActionMode.Callback() {

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Intent result = new Intent();
			setResult(RESULT_CANCELED, result);
			finish();
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.context_create_session, menu);
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem menu) {
			Log.d("StudyTimer", "Action item clicked");
			Intent result = new Intent();
			Bundle sessionInfo = sessionSetupF.getSessionParams()
					.getBundledSessionParams();
			result.putExtras(sessionInfo);
			setResult(RESULT_OK, result);
			finish();
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.session_setup);

		initializeFeilds();

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fl_session_setup, sessionSetupF).commit();
		}
	}

	private void initializeFeilds() {
		sessionSetupF = new SessionSetupFragment();

		if (sessionCreateActionMode == null) {
			sessionCreateActionMode = SessionSetup.this
					.startSupportActionMode(sessionCreateActionModeCallBack);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.session_setup, menu);
		return true;
	}

}
