package com.example.easypdfmerge;

import java.io.File;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;


public class MainActivity extends FragmentActivity {

	ViewPager viewPager=null;
	String dirName = "/storage/sdcard0/MergedFiles";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		File folder = new File(dirName);
		if(!folder.isDirectory())
			folder.mkdir();
		viewPager = (ViewPager) findViewById(R.id.pager);
		FragmentManager fragmanager = getSupportFragmentManager();
		viewPager.setAdapter(new MyAdapter(fragmanager));
	}
	
	public void setCurrentItem (int item, boolean smoothScroll) {
	    viewPager.setCurrentItem(item, smoothScroll);
	}
}



class MyAdapter extends FragmentStatePagerAdapter {

	public MyAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int i) {
		Fragment frag = null;
		if(i == 0) {
			frag = new Merge();
		}
		if(i == 1) {
			frag = new Split();
		}
		if(i == 2) {
			frag = new Result();
		}
		return frag;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		if(position == 0)
		{
			return "MERGE";
		}
		if(position == 1)
		{
			return "SPLIT";
		}
		if(position == 2)
		{
			return "RESULT";
		}
		return null;
	}
	
}