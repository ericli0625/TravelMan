package com.project2.travelman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ActivitySearchLocalCitiesSpot extends Activity {

	private ListView myListView;
	private TextView myTextView;

	private SimpleAdapter adapterHTTP;
	protected List<Traveler> Travelers;
	private Traveler Traveler;

	private String result = new String();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

//		myTextView = (TextView) findViewById(R.id.myTextView);
		myListView = (ListView) findViewById(R.id.myListView);

		Bundle bundle = getIntent().getExtras();
		String spotname = bundle.getString("name");// 鄉鎮縣市區(全區)
		String spotengname = bundle.getString("engname");// 縣市
		
		// 判斷是否有上網
		if (AppStatus.getInstance(this).isOnline(this)) {

			String sql;
			String strText = new String("全部地區");

			visitExternalLinks();

			if (spotname.equals(strText)) {
				sql = "SELECT * FROM " + spotengname + " ";
				result = DBConnector.executeQuery(sql);
			} else {
				sql = "SELECT * FROM " + spotengname + " where city like '"
						+ spotname + "'";
				result = DBConnector.executeQuery(sql);
			}
			
//			myTextView.setText(result + "?");
			
			ShowListView(result);

		} else {
			openOptionsDialogIsNetworkAvailable();
		}
	}

	private void openOptionsDialogIsNetworkAvailable() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_connect)
				.setMessage(R.string.app_connect_msg)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								finish();
							}
						}).show();

	}

	private void ShowListView(String result) {

		// 判斷是否有搜尋到診所
		if (result.length() > 5) {
			Travelers = JsonToList(result);
			setInAdapter();
			Toast.makeText(this, "讀取完成", Toast.LENGTH_SHORT).show();
		} else {
			List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
			String[] from = { "name", "address" };
			int[] to = { R.id.listTextView1, R.id.listTextView2 };
			adapterHTTP = new SimpleAdapter(this, lists,
					R.layout.activity_list, from, to);
			adapterHTTP.notifyDataSetChanged();
			openOptionsDialogIsNoneResult();
		}
		myListView.setAdapter(adapterHTTP);

		myListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// arg0就是ListView，arg1表示Item視圖，arg2表示資料index

						ListView lv = (ListView) arg0;
						// SimpleAdapter返回Map
						HashMap<String, String> traveler = (HashMap<String, String>) lv
								.getItemAtPosition(arg2);

						String telephone, address, name, category, content;

						name = traveler.get("name");
						address = traveler.get("address");
						telephone = traveler.get("telephone");
						category = traveler.get("category");
						content = traveler.get("content");

						Intent intent = new Intent();
						intent.setClass(ActivitySearchLocalCitiesSpot.this,
								ActivitySearchLocalCitiesSoptDetail.class);

						Bundle bundle = new Bundle();

						bundle.putString("name", name);
						bundle.putString("address", address);
						bundle.putString("telephone", telephone);
						bundle.putString("category", category);
						bundle.putString("content", content);

						// 把bundle物件指派給Intent
						intent.putExtras(bundle);

						// Activity (ActivityMenu)
						startActivity(intent);

					}

				});
	}

	private void openOptionsDialogIsNoneResult() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_none_result)
				.setMessage(R.string.app_none_result_msg)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

	protected void setInAdapter() {
		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();

		Map<String, String> map;
		for (Traveler p : Travelers) {
			map = new HashMap<String, String>();

			map.put("_id", p.getId());
			map.put("name", p.getName());
			map.put("category", p.getCategory());
			map.put("cities", p.getCities());
			map.put("city", p.getCity());
			map.put("address", p.getAddress());
			map.put("telephone", p.getTelephone());
			map.put("content", p.getContent());

			lists.add(map);
		}

		// HashMap<String, String>中的key
		String[] from = { "name", "address" };

		int[] to = { R.id.listTextView1, R.id.listTextView2 };

		adapterHTTP = new SimpleAdapter(this, lists, R.layout.activity_list,
				from, to);

	}

	protected List<Traveler> JsonToList(String response) {
		List<Traveler> list = new ArrayList<Traveler>();

		try {
			// 將字符串轉換為Json數組
			JSONArray array = new JSONArray(response);

			int length = array.length();
			for (int i = 0; i < length; i++) {
				// 將每一個數組再轉換成Json對象
				JSONObject obj = array.getJSONObject(i);

				Traveler = new Traveler();
				Traveler.setId(obj.getString("_id"));
				Traveler.setName(obj.getString("name"));
				Traveler.setCategory(obj.getString("category"));
				Traveler.setCities(obj.getString("cities"));
				Traveler.setCity(obj.getString("city"));
				Traveler.setAddress(obj.getString("address"));
				Traveler.setTelephone(obj.getString("telephone"));
				Traveler.setContent(obj.getString("content"));

				list.add(Traveler);
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void visitExternalLinks() {
		// 發送Http請求
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
	}

}