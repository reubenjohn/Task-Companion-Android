package com.reubenjohn.studytimer.welcome;

import java.util.List;
import java.util.Vector;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.reubenjohn.studytimer.R;

public class WelcomeFragment extends Fragment {

	private WelcomePageAdapter welcomePageAdapter;
	ViewPager welcomePager;
	ViewPager viewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_welcome, container, false);
		bridgeXML(v);
		initializeFeilds();
		return v;
	}

	private void bridgeXML(View v) {
		viewPager = (ViewPager) v.findViewById(R.id.vp_welcome);
	}

	private void initializeFeilds() {
		initializePaging();
	}

	private void initializePaging() {
		if(Build.VERSION.SDK_INT>=11)
			viewPager.setPageTransformer(true, new WelcomePageTransformer());
		else{
			// TODO Create alternate page transformers for lower APIs
		}
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(getActivity(),
				WelcomePage1Fragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				WelcomePage2Fragment.class.getName()));
		fragments.add(Fragment.instantiate(getActivity(),
				WelcomePage3Fragment.class.getName()));
		welcomePageAdapter = new WelcomePageAdapter(getActivity()
				.getSupportFragmentManager(), fragments);

		viewPager.setAdapter(welcomePageAdapter);
	}
}
