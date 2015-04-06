package com.suhaib.musictimer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.haloappstudio.musictimer.R;
import com.suhaib.musictimer.utils.Utils;

public class TimerService extends Service {

    public static boolean SERVICE_FLAG;
    private final int mId = 1;
    private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotificationManager;
	private AudioManager mAudioManager;
	private CountDownTimer mMusicTimer;
	private CountDownTimer mSensorTimer;
	private SensorManager mSensorManager;
	private SensorEventListener mSensorEventListener;
	private Sensor mSensor;
	private double mAcceleration;
	private double mLastAcceleration;
	private int mSensitivity;
	private boolean mSensorFlag = false;
	private Intent mUpdateIntent;

	@Override
	public void onCreate() {

        SERVICE_FLAG = true;
        Intent dismissIntent = new Intent(Utils.ACTION_DISMISS_NOTIFICATION);
		dismissIntent.putExtra("mId", mId);
		PendingIntent piDismiss = PendingIntent.getBroadcast(this, 0,
				dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle("Music Timer").setContentText("00:00:00")
				// Set big style
				.setStyle(new NotificationCompat.BigTextStyle())
				.addAction(R.drawable.ic_dismiss, getString(R.string.stop),
						piDismiss);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		startForeground(mId, mBuilder.build());
		
		final long time = 1000 * intent.getIntExtra("time", 0);
		mUpdateIntent = new Intent(Utils.ACTION_UPDATE_TIMER);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		mMusicTimer = new CountDownTimer(time, 1000) {

			public void onTick(long millisUntilFinished) {
				String formattedTime = formatTime(millisUntilFinished / 1000);
				// Update notification
				mBuilder.setContentText(formattedTime).setStyle(
						new NotificationCompat.BigTextStyle()
								.bigText(formattedTime));
				mNotificationManager.notify(mId, mBuilder.build());
				// Update timer
				mUpdateIntent.putExtra("formattedTime", formattedTime);
				mUpdateIntent.putExtra("currentTime",
						millisUntilFinished / 1000);
				mUpdateIntent.putExtra("maxTime", time / 1000);
				sendBroadcast(mUpdateIntent);
			}

			public void onFinish() {
				stopMusic();
			}
		};
		mMusicTimer.start();
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mSensorEventListener = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) {
				// Do nothing
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				mAcceleration = Math.sqrt(event.values[0] * event.values[0]
						+ event.values[1] * event.values[1] + event.values[2]
						* event.values[2]);
				if (mSensorFlag) {
					mSensorTimer.cancel();
					mSensorTimer.start();
				}
			}};

		mSensitivity = Integer.parseInt(Utils.getAppPrefs(this, "sensitivity", "5"));
		mLastAcceleration = mAcceleration;
		mSensorTimer = new CountDownTimer(360000 - (30000 * mSensitivity),
				600 - (50 * mSensitivity)) {

			public void onTick(long millisUntilFinished) {
				if (mLastAcceleration > 1 && mAcceleration > 1) {
					mSensorFlag = true;
				} else {
					mSensorFlag = false;
				}
				mLastAcceleration = mAcceleration;
			}

			public void onFinish() {
				stopMusic();
			}
		};
		
		boolean sensorEnabled = Utils.getAppPrefs(this, "sensor", "disabled").equals("enabled");
		if(sensorEnabled) {
			mSensorManager.registerListener(mSensorEventListener, mSensor, 100);
			mSensorTimer.start();
		}
		
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// Do nothing
		return null;
	}

	@Override
	public void onDestroy() {
		mSensorManager.unregisterListener(mSensorEventListener, mSensor);
		mMusicTimer.cancel();
		mSensorTimer.cancel();
		mUpdateIntent.putExtra("timer_flag", true);
		sendBroadcast(mUpdateIntent);
		mNotificationManager.cancel(mId);
        SERVICE_FLAG = false;
    }

    private String formatTime(long time) {
        int hr, min, sec, itime;
        itime = (int) time;
        String formattedTime = "";
        hr = itime / 3600;
        min = (itime % 3600) / 60;
        sec = (itime % 3600) % 60;
        if (hr < 10 && hr != 0) {
            formattedTime = formattedTime.concat("0" + Integer.toString(hr)
                    + ":");
        } else if (hr != 0) {
            formattedTime = formattedTime.concat(Integer.toString(hr) + ":");
        }
        if (min < 10 && min != 0) {
            formattedTime = formattedTime.concat("0" + Integer.toString(min)
                    + ":");
        } else if (min != 0) {
            formattedTime = formattedTime.concat(Integer.toString(min) + ":");
        }
        if (sec < 10) {
            formattedTime = formattedTime.concat("0" + Integer.toString(sec));
        } else {
            formattedTime = formattedTime.concat(Integer.toString(sec));
        }

        return formattedTime;
    }

    private void stopMusic() {

        mAudioManager = (AudioManager) this
                .getSystemService(Context.AUDIO_SERVICE);
        final long duration = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC) * 1000;
        // Update notification
        mBuilder.setContentText(getString(R.string.stop_msg)).setStyle(
                new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.stop_msg)));
        mNotificationManager.notify(mId, mBuilder.build());
        new CountDownTimer(duration, 2000) {
            public void onTick(long millisUntilFinished) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        (int) millisUntilFinished / 1000, 0);
            }

            public void onFinish() {
                int result = mAudioManager.requestAudioFocus(
                        new AudioManager.OnAudioFocusChangeListener() {
                            @Override
                            public void onAudioFocusChange(int arg0) {
                                // Do nothing
                            }
                        }, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
                String processName = Utils.getAppPrefs(getApplication(), "defaultplayerprocess", "com.google.android.music.ui");
                Utils.removeTask(getApplication(), processName, 0);
                if (result == AudioManager.AUDIOFOCUS_GAIN) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            (int) duration / 1000, 0);
                }

                stopSelf();
            }
        }.start();
    }
}
