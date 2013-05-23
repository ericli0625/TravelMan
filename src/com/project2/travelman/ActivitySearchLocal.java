package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

		adapter = new ArrayAdapter<String>(this,R.layout.item1, cities_init);
		
//		int[] to = { R.id.listTextView1, R.id.listTextView2 };
//		adapterHTTP = new SimpleAdapter(this, lists,
//				R.layout.activity_list, from, to);
		
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

						// Toast.makeText(arg1.getContext(), name,
						// Toast.LENGTH_SHORT)
						// .show();

						// 把bundle物件指派給Intent
						intent.putExtras(bundle);

						// Activity (ActivityMenu)
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
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
		case R.id.item1:
			openOptionsDialogAbout();
			break;
		case R.id.item2:
			openOptionsDialogEmail();
			break;
		case R.id.item3:
			openOptionsDialogExit();
			break;
		case R.id.item_favor:
			Intent intent = new Intent();
			intent.setClass(ActivitySearchLocal.this,
					ActivitySearchLocalCitiesFavor.class);
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

    private void openOptionsDialogEmail() {
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

    private void openOptionsDialogAbout() {
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

    private void openOptionsDialogExit() {
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