package com.project2.travelman;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class ActivitySearchAroundMap extends Activity implements LocationListener {

    private static final String MAP_URL = "file:///android_asset/indexaround.html";
    private WebView webView;
    private Location mostRecentLocation;
    private LocationManager status;

    private String[][] data =  new String[300][4];
    private int count=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_display);




        status = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String best = status.getBestProvider(criteria, true);
            mostRecentLocation = status.getLastKnownLocation(best);

            setupWebView();//載入Webview

        } else {
            Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
        }


    }

    private void setupWebView(){

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);//啟用Webview的JavaScript功能
        webView.addJavascriptInterface(new JavaScriptInterface(), "MapDataFunction");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {

            }

        });
        webView.loadUrl(MAP_URL);  //載入URL

    }

    private class JavaScriptInterface{

        public double getLatitude(){
            return mostRecentLocation.getLatitude();
        }
        public double getLongitude(){
            return mostRecentLocation.getLongitude();
        }

        public String getData1(int x){
            return data[x][0];
        }

        public String getData2(int x){
            return data[x][1];
        }

        public String getData3(int x){
            return data[x][2];
        }

        public String getData4(int x){
            return data[x][3];
        }

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

}