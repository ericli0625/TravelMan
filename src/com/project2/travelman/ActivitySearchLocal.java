package com.project2.travelman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ActivitySearchLocal extends Activity {

	private String[] cities_init;
	private ListView myListView;
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		myListView = (ListView) findViewById(R.id.myListView);

		cities_init = getResources().getStringArray(R.array.cities_init);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, cities_init);
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
						intent.setClass(ActivitySearchLocal.this,
								ActivitySearchLocalCities.class);

						Bundle bundle = new Bundle();
						bundle.putString("name", name);

//						Toast.makeText(arg1.getContext(), name, Toast.LENGTH_SHORT)
//						.show();
						
						// 把bundle物件指派給Intent
						intent.putExtras(bundle);

//						 Activity (ActivityMenu)
						startActivity(intent);

					}

				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

}