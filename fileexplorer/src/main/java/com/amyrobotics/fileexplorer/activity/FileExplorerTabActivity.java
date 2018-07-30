/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.amyrobotics.fileexplorer.activity;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ActionMode;
import android.widget.Toast;

import com.amyrobotics.fileexplorer.R;
import com.amyrobotics.fileexplorer.utils.Util;

import java.io.File;
import java.util.ArrayList;

import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

public class FileExplorerTabActivity extends FragmentActivity {
    private static final String INSTANCESTATE_TAB = "tab";
    private static final int DEFAULT_OFFSCREEN_PAGES = 3;
    ViewPager mViewPager;
    ActionMode mActionMode;
    TabLayout mTabLayout;

    ArrayList<String> tabs;
    int[] tabRes = {
            R.string.tab_category,
            R.string.tab_sd,
            R.string.tab_remote,
    };
    int tabCount;
    ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager_v2);
        initDatas();
    }

    private void initDatas() {
        mViewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tab_layout);

        tabCount = tabRes.length;
        tabs = new ArrayList<>();
        for (int i = 0; i < tabRes.length; i++) {
            tabs.add(getString(tabRes[i]));
        }

        fragments = new ArrayList<>();
        fragments.add(new FileCategoryActivity());
        fragments.add(new FileViewActivity());
        fragments.add(new ServerControlActivity());

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE) {
//                    if(getCurrentSelectIndex() != Util.SDCARD_TAB_INDEX) {
                        ActionMode actionMode = getActionMode();
                        if (actionMode != null) {
                            actionMode.finish();
                        }
//                    }
                }

            }
        });

        mViewPager.setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGES);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        // 使用 TabLayout 和 ViewPager 相关联
        mTabLayout.setupWithViewPager(mViewPager);

        int lastIndex = PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(INSTANCESTATE_TAB, Util.CATEGORY_TAB_INDEX);
        mViewPager.setCurrentItem(lastIndex);

    }

    Activity getActivity() {
        return this;
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putInt(INSTANCESTATE_TAB, getCurrentSelectIndex());
        editor.commit();
    }

    public int getCurrentSelectIndex() {
        return mViewPager.getCurrentItem();
    }

    public void setCurrentSelectIndex(int index) {
        if(index >= 0 && index < tabCount) {
            mViewPager.setCurrentItem(index);
        }

    }

    /**
     * 跳转到文件view
     * @param location 目标位置
     */
    public void jumpLocationFileView(String location) {
        File file = new File(location);
        if(!file.exists()) {
            Toast.makeText(getActivity(), location + " not exist", Toast.LENGTH_SHORT).show();
            return;
        }
        setCurrentSelectIndex(Util.SDCARD_TAB_INDEX);

        Fragment fragment = getFragment(Util.SDCARD_TAB_INDEX);
        if(fragment instanceof FileViewActivity) {
            ((FileViewActivity)fragment).setPath(location);
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (getCurrentSelectIndex() == Util.CATEGORY_TAB_INDEX) {
            FileCategoryActivity categoryFragement = (FileCategoryActivity) getFragment(Util.CATEGORY_TAB_INDEX);
            if (categoryFragement.isHomePage()) {
                reInstantiateCategoryTab();
            } else {
                categoryFragement.setConfigurationChanged(true);
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    public void reInstantiateCategoryTab() {
//        mTabsAdapter.destroyItem(mViewPager, Util.CATEGORY_TAB_INDEX,
//                mTabsAdapter.getItem(Util.CATEGORY_TAB_INDEX));
//        mTabsAdapter.instantiateItem(mViewPager, Util.CATEGORY_TAB_INDEX);
    }

    @Override
    public void onBackPressed() {
        IBackPressedListener backPressedListener = (IBackPressedListener) getFragment(mViewPager.getCurrentItem());
        if (!backPressedListener.onBack()) {
            super.onBackPressed();
        }

    }

    public interface IBackPressedListener {
        /**
         * 处理back事件。
         * @return True: 表示已经处理; False: 没有处理，让基类处理。
         */
        boolean onBack();
    }

    public void setActionMode(ActionMode actionMode) {
        mActionMode = actionMode;
    }

    public ActionMode getActionMode() {
        return mActionMode;
    }

    public Fragment getFragment(int tabIndex) {
        return fragments.get(tabIndex);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position);
        }
    }

}
