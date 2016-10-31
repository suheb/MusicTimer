package com.suhaib.musictimer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.haloappstudio.musictimer.R;
import com.suhaib.musictimer.utils.CustomListAdapter;
import com.suhaib.musictimer.utils.Utils;

import java.util.List;

public class SelectorDialogFragment extends DialogFragment {
	private List<ResolveInfo> mPlayerList;
	private ListView mListView;
	private String mDefaultPlayerProcess;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Inflate Layout
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View rootView = inflater.inflate(R.layout.dialog_selector, null);
		mListView = (ListView) rootView.findViewById(R.id.list_apps);

		// Get Music Player list
		PackageManager packageManager = getActivity().getPackageManager();
		Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
		mPlayerList = packageManager.queryIntentActivities(intent, 0);

		CustomListAdapter adapter = new CustomListAdapter(getActivity(),
				packageManager, mPlayerList);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				view.setSelected(true);

				mDefaultPlayerProcess = mPlayerList.get(position).activityInfo.processName;
			}
		});
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(rootView)
				.setTitle("Choose default player")
				.setPositiveButton("Apply",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Utils.setAppPrefs(getActivity(), "defaultplayerprocess", mDefaultPlayerProcess);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});
		// Create the AlertDialog object and return it
		return builder.create();
	}

}
