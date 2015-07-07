package io.github.scarletsky.bangumi.ui.fragments;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import io.github.scarletsky.bangumi.BangumiApplication;
import io.github.scarletsky.bangumi.R;
import io.github.scarletsky.bangumi.events.ClickNavigateIconEvent;
import io.github.scarletsky.bangumi.events.GetSubjectEvent;
import io.github.scarletsky.bangumi.events.SetToolbarEvent;
import io.github.scarletsky.bangumi.utils.BusProvider;
import io.github.scarletsky.bangumi.utils.SessionManager;

/**
 * Created by scarlex on 15-7-2.
 */
public class DrawerFragment extends Fragment implements OnNavigationItemSelectedListener {

    private static final String TAG = DrawerFragment.class.getSimpleName();
    private static final String TAG_CALENDAR = CalendarFragment.class.getSimpleName();
    private static final String TAG_SUBJECT_DETAIL = SubjectDetailFragment.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private SessionManager mSession = BangumiApplication.getInstance().getSession();

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.drawer_main, new BaseToolbarFragment())
                .replace(R.id.frame_base_toolbar_content, new LoginFragment())
                .commit();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragmentManager = getActivity().getSupportFragmentManager();

        mDrawerLayout = (DrawerLayout) getView().findViewById(R.id.drawer_wrapper);
        mNavigationView = (NavigationView) getView().findViewById(R.id.drawer_nav);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.menu_calendar:

                if (mFragmentManager.findFragmentByTag(TAG_CALENDAR) == null) {
                    mFragmentManager
                            .beginTransaction()
                            .add(R.id.frame_base_toolbar_content, new CalendarFragment(), TAG_CALENDAR)
                            .commit();
                    closeDrawer();
                }
                break;
            case R.id.menu_settings:
                Log.d(TAG, "clickkkk setttttting");
                break;
            case R.id.menu_logout:
                mSession.logout();
                break;
        }

        return false;
    }

    @Produce
    public SetToolbarEvent produceSetToolbarEvent() {
        return new SetToolbarEvent(getString(R.string.app_name), ClickNavigateIconEvent.NavigateIconType.MENU);
    }

    @Subscribe
    public void onClickNavigateIconEvent(ClickNavigateIconEvent event) {
        switch (event.getIconType()) {
            case MENU:
                openDrawer();
                break;
            case BACK:
                break;
        }
    }


    @Subscribe
    public void onGetSubjectIdEvent(GetSubjectEvent event) {
        Log.d(TAG, String.valueOf(event.getSubject()));

        mFragmentManager
                .beginTransaction()
                .add(R.id.drawer_main, new ImageToolbarFragment(event.getSubject()), TAG_SUBJECT_DETAIL)
                .addToBackStack(TAG_SUBJECT_DETAIL)
                .commit();
    }

    private void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    private void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }
}
