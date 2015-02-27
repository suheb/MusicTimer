package com.suhaib.musictimer.utils;

import android.graphics.drawable.Drawable;

public class ListViewItem {
	private String mTitle;
	private Drawable mIcon;

	public ListViewItem() {
	}

	public ListViewItem(String mTitle, Drawable mIcon) {
		this.mTitle = mTitle;
		this.mIcon = mIcon;
	}

	public String getmTitle() {
		return this.mTitle;
	}

	public Drawable getmIcon() {
		return this.mIcon;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public void setmIcon(Drawable mIcon) {
		this.mIcon = mIcon;
	}

}
