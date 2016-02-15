package com.example.actuary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WsStock extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		restorePrefs();
	}

	private Activity fa;
	private Button btnQuery;
	private EditText stockNumber;
	private ListView StockList;
	private List<Map<String, String>> values = new ArrayList<Map<String, String>>();
	private SimpleAdapter adapter;
	private ListView poplistView;
	private DB mDBHelper;
	private Cursor mCursor;
	private static final String PREF = "STOCK_PREF";
	private AlertDialog dialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.stock_main, container, false);

		btnQuery = (Button) rootView.findViewById(R.id.QueryStock);
		stockNumber = (EditText) rootView.findViewById(R.id.edtStock);
		StockList = (ListView) rootView.findViewById(R.id.StockList);
		stockListVO stockVO = new stockListVO();
		// 設定title row
		stockVO.setStockNum(getString(R.string.StcokNumber));
		stockVO.setPrice(getString(R.string.price));
		stockVO.setDate(getString(R.string.date));
		stockVO.setChange(getString(R.string.change));
		setListView(stockVO);
		setListeners();

		return rootView;
	}

	private void setListeners() {
		btnQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuilder url = new StringBuilder();
				url.append("http://finance.yahoo.com/d/quotes.csv?s=");
				url.append(stockNumber.getText().toString());
				url.append(".TW&f=snd1l1c6");
				// new Thread(runnable).start();
				new WebServiceTask().execute(url.toString());
				//關閉小鍵盤
				InputMethodManager imm = (InputMethodManager)fa.getSystemService(fa.INPUT_METHOD_SERVICE); 
				imm.hideSoftInputFromWindow(stockNumber.getWindowToken(), 0);   
			}
		});

		StockList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
				LayoutInflater inflater = LayoutInflater.from(getActivity());
				View pop_view = inflater.inflate(R.layout.popwindow, null);
				poplistView = (ListView) pop_view.findViewById(R.id.poplistView);
				mDBHelper = new DB(fa);
				mDBHelper.open();
				mCursor = mDBHelper.getAll();

				String[] from = new String[] { DB.KEY_NAME };
				int[] to_layout = new int[] { android.R.id.text1 };

				SimpleCursorAdapter adapter = new SimpleCursorAdapter(fa,
						android.R.layout.simple_list_item_1, mCursor, from, to_layout, 0);

				AlertDialog.Builder builder = new AlertDialog.Builder(fa);
				builder.setView(pop_view);
				dialog = builder.create();
				dialog.show();
				Toast.makeText(fa, getString(R.string._Msg), Toast.LENGTH_LONG).show();
				poplistView.setAdapter(adapter);
				poplistView.setOnItemClickListener(new updateNote(mDBHelper, id));
			}
		});

		StockList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				values.remove(position);
				adapter.notifyDataSetChanged();
				return false;
			}

		});
	}

	private class updateNote implements OnItemClickListener {
		private DB mDBHelper;
		private int stockID;

		public updateNote(DB mDBHelper, long id) {
			this.mDBHelper = mDBHelper;
			this.stockID = (int) id;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
			int rowID = (int) id;
			StringBuilder str = new StringBuilder();
			Cursor note = mDBHelper.get(rowID);
			// 先取出DB字串
			str.append(note.getString(note.getColumnIndexOrThrow(DB.KEY_NOTE)));
			// 在儲存畫面上DATA
			str.append(values.get(0).get("stockNum") + "       ");
			str.append(values.get(0).get("date") + "       ");
			str.append(values.get(0).get("price") + "       ");
			str.append(values.get(0).get("change") + "\n");
			str.append(values.get(stockID).get("stockNum") + " ");
			str.append(values.get(stockID).get("date") + " ");
			str.append(values.get(stockID).get("price") + " ");
			str.append(values.get(stockID).get("change") + "\n");

			mDBHelper.update(id, str.toString(), null);
			dialog.dismiss();
		}

	}

	@Override
	public void onPause() {
		/* 紀錄上次輸入股票代號 當離開畫面時 */
		super.onPause();
		if (values.size() != 0) {
			Set<String> stockNum = new HashSet<String>();
			SharedPreferences settings = getActivity().getSharedPreferences(PREF, 0);
			Editor editor = settings.edit();
			// 跳過第一列標題列
			values.remove(0);
			for (Map<String, String> map : values) {
				stockNum.add(map.get("stockNum"));
			}
			editor.putStringSet("stockNum", stockNum).commit();
		}
	}

	/** restore Preferences */
	private void restorePrefs() {
		SharedPreferences settings = getActivity().getSharedPreferences(PREF, 0);
		Set<String> stockNum = settings.getStringSet("stockNum", new HashSet<String>());
		Iterator<String> iterator = stockNum.iterator();
		StringBuilder url = new StringBuilder();
		ProgressDialog progress = ProgressDialog.show(getActivity(), "", "", true);
		while (iterator.hasNext()) {
			url = new StringBuilder();
			url.append("http://finance.yahoo.com/d/quotes.csv?s=");
			url.append(iterator.next().replace("\"", ""));
			url.append("&f=snd1l1c6");
			// new Thread(runnable).start();
			new WebServiceTask().execute(url.toString());
		}
		progress.dismiss();
	}

	/** Set StockListView */
	private void setListView(stockListVO stockVO) {
		String[] key = new String[] { "stockNum", "price", "date", "change" };
		Map<String, String> map = new HashMap<String, String>();
		map.put("stockNum", stockVO.getStockNum());
		map.put("date", stockVO.getDate());
		map.put("price", stockVO.getPrice());
		map.put("change", stockVO.getChange());
		values.add(map);
		fa = getActivity();
		adapter = new SimpleAdapter(fa, values, R.layout.mylistview, key, new int[] {
				R.id.stockNumView, R.id.dateView, R.id.priceView, R.id.changeView }) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View textView = super.getView(position, convertView, parent);
				TextView item = (TextView) textView.findViewById(R.id.changeView);
				switch (item.getText().charAt(1)) {
				case '-':
					item.setTextColor(Color.GREEN);
					break;
				case '+':
					item.setTextColor(Color.RED);
					break;
				default:
					item.setTextColor(Color.BLACK);
					break;
				}
				return textView;
			}
		};
		;
		StockList.setAdapter(adapter);
	}

	/** 透過YAHOO webService 取得股票資訊 */
	private class WebServiceTask extends AsyncTask<String, Void, String> {
		private ProgressDialog progress;
		
		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(getActivity(), "", "", true);

		}

		@Override
		protected String doInBackground(String... params) {
			String line = "";
			URL url;
			try {
				url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				InputStreamReader isr = new InputStreamReader(conn.getInputStream());
				BufferedReader in = new BufferedReader(isr);
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return line;
		}

		@Override
		protected void onPostExecute(String result) {
			String[] aResult = result.split(",");
			if (aResult.length > 1) {
				if ("\"N/A\"".equals(aResult[2])) {
					Toast.makeText(fa, getString(R.string._error_Msg), Toast.LENGTH_LONG).show();
					// 會出現the content of the adapter has changed but ListView
					// did not receive a notification
					// adapter.notifyDataSetChanged();
					// values.remove(values.size() - 1);
				} else {
					stockListVO stockVO = new stockListVO();
					// 設定title row
					stockVO.setStockNum(aResult[0]);
					stockVO.setPrice(aResult[2]);
					stockVO.setDate(aResult[3]);
					stockVO.setChange(aResult[4]);
					setListView(stockVO);
				}
			} else {
				Toast.makeText(fa, getString(R.string._error_Msg1), Toast.LENGTH_LONG).show();
			}

			progress.dismiss();
		}
	}

	// 使用AsyncTask取代
	// Runnable runnable = new Runnable()
	// {
	// @Override
	// public void run()
	// {
	// String urlConnect =
	// "http://finance.yahoo.com/d/quotes.csv?s=2330.TW&f=snd1l1c6";
	// String line = "";
	// URL url;
	// Message msg = new Message();
	// Bundle data = new Bundle();
	// try
	// {
	// url = new URL(urlConnect);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// InputStreamReader isr = new InputStreamReader(conn.getInputStream());
	// BufferedReader in = new BufferedReader(isr);
	// line = in.readLine();
	//
	// data.putString("value", line);
	// msg.setData(data);
	// handler.sendMessage(msg);
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	//
	// }
	// };
	//
	// Handler handler = new Handler()
	// {
	// @Override
	// public void handleMessage(Message msg)
	// {
	// super.handleMessage(msg);
	// Bundle data = msg.getData();
	// String val = data.getString("value");
	// stockView.setText(val);
	// }
	// };
}
