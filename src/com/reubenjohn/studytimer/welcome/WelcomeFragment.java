package com.reubenjohn.studytimer.welcome;

import java.util.List;
import java.util.Vector;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reubenjohn.studytimer.R;
import com.reubenjohn.studytimer.session.setup.SessionSetup;
import com.reubenjohn.studytimer.session.setup.SessionSetupFragment;

public class WelcomeFragment extends Fragment implements OnPageChangeListener {

	private WelcomePageAdapter welcomePageAdapter;
	ViewPager welcomePager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_welcome, container, false);
		bridgeXML(v);
		initializeFeilds();
		return v;
	}

	private void bridgeXML(View v) {
		welcomePager = (ViewPager) v.findViewById(R.id.vp_welcome);
	}

	private void initializeFeilds() {
		initializePaging();
	}

	private void initializePaging() {
		if (Build.VERSION.SDK_INT >= 11)
			welcomePager.setPageTransformer(true, new WelcomePageTransformer());
		else {
			// TODO Create alternate page transformers for lower APIs
		}
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(getActivity(),
				WelcomePage1Fragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				WelcomePage2Fragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				WelcomePage3Fragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				SessionSetupFragment.class.getName()));
		welcomePageAdapter = new WelcomePageAdapter(getActivity()
				.getSupportFragmentManager(), fragments);

		welcomePager.setAdapter(welcomePageAdapter);
		welcomePager.setOnPageChangeListener(this);
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		Log.d(SessionSetupFragment.class.getName(), "OnPageChangelistener set");
		welcomePager.setOnPageChangeListener(listener);

	}

	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 2:
			break;

		default:
			Log.d(SessionSetup.class.getName(), "No action for selected page: "
					+ position);
			break;
		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {

	}

	@Override
	public void onPageScrollStateChanged(int state) {
		switch (state) {
		case 3:
			break;

		default:
			Log.d(SessionSetup.class.getName(), "No action for page state: "
					+ state);
			break;
		}
	}
}
