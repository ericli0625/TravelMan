package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ListView;

public class ActivitySearchLocalCitiesFavor extends Activity {

	private DBHelper DH;
	private Cursor myCursor;

    private SimpleCursorAdapter adapter;

	private int _id;
	private String flag = "1",name, address, telephone, category, content;

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

		adapter = new SimpleCursorAdapter(this, R.layout.activity_list,
				myCursor, new String[] { DH.FIELD_Name, DH.FIELD_Address },
				new int[] { R.id.listTextView1, R.id.listTextView2 }, 0);

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
						content = myCursor.getString(5);

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

						// 把bundle物件指派給Intent
						intent.putExtras(bundle);

						// Activity (ActivityMenu)
						startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}

				});

		myListViewFavor
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
					@Override
					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenu.ContextMenuInfo menuInfo) {
						menu.setHeaderTitle("確定刪除此項目");
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
		DH.delete(_id);

        DH = new DBHelper(this);
        myCursor = DH.select();

        SimpleCursorAdapter adapter_t = new SimpleCursorAdapter(this, R.layout.activity_list,
				myCursor, new String[] { DH.FIELD_Name, DH.FIELD_Address },
				new int[] { R.id.listTextView1, R.id.listTextView2 }, 0);

        adapter_t.notifyDataSetChanged();

		myListViewFavor.setAdapter(adapter_t);

		_id = 0;
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