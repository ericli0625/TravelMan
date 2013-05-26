package com.project2.travelman;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class ActivityWeather extends Activity {

	private ListView myListView;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		AsyncTask task = new MyTask(this).execute();

		myListView = (ListView) findViewById(R.id.myListView123);

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

			List<Map<String, String>> lists = new ArrayList<Map<String, String>>();

			Map<String, String> map;
			for (doubleWeather p : resultLists) {
				map = new HashMap<String, String>();
				map.put("nameCity", p.data1.nameGet());
				map.put("Day_temp", p.data1.a1Get());
				map.put("Night_temp", p.data2.a1Get());
                map.put("Day_image", Integer.toString(flags[Integer.parseInt(p.weather1.v1Get())]) );
                map.put("Night_image", Integer.toString(flags2[Integer.parseInt(p.weather2.v1Get())]) );
				lists.add(map);
			}

			// HashMap<String, String>中的key
			String[] from = { "nameCity", "Day_temp", "Night_temp","Day_image","Night_image"};

			int[] to = { R.id.listTextViewCities, R.id.listTextViewDay_temp, R.id.listTextViewNight_temp,R.id.imageViewDay,R.id.imageViewNight };

			adapterHTTP = new SimpleAdapter(ActivityWeather.this, lists,
					R.layout.activity_list_weather, from, to);

			myListView.setAdapter(adapterHTTP);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
		}

        int[] flags = new int[]{
                R.drawable.d01,
                R.drawable.d02,
                R.drawable.d03,
                R.drawable.d04,
                R.drawable.d05,
                R.drawable.d06,
                R.drawable.d07,
                R.drawable.d08,
                R.drawable.d09,
                R.drawable.d10,
                R.drawable.d11,
                R.drawable.d12,
                R.drawable.d13,
                R.drawable.d14,
                R.drawable.d15,
                R.drawable.d16,
                R.drawable.d17,
                R.drawable.d18,
                R.drawable.d19,
                R.drawable.d20,
                R.drawable.d21,
                R.drawable.d22,
                R.drawable.d23,
                R.drawable.d24,
                R.drawable.d25,
                R.drawable.d26,
                R.drawable.d27,
                R.drawable.d28,
                R.drawable.d29,
                R.drawable.d30,
                R.drawable.d31,
                R.drawable.d32,
                R.drawable.d33,
                R.drawable.d34,
                R.drawable.d35,
                R.drawable.d36,
                R.drawable.d37,
                R.drawable.d38,
                R.drawable.d39,
                R.drawable.d40,
                R.drawable.d41,
                R.drawable.d42,
                R.drawable.d43,
                R.drawable.d44,
                R.drawable.d45,
                R.drawable.d46,
                R.drawable.d47,
                R.drawable.d48,
                R.drawable.d49,
                R.drawable.d50,
                R.drawable.d51,
                R.drawable.d52,
                R.drawable.d53,
                R.drawable.d54,
                R.drawable.d55,
                R.drawable.d56,
                R.drawable.d57,
                R.drawable.d58,
                R.drawable.d59,
                R.drawable.d60,
                R.drawable.d61,
                R.drawable.d62,
                R.drawable.d63,
                R.drawable.d64,
                R.drawable.d65,
        };

        int[] flags2 = new int[]{
                R.drawable.d01,
                R.drawable.d02,
                R.drawable.d03,
                R.drawable.d04,
                R.drawable.d05,
                R.drawable.d06,
                R.drawable.d07,
                R.drawable.d08,
                R.drawable.d09,
                R.drawable.d10,
                R.drawable.d11,
                R.drawable.d12,
                R.drawable.d13,
                R.drawable.d14,
                R.drawable.d15,
                R.drawable.d16,
                R.drawable.d17,
                R.drawable.d18,
                R.drawable.d19,
                R.drawable.d20,
                R.drawable.d21,
                R.drawable.n22,
                R.drawable.n23,
                R.drawable.n24,
                R.drawable.n25,
                R.drawable.d26,
                R.drawable.d27,
                R.drawable.d28,
                R.drawable.n29,
                R.drawable.n30,
                R.drawable.d31,
                R.drawable.d32,
                R.drawable.d33,
                R.drawable.n34,
                R.drawable.n35,
                R.drawable.d36,
                R.drawable.d37,
                R.drawable.d38,
                R.drawable.d39,
                R.drawable.n40,
                R.drawable.d41,
                R.drawable.d42,
                R.drawable.n43,
                R.drawable.d44,
                R.drawable.n45,
                R.drawable.n46,
                R.drawable.d47,
                R.drawable.d48,
                R.drawable.d49,
                R.drawable.d50,
                R.drawable.d51,
                R.drawable.d52,
                R.drawable.n53,
                R.drawable.n54,
                R.drawable.n55,
                R.drawable.n56,
                R.drawable.d57,
                R.drawable.d58,
                R.drawable.d59,
                R.drawable.d60,
                R.drawable.d61,
                R.drawable.d62,
                R.drawable.d63,
                R.drawable.d64,
                R.drawable.d65,
        };

	}

}