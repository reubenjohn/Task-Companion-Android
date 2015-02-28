package com.aspirephile.taskcompanion.session;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.taskcompanion.R;
import com.aspirephile.taskcompanion.TaskCompanion;
import com.aspirephile.taskcompanion.util.SystemUiHider;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SessionComplete extends ActionBarActivity implements
        OnClickListener {
    Logger l = new Logger(SessionComplete.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    Button bStartNew, bRepeat;
    Toast invalidBackError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.session_complete);

        bridgeXML();
        initializeFields();
        setOnClickListeners();
    }

    @Override
    public void onBackPressed() {
        if (asserter.assertPointer(invalidBackError))
            invalidBackError.show();
    }

    protected void bridgeXML() {
        bStartNew = (Button) findViewById(R.id.b_new_session);
        bRepeat = (Button) findViewById(R.id.b_repeat_session);
    }

    @SuppressLint("ShowToast")
    protected void initializeFields() {
        invalidBackError = Toast.makeText(SessionComplete.this,
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
                        TaskCompanion.keys.extras.session_complete_proceedings,
                        view.getId());
                setResult(RESULT_OK, result);
                break;
            default:
                setResult(RESULT_CANCELED);
        }
        finish();
    }

}
