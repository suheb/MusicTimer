package com.suhaib.musictimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.haloappstudio.musictimer.R;
import com.suhaib.musictimer.utils.Utils;

public class TimerFragment extends Fragment {

    private BroadcastReceiver mTimerRecevier;
    private IntentFilter mIntentFilter;
    private TextView mTimerText, mStopText;
    private ProgressBar mTimerBar;
    private Fragment mFragment;
    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_timer, container,
                false);
        mFragment = new StartFragment();
        mStopText = (TextView) rootView.findViewById(R.id.stopText);
        mTimerText = (TextView) rootView.findViewById(R.id.timerText);

        // Custom font
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");
        mStopText.setTypeface(font);
        mTimerText.setTypeface(font);

        mTimerBar = (ProgressBar) rootView.findViewById(R.id.timerBar);
        mTimerBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTimerText.setTextColor(getResources().getColor(
                            R.color.bloodred));
                    mStopText.setTextColor(getResources().getColor(
                            R.color.bloodred));
                } else {
                    mTimerText.setTextColor(Color.BLACK);
                    mStopText.setTextColor(Color.BLACK);
                }
                return false;
            }
        });
        mTimerBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                    Log.d("MAIN", "INTERSTITIAL AD SHOWN");
                } else {
                    Log.d("MAIN", "INTERSTITIAL AD NOT SHOWN");
                    stopTimerService();
                }
            }
        });
        mTimerRecevier = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                int currentTime = (int) intent.getLongExtra("currentTime", 0);
                int maxTime = (int) intent.getLongExtra("maxTime", 0);
                String formattedTime = intent.getStringExtra("formattedTime");
                mTimerText.setText(formattedTime);
                mTimerBar.setMax(maxTime);
                mTimerBar.setProgress(currentTime);
                if (intent.getBooleanExtra("timer_flag", false)
                        || !TimerService.SERVICE_FLAG) {
                    Utils.changeFragment(mFragment, getActivity());
                }
            }
        };

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TimerService.SERVICE_FLAG) {
            Utils.changeFragment(mFragment, getActivity());
        }
        mIntentFilter = new IntentFilter(Utils.ACTION_UPDATE_TIMER);
        getActivity().registerReceiver(mTimerRecevier, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mTimerRecevier);
    }

    private void stopTimerService() {
        Intent serviceIntent = new Intent(getActivity(),
                TimerService.class);
        if (getActivity().stopService(serviceIntent) == true) {
            TimerService.SERVICE_FLAG = false;
        }
        Utils.changeFragment(mFragment, getActivity());
    }
}
