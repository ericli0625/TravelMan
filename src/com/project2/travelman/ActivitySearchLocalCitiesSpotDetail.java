package com.project2.travelman;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivitySearchLocalCitiesSpotDetail extends Activity {

	private TextView myTextView1, myTextView2, myTextView3, myTextView4,
			myTextView5;
	private TextView myTextView1_1, myTextView2_2, myTextView3_3,
			myTextView4_4, myTextView5_5;
	protected List<Traveler> Travelers;
	private ImageButton myButton1, myButton2;

	Intent myintent;

	private String name, address, telephone, category, content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		ActionBar actionBar = getActionBar();
		actionBar.hide();

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
		
		myButton1 = (ImageButton) findViewById(R.id.mapButton);
		myButton2 = (ImageButton) findViewById(R.id.phoneButton);
		
		// 判斷是否有上網
		if (AppStatus.getInstance(this).isOnline(this)) {

			Bundle bundle = getIntent().getExtras();
			name = bundle.getString("name");
			address = bundle.getString("address");
			telephone = bundle.getString("telephone");
			category = bundle.getString("category");
			content = bundle.getString("content");
			
			if(telephone.equals("") || telephone ==null){
				myTextView4.setVisibility(View.GONE);
				myTextView4_4.setVisibility(View.GONE);
				myButton2.setVisibility(View.GONE);
			}else{
				myTextView4.setText("聯絡電話:");
				myTextView4_4.setText(telephone);
			}
			
			myTextView1.setText("景點名稱:");
			myTextView2.setText("景點類型:");
			myTextView3.setText("景點地址:");
			myTextView4.setText("聯絡電話:");
			myTextView5.setText("景點內容:");

			myTextView1_1.setText(name);
			myTextView2_2.setText(category);
			myTextView3_3.setText(address);
			myTextView5_5.setText(content);

			myButton1.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					String mapUri = "geo:0,0?q=" + name + "," + address + "";
					myintent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));
					startActivity(myintent);
				}
			});

			myButton2.setOnClickListener(new Button.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					openOptionsDialogCall();
				}
			});

		} else {
			openOptionsDialogIsNetworkAvailable();
		}

	}

	private void openOptionsDialogCall() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_call)
				.setMessage(R.string.app_call_msg)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Intent call = new Intent(
										"android.intent.action.CALL", Uri
												.parse("tel:" + telephone));
								startActivity(call);
							}
						})
				.setNegativeButton(R.string.str_no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

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