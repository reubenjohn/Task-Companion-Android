package com.aspirephile.studytimer.welcome;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class WelcomePageAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;

	public WelcomePageAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int id) {
		return this.fragments.get(id);
	}

	@Override
	public int getCount() {
		return this.fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Your static title";
	}
}
