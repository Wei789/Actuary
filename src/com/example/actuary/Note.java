package com.example.actuary;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Note extends Fragment {
	private ListView listView;
	private ListView poplistView;
	private Button bthInsert;
	private DB mDBHelper;
	private Cursor mCursor;
	private int mNoteCount = 1;
	private static final int ACTIVITY_EIDT = 1;
	private Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return initView(inflater, container);
	}

	private View initView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.note, container, false);
		listView = (ListView) view.findViewById(R.id.listView);
		bthInsert = (Button) view.findViewById(R.id.btnInsert);
		// fill data from SQLITE
		fillData();
		setListeners();

		return view;
	}

	private void setListeners() {
		// Event
		// click =edit
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
				Intent intent = new Intent(activity, NoteEdit.class);
				intent.putExtra(DB.KEY_ROWID, id);
				startActivityForResult(intent, ACTIVITY_EIDT);
			}
		});
		// longClick =delete
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
				alertshow(id);
				return true;
			}
		});
		// ·s¼W
		bthInsert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNoteCount++;
				String noteName = "Note" + mNoteCount;
				mDBHelper.insert(noteName);
				fillData();
			}
		});
	}

	private void alertshow(long id) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View pop_view = inflater.inflate(R.layout.popwindow, null);
		poplistView = (ListView) pop_view.findViewById(R.id.poplistView);
		String[] listItem = { getString(R.string._rename), getString(R.string._delete) };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_list_item_1, listItem);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(pop_view);
		AlertDialog dialog = builder.create();
		dialog.show();

		poplistView.setAdapter(adapter);

		poplistView.setOnItemClickListener(new ItemClick(id, dialog));

	}

	private void renameDialog(Long id) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View re_view = inflater.inflate(R.layout.rename_win, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setPositiveButton(getString(R.string._confirm), new OkClickListener(id, re_view));
		builder.setNegativeButton(getString(R.string._cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		builder.setView(re_view);

		final AlertDialog dialog = builder.create();
		dialog.show();
	}

	private class ItemClick implements OnItemClickListener {
		Long id;
		AlertDialog dialog;

		public ItemClick(Long id, AlertDialog dialog) {
			this.id = id;
			this.dialog = dialog;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			switch (position) {
			case 0:
				dialog.cancel();
				renameDialog(id);
				break;
			case 1:
				selectItem(id);
				dialog.cancel();
				break;
			default:
			}
		}

	}

	private class OkClickListener implements DialogInterface.OnClickListener {
		Long id;
		View re_view;

		public OkClickListener(Long id, View re_view) {
			this.id = id;
			this.re_view = re_view;
		}

		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			EditText txtName = (EditText) re_view.findViewById(R.id.editText1);
			Boolean i = mDBHelper.update(id, null,txtName.getText().toString());
			Log.d("NAME", i.toString());
			fillData();
		}
	}

	private void fillData() {
		activity = getActivity();
		mDBHelper = new DB(activity);
		mDBHelper.open();
		mCursor = mDBHelper.getAll();

		String[] from = new String[] { DB.KEY_NAME };
		int[] to_layout = new int[] { android.R.id.text1 };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(activity,
				android.R.layout.simple_list_item_1, mCursor, from, to_layout, 0);
		listView.setAdapter(adapter);
	}

	private void selectItem(long position) {
		mDBHelper.delete(position);
		fillData();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// requestCode=1,resultCode=OK
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ACTIVITY_EIDT) {
				fillData();
			}
		}
	}

}
