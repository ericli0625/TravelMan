package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button myButtonSearchLocal, myButtonSearchKeyword, myButton_favor, myButton_weather;
    private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myButtonSearchLocal = (Button) findViewById(R.id.button_Search_Local);
		myButtonSearchKeyword = (Button) findViewById(R.id.button_Search_Keyword);
		myButton_favor = (Button) findViewById(R.id.button_favor);
		myButton_weather = (Button) findViewById(R.id.button_weather);

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

		myButton_favor.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Intent intent = new Intent();
				intent.setClass(MainActivity.this,
						ActivitySearchLocalCitiesFavor.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		myButton_weather.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click

                Intent intent = new Intent();
				intent.setClass(MainActivity.this,
						ActivityWeather.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);

			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.item1:
			openOptionsDialogAbout();
			break;
		case R.id.item2:
			openOptionsDialogEmail();
			break;
		case R.id.item3:
			openOptionsDialogExit();
			break;
		default:
			break;
		}

		return true;
	}

	protected void openOptionsDialogEmail() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_email)
				.setMessage(R.string.app_email_msg)
				.setNegativeButton(R.string.str_no_mail,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						})
				.setPositiveButton(R.string.str_ok_mail,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								Uri uri = Uri
										.parse("mailto:ericli0625@gmail.com");
								Intent it = new Intent(Intent.ACTION_SENDTO,
										uri);
								startActivity(it);

							}
						}).show();

	}

	protected void openOptionsDialogAbout() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_about)
				.setMessage(R.string.app_about_msg)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

	}

	protected void openOptionsDialogExit() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_exit)
				.setMessage(R.string.app_exit_msg)
				.setNegativeButton(R.string.str_no,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						})
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

}
