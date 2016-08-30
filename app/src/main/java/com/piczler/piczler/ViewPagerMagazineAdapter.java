package com.piczler.piczler;

/**
 * Created by pk on 4/22/15.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by hp1 on 21-01-2015.
 */
public class ViewPagerMagazineAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    Context c;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerMagazineAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb, Context c) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
        this.c=c;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            MagazinePiczlerFragment tab1 = new MagazinePiczlerFragment();
            return tab1;
        }
        else if(position == 1)           // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            MagazineFacebookFragment tab2 = new MagazineFacebookFragment();
            return tab2;
        }else{
            MagazineInstagramFragment tab2 = new MagazineInstagramFragment();
            return tab2;
        }


    }

    // This method return the titles for the Tabs in the Tab Strip

    /*
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }
    */

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
