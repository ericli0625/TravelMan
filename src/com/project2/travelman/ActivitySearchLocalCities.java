package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ActivitySearchLocalCities extends Activity {

	private ListView myListView;
	private ArrayAdapter<String> adapter;

	private String engname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		myListView = (ListView) findViewById(R.id.myListView);

		Bundle bundle = getIntent().getExtras();
		String name_a = bundle.getString("name");

		// 判斷是否有上網
		if (AppStatus.getInstance(this).isOnline(this)) {
			choeseCities(name_a);
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

	public void choeseCities(String name) {

		String[] cities_init = null;
		String[] cities = getResources().getStringArray(R.array.cities_init);

		if (cities[0].equals(name)) {
			cities_init = getResources().getStringArray(R.array.keelung_city);
			engname = "keelung_city";
		} else if (cities[1].equals(name)) {
			cities_init = getResources().getStringArray(R.array.taipei_city);
			engname = "taipei_city";
		} else if (cities[2].equals(name)) {
			cities_init = getResources().getStringArray(R.array.xinbei_city);
			engname = "xinbei_city";
		} else if (cities[3].equals(name)) {
			cities_init = getResources().getStringArray(R.array.taoyuan_county);
			engname = "taoyuan_county";
		} else if (cities[4].equals(name)) {
			cities_init = getResources().getStringArray(R.array.hsinchu_city);
			engname = "hsinchu_city";
		} else if (cities[5].equals(name)) {
			cities_init = getResources().getStringArray(R.array.hsinchu_county);
			engname = "hsinchu_county";
		} else if (cities[6].equals(name)) {
			cities_init = getResources().getStringArray(R.array.miaoli_county);
			engname = "miaoli_county";
		} else if (cities[7].equals(name)) {
			cities_init = getResources().getStringArray(R.array.taichung_city);
			engname = "taichung_city";
		} else if (cities[8].equals(name)) {
			cities_init = getResources()
					.getStringArray(R.array.changhua_county);
			engname = "changhua_county";
		} else if (cities[9].equals(name)) {
			cities_init = getResources().getStringArray(R.array.nantou_county);
			engname = "nantou_county";
		} else if (cities[10].equals(name)) {
			cities_init = getResources().getStringArray(R.array.yunlin_county);
			engname = "yunlin_county";
		} else if (cities[11].equals(name)) {
			cities_init = getResources().getStringArray(R.array.chiayi_city);
			engname = "chiayi_city";
		} else if (cities[12].equals(name)) {
			cities_init = getResources().getStringArray(R.array.chiayi_county);
			engname = "chiayi_county";
		} else if (cities[13].equals(name)) {
			cities_init = getResources().getStringArray(R.array.tainan_city);
			engname = "tainan_city";
		} else if (cities[14].equals(name)) {
			cities_init = getResources().getStringArray(R.array.kaohsiung_city);
			engname = "kaohsiung_city";
		} else if (cities[15].equals(name)) {
			cities_init = getResources()
					.getStringArray(R.array.pingtung_county);
			engname = "pingtung_county";
		} else if (cities[16].equals(name)) {
			cities_init = getResources().getStringArray(R.array.yilan_county);
			engname = "yilan_county";
		} else if (cities[17].equals(name)) {
			cities_init = getResources().getStringArray(R.array.hualien_county);
			engname = "hualien_county";
		} else if (cities[18].equals(name)) {
			cities_init = getResources().getStringArray(R.array.taitung_county);
			engname = "taitung_county";
		} else if (cities[19].equals(name)) {
			cities_init = getResources().getStringArray(R.array.penghu_county);
			engname = "penghu_county";
		} else if (cities[20].equals(name)) {
			cities_init = getResources().getStringArray(R.array.kinmen_county);
			engname = "kinmen_county";
		} else if (cities[21].equals(name)) {
			cities_init = getResources().getStringArray(
					R.array.lianjiang_county);
			engname = "lianjiang_county";
		}

        adapter = new ArrayAdapter<String>(this,R.layout.item1,R.id.text123, cities_init);
		myListView.setAdapter(adapter);

		myListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// arg0就是ListView，arg1表示Item視圖，arg2表示資料index

						ListView lv = (ListView) arg0;
						// SimpleAdapter返回Map
						String name = (String) lv.getItemAtPosition(arg2);

						Intent intent = new Intent();
						intent.setClass(ActivitySearchLocalCities.this,
								ActivitySearchLocalCitiesSpotCategory.class);

						Bundle bundle = new Bundle();
						bundle.putString("name", name);// 鄉鎮縣市區
						bundle.putString("engname", engname);// 縣市

						// Toast.makeText(arg1.getContext(), engname +
						// name,Toast.LENGTH_SHORT).show();

						// 把bundle物件指派給Intent
						intent.putExtras(bundle);

                        // Activity (ActivityMenu)
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

					}

				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.item_favor:
			Intent intent = new Intent();
			intent.setClass(ActivitySearchLocalCities.this, ActivitySearchLocalCitiesFavor.class);
			startActivity(intent);
			return true;
		default:
			break;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		return;
	}

}