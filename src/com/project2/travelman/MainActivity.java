package com.project2.travelman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button myButtonSearchLocal, myButtonSearchSpot,
			myButtonSearchKeyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myButtonSearchLocal = (Button) findViewById(R.id.button_Search_Local);
//		myButtonSearchSpot = (Button) findViewById(R.id.button_Search_Spot);
//		myButtonSearchKeyword = (Button) findViewById(R.id.button_Search_Keyword);

		myButtonSearchLocal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ActivitySearchLocal.class);
				startActivity(intent);
			}
		});

//		myButtonSearchSpot.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				// Perform action on click
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, ActivitySearchSpot.class);
//				startActivity(intent);
//			}
//		});
//
//		myButtonSearchKeyword.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				// Perform action on click
//				Intent intent = new Intent();
//				intent.setClass(MainActivity.this, ActivitySearchKeyword .class);
//				startActivity(intent);
//			}
//		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
