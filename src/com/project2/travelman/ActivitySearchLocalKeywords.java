package com.project2.travelman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ActivitySearchLocalKeywords extends Activity {

	private String selectedCities;
	private Button myButtonSubmit, myButtonReset;
	private EditText editText_Search;
	private TextView myTextView;
	private Spinner mySpinner;
	private ListView myListView_Search;
	private Traveler Traveler;
	
	protected List<Traveler> Travelers;
	private String result = new String();

	ArrayAdapter<String> adapter;
	private SimpleAdapter adapterHTTP;

	ArrayAdapter<CharSequence> adapterTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_keyword);

		ActionBar actionBar = getActionBar();
		actionBar.hide();

		visitExternalLinks();
		
		myButtonSubmit = (Button) findViewById(R.id.button_Search_Submit);
		myButtonReset = (Button) findViewById(R.id.button_Search_Reset);
		editText_Search = (EditText) findViewById(R.id.editText_Search);
		mySpinner = (Spinner) findViewById(R.id.spinner4);
		myListView_Search = (ListView) findViewById(R.id.myListView);

		// 設定下拉選單
		adapterTemp = ArrayAdapter.createFromResource(this,
				R.array.cities_init, android.R.layout.simple_spinner_item);
		adapterTemp
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mySpinner.setAdapter(adapterTemp);

		mySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				int pos = mySpinner.getSelectedItemPosition();
				choeseCities(pos);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

		if (AppStatus.getInstance(this).isOnline(this)) {

			myButtonSubmit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					String str1 = editText_Search.getText().toString();

					if (!str1.equals("")) {
						String sql = "SELECT * FROM " + selectedCities
								+ " where name like '%" + str1 + "%'";
						result = DBConnector.executeQuery(sql);

						ShowListView(result);
					} else {
						openOptionsDialogIsNotContext();
					}

				}
			});

			myButtonReset.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					editText_Search.setText("");
				}
			});
		} else {
			openOptionsDialogIsNetworkAvailable();
		}
	}

	private void ShowListView(String result) {

		// 判斷是否有搜尋到診所
		if (result.length() > 5) {
			Travelers = JsonToList(result);
			setInAdapter();
			Toast.makeText(this, "搜尋完成", Toast.LENGTH_LONG).show();
		} else {
			List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
			String[] from = { "name", "address" };
			int[] to = { R.id.listTextView1, R.id.listTextView2 };
			adapterHTTP = new SimpleAdapter(this, lists,
					R.layout.activity_list, from, to);
			adapterHTTP.notifyDataSetChanged();
			openOptionsDialogIsNoneResult();
		}
		myListView_Search.setAdapter(adapterHTTP);

		myListView_Search
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
						intent.setClass(
								ActivitySearchLocalKeywords.this,
								ActivitySearchLocalCitiesSpotDetail.class);

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
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}

				});
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

	private void openOptionsDialogIsNotContext() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_no_context)
				.setMessage(R.string.app_no_context_msg)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						}).show();

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
	
	private void choeseCities(int pos) {

		switch (pos) {
		case 0:
			selectedCities = new String("keelung_city");
			break;
		case 1:
			selectedCities = new String("taipei_city");
			break;
		case 2:
			selectedCities = new String("xinbei_city");
			break;
		case 3:
			selectedCities = new String("taoyuan_county");
			break;
		case 4:
			selectedCities = new String("hsinchu_city");
			break;
		case 5:
			selectedCities = new String("hsinchu_county");
			break;
		case 6:
			selectedCities = new String("miaoli_county");
			break;
		case 7:
			selectedCities = new String("taichung_city");
			break;
		case 8:
			selectedCities = new String("changhua_county");
			break;
		case 9:
			selectedCities = new String("nantou_county");
			break;
		case 10:
			selectedCities = new String("yunlin_county");
			break;
		case 11:
			selectedCities = new String("chiayi_city");
			break;
		case 12:
			selectedCities = new String("chiayi_county");
			break;
		case 13:
			selectedCities = new String("tainan_city");
			break;
		case 14:
			selectedCities = new String("kaohsiung_city");
			break;
		case 15:
			selectedCities = new String("pingtung_county");
			break;
		case 16:
			selectedCities = new String("yilan_county");
			break;
		case 17:
			selectedCities = new String("hualien_county");
			break;
		case 18:
			selectedCities = new String("taitung_county");
			break;
		case 19:
			selectedCities = new String("penghu_county");
			break;
		case 20:
			selectedCities = new String("kinmen_county");
			break;
		case 21:
			selectedCities = new String("lianjiang_county");
			break;
		default:
			break;
		}

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

}