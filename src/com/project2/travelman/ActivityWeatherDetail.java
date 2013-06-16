package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityWeatherDetail extends Activity {

	private ListView myListView;
    private TextView myTextView;
    private SimpleAdapter adapterHTTP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

        myListView = (ListView) findViewById(R.id.myListView123);
        myTextView = (TextView) findViewById(R.id.weatherCityName);


		if (AppStatus.getInstance(this).isOnline(this)) {

            Bundle bundle = getIntent().getExtras();

            myTextView.setText(bundle.getString("nameCity"));

            List<Weather> list = new ArrayList<Weather>();

            Weather Weather_ = new Weather();
            Weather Weather_1 = new Weather();
            Weather Weather_2 = new Weather();

            Weather_.setA("今天");
            Weather_.setB(bundle.getString("Day_temp"));
            Weather_.setC(bundle.getString("Night_temp"));
            Weather_.setD(bundle.getString("Day_image"));
            Weather_.setE(bundle.getString("Night_image"));

            Weather_1.setA("明天");
            Weather_1.setB(bundle.getString("Day_temp_2"));
            Weather_1.setC(bundle.getString("Night_temp_2"));
            Weather_1.setD(bundle.getString("Day_image_2"));
            Weather_1.setE(bundle.getString("Night_image_2"));

            Weather_2.setA("後天");
            Weather_2.setB(bundle.getString("Day_temp_3"));
            Weather_2.setC(bundle.getString("Night_temp_3"));
            Weather_2.setD(bundle.getString("Day_image_3"));
            Weather_2.setE(bundle.getString("Night_image_3"));

            list.add(Weather_);
            list.add(Weather_1);
            list.add(Weather_2);

            List<Map<String, String>> lists = new ArrayList<Map<String,String>>();
            Map<String, String> map;
            for (Weather p : list) {
                map = new HashMap<String, String>();
                map.put("nameCity", p.A);
                map.put("Day_temp", p.B);
                map.put("Night_temp", p.C);
                map.put("Day_image", p.D);
                map.put("Night_image",p.E);
                lists.add(map);
            }

            //HashMap<String, String>‰∏≠ÁöÑkey
            String[] from = { "nameCity", "Day_temp",
                    "Night_temp","Day_image","Night_image"};

            int[] to = { R.id.listTextViewCities, R.id.listTextViewDay_temp,
                    R.id.listTextViewNight_temp,R.id.imageViewDay,R.id.imageViewNight
            };

            adapterHTTP = new SimpleAdapter(this, lists,
                    R.layout.activity_list_weather, from, to);

            myListView.setAdapter(adapterHTTP);

		} else {
			openOptionsDialogIsNetworkAvailable();
		}

	}

    class Weather{

        String A,B,C,D,E;

        public Weather(){

        };

        public void setA(String value){
            A=value;
        }

        public void setB(String value){
            B=value;
        }

        public void setC(String value){
            C=value;
        }

        public void setD(String value){
            D=value;
        }

        public void setE(String value){
            E=value;
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

}