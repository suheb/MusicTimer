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
import android.widget.ShareActionProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.haloappstudio.musictimer.R;
import com.suhaib.musictimer.utils.Utils;

public class MainActivity extends FragmentActivity {

    private Fragment mFragment;
    private AdView mAdView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar actionBar = getActionBar();
            actionBar.setIcon(R.drawable.ic_action_bar);
        }
        MobileAds.initialize(getApplicationContext(), getString(R.string.admob_app_id));

        // Create banner ad
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(getString(R.string.oneplus3_device_id))
                .build();
        mAdView.loadAd(adRequest);

        showFragment(savedInstanceState);

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

    private void showFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            // Create a new Fragment to be placed in the activity layout
            if (TimerService.mServiceFlag)
                mFragment = new TimerFragment();
            else
                mFragment = new StartFragment();

            Utils.changeFragment(mFragment, this);
        }
    }
}