package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitySearchLocalKeywords extends Activity {

    private static final String TAG = "ActivitySearchLocalKeywords";

	private String spottable = "total_spot", flag = "0",selectedCity = new String("所有區域");
	private Button myButtonSubmit, myButtonReset;
	private EditText editText_Search;
	private ListView myListView_Search;
	private Traveler Traveler;

    private ArrayAdapter<CharSequence> adapterTemp;
    private String[] citysArray;
    private Resources res;

	protected List<Traveler> Travelers;
	private String result = new String();

    private String str1;

	private SimpleAdapter adapterHTTP;

    private DBHelper DH = null;

    private LocationManager status;
    private Location mostRecentLocation;

    private Spinner mySpinner;
    private static final int ADD_ID = 0;
    private static final int CAN_ADD_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_keyword);

		visitExternalLinks();

        DH = new DBHelper(this);

        res = getResources();
        citysArray = res.getStringArray(R.array.cities_init_2);

        mySpinner = (Spinner) findViewById(R.id.spinner);
		myButtonSubmit = (Button) findViewById(R.id.button_Search_Submit);
		myButtonReset = (Button) findViewById(R.id.button_Search_Reset);
		editText_Search = (EditText) findViewById(R.id.editText_Search);
		myListView_Search = (ListView) findViewById(R.id.myListView);

		if (AppStatus.getInstance(this).isOnline(this)) {

            adapterTemp = ArrayAdapter.createFromResource(this, R.array.cities_init_2,R.layout.item2);

            adapterTemp
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mySpinner.setAdapter(adapterTemp);

            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View v,
                                           int position, long id) {
                    selectedCity = citysArray[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

			myButtonSubmit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					str1 = editText_Search.getText().toString();

					if (!str1.equals("")) {


                        status = (LocationManager) getSystemService(LOCATION_SERVICE);

                        boolean isGPSEnabled = status.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        boolean isNetworkEnabled = status.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                        if (!isGPSEnabled && !isNetworkEnabled) {
                            Toast.makeText(ActivitySearchLocalKeywords.this, "請開啟定位服務", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//??????
                        } else {

                            double longitude;
                            double latitude;

                            if (isNetworkEnabled) {

                                Log.d("Network", "Network Enabled");
                                if (status != null) {
                                    mostRecentLocation = status.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    if (mostRecentLocation != null) {
                                        latitude = mostRecentLocation.getLatitude();
                                        longitude = mostRecentLocation.getLongitude();
                                        Log.v(TAG, "Network "+ latitude +","+ longitude);
                                    }
                                }
                            }

                            if (isGPSEnabled) {
                                if (mostRecentLocation == null) {
                                    Log.d("GPS", "GPS Enabled");
                                    if (status != null) {
                                        mostRecentLocation = status.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                        if (mostRecentLocation != null) {
                                            latitude = mostRecentLocation.getLatitude();
                                            longitude = mostRecentLocation.getLongitude();
                                            Log.v(TAG, "GPS "+ latitude +","+ longitude);
                                        }
                                    }
                                }
                            }

                            new MyTask(ActivitySearchLocalKeywords.this).execute();

                        }

					} else {
						openOptionsDialogIsNotContext();
					}

				}
			});

			myButtonReset.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Perform action on click
					editText_Search.setText("");
				}
			});
		} else {
			openOptionsDialogIsNetworkAvailable();
		}
	}


    private class MyTask extends AsyncTask<String, Void, String> {

        /**
         * progress dialog to show user that the backup is processing.
         */
        private ProgressDialog dialog;
        /**
         * application context.
         */
        private Activity activity;

        public MyTask(Activity activity) {
            this.activity = activity;
            Activity context = activity;
            dialog = new ProgressDialog(context);
        }

        @Override
        protected String doInBackground(String... strings) {

            if(selectedCity.equals("所有區域")){
                String sql = "SELECT * FROM " + spottable
                        + " where name like '%" + str1 + "%'";
                result = DBConnector.executeQuery(sql);
            }else{

                String sql = "SELECT * FROM " + selectCity(selectedCity)
                        + " where name like '%" + str1 + "%'";
                result = DBConnector.executeQuery(sql);
            }

            return null;
        }

    protected void onPreExecute() {
        this.dialog.setTitle("搜尋中");
        this.dialog.setMessage("請稍後......");
        this.dialog.show();
        this.dialog.setCancelable(false);
    }

    @Override
    protected void onPostExecute(String results) {
        //doInBackground全部執行完後觸發

        ShowListView(result);

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    }

	private void ShowListView(String result) {

		if (result.length() > 5) {
			Travelers = JsonToList(result);
			setInAdapter();
			Toast.makeText(this, "搜尋完成", Toast.LENGTH_SHORT).show();
		} else {
			List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
			String[] from = { "name", "address" };
			int[] to = { R.id.listTextView1, R.id.listTextView2 };
			adapterHTTP = new SimpleAdapter(this, lists,
					R.layout.activity_list, from, to);
			adapterHTTP.notifyDataSetChanged();
			openOptionsDialogIsNoneResult();
		}
		myListView_Search.setAdapter(adapterHTTP);

        registerForContextMenu(myListView_Search);//將ContextMenu註冊到視圖上

		myListView_Search
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

						String telephone, address, name, category, content,longitude,latitude;

						name = traveler.get("name");
						address = traveler.get("address");
						telephone = traveler.get("telephone");
						category = traveler.get("category");
						content = traveler.get("content");

                        longitude = traveler.get("longitude");
                        latitude = traveler.get("latitude");

						Intent intent = new Intent();
						intent.setClass(ActivitySearchLocalKeywords.this,
								ActivitySearchLocalCitiesSpotDetail.class);

						Bundle bundle = new Bundle();

                        bundle.putString("flag", flag);// 標記搜尋頁進入
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
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}

				});
        myListView_Search
                .setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    @Override
                    public void onCreateContextMenu(ContextMenu menu, View v,
                                                    ContextMenu.ContextMenuInfo menuInfo) {
                        menu.setHeaderTitle("加入我的最愛");
                        menu.add(0, ADD_ID, 0, "確定");
                        menu.add(0, CAN_ADD_ID, 0, "取消");
                    }
                });

	}

    public String selectCity(String name){
        String engname = new String();
        String[] cities = getResources().getStringArray(R.array.cities_init);

        if (cities[0].equals(name)) {
            engname = "keelung_city";
        } else if (cities[1].equals(name)) {
            engname = "taipei_city";
        } else if (cities[2].equals(name)) {
            engname = "xinbei_city";
        } else if (cities[3].equals(name)) {
            engname = "taoyuan_county";
        } else if (cities[4].equals(name)) {
            engname = "hsinchu_city";
        } else if (cities[5].equals(name)) {
            engname = "hsinchu_county";
        } else if (cities[6].equals(name)) {
            engname = "miaoli_county";
        } else if (cities[7].equals(name)) {
            engname = "taichung_city";
        } else if (cities[8].equals(name)) {
            engname = "changhua_county";
        } else if (cities[9].equals(name)) {
            engname = "nantou_county";
        } else if (cities[10].equals(name)) {
            engname = "yunlin_county";
        } else if (cities[11].equals(name)) {
            engname = "chiayi_city";
        } else if (cities[12].equals(name)) {
            engname = "chiayi_county";
        } else if (cities[13].equals(name)) {
            engname = "tainan_city";
        } else if (cities[14].equals(name)) {
            engname = "kaohsiung_city";
        } else if (cities[15].equals(name)) {
            engname = "pingtung_county";
        } else if (cities[16].equals(name)) {
            engname = "yilan_county";
        } else if (cities[17].equals(name)) {
            engname = "hualien_county";
        } else if (cities[18].equals(name)) {
            engname = "taitung_county";
        } else if (cities[19].equals(name)) {
            engname = "penghu_county";
        } else if (cities[20].equals(name)) {
            engname = "kinmen_county";
        } else if (cities[21].equals(name)) {
            engname = "lianjiang_county";
        }

        return engname;
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ADD_ID:
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                addFavor(menuInfo.position);
                break;

            case CAN_ADD_ID:
                break;

            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private void addFavor(int id) {

        Traveler p = Travelers.get(id);

        Cursor cursor = DH.matchData(p.getName(), p.getCategory(), p.getAddress(),
                p.getTelephone(), p.getLongitude(),p.getLatitude(), p.getContent());

        int rows_num = cursor.getCount();

        if (rows_num == 1) {
            Toast.makeText(this, "您已經新增過了", Toast.LENGTH_SHORT)
                    .show();
        } else {
            DH.insert(p.getName(), p.getCategory(), p.getAddress(), p.getTelephone(), p.getLongitude(),p.getLatitude(), p.getContent());
            Toast.makeText(this, "新增至我的最愛", Toast.LENGTH_SHORT)
                    .show();
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

	private void openOptionsDialogIsNotContext() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.app_no_context)
				.setMessage(R.string.app_no_context_msg)
				.setPositiveButton(R.string.str_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
							}
						}).show();

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
            map.put("longitude", p.getLongitude());
            map.put("latitude", p.getLatitude());

            String formatStr = String.format("%.1f", Double.parseDouble(p.getDistance()));
            map.put("distance", formatStr+" KM");

			lists.add(map);
		}

		// HashMap<String, String>中的key
		String[] from = { "name", "address","category","distance"};

		int[] to = { R.id.listTextView1, R.id.listTextView2,R.id.listTextView3,R.id.listTextView4};

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
                Traveler.setLongitude(obj.getString("longitude"));
                Traveler.setLatitude(obj.getString("latitude"));

                if(!Traveler.getLatitude().equals("") || !Traveler.getLongitude().equals("")){

                    double ValueLatitude=Double.parseDouble(Traveler.getLatitude());
                    double ValueLongitude=Double.parseDouble(Traveler.getLongitude());

                    double value_dist_d = distance(mostRecentLocation.getLatitude(),mostRecentLocation.getLongitude(),
                            ValueLatitude,ValueLongitude);

                    String ss_t = Double.toString(value_dist_d);

                    Traveler.setDistance(ss_t);

                    list.add(Traveler);

                }
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
			intent.setClass(ActivitySearchLocalKeywords.this, ActivitySearchLocalCitiesFavor.class);
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

    public double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;

        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))

                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))

                * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);

        dist = rad2deg(dist);

        double miles = dist * 60 * 1.1515;

        return miles*1.609344;

    }

    public double deg2rad(double degree) {

        return degree / 180 * Math.PI;

    }

    public double rad2deg(double radian) {

        return radian * 180 / Math.PI;

    }

}