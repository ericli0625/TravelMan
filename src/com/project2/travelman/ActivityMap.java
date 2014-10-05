package com.project2.travelman;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityMap extends Activity implements LocationListener {

    private static final String TAG = "ActivityMap";

    private static final String MAP_URL = "file:///android_asset/index.html";
//    private static final String MAP_URL = "http://gmaps-samples.googlecode.com/svn/trunk/articles-android-webmap/simple-android-map.html";

    private TextView textView_map;

    private Location mostRecentLocation;
    private LocationManager status;

    private String[][] data =  new String[300][4];

    private String coordinate_1;
    private String[] coordinate_array;

    private int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_display);

//        textView_map = (TextView) findViewById(R.id.textView_map);

        Intent intent = getIntent();
        ArrayList<Traveler> list = (ArrayList<Traveler>)intent.getSerializableExtra("list");

        for (Traveler p : list) {
            data[count][0] = p.getLatitude();
            data[count][1] = p.getLongitude();
            data[count][2] = p.getName();
            data[count][3] = p.getCategory();
            count++;
        }

        coordinate_1 = choeseCities(list.get(0).getCities());
        coordinate_array = coordinate_1.split(",");

        status = (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean isGPSEnabled = status.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = status.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
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
                        Log.v(TAG, "Network " + latitude + "," + longitude);
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
                            Log.v(TAG, "GPS " + latitude + "," + longitude);
                        }
                    }
                }
            }

            setupWebView();//載入Webview

        }

    }

    private void setupWebView(){

        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);//啟用Webview的JavaScript功能
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);       //是否支持手指縮放
        webView.getSettings().setDisplayZoomControls(true);      //是否顯示縮放按鈕(內置縮放+/-按鈕)
//        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.addJavascriptInterface(new MapDataFunction(), "MapDataFunction");

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl(MAP_URL);  //載入URL

//        textView_map.setText(data[0][0]);

    }

    public class MapDataFunction{

        @JavascriptInterface
        public double getLatitude(){
            return mostRecentLocation.getLatitude();
        }

        @JavascriptInterface
        public double getLongitude(){
            return mostRecentLocation.getLongitude();
        }

        @JavascriptInterface
        public String centerAtLatitude(){
            return coordinate_array[1];
        }

        @JavascriptInterface
        public String centerAtLongitude(){
            return coordinate_array[0];
        }

        @JavascriptInterface
        public String getData1(int x){
            return data[x][0];
        }

        @JavascriptInterface
        public String getData2(int x){
            return data[x][1];
        }

        @JavascriptInterface
        public String getData3(int x){
            return data[x][2];
        }

        @JavascriptInterface
        public String getData4(int x){
            return data[x][3];
        }

        @JavascriptInterface
        public int getDataSize(){
            return count;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onLocationChanged(Location location) {	//當地點改變時
        // TODO Auto-generated method stub
        mostRecentLocation = location;
    }

    @Override
    public void onProviderDisabled(String arg0) {	//當GPS或網路定位功能關閉時
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {	//當GPS或網路定位功能開啟
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {	//定位狀態改變
        // TODO Auto-generated method stub

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
                intent.setClass(ActivityMap.this, ActivitySearchLocalCitiesFavor.class);
                startActivity(intent);
                return true;
            default:
                break;
        }

        return true;
    }


    public String choeseCities(String name) {

        String cityCoordnate = null;
        String[] cities = getResources().getStringArray(R.array.cities_init);
        String[] coordArray = getResources().getStringArray(R.array.city_coordinate);

        if (cities[0].equals(name)) {
            cityCoordnate = coordArray[0];
        } else if (cities[1].equals(name)) {
            cityCoordnate = coordArray[1];
        } else if (cities[2].equals(name)) {
            cityCoordnate = coordArray[2];
        } else if (cities[3].equals(name)) {
            cityCoordnate = coordArray[3];
        } else if (cities[4].equals(name)) {
            cityCoordnate = coordArray[4];
        } else if (cities[5].equals(name)) {
            cityCoordnate = coordArray[5];
        } else if (cities[6].equals(name)) {
            cityCoordnate = coordArray[6];
        } else if (cities[7].equals(name)) {
            cityCoordnate = coordArray[7];
        } else if (cities[8].equals(name)) {
            cityCoordnate = coordArray[8];
        } else if (cities[9].equals(name)) {
            cityCoordnate = coordArray[9];
        } else if (cities[10].equals(name)) {
            cityCoordnate = coordArray[10];
        } else if (cities[11].equals(name)) {
            cityCoordnate = coordArray[11];
        } else if (cities[12].equals(name)) {
            cityCoordnate = coordArray[12];
        } else if (cities[13].equals(name)) {
            cityCoordnate = coordArray[13];
        } else if (cities[14].equals(name)) {
            cityCoordnate = coordArray[14];
        } else if (cities[15].equals(name)) {
            cityCoordnate = coordArray[15];
        } else if (cities[16].equals(name)) {
            cityCoordnate = coordArray[16];
        } else if (cities[17].equals(name)) {
            cityCoordnate = coordArray[17];
        } else if (cities[18].equals(name)) {
            cityCoordnate = coordArray[18];
        } else if (cities[19].equals(name)) {
            cityCoordnate = coordArray[19];
        } else if (cities[20].equals(name)) {
            cityCoordnate = coordArray[20];
        } else if (cities[21].equals(name)) {
            cityCoordnate = coordArray[21];
        } else{

        }

        return cityCoordnate;
    }

}
