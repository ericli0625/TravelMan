package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ActivitySearchAround extends Activity {

    private ListView myListView;

    private static final String TAG = "ActivitySearchAround";

    private ImageButton myButtonSubmit;
    private Spinner mySpinner;
    private String spotCategory = new String("所有類型");

    private ArrayList<Traveler> Travelers = null;

    private LocationManager status;
    private Location mostRecentLocation = null;

    private SimpleAdapter adapterHTTP;

    private String[] spotCategoryArray;
    private String[] cityAroundArry = null;

    private ArrayAdapter<CharSequence> adapterTemp;

    private static final int ADD_ID = 0;
    private static final int CAN_ADD_ID = 1;

    private DBHelper DH = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_display_around);

        DH = new DBHelper(this);

        spotCategoryArray = getResources().getStringArray(R.array.spot_category);
        mySpinner = (Spinner) findViewById(R.id.spinner_cate);
        myButtonSubmit = (ImageButton) findViewById(R.id.button_Search_submit);
        myListView = (ListView) findViewById(R.id.myListView);

        if (AppStatus.getInstance(this).isOnline(this)) {

            adapterTemp = ArrayAdapter.createFromResource(ActivitySearchAround.this, R.array.spot_category, R.layout.item2);
            adapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mySpinner.setAdapter(adapterTemp);

            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View v,
                                           int position, long id) {
                    spotCategory = spotCategoryArray[position];

                    status = (LocationManager) getSystemService(LOCATION_SERVICE);

                    boolean isGPSEnabled = status.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    boolean isNetworkEnabled = status.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                    if (!isGPSEnabled && !isNetworkEnabled) {
                        Toast.makeText(ActivitySearchAround.this, "請開啟定位服務", Toast.LENGTH_LONG).show();
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

                        if (mostRecentLocation == null){
                            Toast.makeText(ActivitySearchAround.this, "It can't find RecentLocation.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setClass(ActivitySearchAround.this, MainActivity.class);
                            startActivity(intent);
                        } else{
                            new MyTask(ActivitySearchAround.this).execute(spotCategory);
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

        } else {
            openOptionsDialogIsNetworkAvailable();
        }

        myButtonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub

                    Intent intent = new Intent();
                    intent.setClass(ActivitySearchAround.this, ActivityMap.class);
                    intent.putExtra("list",  Travelers);
                    if (Travelers != null) {
                        startActivity(intent);
                        overridePendingTransition(R.anim.my_scale_action,R.anim.my_alpha_action);
                    } else{
                        Toast.makeText(ActivitySearchAround.this, "Travelers.isEmpty()", Toast.LENGTH_LONG).show();
                    }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.item_favor:
                Intent intent = new Intent();
                intent.setClass(ActivitySearchAround.this, ActivitySearchLocalCitiesFavor.class);
                startActivity(intent);
                return true;
            default:
                break;
        }

        return true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }

    private void addFavor(int id) {

        Traveler p = Travelers.get(id);

        Cursor cursor = DH.matchData(p.getName(), p.getCategory(), p.getAddress(),
                p.getTelephone(), p.getLongitude(),p.getLatitude(), p.getContent());

        int rows_num = cursor.getCount();

        if (rows_num == 1) {
            Toast.makeText(ActivitySearchAround.this, "您已經新增過了", Toast.LENGTH_SHORT)
                    .show();
        } else {
            DH.insert(p.getName(), p.getCategory(), p.getAddress(), p.getTelephone(), p.getLongitude(),p.getLatitude(), p.getContent());
            Toast.makeText(ActivitySearchAround.this, "新增至我的最愛", Toast.LENGTH_SHORT)
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

    public void visitExternalLinks() {
        // 發送Http請求
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());
    }

    public String cityAroundDetect(String valueCity){

        String[] cities = getResources().getStringArray(R.array.cities_init);

        if (cities[0].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.keelung_city_around);
        } else if (cities[1].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.taipei_city_around);
        } else if (cities[2].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.xinbei_city_around);
        } else if (cities[3].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.taoyuan_county_around);
        } else if (cities[4].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.hsinchu_city_around);
        } else if (cities[5].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.hsinchu_county_around);
        } else if (cities[6].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.miaoli_county_around);
        } else if (cities[7].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.taichung_city_around);
        } else if (cities[8].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.changhua_county_around);
        } else if (cities[9].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.nantou_county_around);
        } else if (cities[10].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.yunlin_county_around);
        } else if (cities[11].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.chiayi_city_around);
        } else if (cities[12].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.chiayi_county_around);
        } else if (cities[13].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.tainan_city_around);
        } else if (cities[14].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.kaohsiung_city_around);
        } else if (cities[15].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.pingtung_county_around);
        } else if (cities[16].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.yilan_county_around);
        } else if (cities[17].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.hualien_county_around);
        } else if (cities[18].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.taitung_county_around);
        } else if (cities[19].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.penghu_county_around);
        } else if (cities[20].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.kinmen_county_around);
        } else if (cities[21].equals(valueCity)) {
            cityAroundArry = getResources().getStringArray(R.array.lianjiang_county_around);
        } else {
            //default location
//            cityAroundArry = getResources().getStringArray(R.array.taipei_city_around);
        }

        String sqlContent = new String(" where (");
        String sqlLeft = new String(" cities like '");
        String sqlRight = new String("'");
        String sqlTail = new String(" or");

        int count=0;
        if (cityAroundArry!=null) {
            for (String aroundCity : cityAroundArry) {

                if (count == 0) {
                    sqlContent = sqlContent + sqlLeft + aroundCity + sqlRight;
                } else {
                    sqlContent = sqlContent + sqlTail + sqlLeft + aroundCity + sqlRight;
                }
                count++;
            }
        } else {
            Log.v(TAG, "cityAroundArry = "+ cityAroundArry);
        }

        return sqlContent;
    }

    public String getAddressByLocation(Location location) {
        String returnAddress = "";
        try {
            if (location != null) {
                double longitude = location.getLongitude();	//取得經度
                double latitude = location.getLatitude();	//取得緯度

                Geocoder gc = new Geocoder(ActivitySearchAround.this, Locale.TRADITIONAL_CHINESE); 	//地區:台灣
                //自經緯度取得地址
                List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 1);

                if (!Geocoder.isPresent()){ //Since: API Level 9
                    returnAddress = "Sorry! Geocoder service not Present.";
                }
                returnAddress = lstAddress.get(0).getAdminArea();
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return returnAddress;
    }

    private class MyTask extends AsyncTask<String, Void, String> {

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        /** application context. */
        private Activity activity;

        public MyTask(Activity activity) {
            this.activity = activity;
            Activity context = activity;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            this.dialog.setTitle("搜尋中");
            this.dialog.setMessage("請稍後......");
            this.dialog.show();
            this.dialog.setCancelable(false);
        }

        private String result = new String();
        private String spottable = "total_spot";

        @Override
        protected String doInBackground(String... params) {
            //第二個執行方法會在onPreExecute()執行完後立刻執行 ，
            // 並運行在背景執行緒中。主要負責運行耗時的背景處理工作。
            // 本方法是抽象方法，必須實現。注意不能在這裡更新//UI元件//.

            if (mostRecentLocation != null) {

                String mAddress = getAddressByLocation(mostRecentLocation);

                if (mAddress != null) {
                    Log.v(TAG, "mAddress = " + mAddress);
                } else {
                    Log.v(TAG, "mAddress = " + mAddress);
                }

                if (!params[0].equals("") && !params[0].equals("所有類型")) {
                    String sql = "SELECT * FROM " + spottable + cityAroundDetect(mAddress) + ") and category like '" + spotCategory + "'";
                    result = DBConnector.executeQuery(sql);
                } else {
                    String sql = "SELECT * FROM " + spottable + cityAroundDetect(mAddress) + ") ";
                    result = DBConnector.executeQuery(sql);
                }

            } else{
                Log.v(TAG, "mostRecentLocation = " + mostRecentLocation);
            }

            visitExternalLinks();

            return params[0];
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


    protected ArrayList<Traveler> JsonToList(String response) {
        ArrayList<Traveler> list = new ArrayList<Traveler>();
        Traveler Traveler;

        try {
            // 將字符串轉換為Json數組
            JSONArray array = new JSONArray(response);

            int length = array.length();
            int distance_max = 10;
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

                    if ((int)value_dist_d < distance_max){
                        list.add(Traveler);
                    }

                }

            }

            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

        if (lists.isEmpty()){
            openOptionsDialogIsNoneResult();
        }

        // HashMap<String, String>中的key
        String[] from = { "name", "address","category","distance"};

        int[] to = { R.id.listTextView1, R.id.listTextView2,R.id.listTextView3,R.id.listTextView4};

        adapterHTTP = new SimpleAdapter(ActivitySearchAround.this, lists, R.layout.activity_list, from, to);

    }

    private void ShowListView(String result) {

        if (result.length() > 5) {
            Travelers = JsonToList(result);
            setInAdapter();
            Toast.makeText(this, "搜尋完成", Toast.LENGTH_SHORT).show();
        } else {

            List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
            String[] from = {"name", "address"};
            int[] to = {R.id.listTextView1, R.id.listTextView2};
            adapterHTTP = new SimpleAdapter(ActivitySearchAround.this, lists, R.layout.activity_list, from, to);
            adapterHTTP.notifyDataSetChanged();
            openOptionsDialogIsNoneResult();
        }


        myListView.setAdapter(adapterHTTP);

        registerForContextMenu(myListView);//將ContextMenu註冊到視圖上

        myListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        // arg0就是ListView，arg1表示Item視圖，arg2表示資料index

                        ListView lv = (ListView) arg0;
                        // SimpleAdapter返回Map
                        HashMap<String, String> traveler = (HashMap<String, String>) lv.getItemAtPosition(arg2);

                        String telephone, address, name, category, content,longitude,latitude;

                        name = traveler.get("name");
                        address = traveler.get("address");
                        telephone = traveler.get("telephone");
                        category = traveler.get("category");
                        content = traveler.get("content");
                        longitude = traveler.get("longitude");
                        latitude = traveler.get("latitude");

                        Intent intent = new Intent();
                        intent.setClass(
                                ActivitySearchAround.this,
                                ActivitySearchLocalCitiesSpotDetail.class);

                        Bundle bundle = new Bundle();

                        bundle.putString("flag", "0");// 標記搜尋頁進入
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
                        overridePendingTransition(R.anim.my_scale_action,
                                R.anim.my_alpha_action);
                    }

                });

        myListView
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