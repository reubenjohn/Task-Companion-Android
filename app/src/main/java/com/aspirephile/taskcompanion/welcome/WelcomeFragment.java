package com.aspirephile.taskcompanion.welcome;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.taskcompanion.R;
import com.aspirephile.taskcompanion.StudyTimer;
import com.aspirephile.taskcompanion.session.setup.SessionSetup;
import com.aspirephile.taskcompanion.session.setup.SessionSetupFragment;

import java.util.List;
import java.util.Vector;

public class WelcomeFragment extends Fragment implements OnPageChangeListener {
    Logger l = new Logger(WelcomeFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);
    ViewPager welcomePager;
    private WelcomePageAdapter welcomePageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        l.onCreateView();
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        bridgeXML(v);
        initializeFields();
        return v;
    }

    private void bridgeXML(View v) {
        l.bridgeXML();
        welcomePager = (ViewPager) v.findViewById(R.id.vp_welcome);
        l.bridgeXML(asserter.assertPointer(welcomePager));
    }

    private void initializeFields() {
        l.initializeFields();
        initializePaging();
    }

    private void initializePaging() {
        if (Build.VERSION.SDK_INT >= 11)
            welcomePager.setPageTransformer(true, new WelcomePageTransformer());
        // else TODO Create alternate page transformers for lower APIs
        List<Fragment> fragments = new Vector<>();
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
        l.d("OnPageChangelistener set");
        welcomePager.setOnPageChangeListener(listener);

    }

    @Override
    public void onPageSelected(int position) {
        Intent intent;
        switch (position) {
            case 3:
                intent = new Intent(getActivity(), SessionSetup.class);
                intent.putExtra(StudyTimer.keys.extras.first_run, true);
                getActivity().startActivityForResult(intent,
                        StudyTimer.codes.request.sessionSetup);
                break;

            default:
                l.d("No action for selected page: "
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
                l.d("No action for page state: "
                        + state);
                break;
        }
    }

}
