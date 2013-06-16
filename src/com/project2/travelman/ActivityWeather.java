package com.project2.travelman;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ActivityWeather extends Activity {

	private ListView myListView;
    private TextView myTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		
		myListView = (ListView) findViewById(R.id.myListView123);
        myTextView = (TextView) findViewById(R.id.weatherCityName);

		// 判斷是否有上網
		if (AppStatus.getInstance(this).isOnline(this)) {

			AsyncTask task = new MyTask(this).execute();

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

	private class MyTask extends AsyncTask<Void, Void, List<doubleWeather>> {

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
			this.dialog.setTitle("載入中");
			this.dialog.setMessage("請稍後......");
			this.dialog.show();
		}

		private SimpleAdapter adapterHTTP;

		@Override
		protected List<doubleWeather> doInBackground(Void... params) {
			List<doubleWeather> doubleWeatherListTermanal = new ArrayList<doubleWeather>();
			try {
				Document doc = Jsoup
						.connect(
								"http://www.cwb.gov.tw/V7/forecast/week/week.htm")
						.timeout(5000).get();
				Elements strTexts = doc.select("table.BoxTableInside");
				Elements pngs = doc.select("img[src$=.gif]");

				pngs.remove(pngs.first());

				String w1 = "d";
				String w2 = "n";

				List<doubleWeather> doubleWeatherListWeathers = new ArrayList<doubleWeather>();
				dataWeather imageWeather1 = new dataWeather(), imageWeather2 = new dataWeather();
				doubleWeather doubleWeatherImage = new doubleWeather();
				ArrayList<String> test1 = new ArrayList<String>();
				ArrayList<String> test2 = new ArrayList<String>();

				int countImage = 0;
				int countImage2 = 0;
				for (Element element : pngs) {
					String weather_name = element.toString().substring(35,
							element.toString().length());

					if (weather_name.substring(0, 1).equals(w1)) {
						String wee1 = weather_name.substring(4, 6);
						// System.out.println(weather_name);
						test1.add(wee1);
						if (countImage == 6) {
							imageWeather1.weatherDayNight(test1);
							countImage = 0;
							test1.clear();
							continue;
						}
						countImage++;
						// System.out.println(wee1);
					} else {
						String wee2 = weather_name.substring(6, 8);
						// System.out.println(weather_name);
						test2.add(wee2);
						if (countImage2 == 6) {
							imageWeather2.weatherDayNight(test2);
							countImage2 = 0;
							doubleWeatherImage.weather1 = (dataWeather) imageWeather1
									.clone();
							doubleWeatherImage.weather2 = (dataWeather) imageWeather2
									.clone();
							doubleWeatherListWeathers
									.add((doubleWeather) doubleWeatherImage
											.clone());
							test2.clear();
							continue;
						}
						countImage2++;
						// System.out.println(wee2);
					}

				}

				List<doubleWeather> doubleWeatherList = new ArrayList<doubleWeather>();
				doubleWeather doubleWeather1 = new doubleWeather();
				dataWeather dataWeather1 = new dataWeather(), dataWeather2 = new dataWeather();
				Iterator<Element> ite = strTexts.select("tr").iterator();
				String s1 = "白天";
				String s2 = "晚上";

				int count = 0;
				int flag = 0;

				while (ite.hasNext()) {
					String temp = ite.next().text();

					if (temp.substring(0, 2).equals(s1)) {
						// System.out.println(temp);//雜項
					} else {

						// 判斷晚上跟白天
						if (!temp.substring(0, 2).equals(s2)) {
							String words = temp.substring(7, temp.length());
							// System.out.println(temp);
							// System.out.println(words);

							dataWeather1.nameSet(temp.substring(0, 3));
							dataWeather1.splitDay(words, dataWeather1);
							flag = 1;

						} else {
							String words = temp.substring(3, temp.length());
							// System.out.println(temp);
							// System.out.println(words);
							dataWeather2.splitDay(words, dataWeather2);
							flag = 2;

							doubleWeather1.data1 = (dataWeather) dataWeather1
									.clone();
							doubleWeather1.data2 = (dataWeather) dataWeather2
									.clone();
							doubleWeatherList
									.add((doubleWeather) doubleWeather1.clone());

							flag = 0;
							count++;
						}

					}

				}

				doubleWeather doubleWeatherTermanal1 = new doubleWeather();

				for (int i = 0; i < doubleWeatherList.size(); i++) {

					doubleWeatherTermanal1.data1 = (dataWeather) doubleWeatherList
							.get(i).data1.clone();
					doubleWeatherTermanal1.data2 = (dataWeather) doubleWeatherList
							.get(i).data2.clone();
					doubleWeatherTermanal1.weather1 = (dataWeather) doubleWeatherListWeathers
							.get(i).weather1.clone();
					doubleWeatherTermanal1.weather2 = (dataWeather) doubleWeatherListWeathers
							.get(i).weather2.clone();
					doubleWeatherListTermanal
							.add((doubleWeather) doubleWeatherTermanal1.clone());

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			return doubleWeatherListTermanal;
		}

		@Override
		protected void onPostExecute(List<doubleWeather> resultLists) {

			List<Map<String, String>> lists = new ArrayList<Map<String,String>>();

			Map<String, String> map;
			for (doubleWeather p : resultLists) {
			    map = new HashMap<String, String>();
			    map.put("nameCity", p.data1.nameGet());

			    map.put("Day_temp", p.data1.a1Get());
			    map.put("Night_temp", p.data2.a1Get());
			    map.put("Day_image",Integer.toString(flags[Integer.parseInt(p.weather1.v1Get())]));
			    map.put("Night_image",Integer.toString(flags2[Integer.parseInt(p.weather2.v1Get())]));

                map.put("Day_temp_2", p.data1.a2Get());
                map.put("Night_temp_2", p.data2.a2Get());
                map.put("Day_image_2",Integer.toString(flags[Integer.parseInt(p.weather1.v2Get())]));
                map.put("Night_image_2",Integer.toString(flags2[Integer.parseInt(p.weather2.v2Get())]));

                map.put("Day_temp_3", p.data1.a3Get());
                map.put("Night_temp_3", p.data2.a3Get());
                map.put("Day_image_3",Integer.toString(flags[Integer.parseInt(p.weather1.v3Get())]));
                map.put("Night_image_3",Integer.toString(flags2[Integer.parseInt(p.weather2.v3Get())]));

			    lists.add(map);
			}

			//HashMap<String, String>中的key
			String[] from = { "nameCity", "Day_temp",
			"Night_temp","Day_image","Night_image"};

			int[] to = { R.id.listTextViewCities, R.id.listTextViewDay_temp,
			R.id.listTextViewNight_temp,R.id.imageViewDay,R.id.imageViewNight
			};

			adapterHTTP = new SimpleAdapter(ActivityWeather.this, lists,
			R.layout.activity_list_weather, from, to);

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
                            HashMap<String, String> weathers = (HashMap<String, String>) lv.getItemAtPosition(arg2);

                            String name,tempDay_1,tempDay_2,tempDay_3,tempNight_1,tempNight_2,tempNight_3,Day_image_1,Day_image_2,Day_image_3,Night_image_1,Night_image_2,Night_image_3;

                            name = weathers.get("nameCity");
                            tempDay_1 = weathers.get("Day_temp");
                            tempNight_1 = weathers.get("Night_temp");
                            Day_image_1 = weathers.get("Day_image");
                            Night_image_1 = weathers.get("Night_image");

                            tempDay_2 = weathers.get("Day_temp_2");
                            tempNight_2 = weathers.get("Night_temp_2");
                            Day_image_2 = weathers.get("Day_image_2");
                            Night_image_2 = weathers.get("Night_image_2");

                            tempDay_3 = weathers.get("Day_temp_3");
                            tempNight_3 = weathers.get("Night_temp_3");
                            Day_image_3 = weathers.get("Day_image_3");
                            Night_image_3 = weathers.get("Night_image_3");

                            Intent intent = new Intent();
                            intent.setClass(ActivityWeather.this,ActivityWeatherDetail.class);

                            Bundle bundle = new Bundle();

                            bundle.putString("nameCity", name);
                            bundle.putString("Day_temp", tempDay_1);
                            bundle.putString("Night_temp", tempNight_1);
                            bundle.putString("Day_image", Day_image_1);
                            bundle.putString("Night_image", Night_image_1);

                            bundle.putString("Day_temp_2", tempDay_2);
                            bundle.putString("Night_temp_2", tempNight_2);
                            bundle.putString("Day_image_2", Day_image_2);
                            bundle.putString("Night_image_2", Night_image_2);

                            bundle.putString("Day_temp_3", tempDay_3);
                            bundle.putString("Night_temp_3", tempNight_3);
                            bundle.putString("Day_image_3", Day_image_3);
                            bundle.putString("Night_image_3", Night_image_3);

                            // 把bundle物件指派給Intent
                            intent.putExtras(bundle);

                            // Activity (ActivityMenu)
                            startActivity(intent);
                            overridePendingTransition(R.anim.my_scale_action,
                                    R.anim.my_alpha_action);
                        }

                    });

            myTextView.setText("今日天氣狀況");

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

		}

		int[] flags = new int[] {R.drawable.d01, R.drawable.d01, R.drawable.n02,
				R.drawable.n03, R.drawable.n04, R.drawable.n05, R.drawable.n06,
				R.drawable.d07, R.drawable.d08, R.drawable.d09, R.drawable.d10,
				R.drawable.d11, R.drawable.d12, R.drawable.d13, R.drawable.d14,
				R.drawable.d15, R.drawable.d16, R.drawable.d17, R.drawable.d18,
				R.drawable.d19, R.drawable.d20, R.drawable.d21, R.drawable.d22,
				R.drawable.d23, R.drawable.d24, R.drawable.d25, R.drawable.d26,
				R.drawable.d27, R.drawable.d28, R.drawable.d29, R.drawable.d30,
				R.drawable.d31, R.drawable.d32, R.drawable.d33, R.drawable.d34,
				R.drawable.d35, R.drawable.d36, R.drawable.d37, R.drawable.d38,
				R.drawable.d39, R.drawable.d40, R.drawable.d41, R.drawable.d42,
				R.drawable.d43, R.drawable.d44, R.drawable.d45, R.drawable.d46,
				R.drawable.d47, R.drawable.d48, R.drawable.d49, R.drawable.d50,
				R.drawable.d51, R.drawable.d52, R.drawable.d53, R.drawable.d54,
				R.drawable.d55, R.drawable.d56, R.drawable.d57, R.drawable.d58,
				R.drawable.d59, R.drawable.d60, R.drawable.d61, R.drawable.d62,
				R.drawable.d63, R.drawable.d64, R.drawable.d65, };

		int[] flags2 = new int[] { R.drawable.n01, R.drawable.n01, R.drawable.n02,
				R.drawable.n03, R.drawable.n04, R.drawable.n05, R.drawable.n06,
				R.drawable.n07, R.drawable.n08, R.drawable.n09, R.drawable.d10,
				R.drawable.d11, R.drawable.d12, R.drawable.d13, R.drawable.d14,
				R.drawable.d15, R.drawable.d16, R.drawable.d17, R.drawable.d18,
				R.drawable.d19, R.drawable.d20, R.drawable.d21, R.drawable.n22,
				R.drawable.n23, R.drawable.n24, R.drawable.n25, R.drawable.d26,
				R.drawable.d27, R.drawable.d28, R.drawable.n29, R.drawable.n30,
				R.drawable.d31, R.drawable.d32, R.drawable.d33, R.drawable.n34,
				R.drawable.n35, R.drawable.d36, R.drawable.d37, R.drawable.d38,
				R.drawable.d39, R.drawable.n40, R.drawable.d41, R.drawable.d42,
				R.drawable.n43, R.drawable.d44, R.drawable.n45, R.drawable.n46,
				R.drawable.d47, R.drawable.d48, R.drawable.d49, R.drawable.d50,
				R.drawable.d51, R.drawable.d52, R.drawable.n53, R.drawable.n54,
				R.drawable.n55, R.drawable.n56, R.drawable.d57, R.drawable.d58,
				R.drawable.d59, R.drawable.d60, R.drawable.d61, R.drawable.d62,
				R.drawable.d63, R.drawable.d64, R.drawable.d65, };

	}

}