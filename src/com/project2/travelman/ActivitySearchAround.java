package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivitySearchAround extends Activity {

    private String spottable = "total_spot";
    private SimpleAdapter adapterHTTP;

    private TextView mytextView;
    private ListView myListView;
    private String result = new String();
    private Button myButtonSubmit;
    private Spinner mySpinner;
    private String spotCategory = new String("所有類型");

    private ArrayList<Traveler> Travelers;
    private Traveler Traveler;

    private LocationManager status;
    private Location mostRecentLocation;
    private String[] spotCategoryArray;
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
        myButtonSubmit = (Button) findViewById(R.id.button_Search_submit);
        myListView = (ListView) findViewById(R.id.myListView);

        if (AppStatus.getInstance(this).isOnline(this)) {

        adapterTemp = ArrayAdapter.createFromResource(this, R.array.spot_category, R.layout.item2);
        adapterTemp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mySpinner.setAdapter(adapterTemp);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                spotCategory = spotCategoryArray[position];

                if (!spotCategory.equals("") && !spotCategory.equals("所有類型")) {
                    String sql = "SELECT * FROM " + spottable + " where category like '" + spotCategory + "'";
                    result = DBConnector.executeQuery(sql);
                }else {
                    String sql = "SELECT * FROM " + spottable + " ";
                    result = DBConnector.executeQuery(sql);
                }

                status = (LocationManager) getSystemService(LOCATION_SERVICE);

                if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    String best = status.getBestProvider(criteria, true);
                    mostRecentLocation = status.getLastKnownLocation(best);

                    ShowListView(result);

                } else {
                    Toast.makeText(ActivitySearchAround.this, "請開啟定位服務", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//??????
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

            visitExternalLinks();

            myButtonSubmit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                }
            });

        } else {
            openOptionsDialogIsNetworkAvailable();
        }

    }

    private void ShowListView(String result) {

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

        registerForContextMenu(myListView);//將ContextMenu註冊到視圖上

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
                intent.setClass(ActivitySearchAround.this, ActivitySearchLocalCitiesFavor.class);
                startActivity(intent);
                return true;
            default:
                break;
        }

        return true;
    }

    protected ArrayList<Traveler> JsonToList(String response) {
        ArrayList<Traveler> list = new ArrayList<Traveler>();

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

    public void visitExternalLinks() {
        // 發送Http請求
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                .build());
    }

}