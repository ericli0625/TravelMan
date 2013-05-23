package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitySearchLocalCitiesSpotCategory extends Activity {

	private ListView myListView;
	private TextView myTextView;
	private Spinner mySpinner;
	private SimpleAdapter adapterHTTP;
	protected List<Traveler> Travelers;
	private Traveler Traveler;
	String[] spotCategoryArray;
	Resources res;

	private String spotname, spotengname, flag = "0";
	private String spotCategory = new String("所有類型");
	private String strText = new String("全部地區");
	private String result = new String();
	private ArrayAdapter<CharSequence> adapterTemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_category);

		// myTextView = (TextView) findViewById(R.id.myTextView);
		myListView = (ListView) findViewById(R.id.myListView);
		mySpinner = (Spinner) findViewById(R.id.spinner1);

		Bundle bundle = getIntent().getExtras();
		spotname = bundle.getString("name");// 鄉鎮縣市區(全區)
		spotengname = bundle.getString("engname");// 縣市

		// 判斷是否有上網
		if (AppStatus.getInstance(this).isOnline(this)) {

			visitExternalLinks();

			if (spotname.equals(strText)) {
				String sql = "SELECT * FROM " + spotengname + " ";
				result = DBConnector.executeQuery(sql);
				ShowListView(result);
			} else {
				String sql = "SELECT * FROM " + spotengname
						+ " where city like '" + spotname + "'";
				result = DBConnector.executeQuery(sql);
				ShowListView(result);
			}

			// 讀取string array
			res = getResources();
			spotCategoryArray = res.getStringArray(R.array.spot_category);

//			adapterTemp = ArrayAdapter.createFromResource(this, R.array.spot_category,android.R.layout.simple_spinner_item);
			adapterTemp = ArrayAdapter.createFromResource(this, R.array.spot_category,R.layout.item2);
			
			adapterTemp
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			mySpinner.setAdapter(adapterTemp);

			mySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View v,
						int position, long id) {
					spotCategory = spotCategoryArray[position];

					if (!spotCategory.equals("所有類型")
							&& !spotCategory.equals("")) {
						String sql = "SELECT * FROM " + spotengname
								+ " where city like '" + spotname
								+ "' and category like '" + spotCategory + "'";
						result = DBConnector.executeQuery(sql);
					} else if (!spotCategory.equals("")) {
						String sql = "SELECT * FROM " + spotengname
								+ " where city like '" + spotname + "'";
						result = DBConnector.executeQuery(sql);
					}

					if (spotname.equals(strText)
							&& !spotCategory.equals("所有類型")) {
						String sql = "SELECT * FROM " + spotengname
								+ " where category like '" + spotCategory + "'";
						result = DBConnector.executeQuery(sql);
					} else if (spotname.equals(strText)
							&& spotCategory.equals("所有類型")) {
						String sql = "SELECT * FROM " + spotengname + " ";
						result = DBConnector.executeQuery(sql);
					}

					ShowListView(result);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

			// myTextView.setText(result + "?");

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

	private void ShowListView(String result) {

		// 判斷是否有搜尋到診所
		if (result.length() > 5) {
			Travelers = JsonToList(result);
			setInAdapter();
			Toast.makeText(this, spotCategory, Toast.LENGTH_SHORT).show();
		} else {
			List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
			String[] from = { "name", "address" };
			int[] to = { R.id.listTextView1, R.id.listTextView2 };
			adapterHTTP = new SimpleAdapter(this, lists,
					R.layout.activity_list, from, to);
			adapterHTTP.notifyDataSetChanged();
			openOptionsDialogIsNoneResult();
		}
		myListView.setAdapter(adapterHTTP);

		myListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@SuppressWarnings("unchecked")
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// arg0就是ListView，arg1表示Item視圖，arg2表示資料index

						ListView lv = (ListView) arg0;
						// SimpleAdapter返回Map
						HashMap<String, String> traveler = (HashMap<String, String>) lv
								.getItemAtPosition(arg2);

						String telephone, address, name, category, content;

						name = traveler.get("name");
						address = traveler.get("address");
						telephone = traveler.get("telephone");
						category = traveler.get("category");
						content = traveler.get("content");

						Intent intent = new Intent();
						intent.setClass(
								ActivitySearchLocalCitiesSpotCategory.this,
								ActivitySearchLocalCitiesSpotDetail.class);

						Bundle bundle = new Bundle();

						bundle.putString("flag", flag);// 標記搜尋頁進入
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
	}

	private void openOptionsDialogIsNoneResult() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_none_result)
				.setMessage(R.string.app_none_result_msg)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						}).show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

	protected void setInAdapter() {
		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();

		Map<String, String> map;
		for (Traveler p : Travelers) {
			map = new HashMap<String, String>();

			map.put("_id", p.getId());
			map.put("name", p.getName());
			map.put("category", p.getCategory());
			map.put("cities", p.getCities());
			map.put("city", p.getCity());
			map.put("address", p.getAddress());
			map.put("telephone", p.getTelephone());
			map.put("content", p.getContent());

			lists.add(map);
		}

		// HashMap<String, String>中的key
		String[] from = { "name", "address" };

		int[] to = { R.id.listTextView1, R.id.listTextView2 };

		adapterHTTP = new SimpleAdapter(this, lists, R.layout.activity_list,
				from, to);

	}

	protected List<Traveler> JsonToList(String response) {
		List<Traveler> list = new ArrayList<Traveler>();

		try {
			// 將字符串轉換為Json數組
			JSONArray array = new JSONArray(response);

			int length = array.length();
			for (int i = 0; i < length; i++) {
				// 將每一個數組再轉換成Json對象
				JSONObject obj = array.getJSONObject(i);

				Traveler = new Traveler();
				Traveler.setId(obj.getString("_id"));
				Traveler.setName(obj.getString("name"));
				Traveler.setCategory(obj.getString("category"));
				Traveler.setCities(obj.getString("cities"));
				Traveler.setCity(obj.getString("city"));
				Traveler.setAddress(obj.getString("address"));
				Traveler.setTelephone(obj.getString("telephone"));
				Traveler.setContent(obj.getString("content"));

				list.add(Traveler);
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void visitExternalLinks() {
		// 發送Http請求
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
				.build());
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
			intent.setClass(ActivitySearchLocalCitiesSpotCategory.this, ActivitySearchLocalCitiesFavor.class);
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