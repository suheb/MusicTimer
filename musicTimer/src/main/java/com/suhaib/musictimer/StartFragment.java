package com.suhaib.musictimer;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.haloappstudio.musictimer.R;
import com.suhaib.musictimer.utils.Utils;

import net.simonvt.numberpicker.NumberPicker;

public class StartFragment extends Fragment {

    private SeekBar mSeekBar;
    private NumberPicker mHourPicker, mMinutePicker, mSecondPicker;
    private Button mStartButton, mResetButton;
    private CheckBox mCheckBox;
    private InterstitialAd mInterstitialAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_start, container,
                false);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Light.ttf");

        // Hour picker
        mHourPicker = (NumberPicker) rootView.findViewById(R.id.hourPicker);
        mHourPicker.setTypeface(font);
        mHourPicker.setMaxValue(12);
        mHourPicker.setMinValue(0);
        mHourPicker.setFocusable(true);
        mHourPicker.setFocusableInTouchMode(true);
        mHourPicker.setFormatter(NumberPicker.getTwoDigitFormatter());

        // Minute picker
        mMinutePicker = (NumberPicker) rootView
                .findViewById(R.id.minutePicker);
        mMinutePicker.setTypeface(font);
        mMinutePicker.setMaxValue(59);
        mMinutePicker.setMinValue(0);
        mMinutePicker.setFocusable(true);
        mMinutePicker.setFocusableInTouchMode(true);
        mMinutePicker.setFormatter(NumberPicker.getTwoDigitFormatter());

        // Second picker
        mSecondPicker = (NumberPicker) rootView
                .findViewById(R.id.secondPicker);
        mSecondPicker.setTypeface(font);
        mSecondPicker.setMaxValue(59);
        mSecondPicker.setMinValue(0);
        mSecondPicker.setFocusable(true);
        mSecondPicker.setFocusableInTouchMode(true);
        mSecondPicker.setFormatter(NumberPicker.getTwoDigitFormatter());

        // Seek bar
        mSeekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        int progress = Integer.parseInt(Utils.getAppPrefs(getActivity(), "sensitivity", "5"));
        boolean sensorEnabled = Utils.getAppPrefs(getActivity(), "sensor", "disabled").equals("enabled");
        mSeekBar.setProgress(progress);
        mSeekBar.setEnabled(sensorEnabled);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Utils.setAppPrefs(getActivity(), "sensitivity", Integer.toString(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Do nothing

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Do nothing

            }
        });

        // Start button
        mStartButton = (Button) rootView.findViewById(R.id.startButton);
        mStartButton.setTypeface(font);
        mStartButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    startTimerService();
                }

            }
        });

        // Reset button
        mResetButton = (Button) rootView.findViewById(R.id.resetButton);
        mResetButton.setTypeface(font);
        mResetButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                mHourPicker.setValue(0);
                mMinutePicker.setValue(0);
                mSecondPicker.setValue(0);

            }
        });

        // Sensor checkbox
        mCheckBox = (CheckBox) rootView.findViewById(R.id.sensorCheckBox);
        mCheckBox.setTypeface(font);
        mCheckBox.setChecked(Utils.getAppPrefs(getActivity(), "sensor", "disabled").equals("enabled"));
        mCheckBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
                if (isChecked) {
                    Utils.setAppPrefs(getActivity(), "sensor", "enabled");
                    mSeekBar.setEnabled(true);
                } else {
                    Utils.setAppPrefs(getActivity(), "sensor", "disabled");
                    mSeekBar.setEnabled(false);
                }
            }
        });

        // Sensitivity text
        TextView sensText = (TextView) rootView.findViewById(R.id.sensText);
        font = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-LightItalic.ttf");
        sensText.setTypeface(font);

        return rootView;
    }

    public void onDestroyView() {
        super.onDestroyView();

    }

    private void startTimerService() {
        // Set sensitivity
        String sensitivity = Integer.toString(mSeekBar.getProgress());
        Utils.setAppPrefs(getActivity(), "sensitivity", sensitivity);

        // Set time and start service
        if (mHourPicker.getValue() != 0
                || mMinutePicker.getValue() != 0
                || mSecondPicker.getValue() != 0) {

            int time = (mHourPicker.getValue()) * 3600
                    + (mMinutePicker.getValue()) * 60
                    + mSecondPicker.getValue();

            Intent serviceIntent = new Intent(getActivity(),
                    TimerService.class);
            serviceIntent.putExtra("time", time);
            if (getActivity().startService(serviceIntent) != null) {
                TimerService.SERVICE_FLAG = true;
            }
            Fragment fragment = new TimerFragment();
            Utils.changeFragment(fragment, getActivity());
        } else {
            Toast.makeText(getActivity(),
                    getString(R.string.timer_msg), Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
