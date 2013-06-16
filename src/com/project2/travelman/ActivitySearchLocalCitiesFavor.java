package com.project2.travelman;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class ActivitySearchLocalCitiesFavor extends Activity {

	private DBHelper DH;
	private Cursor myCursor;

    private SimpleCursorAdapter adapter;

	private int _id;
	private String flag = "1",name, address, telephone, category, content,latitude,longitude;

	private static final int DELETE_ID = 0;
	private static final int CAN_DELETE_ID = 1;

	ListView myListViewFavor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		myListViewFavor = (ListView) findViewById(R.id.myListView);

		DH = new DBHelper(this);
		myCursor = DH.select();

		adapter = new SimpleCursorAdapter(this, R.layout.activity_list_favor,
				myCursor, new String[] { DH.FIELD_Name, DH.FIELD_Address ,DH.FIELD_Category},
				new int[] { R.id.listTextView1, R.id.listTextView2 , R.id.listTextView3 }, 0);

		myListViewFavor.setAdapter(adapter);

		registerForContextMenu(myListViewFavor);//將ContextMenu註冊到視圖上

		myListViewFavor
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {

						myCursor.moveToPosition(arg2);

						name = myCursor.getString(1);
						category = myCursor.getString(2);
						address = myCursor.getString(3);
						telephone = myCursor.getString(4);
                        longitude = myCursor.getString(5);
                        latitude = myCursor.getString(6);
						content = myCursor.getString(7);

						Intent intent = new Intent();
						intent.setClass(ActivitySearchLocalCitiesFavor.this,
								ActivitySearchLocalCitiesSpotDetail.class);

						Bundle bundle = new Bundle();
						
						bundle.putString("flag", flag);//標記搜尋頁進入
						bundle.putString("name", name);
						bundle.putString("address", address);
						bundle.putString("telephone", telephone);
						bundle.putString("category", category);
						bundle.putString("content", content);

                        bundle.putString("longitude", longitude);
                        bundle.putString("latitude", latitude);

						// 把bundle物件指派給Intent
						intent.putExtras(bundle);

						// Activity (ActivityMenu)
						startActivity(intent);
//						overridePendingTransition(R.anim.in_from_right,
//								R.anim.out_to_left);
						overridePendingTransition(R.anim.my_scale_action,
								R.anim.my_alpha_action);
					}

				});

		myListViewFavor
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenu.ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("刪除此項目");
						menu.add(0, DELETE_ID, 0, "確定");
						menu.add(0, CAN_DELETE_ID, 0, "取消");
					}
				});

	}

	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
					.getMenuInfo();
			deleteFavor(menuInfo.position);
			break;

		case CAN_DELETE_ID:
			break;

		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
	
	private void deleteFavor(int id) {
		myCursor.moveToPosition(id);

		_id = myCursor.getInt(0);
        String name_temp= myCursor.getString(1);
		DH.delete(_id);

        DH = new DBHelper(this);
        myCursor = DH.select();

        SimpleCursorAdapter adapter_t = new SimpleCursorAdapter(this, R.layout.activity_list_favor,
				myCursor, new String[] { DH.FIELD_Name, DH.FIELD_Address ,DH.FIELD_Category},
				new int[] { R.id.listTextView1, R.id.listTextView2 ,R.id.listTextView3}, 0);

        adapter_t.notifyDataSetChanged();

		myListViewFavor.setAdapter(adapter_t);

		_id = 0;

        Toast.makeText(this, name_temp+"已被刪除" , Toast.LENGTH_SHORT)
                .show();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		return;
	}

}