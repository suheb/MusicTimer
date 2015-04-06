package com.suhaib.musictimer;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.haloappstudio.musictimer.R;
import com.suhaib.musictimer.utils.Utils;

public class MainActivity extends FragmentActivity {
	
	private final String AD_UNIT_ID = "ca-app-pub-1187028475051204/8008072575";
    private Fragment mFragment;
    private AdView mAdView;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        ActionBar actionBar = getActionBar();
	        actionBar.setIcon(R.drawable.ic_action_bar);
		}
	    
		// Create an ad.
	    mAdView = new AdView(this);
	    mAdView.setAdSize(AdSize.SMART_BANNER);
	    mAdView.setAdUnitId(AD_UNIT_ID);

	    // Add the AdView to the view hierarchy. The view will have no size
	    // until the ad is loaded.
	    LinearLayout layout = (LinearLayout) findViewById(R.id.adLayout);
	    layout.addView(mAdView);

	    // Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    AdRequest adRequest = new AdRequest.Builder()
	    					.addTestDevice("FB5DE11AEA131AC62B8978FB62009488")
	    					.build();

	    // Start loading the ad in the background.
	    mAdView.loadAd(adRequest);

		if (findViewById(R.id.fragment_container) != null) {
			if (savedInstanceState != null) {
	            return;
	        }
			
	        // Create a new Fragment to be placed in the activity layout
            if (TimerService.SERVICE_FLAG)
                mFragment = new TimerFragment();
			else
				mFragment = new StartFragment();
			
			Utils.changeFragment(mFragment, this);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			MenuItem actionItem = menu.findItem(R.id.action_share);
			ShareActionProvider actionProvider = (ShareActionProvider) actionItem
					.getActionProvider();
			actionProvider.setShareIntent(createShareIntent());
		}

		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_settings) {
			DialogFragment newFragment = new SelectorDialogFragment();
		    newFragment.show(getSupportFragmentManager(), "Music Player");
		}
			
		return true;
	}
	public Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Music Timer");
		shareIntent
				.putExtra(Intent.EXTRA_TEXT,
						"https://play.google.com/store/apps/details?id=com.haloappstudio.musictimer");
		return shareIntent;
	}
}