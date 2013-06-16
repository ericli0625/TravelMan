package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ActivitySearchLocalCitiesSpotDetail extends Activity {

	private TextView myTextView1, myTextView2, myTextView3, myTextView4,
			myTextView5;
	private TextView myTextView1_1, myTextView2_2, myTextView3_3,
			myTextView4_4, myTextView5_5;
	protected List<Traveler> Travelers;
	private ImageButton myButton1, myButton2, myButton3;
    private Button myButton4;
	
	Intent myintent;
	private DBHelper DH = null;
	private String flag, name, address, telephone, category, content,latitude,longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		DH = new DBHelper(this);

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
		myButton3 = (ImageButton) findViewById(R.id.favorButton);
        myButton4 = (Button) findViewById(R.id.imageButtonWeb);

		mainRush();

	}

	private void mainRush() {

		Bundle bundle = getIntent().getExtras();
		flag = bundle.getString("flag");
		name = bundle.getString("name");
		address = bundle.getString("address");
		telephone = bundle.getString("telephone");
		category = bundle.getString("category");
		content = bundle.getString("content");

        longitude = bundle.getString("longitude");
        latitude = bundle.getString("latitude");

		if (telephone.equals("") || telephone == null) {
			myTextView4.setVisibility(View.GONE);
			myTextView4_4.setVisibility(View.GONE);
			myButton2.setVisibility(View.GONE);
		} else {
			myTextView4.setText("聯絡電話:");
			myTextView4_4.setText(telephone);
		}

		if (flag.equals("1")) {
			myButton3.setVisibility(View.GONE);
		} else {

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

		myButton1.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				String mapUri = "geo:0,0?q=" + latitude +","+longitude+ "("+name+")";
				myintent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));
				startActivity(myintent);
			}
		});

		myButton2.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				openOptionsDialogCall();
			}
		});

		myButton3.setOnClickListener(new ImageButton.OnClickListener() {
			public void onClick(View v) {
				// Perform action on click
				Cursor cursor = DH.matchData(name, category, address, telephone,longitude,latitude, content);

				int rows_num = cursor.getCount();

				if (rows_num == 1) {
					Toast.makeText(v.getContext(), "您已經新增過了", Toast.LENGTH_SHORT)
							.show();
				} else {
					DH.insert(name, category, address, telephone, longitude, latitude, content);
					Toast.makeText(v.getContext(), "新增至我的最愛", Toast.LENGTH_SHORT)
							.show();
				}

			}
		});

        myButton4.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, name);
                startActivity(intent);
            }
        });

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.item_favor:
                Intent intent = new Intent();
                intent.setClass(ActivitySearchLocalCitiesSpotDetail.this, ActivitySearchLocalCitiesFavor.class);
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
//		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		return;
	}

}