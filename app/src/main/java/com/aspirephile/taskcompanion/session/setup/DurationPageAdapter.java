package com.aspirephile.taskcompanion.session.setup;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class DurationPageAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;
	String titles[] = null;

	public void setPageTitles(String... titles) {
		this.titles = titles;
	}

	public DurationPageAdapter(FragmentManager fm, List<Fragment> fragments) {
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
		if (titles != null) {
			if (titles[position] != null)
				return titles[position];
		}
		return "";
	}
}
