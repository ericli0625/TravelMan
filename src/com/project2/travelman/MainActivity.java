package com.project2.travelman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	public ProgressDialog myDialog = null;

	private Button myButtonSearchLocal, myButtonSearchKeyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// final CharSequence strDialogTitle = "請稍後片刻";
		// final CharSequence strDialogBody = "載入中...";
		//
		// myDialog = ProgressDialog.show(this, strDialogTitle, strDialogBody);
		//
		// new Thread(){
		// public void run() {
		// try {
		// sleep(2000);
		// } catch (Exception e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// }
		// finally{
		// myDialog.dismiss();
		// }
		// }
		// }.start();

		myButtonSearchLocal = (Button) findViewById(R.id.button_Search_Local);
		myButtonSearchKeyword = (Button) findViewById(R.id.button_Search_Keyword);

		myButtonSearchLocal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, ActivitySearchLocal.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		myButtonSearchKeyword.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,
						ActivitySearchLocalKeywords.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

	}

}
