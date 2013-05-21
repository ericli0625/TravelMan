package com.project2.travelman;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class ActivitySearchLocalCitiesSoptDetail extends Activity {

	private TextView myTextView1,myTextView2,myTextView3,myTextView4,myTextView5;
	private TextView myTextView1_1,myTextView2_2,myTextView3_3,myTextView4_4,myTextView5_5;
	protected List<Traveler> Travelers;

	private String name, address, telephone, category, content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		// 判斷是否有上網
		if (AppStatus.getInstance(this).isOnline(this)) {

			myTextView1 = (TextView) findViewById(R.id.listTextView1);
			myTextView2 = (TextView) findViewById(R.id.listTextView2);
			myTextView3 = (TextView) findViewById(R.id.listTextView3);
			myTextView4 = (TextView) findViewById(R.id.listTextView4);
			myTextView5 = (TextView) findViewById(R.id.listTextView5);
			
			myTextView1_1 = (TextView) findViewById(R.id.listTextView1_1);
			myTextView2_2 = (TextView) findViewById(R.id.listTextView2_2);
			myTextView3_3 = (TextView) findViewById(R.id.listTextView3_3);
			myTextView4_4 = (TextView) findViewById(R.id.listTextView4_4);
			myTextView5_5 = (TextView) findViewById(R.id.listTextView5_5);
			
			Bundle bundle = getIntent().getExtras();
			name = bundle.getString("name");
			address = bundle.getString("address");
			telephone = bundle.getString("telephone");
			category = bundle.getString("category");
			content = bundle.getString("content");

			
			myTextView1.setText("景點名稱:");
			myTextView2.setText("景點類型:");
			myTextView3.setText("景點地址:");
			myTextView4.setText("聯絡電話:");
			myTextView5.setText("景點內容:");
			
			myTextView1_1.setText(name);
			myTextView2_2.setText(category);
			myTextView3_3.setText(address);
			myTextView4_4.setText(telephone);
			myTextView5_5.setText(content);
			
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

}